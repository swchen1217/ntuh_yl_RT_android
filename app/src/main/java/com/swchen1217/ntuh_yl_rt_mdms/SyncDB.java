package com.swchen1217.ntuh_yl_rt_mdms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

//public class SyncDB extends AppCompatActivity {
public class SyncDB {
    ProgressDialog pd;
    String server_url="";
    UpdateStatusActivity context;

    SyncDB(UpdateStatusActivity _context) {
        context = _context;
    }

    public void SyncDeviceTable(){

    }

    public String PostDataToSrever(String file, FormBody formBody) throws IOException {
        context.runOnUiThread(new Runnable() {
            public void run() {
                //Code goes here
                context.pd=new ProgressDialog(context);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("與伺服器連線中...");
                pd.setCancelable(false);
            }
        });
        context.runOnUiThread(new Runnable() {
            public void run() {
                //Code goes here
                pd.show();
            }
        });
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient()
                .newBuilder().addInterceptor(logging)
                .connectTimeout(5, TimeUnit.SECONDS)
                .dns(new OkHttpDns2(10000))
                .build();
        Request request = new Request.Builder()
                .url("http://"+server_url+file)
                .post(formBody) // 使用post連線
                .build();
        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            return response.body().string();
        }catch(Exception e){
            Log.d("OkHttp","Error:"+e.toString());
            if (e instanceof UnknownHostException) {
                OkHttpClient client2 = new OkHttpClient()
                        .newBuilder().addInterceptor(logging)
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .dns(new OkHttpDns2(10000))
                        .build();
                Request request2 = new Request.Builder()
                        .url("https://www.google.com/") //Google
                        .build();
                Call call2 = client2.newCall(request2);
                try (Response response2 = call2.execute()){
                    context.runOnUiThread(new Runnable() {
                        public void run() {
                            //Code goes here
                            context.Toast.makeText(context, "無法連接至伺服器", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch(Exception e2){
                    context.runOnUiThread(new Runnable() {
                        public void run() {
                            //Code goes here
                            context.Toast.makeText(context, "無法連接至網際網路", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            if (e instanceof SocketTimeoutException) {
                //判断超时异常
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        //Code goes here
                        context.Toast.makeText(context, "無法連接至伺服器", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
        finally {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    //Code goes here
                    pd.dismiss();
                }
            });
        }
    }
}
