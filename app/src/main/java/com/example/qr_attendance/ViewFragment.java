package com.example.qr_attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.concurrent.ExecutionException;

public class ViewFragment extends Fragment
{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    TextView text;

    String data[];
    ListView courseListView;
    ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, null);

        text = view.findViewById(R.id.text);
        courseListView = view.findViewById(R.id.courseListView);

    //getting the info of the logged user
        sharedPreferences = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String user_id_cookie = sharedPreferences.getString("user_id", "DNE");
        editor = sharedPreferences.edit();

        final String user_id = decrypt(user_id_cookie);

        //checking if phone if connected to net or not
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) //phone is connected
        {
            //getting the course list of the user from database
            String type = "get_user_courses";
            try
            {
                String get_user_courses_result = (new courseData().execute(type, user_id).get());

                //parse JSON data
                JSONArray ja = new JSONArray(get_user_courses_result);
                JSONObject jo = null;

                data = new String[ja.length()];

                String temp_courses = "";
                for (int i =0; i<ja.length(); i++)
                {
                    jo = ja.getJSONObject(i);

                    String course_code = jo.getString("course_code");
                    String course_id = jo.getString("id");

                    String temp = course_code + " # " + course_id;
                    temp_courses += (temp + ",");

                    data[i] = temp;
                }

                //listing courses in listview
                adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, data);
                courseListView.setAdapter(adapter);

                //creating cookie of the registered courses of that student
                editor.putString("studentCourses", temp_courses);
                editor.apply();

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else //phone is not connected to internet
        {
            String studentCourses_cookie = sharedPreferences.getString("studentCourses", null);

            String old_saved_courses[] = studentCourses_cookie.split(",");

            //listing courses in listview
            adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, old_saved_courses);
            courseListView.setAdapter(adapter);
        }

        //on clicking on any list item
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
            //creating cookie for course_id
                String listViewText = ((TextView)view).getText().toString();
                String temp[] = listViewText.split(" # ");

                String course_id_cookie = temp[ temp.length - 1];

                editor.putString("course_id", course_id_cookie);
                editor.apply();

                //redirecting to the qr code generator page
                Intent ViewAttendanceIntent = new Intent(getActivity().getApplicationContext(), ViewAttendance.class);
                startActivity(ViewAttendanceIntent);
            }
        });

        return view;
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
