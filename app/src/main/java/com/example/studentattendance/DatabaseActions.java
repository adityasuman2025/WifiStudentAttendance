package com.example.studentattendance;

import android.os.AsyncTask;

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

public class DatabaseActions extends AsyncTask<String,Void,String>
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
        else if(type.equals("check_phone_registered"))
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
        else if(type.equals("get_user_courses"))
        {
            String login_url = base_url + "get_user_courses.php";
            try
            {
                String user_id = params[1];

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

                String post_data = URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8");

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
        else if(type.equals("get_courses"))
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
                String student_id = params[2];

                //connecting with server
                url = new URL(login_url);
                HttpURLConnection httpURLConnection = null;
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                //sending info to the server
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("course_id","UTF-8")+"="+URLEncoder.encode(course_id_to_delete,"UTF-8") + "&"
                        + URLEncoder.encode("student_id","UTF-8")+"="+URLEncoder.encode(student_id,"UTF-8") ;

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                //getting the data coming from server
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
        else if(type.equals("get_student_details"))
        {
            String login_url = base_url + "get_student_details.php";
            try
            {
                String user_id = params[1];

                //connecting with server
                url = new URL(login_url);
                HttpURLConnection httpURLConnection = null;
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                //sending data to the server
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

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
        else if(type.equals("get_student_attendance"))
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
        else if(type.equals("check_stud_roll_and_serial_exist_in_db"))
        {
            String login_url = base_url + "check_stud_roll_and_serial_exist_in_db.php";

            try
            {
                String roll_no = params[1];
                String serial_number = params[2];

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

                String post_data = URLEncoder.encode("roll_no","UTF-8")+"="+URLEncoder.encode(roll_no,"UTF-8") + "&"
                        + URLEncoder.encode("serial_number", "UTF-8") + "=" + URLEncoder.encode(serial_number, "UTF-8");

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
        else if(type.equals("change_stud_password"))
        {
            String login_url = base_url + "change_stud_password.php";

            try
            {
                String stud_id = params[1];
                String new_password = params[2];

                //connecting with servverify_loginer
                url = new URL(login_url);
                HttpURLConnection httpURLConnection = null;
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                //sending login info to the server
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("stud_id","UTF-8")+"="+URLEncoder.encode(stud_id,"UTF-8") + "&"
                        + URLEncoder.encode("new_password", "UTF-8") + "=" + URLEncoder.encode(new_password, "UTF-8");

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
        //else if

        return result;
    }
}
