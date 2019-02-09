package com.example.qr_attendance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
    SharedPreferences sharedPreferences;

    TextView text;
    String type;

    String data[];
    String data1[];

    Spinner courseListSpinnner;
    ListView deleteCourseLV;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_course);

        text = findViewById(R.id.text);
        courseListSpinnner = findViewById(R.id.courseListSpinnner);
        deleteCourseLV = findViewById(R.id.deleteCourseLV);

    //getting the info of the logged user
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final String user_id_cookie = decrypt(sharedPreferences.getString("user_id", "DNE"));

    //checking if phone if connected to net or not
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
        //we are connected to a network
            try
            {
            //getting the list of all courses in the database
                type = "get_courses";
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

                //showing the list of student courses present in the db (to show in the drop down menu for adding new courses)
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

                            String course_id = (temp[ temp.length - 1]).trim();

                        //inserting that course_id in the student_course table in the database
                            type = "insert_student_course_in_db";

                            if(course_id !="" && course_id != "0")
                            {
                                String insert_student_courseResult = null;
                                try {
                                    insert_student_courseResult = new ManageCoursesData().execute(type, user_id_cookie, course_id).get();
                                    if(insert_student_courseResult.equals("1")) //if that course_id is successfully added
                                    {
                                    //reloading this activity
                                        finish();
                                        startActivity(getIntent());
                                    }
                                    else
                                    {
                                        text.setText("Something went wrong while adding that course");
                                    }
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                text.setText("");
                            }
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

            //getting the list of all the courses of that student (to show in the list for deleting any course)
                type = "get_user_courses";
                String get_user_courses_result = (new courseData().execute(type, user_id_cookie).get());

                if(!get_user_courses_result.equals("0") && !get_user_courses_result.equals("-1") && !get_user_courses_result.equals("Something went wrong"))
                {
                    //parse JSON data
                    JSONArray ja = new JSONArray(get_user_courses_result);
                    JSONObject jo = null;

                    data1 = new String[ja.length()];

                    for (int i =0; i<ja.length(); i++)
                    {
                        jo = ja.getJSONObject(i);

                        String course_code = jo.getString("course_code");
                        String course_id = jo.getString("id");

                        String temp = course_code + " # " + course_id;

                        data1[i] = temp;
                    }

                //listing courses in listview
                    adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data1);
                    deleteCourseLV.setAdapter(adapter1);
                }

            //on clicking on any list item under delete course
                deleteCourseLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                    //creating cookie for course_id
                        String listViewText = ((TextView)view).getText().toString();
                        String temp[] = listViewText.split(" # ");

                        final String course_id_to_delete = temp[ temp.length - 1];

                    //asking for confirm deletion by creating a dialog box
                        new AlertDialog.Builder(ManageCourse.this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure to delete this course from your registered courses")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                //delete the course_id in the student_courses table
                                try
                                {
                                    type = "delete_course_id_from_student_courses";
                                    String delete_course_id_from_student_coursesResult = (new ManageCoursesData().execute(type, course_id_to_delete).get());

                                    if(delete_course_id_from_student_coursesResult.equals("1"))
                                    {
                                        //reloading this activity
                                        finish();
                                        startActivity(getIntent());
                                    }
                                    else
                                    {
                                        text.setText("Something went wrong while deleting course from student course");
                                    }
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
                    }
                });
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e)
            {
                text.setText("Something went wrong while listing the student courses form database");
                e.printStackTrace();
            }
        }
        else
        {
            text.setText("Internet connection is not available");
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
        else if(type.equals("insert_student_course_in_db"))
        {
            String login_url = base_url + "insert_student_course_in_db.php";
            try
            {
                String user_id_cookie = params[1];
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

                String post_data = URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id_cookie,"UTF-8") + "&"
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
        else if(type.equals("delete_course_id_from_student_courses"))
        {
            String login_url = base_url + "delete_course_id_from_student_courses.php";
            try
            {
                String course_id_to_delete = params[1];

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

                String post_data = URLEncoder.encode("course_id","UTF-8")+"="+URLEncoder.encode(course_id_to_delete,"UTF-8");

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
