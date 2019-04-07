package com.example.studentattendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class AttendanceQR extends AppCompatActivity
{
    TextView courseCode;

    ImageView wifImg;
    Button markBtn;
    TextView qr_text;

    String hotspotIP = "192.168.43.1";
    int port = 3399;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_qr);

        courseCode = findViewById(R.id.courseCode);

        wifImg = findViewById(R.id.wifImg);
        markBtn = findViewById(R.id.markBtn);
        qr_text = findViewById(R.id.qr_text);

    //to get the cookie values
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final String user_id_cookie = new Encryption().decrypt(sharedPreferences.getString("user_id", "DNE"));

        String course_code_cookie = sharedPreferences.getString("course_code", "DNE");
        final String course_id_cookie = sharedPreferences.getString("course_id", "DNE");

        courseCode.setText(course_code_cookie);

    //checking if wifi is on or not
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(!mWifi.isConnected()) //if wifi is not connected
        {
            qr_text.setText("Your mobile Wifi is OFF. Turn it ON and restart the App");
        }
        else//if wifi is connected
        {
            wifImg.setImageResource(R.drawable.wifi);
        }

    //on clicking on mark attendance button
        markBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            //checking if wifi is on or not
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if(!mWifi.isConnected()) //if wifi is not connected
                {
                    qr_text.setText("Your mobile Wifi is OFF. Turn it ON and restart the App");
                }
                else//if wifi is connected
                {
                //on clicking on mark button
                    markBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            //to get current timestamps
                            Long tsLong = System.currentTimeMillis()/1000;
                            String ts = tsLong.toString();

                            //storing attendance info into JSON and encrypting it
                            String qr_data[] = {user_id_cookie, course_id_cookie, ts}; //JSON format: userID, courseID, currentTimestamps
                            JSONArray mJSONArray = new JSONArray(Arrays.asList(qr_data));

                            String encrypted_data = new Encryption().encrypt(mJSONArray.toString());

                            if(user_id_cookie != null && course_id_cookie != null)
                            {
                                MyClientTask myClientTask = new MyClientTask(hotspotIP, port, encrypted_data);
                                myClientTask.execute();
                            }
                            else
                            {
                                qr_text.setText("Something went wrong");
                            }
                        }
                    });
                }
            }
       });
    }

//inner class for using socket in wifi
    public class MyClientTask extends AsyncTask<Void, Void, Void>
    {
        String dstAddress;
        int dstPort;
        String msgToServer;

        String response = "";

        MyClientTask(String addr, int port, String msgTo)
        {
            dstAddress = addr;
            dstPort = port;
            msgToServer = msgTo;
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try
            {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if(msgToServer != null){
                    dataOutputStream.writeUTF(msgToServer);
                }

                response = dataInputStream.readUTF();

            } catch (UnknownHostException e)
            {
                e.printStackTrace();
                response = "Failed to connect to the Server. You are connected to wrong WI-Fi or Host may not active at the moment";
            } catch (IOException e)
            {
                e.printStackTrace();
                response = "Failed to connect to the Server. You are connected to wrong WI-Fi or Host may not active at the moment";;
            } finally
            {
                if (socket != null)
                {
                    try
                    {
                        socket.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null)
                {
                    try
                    {
                        dataOutputStream.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            qr_text.setText(response);
            super.onPostExecute(result);
        }
    }

}
