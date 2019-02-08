package com.example.qr_attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.concurrent.ExecutionException;

public class ViewAttendance extends AppCompatActivity
{
    SharedPreferences sharedPreferences;

    TextView qr_text;
    TextView courseCode;
    TextView courseName;
    TextView courseDuration;
    TextView noOfClasses;
    TextView presentDays;
    TextView percentage;
    TextView warningText;

    String data[];
    ListView presentDateLV;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        qr_text = findViewById(R.id.qr_text);
        courseCode = findViewById(R.id.courseCode);
        courseName = findViewById(R.id.courseName);
        courseDuration = findViewById(R.id.courseDuration);
        noOfClasses = findViewById(R.id.noOfClasses);
        presentDays = findViewById(R.id.presentDays);
        percentage = findViewById(R.id.percentage);
        warningText = findViewById(R.id.warningText);

        presentDateLV = findViewById(R.id.presentDateLV);

        //to get the cookie values
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String user_id_cookie = decrypt(sharedPreferences.getString("user_id", "DNE"));
        String course_id_cookie = sharedPreferences.getString("course_id", "DNE");

        //checking if phone if connected to net or not
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            //we are connected to a network
            try
            {
            //to get attendance details for a student and course
                String type= "get_student_attendance";
                String get_user_courses_result = new getAttendanceData().execute(type, user_id_cookie, course_id_cookie).get();

                if(get_user_courses_result != "0" && get_user_courses_result != "-1" && get_user_courses_result != "Something went wrong")
                {
                    //parse JSON and getting data
                    JSONArray ja = new JSONArray(get_user_courses_result);
                    JSONObject jo = ja.getJSONObject(0);

                    int no_of_present_days = ja.length();

                    String course_code_string = jo.getString("course_code");
                    String course_name_string = jo.getString("course_name");
                    String course_from_string = jo.getString("course_from");
                    String course_to_string = jo.getString("course_to");

                //to get all the dates when that student was present
                    data = new String[ja.length()];

                    for (int i = 0; i<ja.length(); i++)
                    {
                        jo = ja.getJSONObject(i);

                        String temp = jo.getString("date");

                    //formatting date in our desired format
                        Date course_from_date = new SimpleDateFormat("yyyy-MM-dd").parse(temp);
                        DateFormat df = new SimpleDateFormat("dd MMM YYYY EEE");
                        String strDate = df.format(course_from_date);

                        data[i] = strDate;
                    }

                //listing the present dates in listview
                    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
                    presentDateLV.setAdapter(adapter);

                    //to get class dates for a course
                    type= "get_course_class_count";
                    String get_course_class_count_result = (new getAttendanceData().execute(type, course_id_cookie).get());

                    if(get_course_class_count_result != "0" && get_course_class_count_result != "-1" && get_course_class_count_result != "Something went wrong")
                    {
                        float no_of_classes = Float.parseFloat(get_course_class_count_result);
                        float present_percentage = (no_of_present_days/no_of_classes)*100;

                    //if attendance percentage is below 75% then giving warning to the student
                        if(present_percentage < 75.0)
                        {
                            warningText.setText("You have low attendance in this course");
                        }

                        noOfClasses.setText("No of classes till date: " + get_course_class_count_result);
                        percentage.setText("Attendance Percentage: " + Float.toString(present_percentage) + "%");
                    }
                    else
                    {
                        qr_text.setText("Something went wrong while getting number of classes of the course");
                    }

                    courseCode.setText(course_code_string);
                    courseName.setText(course_name_string);
                    courseDuration.setText("(From: " + course_from_string + ",  To: " + course_to_string + ")");
                    presentDays.setText("Present days: " + Integer.toString(no_of_present_days));
                }
                else
                {
                    qr_text.setText("Something went wrong while getting attendance data");
                }

            } catch (ExecutionException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                qr_text.setText("No Attendance data found for this course");
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        else
        {
            qr_text.setText("Internet connection is not available");
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
                    result += (line);
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
        else if(type.equals("get_course_class_count"))
        {
            String login_url = base_url + "get_course_class_count.php";

            try
            {
                String course_id = params[1];

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

                String post_data = URLEncoder.encode("course_id","UTF-8")+"="+URLEncoder.encode(course_id,"UTF-8");

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
                    result += (line);
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

//class to get count of no of days between two dates
class Date_class
{
//function to count no of days between two dates
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

        do
         {
            //excluding start date
             startCal.add(Calendar.DAY_OF_MONTH, 1);
             ++workDays;

//            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
//                ++workDays;
//            }
        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

        return workDays;
    }
}
