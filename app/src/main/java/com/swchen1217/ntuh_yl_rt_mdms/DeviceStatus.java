package com.swchen1217.ntuh_yl_rt_mdms;

public interface DeviceStatus {
    //int STATUS_DISABLE = -1; //停用
    int STATUS_NULL = 0; //無狀態
    int STATUS_USE = 1; //使用中
    int STATUS_STOREROOM = 2; //倉庫
    int STATUS_FIX = 3; //維修中
    int STATUS_MAINTENANCE_CHECK = 4; //保養中
    String[] StatusStr = {"無狀態",
            "使用中",
            "倉庫",
            "維修中",
            "保養中"};
}
