package com.swchen1217.ntuh_yl_rt_mdms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class SyncDB {
    ProgressDialog pd;
    String server_url="";
    Activity activity;
    SharedPreferences spf_SyncDB;

    SyncDB(UpdateStatusActivity updateStatusActivity) {
        activity = updateStatusActivity;
        Create();
    }

    SyncDB(MenuActivity menuActivity) {
        activity = menuActivity;
        Create();
    }

    public void Create(){
        spf_SyncDB=activity.getSharedPreferences("SyncDB",Context.MODE_PRIVATE);
    }

    public void SyncDeviceTable(){
        spf_SyncDB.getString("device_tb_LastModified","");

        Date now = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        spf_SyncDB.edit()
                .putString("device_tb_LastModified",sdf.format(now))
                .commit();
    }

    public String PostDataToSrever(String file, FormBody formBody) throws IOException {
        server_url=PrefsActivity.getServer(activity)+"/ntuh_yl_RT_mdms_php/";
        activity.runOnUiThread(new Runnable() {
            public void run() {
                //Code goes here
                pd=new ProgressDialog(activity);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("與伺服器連線中...");
                pd.setCancelable(false);
            }
        });
        activity.runOnUiThread(new Runnable() {
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
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            //Code goes here
                            Toast.makeText(activity, "無法連接至伺服器", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch(Exception e2){
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            //Code goes here
                            Toast.makeText(activity, "無法連接至網際網路", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            if (e instanceof SocketTimeoutException) {
                //判断超时异常
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        //Code goes here
                        Toast.makeText(activity, "無法連接至伺服器", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
        finally {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    //Code goes here
                    pd.dismiss();
                }
            });
        }
    }
}
