package com.example.qr_attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.Arrays;

public class AttendanceQR extends AppCompatActivity
{
    SharedPreferences sharedPreferences;
    TextView qr_text;
    ImageView qr_view;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_qr);

        qr_text = findViewById(R.id.qr_text);
        qr_view = findViewById(R.id.qr_view);

    //to get the cookie values
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String user_id_cookie = decrypt(sharedPreferences.getString("user_id", "DNE"));
        String course_id_cookie = sharedPreferences.getString("course_id", "DNE");

    //to get current timestamps
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

    //storing info into JSON and encrypting it
        String qr_data[] = {user_id_cookie, course_id_cookie, ts}; //JSON format: userID, courseID, currentTimestamps
        JSONArray mJSONArray = new JSONArray(Arrays.asList(qr_data));

        String encrypted_data = encrypt(mJSONArray.toString());

        if(user_id_cookie != null && course_id_cookie != null)
        {
            try
            {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                BitMatrix bitMatrix = multiFormatWriter.encode(encrypted_data, BarcodeFormat.QR_CODE,500,500);

                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                qr_view.setImageBitmap(bitmap);
            }
            catch(WriterException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            qr_text.setText("Something went wrong");
        }
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
