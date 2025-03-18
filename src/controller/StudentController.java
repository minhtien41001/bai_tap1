package controller;

import service.IStudentService;
import service.impl.StudentService;

import java.util.Scanner;

public class StudentController {
    private IStudentService iStudentService = new StudentService();
    private Scanner scanner = new Scanner(System.in);

    public void menuStudent(){
        do{
            System.out.println("---CHƯƠNG TRÌNH QUẢN LÝ SINH VIÊN--- \n" +
                    "1. Thêm mới sinh viên \n" +
                    "2. Xem danh sách sinh viên \n" +
                    "3. Xóa sinh viên theo ID \n" +
                    "4. Chỉnh sửa sinh viên theo ID \n" +
                    "5. Tìm kiếm sinh viên theo tên \n" +
                    "6. Trở về menu chính");

            int choice = 0;
            try {
                System.out.println("Chọn chức năng: ");
                choice = Integer.parseInt(scanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Vui lòng nhập số! ");
            }

            switch (choice){
                case 1:{
                    iStudentService.addStudent();
                    break;
                }
                case 2:{
                    iStudentService.displayStudent();
                    break;
                }
                case 3:{
                    iStudentService.removeStudent();
                    break;
                }
                case 4:{
                    iStudentService.updateStudent();
                    break;
                }
                case 5:{
                    iStudentService.findByName();
                    break;
                }
                case 6:{
                    return;
                }
            }
        }while (true);
    }
}
