package com.csumb.cst363;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import java.sql.Date;
@Service

public class Sanitizer {

    /**
     * This accepts a string and returns true if it is a valid date from 1900 to 2021
     */
    public static boolean isDOB(Date d){
		if ((d.getYear() >= 1900 && d.getYear() <= 2022 && d.getMonth() >= 1 && d.getMonth() <= 12 && d.getDay() >= 1
				&& d.getDay() <= 31)) {
			return true;
		} else {
			return false;
		}
    }
    /**
    * This accepts a string and returns true if its an address
     * A valid name can contain letters (uppercase and lowercase), spaces, commas, hyphens, and apostrophes.
     */
    public static boolean isStreet(String s) {
        String regex = "[0-9a-zA-Z ,\\-\\.]+";
        if (Pattern.matches(regex, s)) return true;
        else return false;
    }

    /**
     * This accepts a string and returns true if its a name
     * A valid name can contain letters (uppercase and lowercase), spaces, hyphens, and apostrophes.
     */
    public static boolean isString(String s) {
        String regex = "[a-z A-Z\\-']+";
       if (Pattern.matches(regex,s))return true;
       else return false;
    }

    /**
     * This accepts an int and returns true if its a valid ssn
     */
    public static boolean isSSN(int ssn) {
    	
        String ssnString = String.valueOf(ssn);
        if (ssnString.length() < 9 || ssnString.length() > 9) {
        	return false;
        }
        else if (ssnString.charAt(0) == '0' || ssnString.charAt(0) == '9') {
        	return false;
        }
        else if (ssnString.charAt(3) == '0' && ssnString.charAt(4) == '0') {
        	return false;
        }
        else if (ssnString.charAt(5) == '0' && ssnString.charAt(6) == '0'
        		&& ssnString.charAt(7) == '0' && ssnString.charAt(8) == '0') {
        	return false;
        }
        return true;
    }

    /**
     * Accepts string. returns true if it is a valid zip containing 5 numbers excluding 00000
     */
    public static boolean isZip(int zip){
    	String zipString = String.valueOf(zip);
    	if (zipString.length() == 5 || zipString.length() == 9) {
        	return true;
        }
    	else {
    		return false;
    	}
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