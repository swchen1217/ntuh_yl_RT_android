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
    ProgressDialog pd, pd2, pd3;
    String server_url = "";
    Activity activity;
    SharedPreferences spf_SyncDB, spf_LoginInfo;
    String key[] = {"DID", "category", "model", "number", "user", "position", "status", "LastModified"};

    SyncDB(Activity _activity) {
        activity = _activity;
        spf_SyncDB = activity.getSharedPreferences("SyncDB", Context.MODE_PRIVATE);
        spf_LoginInfo = activity.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
    }

    public boolean UpdateDeviceTableStoreroom(String DID, String position) {
        try {
            String update = PostDataToSrever("db.php",
                    new FormBody.Builder()
                            .add("mode", "update_device_tb_storeroom")
                            .add("acc", spf_LoginInfo.getString("acc", ""))
                            .add("pw", spf_LoginInfo.getString("pw", ""))
                            .add("DID", DID)
                            .add("position", position)
                            .build());
            if (update != null) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean UpdateDeviceTableUse(String DID, String user, String position) {
        try {
            String update = PostDataToSrever("db.php",
                    new FormBody.Builder()
                            .add("mode", "update_device_tb_use")
                            .add("acc", spf_LoginInfo.getString("acc", ""))
                            .add("pw", spf_LoginInfo.getString("pw", ""))
                            .add("DID", DID)
                            .add("user", user)
                            .add("position", position)
                            .build());
            if (update != null) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void SyncPositionItemTable() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            //Code goes here
                            pd3 = new ProgressDialog(activity);
                            pd3.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pd3.setMessage("位置資料同步中...");
                            pd3.setCancelable(false);

                            pd3.show();
                        }
                    });

                    String LastSync = spf_SyncDB.getString("position_item_tb_LastSync", "first");
                    Log.d("date_test", "LastSync:" + LastSync);
                    String LastModified = PostDataToSrever("db.php",
                            new FormBody.Builder()
                                    .add("mode", "GetSystem_tb")
                                    .add("id", "position_item_tb_LastModified")
                                    .add("acc", spf_LoginInfo.getString("acc", ""))
                                    .add("pw", spf_LoginInfo.getString("pw", ""))
                                    .build());
                    if (LastModified != null) {
                        if (!LastModified.equals("user_error")) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date a = sdf.parse(LastModified);
                            Date b = sdf.parse(LastSync.equals("first") ? "2019-01-01 00:00:00" : LastSync);
                            Log.d("date_test", a.toString());
                            Log.d("date_test", b.toString());
                            if (a.after(b)) {
                                Log.d("date_test", "需要同步");
                                SQLite sql = new SQLite(activity);
                                sql.deletetb("position_item_tb");
                                String data = PostDataToSrever("db.php",
                                        new FormBody.Builder()
                                                .add("mode", "sync_position_item_tb_download")
                                                .add("acc", spf_LoginInfo.getString("acc", ""))
                                                .add("pw", spf_LoginInfo.getString("pw", ""))
                                                .build());
                                if (data != null) {
                                    if (!data.equals("user_error")) {
                                        Log.d("data_", "data:" + data);
                                        JSONArray jsonA = new JSONArray(data);
                                        Log.d("data_", "jsonA.length():" + jsonA.length() + "");
                                        for (int i = 0; i < jsonA.length(); i++) {
                                            JSONObject jsonO = jsonA.getJSONObject(i);
                                            Log.d("data_", "jsonO.toString():" + jsonO.toString());
                                            ContentValues cv = new ContentValues();
                                            cv.put("type", jsonO.getString("type"));
                                            cv.put("item", jsonO.getString("item"));
                                            sql.inster("position_item_tb", cv);
                                        }
                                        Cursor c2 = sql.select("position_item_tb", null, null, null, null, null);
                                        int rows_num = c2.getCount();
                                        if (rows_num != 0) {
                                            c2.moveToFirst();           //將指標移至第一筆資料
                                            for (int j = 0; j < rows_num; j++) {
                                                String str = "";
                                                for (int k = 0; k < 2; k++) {
                                                    str += c2.getString(k) + ",";
                                                }
                                                Log.d("data_", str);
                                                c2.moveToNext();        //將指標移至下一筆資料
                                            }
                                        }
                                    }
                                }
                            }
                            Date now = new Date();
                            SimpleDateFormat sdf2 = new SimpleDateFormat();
                            sdf2.applyPattern("yyyy-MM-dd HH:mm:ss");
                            spf_SyncDB.edit()
                                    .putString("position_item_tb_LastSync", sdf2.format(now))
                                    //.putString("device_tb_LastSync","2019-01-01 00:00:00")
                                    .commit();
                        }
                    }
                } catch (Exception e) {

                } finally {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd3.dismiss();
                        }
                    });
                }
            }
        });
        thread.start();
    }

    public boolean SyncDeviceTable(boolean AlertDialog) throws IOException {
        try {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    //Code goes here
                    pd2 = new ProgressDialog(activity);
                    pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd2.setMessage("設備資料同步中...");
                    pd2.setCancelable(false);

                    pd2.show();

                }
            });
            String LastSync;
            LastSync = spf_SyncDB.getString("device_tb_LastSync", "first");
            String data = PostDataToSrever("db.php",
                    new FormBody.Builder()
                            .add("mode", "sync_device_tb_download")
                            .add("LastModified", LastSync.equals("first") ? "2019-01-01 00:00:00" : LastSync)
                            .add("acc", spf_LoginInfo.getString("acc", ""))
                            .add("pw", spf_LoginInfo.getString("pw", ""))
                            .build());
            if (data != null) {
                if (!data.equals("user_error")) {
                    if (!data.equals("no_data")) {
                        JSONArray jsonA = new JSONArray(data);
                        Log.d("data_", jsonA.length() + "");
                        SQLite sql = new SQLite(activity);
                        /*ContentValues cv=new ContentValues();
                        cv.put("DID","MDMS.D0003");
                        sql.inster("device_tb", cv);*/
                        //sql.remove("device_tb",null);
                        for (int i = 0; i < jsonA.length(); i++) {
                            JSONObject jsonO = jsonA.getJSONObject(i);
                            //Object jsonOb=jsonA.get(i);
                            Log.d("data_", jsonO.toString());
                            Log.d("data_", jsonO.getString("DID"));

                            Cursor c = sql.select("device_tb", null, "DID='" + jsonO.getString("DID") + "'", null, null, null);
                            if (c.getCount() == 0) {
                                Log.d("data_", "0");
                                if (!jsonO.getString("status").equals("-1"))
                                    sql.inster("device_tb", JsonToContentValues(jsonO));
                            } else {
                                if (!jsonO.getString("status").equals("-1")) {
                                    Log.d("data_", "1");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    c.moveToFirst();
                                    Date a = sdf.parse(jsonO.getString("LastModified"));
                                    Date b = sdf.parse(c.getString(7));
                                    Log.d("data_", a.toString());
                                    Log.d("data_", b.toString());
                                    if (a.after(b)) {
                                        sql.update("device_tb", JsonToContentValues(jsonO), "DID='" + jsonO.getString("DID") + "'");
                                    }
                                } else {
                                    sql.delete("device_tb", "DID='" + jsonO.getString("DID") + "'");
                                }
                            }
                            Cursor c2 = sql.select("device_tb", null, null, null, null, null);
                            int rows_num = c2.getCount();
                            if (rows_num != 0) {
                                c2.moveToFirst();           //將指標移至第一筆資料
                                for (int j = 0; j < rows_num; j++) {
                                    String str = "";
                                    for (int k = 0; k < 8; k++) {
                                        str += c2.getString(k) + ",";
                                    }
                                    Log.d("data_", str);
                                    c2.moveToNext();        //將指標移至下一筆資料
                                }
                            }
                        }
                    }
                    if (AlertDialog) {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                //Code goes here
                                new AlertDialog.Builder(activity)
                                        .setTitle("設備資料同步完成!!")
                                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                            }
                        });
                    }
                    Date now = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                    spf_SyncDB.edit()
                            .putString("device_tb_LastSync", sdf.format(now))
                            //.putString("device_tb_LastSync","2019-01-01 00:00:00")
                            .commit();
                    return true;
                }
                return false;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        } finally {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd2.dismiss();
                }
            });
        }
    }

    public String PostDataToSrever(String file, FormBody formBody) throws IOException {
        server_url = PrefsActivity.getServer(activity) + "/ntuh_yl_RT_mdms_api/";
        activity.runOnUiThread(new Runnable() {
            public void run() {
                //Code goes here
                pd = new ProgressDialog(activity);
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
                .url("http://" + server_url + file)
                .post(formBody) // 使用post連線
                .build();
        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            if(response.code()==200){
                return response.body().string();
            }else{
                Toast.makeText(activity, "伺服器錯誤,請聯繫管理員", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (Exception e) {
            Log.d("OkHttp", "Error:" + e.toString());
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
                try (Response response2 = call2.execute()) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            //Code goes here
                            Toast.makeText(activity, "無法連接至伺服器", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e2) {
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
        } finally {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    //Code goes here
                    pd.dismiss();
                }
            });
        }
    }

    public ContentValues JsonToContentValues(JSONObject jsonOin) {
        ContentValues cv = new ContentValues();
        try {
            cv.put("DID", jsonOin.getString("DID"));
            cv.put("category", jsonOin.getString("category"));
            cv.put("model", jsonOin.getString("model"));
            cv.put("number", jsonOin.getString("number"));
            cv.put("user", jsonOin.getString("user"));
            cv.put("position", jsonOin.getString("position"));
            cv.put("status", jsonOin.getString("status"));
            cv.put("LastModified", jsonOin.getString("LastModified"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cv;
    }

    public void test() {
        spf_SyncDB.edit()
                .putString("device_tb_LastSync", "2019-06-29 00:00:00")
                .commit();
    }
}
