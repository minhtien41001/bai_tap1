package service.impl;

import exception.DuplicateIDException;
import exception.EmailException;
import exception.InputClassException;
import exception.InputNameException;
import model.Student;
import service.IStudentService;
import utils.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StudentService implements IStudentService {

    private static Scanner scanner = new Scanner(System.in);
    private static final String PATH = "src/data/studentList.csv";
    private static final int THREAD_POOL_SIZE = 5; // Số luồng tối đa
    private static final int BATCH_SIZE = 10; // Mỗi luồng xử lý 10 dòng
    private static final String LOG_PATH = "src/data/process_log.txt";

    @Override
    public void displayStudent() {
        List<Student> studentList = ReadFileUtil.readStudentFile(PATH);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        List<List<Student>> partitions = partitionList(studentList, BATCH_SIZE);

        for (List<Student> batch : partitions) {
            executor.execute(() -> processBatch(batch));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Lỗi khi chờ luồng hoàn thành: " + e.getMessage());
        }
    }

    @Override
    public void addStudent() {
        List<Student> studentList = ReadFileUtil.readStudentFile(PATH);
        Student student = infoStudent();
        studentList.add(student);
        System.out.println("Thêm mới sinh viên thành công! ");

        WriteFileUtil.writeStudentFile(PATH,studentList);
    }

    @Override
    public void removeStudent() {
        List<Student> studentList = ReadFileUtil.readStudentFile(PATH);
        System.out.println("Nhập ID sinh viên cần xóa: ");
        int idRemove = Integer.parseInt(scanner.nextLine());
        boolean isFlag = false;

        for (Student student : studentList){
            if (student.getId() == idRemove){
                System.out.println("Bạn có chắc muốn xóa sinh viên này? \n " +
                        "1. CÓ \n" +
                        "2. KHÔNG");
                int choiceYesNo = Integer.parseInt(scanner.nextLine());
                if (choiceYesNo == 1){
                    studentList.remove(student);
                    System.out.println("Xóa thành công! ");

                    WriteFileUtil.writeStudentFile(PATH,studentList);
                }
                isFlag =true;
                break;
            }
        }
        if (!isFlag){
            System.out.println("Không tìm thấy sinh viên này! ");
        }
    }

    @Override
    public void findById() {
        List<Student> studentList = ReadFileUtil.readStudentFile(PATH);
        boolean check = false;
        System.out.println("Nhập ID sinh viên cần tìm: ");
        int findId = Integer.parseInt(scanner.nextLine());

        for (Student student : studentList){
            if (findId == student.getId()){
                System.out.println(student);
                check = true;
                break;
            }
        }
        if (!check){
            System.out.println("Không tìm thấy sinh viên có ID " + findId);
        }
    }

    @Override
    public void updateStudent() {
        List<Student> studentList = ReadFileUtil.readStudentFile(PATH);
        System.out.println("Nhập ID sinh viên cần sửa: ");

        int idUpdate;
        while (true) {
            try {
                idUpdate = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số nguyên hợp lệ!");
            }
        }

        boolean isFound = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Student student : studentList) {
            if (student.getId() == idUpdate) {
                System.out.println("Nhập thông tin mới cho sinh viên (bấm Enter để bỏ qua và giữ nguyên giá trị cũ):");

                // Nhập tên mới
                while (true) {
                    try {
                        String newName = InputUtil.getString("Tên mới: ");
                        if (!newName.isEmpty()) {
                            student.setFullName(InputNameUtil.getNameUtil(newName));
                        }
                        break;
                    } catch (InputNameException e) {
                        System.err.println("Lỗi: " + e.getMessage());
                    }
                }

                // Nhập email mới
                while (true) {
                    try {
                        String newEmail = InputUtil.getString("Email mới: ");
                        if (!newEmail.isEmpty()) {
                            student.setEmail(InputEmailUtil.getEmailRegex(newEmail));
                        }
                        break;
                    } catch (EmailException e){
                        System.err.println(e.getMessage());
                    }
                }

                // Nhập ngày sinh mới (dd/MM/yyyy)
                while (true) {
                    try {
                        String newDob = InputUtil.getString("Nhập ngày sinh mới (dd/MM/yyyy): ");
                        if (!newDob.isEmpty()) {
                            student.setDateOfBirth(LocalDate.parse(newDob, formatter));
                        }
                        break;
                    } catch (DateTimeParseException e) {
                        System.out.println("Lỗi: Định dạng ngày không hợp lệ! Vui lòng nhập lại theo định dạng dd/MM/yyyy.");
                    }
                }

                // Nhập lớp mới
                while (true) {
                    try {
                        String newClassName = InputUtil.getString("Lớp mới: ");
                        if (!newClassName.isEmpty()) {
                            student.setClassName(InputClassUtil.getClassRegex(newClassName));
                        }
                        break;
                    } catch (InputClassException e) {
                        System.err.println("Lỗi: " + e.getMessage());
                    }
                }

                // Nhập điểm mới
                while (true) {
                    try {
                        String newPoints = InputUtil.getString("Điểm mới: ");
                        if (!newPoints.isEmpty()) {
                            student.setPoints(Double.parseDouble(newPoints));
                        }
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Lỗi: Điểm phải là số hợp lệ. Vui lòng nhập lại.");
                    }
                }

                System.out.println("Cập nhật sinh viên thành công!");
                isFound = true;
                break;
            }
        }

        if (!isFound) {
            System.out.println("Không tìm thấy sinh viên có ID: " + idUpdate);
        } else {
            WriteFileUtil.writeStudentFile(PATH, studentList);
        }
    }

    @Override
    public void findByName() {
        List<Student> studentList = ReadFileUtil.readStudentFile(PATH);
        boolean check = false;

        System.out.println("Nhập tên sinh viên cần tìm: ");
        String findName = scanner.nextLine();

        for (Student student : studentList) {
            if (student.getFullName().contains(findName)) {
                System.out.println(student);
                check = true;
            }
        }

        if (!check) {
            System.out.println("Không tìm thấy sinh viên có tên: " + findName);
        }
    }

    public static Student infoStudent(){
        List<Student> studentList = ReadFileUtil.readStudentFile(PATH);
        int id;
        while (true) {
            try {
                System.out.println("Nhập ID sinh viên: ");
                id = Integer.parseInt(scanner.nextLine());

                for (Student student :studentList){
                    if (student.getId() == id){
                        throw new DuplicateIDException("Trùng ID,vui lòng nhập lại! ");
                    }
                }
                break;
            }catch (NumberFormatException e){
                System.out.println("Vui lòng nhập số!");
            }catch (DuplicateIDException e){
                System.out.println(e.getMessage());
            }
        }

        String fullName;
        while (true) {
            try {
                fullName= InputUtil.getString("Nhập tên học sinh mới: ");
                fullName = InputNameUtil.getNameUtil(fullName);
                break;
            } catch (InputNameException e) {
                System.err.println(e.getMessage());
            }

        }

        LocalDate dateOfBirth = InputDateOfBirthUtil.getBirthDay("Nhập ngày sinh của sinh viên: ");

        String email;
        while (true) {
            try {
                email= InputUtil.getString("Nhập email của sinh viên: ");
                email = InputEmailUtil.getEmailRegex(email);
                break;
            } catch (EmailException e) {
                System.err.println(e.getMessage());
            }

        }

        String className ;
        while (true) {


            try {
                className = InputUtil.getString("Nhập lớp của sinh viên: ");
                className = InputClassUtil.getClassRegex(className);
                break;
            } catch (InputClassException e) {
                System.err.println(e.getMessage());
            }
        }

        double points;
        while (true){
            try{
                System.out.println("Nhập điểm của sinh viên: ");
                points = Double.parseDouble(scanner.nextLine());
                break;
            }catch (NumberFormatException e){
                System.out.println("Vui lòng nhập số!");
            }
        }
        return new Student(id,fullName,email,dateOfBirth,className,points);
    }

    private void processBatch(List<Student> batch) {
        for (Student student : batch) {
            processStudent(student);
        }
    }

    private void processStudent(Student student) {
        try {
            // Xử lý sinh viên (ở đây chỉ là in ra màn hình)
            System.out.println(student);
            // Ghi log vào file
            writeLog(student);
        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý sinh viên ID: " + student.getId());
        }
    }

    private void writeLog(Student student) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String threadName = Thread.currentThread().getName();
        String logMessage = String.format("[%s] [%s] Đã xử lý sinh viên: %d - %s%n",
                timestamp, threadName, student.getId(), student.getFullName());

        synchronized (this) { // Đảm bảo các luồng không bị ghi đè lẫn nhau
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_PATH, true))) {
                writer.write(logMessage);
            } catch (IOException e) {
                System.err.println("Lỗi khi ghi log: " + e.getMessage());
            }
        }
    }

    private List<List<Student>> partitionList(List<Student> list, int size) {
        List<List<Student>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

    private void writeSuccessLog() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String threadName = Thread.currentThread().getName();
        String successMessage = String.format("[%s] [%s] Đọc thành công%n", timestamp, threadName);

        synchronized (this) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_PATH, true))) {
                writer.write(successMessage);
            } catch (IOException e) {
                System.err.println("Lỗi khi ghi log: " + e.getMessage());
            }
        }
    }
}
