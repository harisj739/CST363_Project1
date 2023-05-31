package com.csumb.cst363;

import java.util.regex.Pattern;

public class Sanitizer {

    /**
     * This accepts a string and returns true if it is a valid date from 1900 to 2021
     */
    public static boolean isDOB(String s){
        String regex = "((19[0-9]{2})|(20(([0-1][0-9])|(2[0-1]))))-(02-(0[1-9]|[12][0-9])|(0[469]|11)-(0[1-9]|[12][0-9]|30)|(0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))";
        if (Pattern.matches(regex, s) )return true;
        else return false;
    }
    /**
    * This accepts a string and returns true if its an address
     * A valid name can contain letters (uppercase and lowercase), spaces, commas, hyphens, and apostrophes.
     */
    public static boolean isAddress(String s) {
        String regex = "[0-9a-zA-Z ,\\-\\.]+";
        if (Pattern.matches(regex, s)) return true;
        else return false;
    }

    /**
     * This accepts a string and returns true if its a name
     * A valid name can contain letters (uppercase and lowercase), spaces, hyphens, and apostrophes.
     */
    public static boolean isName(String s) {
        String regex = "[a-z A-Z\\-']+";
       if (Pattern.matches(regex,s))return true;
       else return false;
    }

    /**
     * This accepts an int and returns true if its a valid ssn
     */
    public static boolean isSSN(int ssn) {
        String ssnString = String.valueOf(ssn);
        String regex = "^(?!000|666)(\\d{3}-?\\d{2}-?\\d{4})$";
        return Pattern.matches(regex, ssnString);
    }

    /**
     * Accepts string. returns true if it is a valid zip containing 5 numbers excluding 00000
     */
    public static boolean isZip(String s){
        String regex ="(?!00000)[0-9]{5}";
        if (Pattern.matches(regex, s)) return true;
        else return false;
    }

    public static boolean isFullAddress(String s){
        String regex = "[A-Za-z0-9' -]+,[A-Za-z0-9' -]+,[A-Za-z0-9' -]+,(?!00000)[0-9]{5}";
        if (Pattern.matches(regex, s)) return true;
        else return false;
    }

    public static void main(String[] args) {
        System.out.println(Sanitizer.isFullAddress("123 something street,LA,CA"));
        System.out.println(Sanitizer.isFullAddress("123 something street,LA,CA,900"));
        System.out.println(Sanitizer.isFullAddress("123 something street,LA,CA,90210"));
        System.out.println(Sanitizer.isFullAddress("123 something st, Los angeles, CA 90210"));
    }
}