package com.ddd.chulsi.infrastructure.util;

import java.util.List;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 영어체크
     */
    public static boolean isEnglish(char ch) {
        return (ch >= (int)'A' && ch <= (int)'Z') || (ch >= (int)'a' && ch <= (int)'z');
    }

    /**
     * 영어체크
     */
    public static boolean isSecondEnglish(String value) {
        String pattern = "^(.*[A-Za-z])$";
        return Pattern.matches(pattern, value);
    }

    /**
     * 한글체크
     */
    public static boolean isKorean(char ch) {
        return ch >= Integer.parseInt("AC00", 16) && ch <= Integer.parseInt("D7A3", 16);
    }

    /**
     * 한글체크
     */
    public static boolean isKorean(String value) {
        String pattern = "^[ㄱ-ㅎ가-힣]*$";
        return Pattern.matches(pattern, value);
    }

    /**
     * 숫자체크
     */
    public static boolean isNumber(char ch) {
        return ch >= (int)'0' && ch <= (int)'9';
    }

    /**
     * 숫자체크
     */
    public static boolean isNumberValid(String value) {
        String pattern = "^[0-9]*$";
        return Pattern.matches(pattern, value);
    }

    /**
     * 영어 + 숫자 체크
     */
    public static boolean isEnglishAndNumber(String value) {
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]$";
        return Pattern.matches(pattern, value);
    }

    /**
     * 영어 + 숫자 + 길이 체크
     */
    public static boolean isEnglishAndNumberAndLength(String value, int min) {
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{" + min + ",}$";
        return Pattern.matches(pattern, value);
    }

    /**
     * 영문 + 숫자 + 특수문자 체크
     */
    public static boolean isEnglishAndNumberAndSpecial(String value, int min) {
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{" + min + ",}$";
        return Pattern.matches(pattern, value);
    }

    /**
     * 특수문자체크
     */
    public static boolean isSpecial(char ch) {
        return (ch >= (int)'!' && ch <= (int)'/') // !"#$%&'()*+,-./
            || (ch >= (int)':' && ch <= (int)'@') // :;<=>?@
            || (ch >= (int)'[' && ch <= (int)'`') // [\]^_`
            || (ch >= (int)'{' && ch <= (int)'~'); // {|}~
    }

    /**
     * tag convert
     */
    public static String tagConvert(String in) {

        String new_str = in;
        if(new_str == null) return new String();
        String[] search = {";", "&", "'", "\"", "<", ">", "\\{", "}"};
        String[] replace = {"", "&amp;", "&#039;", "&quot;", "&lt;", "&gt;", "&openbrace;", "&closebrace;"};
        for( int i=0 ; i<search.length ; i++ ) {
            new_str = new_str.replaceAll(search[i], replace[i]);
        }
        return new_str;
    }


    /**
     * To convert untagged to tag. This method is to be used standalone like.
     */
    public static String tagRestore(String in) {

        String new_str = in;
        if(new_str == null) return new String();
        String[] search = {"&amp;", "&#039;", "&quot;", "&lt;", "&gt;", "&openbrace;", "&closebrace;"};
        String[] replace = {"&", "'", "\"", "<", ">", "{", "}"};
        for( int i=0 ; i<search.length ; i++ ) {
            new_str = new_str.replaceAll(search[i], replace[i]);
        }
        return new_str;
    }

    /**
     * 휴대폰번호 Dash 추가
     */
    public static String phoneAddDash(String phone) {
        String first = phone.substring(0, 3);
        String mid = phone.length() == 10 ? phone.substring(3, 6) : phone.substring(3, 7);
        String last = phone.length() == 10 ? phone.substring(6) : phone.substring(7);
        
        return String.format("%s-%s-%s", first, mid, last);
    }

    /**
     * array toString add double quote
     */
    public static String toString(Object[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            if (a[i] instanceof Integer) {
                b.append(String.valueOf(a[i]));
            } else {
                b.append("\"");
                b.append(String.valueOf(a[i]));
                b.append("\"");
            }
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    /**
     * list toString add double quote
     */
    public static String toString(List T) {
        if (T == null)
            return "null";

        int iMax = T.size() - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            if (T.get(i) instanceof Integer) {
                b.append(String.valueOf(T.get(i)));
            } else {
                b.append("\"");
                b.append(String.valueOf(T.get(i)));
                b.append("\"");
            }
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static boolean isValidNumber(String str, int max) {
        // 입력 값이 null 이거나 길이가 0 이상 max 이하인지 확인합니다.
        if (str == null || str.length() == 0 || str.length() > max)
            return false;

        return isNumberValid(str);
    }

    public static boolean in(Object value, Object ... values) {
        String str = String.valueOf(value);

        for (Object v : values) {
            if (String.valueOf(v).equals(str)) return true;
        }

        return false;
    }

    public static boolean notIn(Object value, Object ... values) {
        boolean result = true;

        String str = String.valueOf(value);

        for (Object v : values) {
            if (String.valueOf(v).equals(str)) return false;
        }

        return result;
    }


}
