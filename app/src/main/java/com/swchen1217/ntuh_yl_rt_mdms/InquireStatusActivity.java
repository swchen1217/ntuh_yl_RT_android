package com.swchen1217.ntuh_yl_rt_mdms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;

import java.util.ArrayList;
import java.util.List;

public class InquireStatusActivity extends AppCompatActivity {

    SmartTable table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_status);
        table=findViewById(R.id.table);
        test();
    }
    void test(){
        Column<String> city = new Column<>("設備ID", "DID");
        Column<String> category = new Column<>("分類", "category");
        Column<String> model = new Column<>("型號", "model");
        Column<String> number = new Column<>("編號", "number");
        Column<String> user = new Column<>("使用者", "user");
        Column<String> position = new Column<>("位置", "position");
        Column<String> status = new Column<>("狀態", "status");
        Column<String> LastModified = new Column<>("修改日期", "LastModified");
        List<String> data =new ArrayList<>();
        data.add;
    }
}
