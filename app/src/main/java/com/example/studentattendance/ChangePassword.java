package com.example.studentattendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class ChangePassword extends AppCompatActivity {

    EditText new_pass_input;
    EditText confirm_pass_input;
    TextView change_pass_feed;
    Button change_btn;

    String type;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        new_pass_input = findViewById(R.id.new_pass_input);
        confirm_pass_input = findViewById(R.id.confirm_pass_input);
        change_pass_feed = findViewById(R.id.change_pass_feed);
        change_btn = findViewById(R.id.change_btn);

    //getting the cookie of the forgot password student id
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final String forgot_password_stud_id = sharedPreferences.getString("forgot_password_stud_id", "DNE");

        if (forgot_password_stud_id.equals("DNE")) {
            finish();
        }

    //on clicking on change button
        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String new_pass = new_pass_input.getText().toString();
                String confirm_pass = confirm_pass_input.getText().toString();

                if(new_pass.length() != 0 && confirm_pass.length() != 0 && new_pass.equals(confirm_pass))
                {
                    //checking if phone if connected to net or not
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                    {
                    //changing password of that student
                        try
                        {
                            type = "change_stud_password";

                            String change_stud_passwordResult = new DatabaseActions().execute(type, forgot_password_stud_id, new_pass).get();

                            if(change_stud_passwordResult.equals("Something went wrong"))
                            {
                                change_pass_feed.setText(change_stud_passwordResult);
                            }
                            else if(change_stud_passwordResult.equals("-1"))
                            {
                                change_pass_feed.setText("Database issue found");
                            }
                            else if(change_stud_passwordResult.equals("0"))
                            {
                                change_pass_feed.setText("Something went wrong while updating password");
                            }
                            else if(change_stud_passwordResult.equals("1"))
                            {
                                Toast.makeText(ChangePassword.this, "Password Successfully changed", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else
                            {
                                change_pass_feed.setText("Unknown Error");
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        change_pass_feed.setText("Internet connection is not available");
                    }
                }
                else
                {
                    change_pass_feed.setText("Password do not match");
                }
            }
        });
    }
}
