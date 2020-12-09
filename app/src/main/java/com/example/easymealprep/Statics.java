package com.example.easymealprep;

import java.util.ArrayList;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Statics {
    static SQLConnection connection;
    static boolean loop = true;
    static boolean check = false;
    static String currUserAccount, currUserEmail, currPassword, currName;
    static  boolean home = true;
    // currFood[0] will be foodID
    // currFood[1] will be food name
    // currFood[2] will be food description
    // currFood[3] will be food picture
    static Object[] currFood = new Object[4];
    static ArrayList<Integer> currFavList;

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    // encodes the password to different string
    public static String encoder(String input) {
        String result = "";
        int count = 0;
        for (char ch: input.toCharArray()) {
            int temp = ch + (6 + count);
            if (temp > 126) {
                temp = temp - 127+32;
            }
            result = result + ((char) temp);
            count++;
        }
        return result;
    }

    // decodes the encoded password to original string
    public static String decoder(String input) {
        String result = "";
        int count = 0;
        for (char ch: input.toCharArray()) {
            int temp = ch - (6 + count);
            if (temp < 32) {
                temp = 127 + temp-32;
            }
            result = result + ((char) temp);
            count++;
        }
        return result;
    }
}

