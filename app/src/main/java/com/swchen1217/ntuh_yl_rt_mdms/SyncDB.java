package com.swchen1217.ntuh_yl_rt_mdms;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
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

public class SyncDB extends Activity {

    public void SyncDeviceTable(){

    }

    /*public String PostDataToSrever(String data, FormBody formBody) throws IOException {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            if(getServerIP_check()){
                runOnUiThread(new Runnable() {
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
                        .url("http://"+server_url+data)
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
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    //Code goes here
                                    Toast.makeText(LoginActivity.this, "無法連接至伺服器", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch(Exception e2){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    //Code goes here
                                    Toast.makeText(LoginActivity.this, "無法連接至網際網路", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    if (e instanceof SocketTimeoutException) {
                        //判断超时异常
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //Code goes here
                                Toast.makeText(LoginActivity.this, "無法連接至伺服器", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    return null;
                }
                finally {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //Code goes here
                            pd.dismiss();
                        }
                    });
                }
            }
        }else{
            runOnUiThread(new Runnable() {
                public void run() {
                    //Code goes here
                    Toast.makeText(LoginActivity.this, "無網路連接", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return null;
    }

    public boolean getServerIP_check(){
        if(PrefsActivity.getServer(LoginActivity.this)!=""){
            server_url=PrefsActivity.getServer(LoginActivity.this)+"/ntuh_yl_RT_mdms_php/";
            return true;
        }else{
            runOnUiThread(new Runnable() {
                public void run() {
                    //Code goes here
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("未設定伺服器位址!!")
                            .setMessage("請聯繫管理員取得伺服器位址")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(LoginActivity.this, PrefsActivity.class));
                                }
                            })
                            .show();
                }
            });
            return false;
        }
    }*/
}
