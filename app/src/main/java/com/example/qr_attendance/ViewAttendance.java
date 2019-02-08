package com.example.qr_attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ViewAttendance extends AppCompatActivity
{

    SharedPreferences sharedPreferences;
    TextView qr_text;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        qr_text = findViewById(R.id.qr_text);

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

        String encrypted_data = (mJSONArray.toString());

        //checking if phone if connected to net or not
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            //we are connected to a network
            String type= "get_student_attendance";
            String get_user_courses_result = null;

            try {
                get_user_courses_result = (new getAttendanceData().execute(type, user_id_cookie, course_id_cookie).get());

                //parse JSON data
                JSONArray ja = new JSONArray(get_user_courses_result);
                JSONObject jo = ja.getJSONObject(0);

                String course_from_string = jo.getString("course_from");
                String course_to_string = jo.getString("course_to");

                int present_days_count = ja.length();

                Date course_from_date = new SimpleDateFormat("yyyy-MM-dd").parse(course_from_string);
                Date course_to_date = new SimpleDateFormat("yyyy-MM-dd").parse(course_to_string);

                Date_class date_class = new Date_class();
                int no_of_days = date_class.getWorkingDaysBetweenTwoDates(course_from_date, course_to_date);

                //qr_text.setText("From: " + course_from + "\nTo: " + course_to + "\nDays count:" + Integer.toString(present_days_count));
                qr_text.setText(Integer.toString(no_of_days));

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else
        {
            qr_text.setText("Internet connection does not exist");
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

class getAttendanceData extends AsyncTask<String,Void,String>
{
    String base_url = "http://mngo.in/qr_attendance/";

    @Override
    protected String doInBackground(String... params)
    {
        String type = params[0];
        String result = "Something went wrong";
        URL url;

        if(type.equals("get_student_attendance"))
        {
            String login_url = base_url + "get_student_attendance.php";
            try
            {
                String user_id = params[1];
                String course_id = params[2];

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

                String post_data = URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8") + "&"
                        + URLEncoder.encode("course_id", "UTF-8") + "=" + URLEncoder.encode(course_id, "UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                //getting the data coming from server after logging
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

                result="";
                String line;

                while((line = bufferedReader.readLine())!= null)
                {
                    result += (line + "\n");
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

class Date_class
{
    public static int getWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        int workDays = 0;

        //Return 0 if start and end are the same
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            return 0;
        }

        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }

        do {
            //excluding start date
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                ++workDays;
            }
        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

        return workDays;
    }
}
