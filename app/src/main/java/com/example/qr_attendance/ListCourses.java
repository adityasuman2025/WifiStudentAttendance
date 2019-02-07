package com.example.qr_attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

public class ListCourses extends AppCompatActivity
{
//define variables
    SharedPreferences sharedPreferences;
    TextView userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_courses);

        userInfo = findViewById(R.id.userInfo);

    //checking the existing cookie
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String user_id_cookie = decrypt(sharedPreferences.getString("user_id", "DNE"));
        String roll_no_cookie = decrypt(sharedPreferences.getString("roll_no", "DNE"));

        userInfo.setText("Roll No: " + roll_no_cookie + "\nUser ID: " + user_id_cookie);
    }


//function for encrypting and decrypting the text
    public static String encrypt(String input)
    {
        // This is base64 encoding, which is not an encryption
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    public static String decrypt(String input)
    {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }
}
