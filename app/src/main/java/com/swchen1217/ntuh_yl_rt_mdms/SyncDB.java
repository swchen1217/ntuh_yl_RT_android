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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    ProgressDialog pd,pd2;
    String server_url="";
    Activity activity;
    SharedPreferences spf_SyncDB;

    SyncDB(Activity _activity) {
        activity = _activity;
        Create();
    }

    public void Create(){
        spf_SyncDB=activity.getSharedPreferences("SyncDB",Context.MODE_PRIVATE);
    }

    public void SyncDeviceTable() throws IOException {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            //Code goes here
                            pd2=new ProgressDialog(activity);
                            pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pd2.setMessage("資料同步中...");
                            pd2.setCancelable(false);

                            pd2.show();

                        }
                    });
                    String LastModified;
                    LastModified=spf_SyncDB.getString("device_tb_LastModified","first");
                    String data = PostDataToSrever("db.php",
                            new FormBody.Builder()
                                    .add("mode", "sync_device_tb")
                                    .add("LastModified", LastModified.equals("first")?"2019-01-01 00:00:00":LastModified)
                                    .build());

                    Log.d("data_",data);
                    if(!data.equals("no_data")){
                        JSONArray json= new JSONArray(data);
                        Log.d("data_",json.length()+"");
                        SQLite in=new SQLite(activity);
                        for(int i=0;i<json.length();i++){
                            Object jsonOb=json.get(i);
                            Log.d("data_",jsonOb.toString());

                        }
                    }else{

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            //Code goes here
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    //Code goes here
                                    new AlertDialog.Builder(activity)
                                            .setTitle("同步完成!!")
                                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    pd2.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            });
                            Date now = new Date();
                            SimpleDateFormat sdf=new SimpleDateFormat();
                            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                            spf_SyncDB.edit()
                                    //.putString("device_tb_LastModified",sdf.format(now))
                                    .putString("device_tb_LastModified","2019-01-01 00:00:00")
                                    .commit();
                        }
                    });
                }
            }
        });
        thread.start();
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
