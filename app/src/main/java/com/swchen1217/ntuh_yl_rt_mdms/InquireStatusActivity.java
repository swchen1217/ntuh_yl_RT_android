package com.swchen1217.ntuh_yl_rt_mdms;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.column.ColumnInfo;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.bg.IBackgroundFormat;
import com.bin.david.form.data.format.bg.ICellBackgroundFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.style.LineStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnClickListener;
import com.bin.david.form.listener.OnColumnItemClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InquireStatusActivity extends AppCompatActivity {

    SmartTable table;
    SwipeRefreshLayout mSwipeLayout;

    int ClickCount=0;
    int ClickPosition=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_status);
        table = findViewById(R.id.table);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setColorSchemeColors(Color.RED);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeLayout.setRefreshing(false);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (new SyncDB(InquireStatusActivity.this).SyncDeviceTable(false)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(InquireStatusActivity.this, "已重新整理", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ClickCount=0;
        ClickPosition=-1;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new SyncDB(InquireStatusActivity.this).SyncDeviceTable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        show();
    }

    void show() {
        Column<String> DID = new Column<>("設備ID", "DID");
        Column<String> category = new Column<>("分類", "category");
        Column<String> model = new Column<>("型號", "model");
        Column<String> number = new Column<>("編號", "number");
        Column<String> user = new Column<>("使用者", "user");
        Column<String> position = new Column<>("位置", "position");
        Column<String> status = new Column<>("狀態", "status");
        Column<String> LastModified = new Column<>("修改日期", "LastModified");
        List<DeviceTable> data = new ArrayList<>();

        DID.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String value, String s, int position) {
                Log.d("Column_value",value);
                Log.d("Column_position",position+"");

                if(ClickPosition==position){
                    Intent it=new Intent(InquireStatusActivity.this,UpdateStatusActivity.class);
                    it.putExtra("DID",value);
                    startActivity(it);
                }else{
                    table.getConfig().setContentCellBackgroundFormat(new ICellBackgroundFormat<CellInfo>() {
                        @Override
                        public void drawBackground(Canvas canvas, Rect rect, CellInfo cellInfo, Paint paint) {
                            Log.d("CellInfo_my",cellInfo.value);
                            if(cellInfo.row%2==1){
                                paint.setColor(ContextCompat.getColor(InquireStatusActivity.this, R.color.bg));
                                canvas.drawRect(rect,paint);
                            }
                            if(cellInfo.row==position){
                                paint.setColor(ContextCompat.getColor(InquireStatusActivity.this, R.color.bg2_b));
                                canvas.drawRect(rect,paint);
                            }
                        }

                        @Override
                        public int getTextColor(CellInfo cellInfo) {
                            return 0;
                        }
                    });
                    ClickPosition=position;
                    ClickCount=0;
                    ClickCount++;
                }
            }
        });

        SQLite sql = new SQLite(this);
        Cursor c = sql.select("device_tb", null, null, null, null, null);
        int rows_num = c.getCount();
        if (rows_num != 0) {
            c.moveToFirst();
            for (int j = 0; j < rows_num; j++) {
                String str = "";
                data.add(new DeviceTable(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), DeviceStatus.StatusStr[Integer.parseInt(c.getString(6))], c.getString(7)));
                c.moveToNext();
            }
        }

        TableData<DeviceTable> td = new TableData<>("所有儀器狀態", data, DID, category, model, number, user, position, status, LastModified);
        table.setTableData(td);
        table.getConfig().setContentStyle(new FontStyle(50, Color.BLACK));
        table.getConfig().setColumnTitleStyle(new FontStyle(60, Color.BLACK));
        table.getConfig().setTableTitleStyle(new FontStyle(60, Color.BLACK));
        table.getConfig().setContentCellBackgroundFormat(new ICellBackgroundFormat<CellInfo>() {
            @Override
            public void drawBackground(Canvas canvas, Rect rect, CellInfo cellInfo, Paint paint) {
                if(cellInfo.row%2==1){
                    paint.setColor(ContextCompat.getColor(InquireStatusActivity.this, R.color.bg));
                    canvas.drawRect(rect,paint);
                }
            }

            @Override
            public int getTextColor(CellInfo cellInfo) {
                return 0;
            }
        });
        table.setZoom(false);
        table.getConfig().setShowXSequence(false);
        table.getConfig().setShowYSequence(false);
        table.getConfig().setColumnTitleGridStyle(new LineStyle(5, Color.DKGRAY));
        table.getConfig().setContentGridStyle(new LineStyle(3, Color.GRAY));
    }
}
