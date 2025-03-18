package utils;

import exception.BirthOfDateException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputDateOfBirthUtil {
    private static final String DATEOFBIRTH_REGEX = "\\d{2}/(0[1-9]|1[0-2])/(19|20)\\d{2}";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate getBirthDay(String str) {
        Scanner scanner = new Scanner(System.in);
        LocalDate birthDay;

        while (true) {
            try {
                System.out.println(str);
                String input = scanner.nextLine().trim();

                if (!input.matches(DATEOFBIRTH_REGEX)) {
                    throw new BirthOfDateException("LỖI: Nhập sai định dạng! Phải nhập theo định dạng dd/MM/yyyy.");
                }

                birthDay = LocalDate.parse(input, FORMATTER);

                int year = birthDay.getYear();
                int month = birthDay.getMonthValue();
                int day = birthDay.getDayOfMonth();

                if (year < 1900 || year > LocalDate.now().getYear()) {
                    throw new BirthOfDateException("LỖI: Năm sinh phải từ 1900 đến " + LocalDate.now().getYear() + "!");
                }

                if (!isValidDate(day, month, year)) {
                    throw new BirthOfDateException("LỖI: Ngày không hợp lệ!");
                }

                break;
            } catch (BirthOfDateException e) {
                System.err.println(e.getMessage());
            } catch (DateTimeParseException e) {
                System.err.println("LỖI: Định dạng ngày không hợp lệ.");
            }
        }
        return birthDay;
    }

    private static boolean isValidDate(int day, int month, int year) {
        switch (month) {
            case 2:
                if (isLeapYear(year)) {
                    return day <= 29;
                } else {
                    return day <= 28;
                }
            case 4: case 6: case 9: case 11:
                return day <= 30;
            default:
                return day <= 31;
        }
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}