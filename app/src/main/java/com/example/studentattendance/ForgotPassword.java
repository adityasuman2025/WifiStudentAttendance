package com.example.studentattendance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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

public class ForgotPassword extends AppCompatActivity {

    EditText roll_input;
    EditText serial_input;
    TextView forgot_pass_feed;
    Button done_btn;

    String type;

    int androidVersion;
    String uniqueID;
    int PERMISSION_CODE = 1;

    SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        roll_input = findViewById(R.id.roll_input);
        serial_input = findViewById(R.id.serial_input);
        forgot_pass_feed = findViewById(R.id.forgot_pass_feed);
        done_btn = findViewById(R.id.done_btn);

        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

    // checking if permission to get serial number of phone is granted or not
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
        //permission is not granted so asking for grating the permission
            done_btn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    ActivityCompat.requestPermissions(ForgotPassword.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_CODE);
                }
            });
        }
        else // Permission is already granted
        {
            androidVersion = android.os.Build.VERSION.SDK_INT;

            if (androidVersion < 28) //less than 9 (SDK: 29)
            {
                uniqueID = android.os.Build.SERIAL; // Serial_no
            } else {
                uniqueID = Build.getSerial();
            }

        //on clicking on done button
            done_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String roll_no = roll_input.getText().toString().toUpperCase();
                    String serial_no = serial_input.getText().toString();

                    if(roll_no.length() != 0 && serial_no.length() != 0)
                    {
                        if(serial_no.equals(uniqueID)) //serial number of that phone is same as that of entered serial number
                        {
                            //checking if phone if connected to net or not
                            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                            {
                                try
                                {
                                    type = "check_stud_roll_and_serial_exist_in_db";
                                    String check_stud_roll_and_serial_exist_in_dbResult = new DatabaseActions().execute(type, roll_no, serial_no).get();

                                    if(check_stud_roll_and_serial_exist_in_dbResult.equals("-1"))
                                    {
                                        forgot_pass_feed.setText("Database issue found");
                                    }
                                    else if(check_stud_roll_and_serial_exist_in_dbResult.equals("Something went wrong"))
                                    {
                                        forgot_pass_feed.setText(check_stud_roll_and_serial_exist_in_dbResult);
                                    }
                                    else if(Integer.parseInt(check_stud_roll_and_serial_exist_in_dbResult)> 0)
                                    {
                                    //creating cookie of the forgot_password_stud_id
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("forgot_password_stud_id", check_stud_roll_and_serial_exist_in_dbResult);
                                        editor.apply();

                                    //redirecting to the change password page
                                        Intent ChangePasswordIntent = new Intent(ForgotPassword.this, ChangePassword.class);
                                        startActivity(ChangePasswordIntent);
                                        finish();
                                    }
                                    else
                                    {
                                        forgot_pass_feed.setText("Entered details do not match in database");
                                    }

                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                forgot_pass_feed.setText("Internet connection is not available");
                            }
                        }
                        else //serial number of this phone is not matching with the entered serial number
                        {
                            forgot_pass_feed.setText("Entered serial number is not matching with this phone serial");
                        }
                    }
                    else
                    {
                        forgot_pass_feed.setText("Please fill all the fields");
                    }
                }
            });
        }
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
