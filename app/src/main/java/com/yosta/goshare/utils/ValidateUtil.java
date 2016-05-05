package com.yosta.goshare.utils;

import java.util.regex.Pattern;

public class ValidateUtil {

    public static int MAX_PWD_LENGTH = 5;
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    public static boolean IsMinLengthValidated(String text, int minLength) {
        return (text.length() >= minLength);
    }

    public static boolean IsValidateWithRegex(String text, String regex) {
        return (Pattern.matches(regex, text));
    }

    public static boolean IsValidateEmail(String text, int minLength, String regex) {
        return (IsMinLengthValidated(text, minLength) && IsValidateWithRegex(text, regex));

    }

    public static boolean IsValidatePassword(String text, int minLength) {
        return IsMinLengthValidated(text, minLength);
    }
}
