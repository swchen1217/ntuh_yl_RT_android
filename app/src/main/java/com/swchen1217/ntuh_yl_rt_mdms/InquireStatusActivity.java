package com.swchen1217.ntuh_yl_rt_mdms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bin.david.form.core.SmartTable;

public class InquireStatusActivity extends AppCompatActivity {

    SmartTable table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_status);
        table=findViewById(R.id.table);
    }
}
