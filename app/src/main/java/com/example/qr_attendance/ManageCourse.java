package com.example.qr_attendance;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ManageCourse extends AppCompatActivity
{
//defining variables
    TextView text;

    String data[];

    Spinner courseListSpinnner;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_course);

        text = findViewById(R.id.text);
        courseListSpinnner = findViewById(R.id.courseListSpinnner);

    //checking if phone if connected to net or not
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
        //we are connected to a network
            try
            {
            //getting the list of all courses in the database
                String type = "get_courses";
                String get_courseResults = new ManageCoursesData().execute(type).get();

                if(get_courseResults != "0" && get_courseResults != "-1" && get_courseResults != "Something went wrong")
                {
                //parse JSON and getting data
                    JSONArray ja = new JSONArray(get_courseResults);
                    JSONObject jo = null;

                    data = new String[ja.length() + 1];
                    data[0] = "";

                    for (int i = 0; i < ja.length(); i++)
                    {
                        jo = ja.getJSONObject(i);

                        String course_id = jo.getString("id");
                        String course_code = jo.getString("course_code");

                        data[i+1] = course_code + " # " + course_id;
                    }

                    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, data);
                    courseListSpinnner.setAdapter(adapter);

                //on selecting any option in the drop down menu of the courses
                    courseListSpinnner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
                        {
                            String selected = parentView.getItemAtPosition(position).toString();
                            String temp[] = selected.split(" # ");

                            String course_id = temp[ temp.length - 1];

                            text.setText(course_id);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView)
                        {
                            text.setText("hello");
                        }
                    });
                }
                else
                {
                    text.setText("Something went wrong while getting courses list");
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            text.setText("Internet connection is not available");
        }
    }
}

class ManageCoursesData extends AsyncTask<String,Void,String>
{
    String base_url = "http://mngo.in/qr_attendance/";

    @Override
    protected String doInBackground(String... params)
    {
        String type = params[0];
        String result = "Something went wrong";
        URL url;

        if(type.equals("get_courses"))
        {
            String login_url = base_url + "get_courses.php";
            try
            {
                //connecting with server
                url = new URL(login_url);
                HttpURLConnection httpURLConnection = null;
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                //getting the data coming from server
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
