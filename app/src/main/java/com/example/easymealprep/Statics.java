package com.example.easymealprep;

import java.util.ArrayList;

public class Statics {
    static SQLConnection connection;
    static boolean loop = true;
    static boolean check = false;
    static String currUserAccount, currUserEmail, currPassword, currName;
    // currFood[0] will be foodID
    // currFood[1] will be food name
    // currFood[2] will be food description
    // currFood[3] will be food picture
    static Object[] currFood = new Object[4];
    static ArrayList<Integer> currFavList;
}
