package com.swchen1217.ntuh_yl_rt_mdms;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MenuActivity extends AppCompatActivity {
    Button btn_UpdateStatus,btn_InquireStatus,btn_Log,btn_Repair,btn_MaintenanceCheck,btn_Manage;
    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btn_UpdateStatus=findViewById(R.id.btn_menu_1);
        btn_InquireStatus=findViewById(R.id.btn_menu_2);
        btn_Log=findViewById(R.id.btn_menu_3);
        btn_Repair=findViewById(R.id.btn_menu_4);
        btn_MaintenanceCheck=findViewById(R.id.btn_menu_5);
        btn_Manage=findViewById(R.id.btn_menu_6);

        setListener();
    }

    public void setListener(){
        btn_UpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this,UpdateStatusActivity.class));
            }
        });
        btn_InquireStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        btn_Log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_Repair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_MaintenanceCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_Manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if (System.currentTimeMillis() - exitTime > 3000) {
                Toast.makeText(getApplicationContext(), "再按一次返回鍵退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                //new SQLite(MenuActivity.this).remove("device_tb",null);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            case R.id.updata_device_tb:
                try {
                    new SyncDB(MenuActivity.this).SyncDeviceTable();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return false;
    }
}
