package com.example.studentattendance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    //defining variables
    Button login_btn;
    Button register_btn;
    Button forgot_pass_btn;

    EditText roll_no_input;
    EditText password_input;
    TextView login_feed;

    SharedPreferences sharedPreferences;

    String androidId;
    String uniqueID;

    int androidVersion;

    int PERMISSION_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);
        forgot_pass_btn = findViewById(R.id.forgot_pass_btn);

        roll_no_input = findViewById(R.id.roll_no_input);
        password_input = findViewById(R.id.password_input);
        login_feed = findViewById(R.id.login_feed);

        //checking if already loggedIn or not
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String user_id_cookie = sharedPreferences.getString("user_id", "DNE");

        if (user_id_cookie.equals("DNE")) {
            //login_feed.setText("No one is logged in");
        }
        else //if someone is already logged in
        {
            //redirecting the list course page
            Intent ListCourseIntent = new Intent(MainActivity.this, Dashboard.class);
            startActivity(ListCourseIntent);
            finish(); //used to delete the last activity history which we want to delete
        }

        //to get unique identification of a phone and displaying it
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); // android id

        // Permission is not granted of READ_PHONE_STATE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            //asking for grating the permission
            login_btn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_CODE);
                }
            });
        }
        // Permission is granted
        else
        {
            androidVersion = android.os.Build.VERSION.SDK_INT;

            if (androidVersion < 28) //less than 9 (SDK: 29)
            {
                uniqueID = android.os.Build.SERIAL; // Serial_no
            } else {
                uniqueID = Build.getSerial();
            }

        //on clicking on login button
            login_btn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String roll_no = roll_no_input.getText().toString().toUpperCase();
                    String password = password_input.getText().toString();
                    String type = "verify_login";

                    //checking if phone if connected to net or not
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                    {
                        //trying to login the user
                        try
                        {
                            String login_result = new DatabaseActions().execute(type, roll_no, password, androidId, uniqueID).get();

                            if(login_result.equals("-1"))
                            {
                                login_feed.setText("Database issue found");
                            }
                            else if(login_result.equals("Something went wrong"))
                            {
                                login_feed.setText(login_result);
                            }
                            else if(Integer.parseInt(login_result)> 0)
                            {
                                //creating cookie of the logged in user
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("roll_no", new Encryption().encrypt(roll_no));
                                editor.putString("user_id", new Encryption().encrypt(login_result));
                                editor.apply();

                                //redirecting the list course page
                                Intent ListCourseIntent = new Intent(MainActivity.this, Dashboard.class);
                                startActivity(ListCourseIntent);
                                finish(); //used to delete the last activity history which we don't want to delete
                            }
                            else
                            {
                                //login_feed.setText(login_result);

                                login_feed.setText("Your login credentials may be incorrect or this may be not your registered phone.");
                            }

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        login_feed.setText("Internet connection is not available");
                    }
                }
            });
        }

    //on clicking on register button
        register_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent RegisterIntent = new Intent(MainActivity.this, Register.class);
                startActivity(RegisterIntent);
                finish(); //used to delete the last activity history which we want to delete
            }
        });

    //on clicking on forgot password button
        forgot_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ForgotPasswordIntent = new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(ForgotPasswordIntent);
            }
        });
    }

//function to ask for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if(PERMISSION_CODE == 1)//READ_PHONE_STATE
        {
            //restarting app
            finish();
            startActivity(getIntent());

            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission Granted! Please Restart the App.", Toast.LENGTH_SHORT);
            }
            else
            {
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT);
            }
        }
    }
}
