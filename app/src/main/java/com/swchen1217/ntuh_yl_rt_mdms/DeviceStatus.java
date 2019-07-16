package com.swchen1217.ntuh_yl_rt_mdms;

public interface DeviceStatus {
    int STATUS_DISABLE=-1;
    int STATUS_NULL=0;
    int STATUS_USE=1;
    int STATUS_STOCK=2;
    int STATUS_FIX=3;
    int STATUS_MAINTENANCE_CHECK=4;
    String[] StatusStr = {"停用",
                    "可使用",
                    "使用中",
                    "倉庫",
                    "維修中",
                    "保養中"};
}
