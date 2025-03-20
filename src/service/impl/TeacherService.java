package service.impl;

import exception.DuplicateIDException;
import exception.EmailException;
import exception.InputClassException;
import exception.InputNameException;
import model.Student;
import model.Teacher;
import service.ITeacherService;
import utils.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TeacherService implements ITeacherService {
    private static Scanner scanner = new Scanner(System.in);
    private static final String PATH = "src/data/teacherList.csv";
    private static final int THREAD_POOL_SIZE = 5; // Số luồng tối đa
    private static final int BATCH_SIZE = 10; // Mỗi luồng xử lý 10 dòng
    private static final String LOG_PATH = "src/data/process_log.txt";

    @Override
    public void displayTeacher() {
        List<Teacher> teacherList = ReadFileUtil.readTeacherFile(PATH);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        List<List<Teacher>> partitions = partitionList(teacherList, BATCH_SIZE);

        for (List<Teacher> batch : partitions) {
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
    public void addTeacher() {
        List<Teacher> teacherList = ReadFileUtil.readTeacherFile(PATH);
        Teacher teacher = infoTeacher();
        teacherList.add(teacher);
        System.out.println("Thêm mới giáo viên thành công! ");

        WriteFileUtil.writeTeacherFile(PATH,teacherList);
    }

    @Override
    public void removeTeacher() {
        List<Teacher> teacherList = ReadFileUtil.readTeacherFile(PATH);
        System.out.println("Nhập ID giáo viên cần xóa: ");
        int idRemove = Integer.parseInt(scanner.nextLine());
        boolean isFlag = false;

        for (Teacher teacher : teacherList){
            if (teacher.getId() == idRemove){
                System.out.println("Bạn có chắc muốn xóa giáo viên này? \n " +
                        "1. CÓ \n" +
                        "2. KHÔNG");
                int choiceYesNo = Integer.parseInt(scanner.nextLine());
                if (choiceYesNo == 1){
                    teacherList.remove(teacher);
                    System.out.println("Xóa thành công! ");

                    WriteFileUtil.writeTeacherFile(PATH,teacherList);
                }
                isFlag =true;
                break;
            }
        }
        if (!isFlag){
            System.out.println("Không tìm thấy giáo viên này! ");
        }
    }

    @Override
    public void findById() {

    }

    @Override
    public void updateTeacher() {
        List<Teacher> teacherList = ReadFileUtil.readTeacherFile(PATH);
        System.out.println("Nhập ID giáo viên cần sửa: ");

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

        for (Teacher teacher : teacherList) {
            if (teacher.getId() == idUpdate) {
                System.out.println("Nhập thông tin mới cho giáo viên (bấm Enter để bỏ qua và giữ nguyên giá trị cũ):");

                // Nhập tên mới
                while (true) {
                    try {
                        String newName = InputUtil.getString("Tên mới: ");
                        if (!newName.isEmpty()) {
                            teacher.setFullName(InputNameUtil.getNameUtil(newName));
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
                            teacher.setEmail(InputEmailUtil.getEmailRegex(newEmail));
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
                            teacher.setDateOfBirth(LocalDate.parse(newDob, formatter));
                        }
                        break;
                    } catch (DateTimeParseException e) {
                        System.out.println("Lỗi: Định dạng ngày không hợp lệ! Vui lòng nhập lại theo định dạng dd/MM/yyyy.");
                    }
                }

                // Nhập chuyên ngành mới
                while (true) {
                        String newSpecialize = InputUtil.getString("Chuyên ngành mới: ");
                        if (!newSpecialize.isEmpty()) {
                            teacher.setSpecialize(newSpecialize);
                        }
                        break;
                }

                System.out.println("Cập nhật giáo viên thành công!");
                isFound = true;
                break;
            }
        }

        if (!isFound) {
            System.out.println("Không tìm thấy giáo viên có ID: " + idUpdate);
        } else {
            WriteFileUtil.writeTeacherFile(PATH, teacherList);
        }
    }

    @Override
    public void findByName() {
        List<Teacher> teacherList = ReadFileUtil.readTeacherFile(PATH);
        boolean check = false;

        System.out.println("Nhập tên giáo viên cần tìm: ");
        String findName = scanner.nextLine();

        for (Teacher teacher : teacherList) {
            if (teacher.getFullName().contains(findName)) {
                System.out.println(teacher);
                check = true;
            }
        }

        if (!check) {
            System.out.println("Không tìm thấy giáo viên có tên: " + findName);
        }
    }

    public static Teacher infoTeacher() {
        List<Teacher> teacherList = ReadFileUtil.readTeacherFile(PATH);
        int id;
        while (true) {
            try {
                System.out.println("Nhập ID giáo viên: ");
                id = Integer.parseInt(scanner.nextLine());

                for (Teacher teacher :teacherList){
                    if (teacher.getId() == id){
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
                fullName= InputUtil.getString("Nhập tên giáo viên mới: ");
                fullName = InputNameUtil.getNameUtil(fullName);
                break;
            } catch (InputNameException e) {
                System.err.println(e.getMessage());
            }

        }

        LocalDate dateOfBirth = InputDateOfBirthUtil.getBirthDay("Nhập ngày sinh của giáo viên: ");

        String email;
        while (true) {
            try {
                email= InputUtil.getString("Nhập email của giáo viên: ");
                email = InputEmailUtil.getEmailRegex(email);
                break;
            } catch (EmailException e) {
                System.err.println(e.getMessage());
            }

        }

        String specialize;
        while (true){

                System.out.println("Nhập chuyên ngành của giáo viên: ");
                specialize = scanner.nextLine();
                break;
        }
        return new Teacher(id,fullName,email,dateOfBirth,specialize);
    }

    private void processBatch(List<Teacher> batch) {
        for (Teacher teacher : batch) {
            processTeacher(teacher);
        }
    }

    private void processTeacher(Teacher teacher) {
        try {
            // Xử lý giáo viên (ở đây chỉ là in ra màn hình)
            System.out.println(teacher);
            // Ghi log vào file
            writeLog(teacher);
        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý giáo viên ID: " + teacher.getId());
        }
    }

    private void writeLog(Teacher teacher) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String threadName = Thread.currentThread().getName();
        String logMessage = String.format("[%s] [%s] Đã xử lý giáo viên: %d - %s%n",
                timestamp, threadName, teacher.getId(), teacher.getFullName());

        synchronized (this) { // Đảm bảo các luồng không bị ghi đè lẫn nhau
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_PATH, true))) {
                writer.write(logMessage);
            } catch (IOException e) {
                System.err.println("Lỗi khi ghi log: " + e.getMessage());
            }
        }
    }

    private List<List<Teacher>> partitionList(List<Teacher> list, int size) {
        List<List<Teacher>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }
}
