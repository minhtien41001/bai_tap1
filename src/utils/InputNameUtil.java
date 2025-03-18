package utils;

import exception.InputNameException;

public class InputNameUtil {
    private static final String NAME_REGEX = "([\\p{Lu}][\\p{Ll}]{1,5}[ ]){1,4}([\\p{Lu}][\\p{Ll}]{1,5})";
    public static String getNameUtil(String str) throws InputNameException {
        StringBuilder string = new StringBuilder();
        if (str.matches(NAME_REGEX)) {
            str = str.toLowerCase().trim();
            str = str.replaceAll("[ ]+", " ");
            String[] arrName = str.split(" ");

            for (String s : arrName) {
                string.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
            }
        } else {
            throw new InputNameException("LỖI: Phải nhập họ và tên, mời nhập lại!!!");
        }
        return string.toString().trim();
    }
}