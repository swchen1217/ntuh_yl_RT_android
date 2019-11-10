package com.swchen1217.ntuh_yl_rt_mdms;

public interface Permission {
    int PERMISSINO_DELETE=-1; //刪除
    int PERMISSINO_NOT_ENABLED=0; //未啟用
    int PERMISSINO_INQUIRT_STARUS=1; //查看device tb
    int PERMISSINO_UPDATE_STATUS=2; //登錄
    int PERMISSINO_READ_LOG=3; //查詢log
    int PERMISSINO_MANAGE_DEVICE=4; //device管理
    int PERMISSINO_MANAGE_USER=5; //user管理
    int PERMISSINO_ROOT=9; //super user
}
