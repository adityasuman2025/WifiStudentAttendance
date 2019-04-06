package com.example.studentattendance;

import android.util.Base64;

public class Encryption
{
    //function for encrypting and decrypting the text
    public static String encrypt(String input)
    {
        // This is base64 encoding, which is not an Encryption
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    public static String decrypt(String input)
    {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }
}
