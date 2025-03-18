package utils;

import exception.EmailException;
import exception.InputClassException;

public class InputEmailUtil {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static String getEmailRegex(String str) throws EmailException {
        StringBuilder string = new StringBuilder();
        if (str.matches(EMAIL_REGEX)) {
            str = str.toLowerCase().trim();
            str = str.replaceAll("[ ]+", " ");
            String[] arrName = str.split(" ");

            for (String s : arrName) {
                string.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
            }
        } else {
            throw new EmailException("LỖI: Bạn phải nhập email theo đúng định dạng, mời nhập lại!!!");
        }
        return string.toString().trim();
    }
}
