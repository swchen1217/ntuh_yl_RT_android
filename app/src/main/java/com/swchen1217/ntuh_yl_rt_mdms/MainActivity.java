package com.swchen1217.ntuh_yl_rt_mdms;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    String output="no change";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=findViewById(R.id.tv);
        connection_server("index","in=in&in2=intwo");
        tv.setText(output);
    }
    /*Handler mHandler = new Handler() {
        @Override    public void handleMessage(Message msg) {
            switch(msg.what)
            {
                case 1:

            }
            super.handleMessage(msg);
        }
    };*/
    void connection_server(final String urlfile, final String poststr){
        new Thread()
        {
            private String output;

            public void run()
            {
                String urlString = "http://swchen1217.ddns.net/ntuh_yl_RT_mdms_php/"+urlfile+".php";
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(1500);
                    connection.setConnectTimeout(1500);
                    connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
                    connection.setInstanceFollowRedirects(true);
                    DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                    dos.writeBytes(poststr);
                    if( connection.getResponseCode() == HttpsURLConnection.HTTP_OK ){
                        try(BufferedReader br = new BufferedReader(
                                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                            final StringBuilder response = new StringBuilder();
                            String responseLine = null;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            /*Message message;
                            message = mHandler.obtainMessage(1,response.toString());
                            mHandler.sendMessage(message);*/
                            //update(response.toString());
                            output=response.toString();
                            /*runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // update TextView here!
                                    tv.setText(response.toString());

                                }
                            });*/
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if( connection != null ) {
                        connection.disconnect();
                    }
                }

            }
        }.start();
    }
    /*void update(String in){
        output=in;
    }*/
}
