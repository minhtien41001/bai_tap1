package utils;

import model.Student;
import model.Teacher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReadFileUtil {
    private static List<String> readFile(String path) {
        List<String> stringList = new ArrayList<>();
        String line;
        try {
            File file = new File(path);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                stringList.add(line);
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringList;
    }

    public static List<Teacher> readTeacherFile(String path) {
        List<String> stringList = readFile(path);
        List<Teacher> teacherList = new ArrayList<>();
        String[] info;
        for (String line : stringList) {
            info = line.split(",");
            if (info.length == 5) {
                teacherList.add(new Teacher(Integer.parseInt(info[0]), info[1], info[2], LocalDate.parse(info[3]), info[4]));
            }
        }

        return teacherList;
    }

    public static List<Student> readStudentFile(String path) {
        List<String> stringList = readFile(path);
        List<Student> studentList = new ArrayList<>();
        String[] info;

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String line : stringList) {
            info = line.split(",");
            if (info.length == 6) {
                LocalDate date;
                try {
                    date = LocalDate.parse(info[3], formatter1);
                } catch (Exception e) {
                    try {
                        date = LocalDate.parse(info[3], formatter2);
                    } catch (Exception ex) {
                        System.err.println("Lỗi định dạng ngày: " + info[3]);
                        continue;
                    }
                }

                studentList.add(new Student(Integer.parseInt(info[0]), info[1], info[2], date, info[4],
                        Double.parseDouble(info[5])));
            }
        }
        return studentList;
    }
}
