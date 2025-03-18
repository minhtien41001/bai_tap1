package controller;

import service.ITeacherService;
import service.impl.TeacherService;

import java.util.Scanner;

public class TeacherController {
    private ITeacherService iTeacherService = new TeacherService();
    private Scanner scanner = new Scanner(System.in);

    public void menuTeacher(){
        do {
            System.out.println("---CHƯƠNG TRÌNH QUẢN LÝ GIẢNG VIÊN--- \n" +
                    "1. Thêm mới giảng viên \n" +
                    "2. Xem danh sách giảng viên \n"  +
                    "3. Xóa giáo viên theo ID \n"  +
                    "4. Cập nhật giáo viên theo ID \n"  +
                    "5. Tìm kiếm giáo viên theo tên \n"  +
                    "6. Trở về menu chính");
            int choice = 0;
            try {
                System.out.println("Chọn chức năng: ");
                choice = Integer.parseInt(scanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Vui lòng nhập số! ");
            }

            switch (choice){
                case 1:{
                    iTeacherService.addTeacher();
                    break;
                }
                case 2:{
                    iTeacherService.displayTeacher();
                    break;
                }
                case 3:{
                    iTeacherService.removeTeacher();
                    break;
                }
                case 4:{
                    iTeacherService.updateTeacher();
                    break;
                }
                case 5:{
                    iTeacherService.findByName();
                    break;
                }
                case 6:{
                    return;
                }
            }
        }while (true);
    }
}
