package com.swchen1217.ntuh_yl_rt_mdms;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=findViewById(R.id.tv);
        new Thread()
        {
            public void run()
            {
                String urlString = "http://swchen1217.ddns.net/ntuh_yl_RT_mdms_php/index.php?in=test";
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(1500);
                    connection.setConnectTimeout(1500);
                    connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
                    connection.setInstanceFollowRedirects(true);
                    if( connection.getResponseCode() == HttpsURLConnection.HTTP_OK ){
                        try(BufferedReader br = new BufferedReader(
                                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                            final StringBuilder response = new StringBuilder();
                            String responseLine = null;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // update TextView here!
                                    tv.setText(response.toString());
                                }
                            });
                        }
                        /*InputStream inputStream= connection.getInputStream();
                        BufferedReader bufferedReader  = new BufferedReader( new InputStreamReader(inputStream) );
                        String tempStr;
                        StringBuffer stringBuffer = new StringBuffer();
                        while( ( tempStr = bufferedReader.readLine() ) != null ) {
                            stringBuffer.append( tempStr );
                        }
                        bufferedReader.close();
                        inputStream.close();
                        final String responseString = stringBuffer.toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                tv.setText(responseString);
                            }
                        });*/
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
}
