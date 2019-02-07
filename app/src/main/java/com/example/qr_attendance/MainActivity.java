package com.example.qr_attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
{
//defining variables
    Button login_btn;
    Button register_btn;
    EditText roll_no_input;
    EditText password_input;
    TextView login_feed;

    SharedPreferences sharedPreferences;
    String androidId;
    String uniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);
        roll_no_input = findViewById(R.id.roll_no_input);
        password_input = findViewById(R.id.password_input);
        login_feed = findViewById(R.id.login_feed);

    //checking if already loggedIn or not
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String user_id_cookie = sharedPreferences.getString("user_id", "DNE");

        if(user_id_cookie.equals("DNE"))
        {
            //login_feed.setText("No one is logged in");
        }
        else //if someone is already logged in
        {
        //redirecting the list course page
            Intent ListCourseIntent = new Intent(MainActivity.this, ListCourses.class);
            startActivity(ListCourseIntent);
            finish(); //used to delete the last activity history which we want to delete
        }

    //to get unique identification of a phone and displaying it
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); // android id
        uniqueID = android.os.Build.SERIAL; // Serial_no

    //on clicking on login button
        login_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String roll_no = roll_no_input.getText().toString().toUpperCase();
                String password = password_input.getText().toString();
                String type = "verify_login";

            //trying to login the user
                try
                {
                    int login_result = Integer.parseInt(new getData().execute(type, roll_no, password, androidId, uniqueID).get());

                    if(login_result > 0)
                    {
                    //creating cookie of the logged in user
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("roll_no", encrypt(roll_no));
                        editor.putString("user_id", encrypt(Integer.toString(login_result)));
                        editor.apply();

                        //login_feed.setText(Integer.toString(login_result));

                    //redirecting the list course page
                        Intent ListCourseIntent = new Intent(MainActivity.this, ListCourses.class);
                        startActivity(ListCourseIntent);
                        finish(); //used to delete the last activity history which we don't want to delete
                    }
                    else if(login_result == -1)
                    {
                        login_feed.setText("Database issue found");
                    }
                    else
                    {
                        login_feed.setText("Your login credentials may be incorrect or this may be not your registered phone.");
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    //on clicking on register button
        register_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent homeIntent = new Intent(MainActivity.this, Register.class);
                startActivity(homeIntent);
                finish(); //used to delete the last activity history which we want to delete
            }
        });
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

class getData extends AsyncTask<String,Void,String>
{
    String base_url = "http://mngo.in/qr_attendance/";

    @Override
    protected String doInBackground(String... params)
    {
        String type = params[0];
        String result = "Something went wrong";
        URL url;

        if(type.equals("verify_login"))
        {
            String login_url = base_url + "verify_login.php";
            try
            {
                String roll_no = params[1];
                String password = params[2];
                String androidId = params[3];
                String uniqueID = params[4];

            //connecting with server
                url = new URL(login_url);
                HttpURLConnection httpURLConnection = null;
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

            //sending login info to the server
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("roll_no","UTF-8")+"="+URLEncoder.encode(roll_no,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"
                        +URLEncoder.encode("androidId","UTF-8")+"="+URLEncoder.encode(androidId,"UTF-8")+"&"
                        +URLEncoder.encode("uniqueID","UTF-8")+"="+URLEncoder.encode(uniqueID,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

            //getting the data coming from server after logging
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

                result="";
                String line;

                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}


