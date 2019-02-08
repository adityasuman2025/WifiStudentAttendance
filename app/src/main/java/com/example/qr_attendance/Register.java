package com.example.qr_attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class Register extends AppCompatActivity
{
//defining variables
    EditText reg_name_input;
    EditText reg_roll_input;
    EditText reg_pass_input;
    EditText reg_con_pass_input;

    TextView reg_feed;
    Button reg_new_btn;

    SharedPreferences sharedPreferences;
    String androidId;
    String uniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg_name_input = findViewById(R.id.reg_name_input);
        reg_roll_input = findViewById(R.id.reg_roll_input);
        reg_pass_input = findViewById(R.id.reg_pass_input);
        reg_con_pass_input = findViewById(R.id.reg_con_pass_input);

        reg_feed = findViewById(R.id.reg_feed);
        reg_new_btn = findViewById(R.id.reg_new_btn);

    //to get unique identification of a phone and displaying it
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); // android id
        uniqueID = android.os.Build.SERIAL; // Serial_no

        reg_new_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String reg_name = reg_name_input.getText().toString();
                String reg_roll = reg_roll_input.getText().toString();
                String reg_pass = reg_pass_input.getText().toString();
                String reg_con_pass = reg_con_pass_input.getText().toString();

                //checking if phone if connected to net or not
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    if (reg_pass.equals(reg_con_pass) && reg_pass.length() != 0 && reg_con_pass.length() != 0) {
                        //check if that phone is already registered or not
                        String type = "check_phone_registered";
                        try {
                            int check_phone_result = Integer.parseInt(new registerRequest().execute(type, androidId, uniqueID).get());

                            if (check_phone_result == 1)//yes phone is already registered
                            {
                                reg_feed.setText("This phone is already registered for a student.");
                            } else if (check_phone_result == -1) {
                                reg_feed.setText("Database issue found");
                            } else {
                                //checking if all input fields have been filled or not
                                if (reg_name.length() != 0 && reg_roll.length() != 0) {
                                    //check if that roll number is already registered or not
                                    type = "check_roll_exist";
                                    int check_roll_result = Integer.parseInt(new registerRequest().execute(type, reg_roll).get());

                                    if (check_roll_result == 1)//yes roll number already exist
                                    {
                                        reg_feed.setText("This Roll Number is already registered.");
                                    } else if (check_roll_result == -1) {
                                        reg_feed.setText("Database issue found");
                                    } else {
                                        //if everything fine then registering the new user in the databse
                                        type = "register_new_user_in_db";
                                        int register_new_user_result = Integer.parseInt(new registerRequest().execute(type, reg_roll, reg_name, reg_pass, androidId, uniqueID).get());

                                        if (register_new_user_result > 0)//successfully registered
                                        {
                                            //creating cookie of the logged in user
                                            sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("roll_no", encrypt(reg_roll));
                                            editor.putString("user_id", encrypt(Integer.toString(register_new_user_result)));
                                            editor.apply();

                                            //reg_feed.setText("Successfully registered");

                                            //redirecting the list course page
                                            Intent ListCourseIntent = new Intent(Register.this, ListCourses.class);
                                            startActivity(ListCourseIntent);
                                            finish(); //used to delete the last activity history which we want to delete
                                        } else if (register_new_user_result == -1) {
                                            reg_feed.setText("Database issue found");
                                        } else {
                                            reg_feed.setText("Something went wrong registering user");
                                        }
                                    }
                                } else {
                                    reg_feed.setText("Please fill all the input fields");
                                }
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        reg_feed.setText("Password do not match");
                    }
                } else {
                    reg_feed.setText("Internet connection is not available");
                }
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

class registerRequest extends AsyncTask<String,Void,String> {
    String base_url = "http://mngo.in/qr_attendance/";

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String result = "Something went wrong";
        URL url;

        if(type.equals("check_phone_registered"))
        {
            String login_url = base_url + "check_phone_registered.php";
            try {
                String androidId = params[1];
                String uniqueID = params[2];

                //connecting with server
                url = new URL(login_url);
                HttpURLConnection httpURLConnection = null;
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                //sending phone info to the server
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("androidId", "UTF-8") + "=" + URLEncoder.encode(androidId, "UTF-8") + "&"
                        + URLEncoder.encode("uniqueID", "UTF-8") + "=" + URLEncoder.encode(uniqueID, "UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                //getting the data coming from server after logging
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                result = "";
                String line;

                while ((line = bufferedReader.readLine()) != null) {
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
        else if(type.equals("check_roll_exist"))
        {
            String login_url = base_url + "check_roll_exist.php";
            try {
                String reg_roll = params[1];

                //connecting with server
                url = new URL(login_url);
                HttpURLConnection httpURLConnection = null;
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                //sending phone info to the server
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("reg_roll", "UTF-8") + "=" + URLEncoder.encode(reg_roll, "UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                //getting the data coming from server after logging
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                result = "";
                String line;

                while ((line = bufferedReader.readLine()) != null) {
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
        else if(type.equals("register_new_user_in_db"))
        {
            String login_url = base_url + "register_new_user_in_db.php";
            try {
                String roll_no = params[1];
                String name = params[2];
                String password = params[3];
                String androidId = params[4];
                String uniqueID = params[5];

                //connecting with server
                url = new URL(login_url);
                HttpURLConnection httpURLConnection = null;
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                //sending phone info to the server
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("roll_no","UTF-8")+"="+URLEncoder.encode(roll_no,"UTF-8")+"&"
                        +URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"
                        +URLEncoder.encode("androidId","UTF-8")+"="+URLEncoder.encode(androidId,"UTF-8")+"&"
                        +URLEncoder.encode("uniqueID","UTF-8")+"="+URLEncoder.encode(uniqueID,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                //getting the data coming from server after logging
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                result = "";
                String line;

                while ((line = bufferedReader.readLine()) != null) {
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