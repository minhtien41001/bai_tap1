package utils;

import exception.InputClassException;

public class InputClassUtil {
    private static final String CLASS_REGEX = "(19|2[0-5])DTCLC[1-4]";
    public static String getClassRegex(String str) throws InputClassException {
        StringBuilder string = new StringBuilder();
        if (str.matches(CLASS_REGEX)) {
            str = str.toLowerCase().trim();
            str = str.replaceAll("[ ]+", " ");
            String[] arrName = str.split(" ");

            for (String s : arrName) {
                string.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
            }
        } else {
            throw new InputClassException("LỖI: Bạn phải nhập tên lớp theo đúng định dạng, mời nhập lại!!!");
        }
        return string.toString().trim();
    }
}
