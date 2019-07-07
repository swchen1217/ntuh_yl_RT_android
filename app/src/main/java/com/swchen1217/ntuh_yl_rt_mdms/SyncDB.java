package com.swchen1217.ntuh_yl_rt_mdms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
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
    String key[]={"DID","category","model","number","user","position","status","LastModified"};

    SyncDB(Activity _activity) {
        activity = _activity;
        Create();
    }

    public void Create(){
        spf_SyncDB=activity.getSharedPreferences("SyncDB",Context.MODE_PRIVATE);
    }

    public void SyncPositionItemTable(){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /*activity.runOnUiThread(new Runnable() {
                        public void run() {
                            //Code goes here
                            pd2=new ProgressDialog(activity);
                            pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pd2.setMessage("資料同步中...");
                            pd2.setCancelable(false);

                            pd2.show();
                        }
                    });*/

                    String LastSync=spf_SyncDB.getString("position_item_tb_LastSync","first");
                    String data = PostDataToSrever("db.php",
                            new FormBody.Builder()
                                    .add("mode", "GetSystem_tb")
                                    .add("id", "position_item_tb_LastModified")
                                    .build());

                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date a=sdf.parse(data);
                    Date b=sdf.parse(LastSync.equals("first")?"2019-01-01 00:00:00":LastSync);
                    Log.d("date_test",a.toString());
                    Log.d("date_test",b.toString());
                    if(a.after(b))
                        Log.d("date_test","a.after(b)");
                    else
                        Log.d("date_test","a.before(b)");

                } catch (Exception e) {

                }
            }
        });
        thread.start();
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
                    String LastSync;
                    LastSync=spf_SyncDB.getString("device_tb_LastSync","first");
                    String data = PostDataToSrever("db.php",
                            new FormBody.Builder()
                                    .add("mode", "sync_device_tb_download")
                                    .add("LastModified", LastSync.equals("first")?"2019-01-01 00:00:00":LastSync)
                                    .build());

                    Log.d("data_",data);
                    if(!data.equals("no_data")){
                        JSONArray jsonA= new JSONArray(data);
                        Log.d("data_",jsonA.length()+"");
                        SQLite sql=new SQLite(activity);
                        /*ContentValues cv=new ContentValues();
                        cv.put("DID","MDMS.D0003");
                        sql.inster("device_tb", cv);*/
                        //sql.remove("device_tb",null);
                        for(int i=0;i<jsonA.length();i++){
                            JSONObject jsonO = jsonA.getJSONObject(i);
                            //Object jsonOb=jsonA.get(i);
                            Log.d("data_",jsonO.toString());
                            Log.d("data_",jsonO.getString("DID"));

                            Cursor c=sql.select("device_tb",null,"DID='"+jsonO.getString("DID")+"'",null,null,null);
                            if(c.getCount()==0){
                                Log.d("data_","0");
                                sql.inster("device_tb",JsonToContentValues(jsonO));
                            }else{
                                Log.d("data_","1");
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                c.moveToFirst();
                                Date a=sdf.parse(jsonO.getString("LastModified"));
                                Date b=sdf.parse(c.getString(7));
                                Log.d("data_",a.toString());
                                Log.d("data_",b.toString());
                                if(a.after(b)){
                                    sql.update("device_tb",JsonToContentValues(jsonO),"DID='"+jsonO.getString("DID")+"'");
                                }
                            }
                            Cursor c2=sql.select("device_tb",null,null,null,null,null);
                            int rows_num = c2.getCount();
                            if(rows_num != 0) {
                                c2.moveToFirst();           //將指標移至第一筆資料
                                for(int j=0; j<rows_num; j++) {
                                    String str = "";
                                    for(int k=0;k<8;k++){
                                        str+=c2.getString(k)+",";
                                    }
                                    Log.d("data_",str);
                                    c2.moveToNext();        //將指標移至下一筆資料
                                }
                            }
                        }
                    }

                    /*SQLite sql=new SQLite(activity);
                    Cursor c=sql.select("device_tb",null,"LastModified > '"+(LastSync.equals("first")?"2019-01-01 00:00:00":LastSync)+"'",null,null,null);
                    if(c.getCount()!=0){
                        c.moveToFirst();
                        JsonArray jsonArray=new JsonArray();
                        for(int i=0;i<c.getCount();i++){
                            JsonObject jsonObject=new JsonObject();
                            for(int j=0;j<8;j++){
                                jsonObject.addProperty(key[j],c.getString(j));
                            }
                            jsonArray.add(jsonObject);
                            c.moveToNext();
                        }
                        Log.d("data_",jsonArray.toString());
                        PostDataToSrever("db.php",
                                new FormBody.Builder()
                                        .add("mode", "sync_device_tb_upload")
                                        .add("josn_data", jsonArray.toString())
                                        .build());
                    }*/
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
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
                            .putString("device_tb_LastSync",sdf.format(now))
                            //.putString("device_tb_LastSync","2019-01-01 00:00:00")
                            .commit();
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

    public ContentValues JsonToContentValues(JSONObject jsonOin){
        ContentValues cv=new ContentValues();
        try {
            cv.put("DID",jsonOin.getString("DID"));
            cv.put("category",jsonOin.getString("category"));
            cv.put("model",jsonOin.getString("model"));
            cv.put("number",jsonOin.getString("number"));
            cv.put("user",jsonOin.getString("user"));
            cv.put("position",jsonOin.getString("position"));
            cv.put("status",jsonOin.getString("status"));
            cv.put("LastModified",jsonOin.getString("LastModified"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cv;
    }

    public void test(){
        spf_SyncDB.edit()
                .putString("device_tb_LastSync","2019-06-29 00:00:00")
                .commit();
    }
}
