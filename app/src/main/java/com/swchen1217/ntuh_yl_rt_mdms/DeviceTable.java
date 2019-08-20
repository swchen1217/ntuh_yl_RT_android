package com.swchen1217.ntuh_yl_rt_mdms;

public class DeviceTable {
    public DeviceTable(String DID, String category, String model, String number, String user, String position, String status, String LastModified) {
        this.DID = DID;
        this.category = category;
        this.model = model;
        this.number = number;
        this.user = user;
        this.position = position;
        this.status = status;
        this.LastModified = LastModified;
    }

    private String DID;
    private String category;
    private String model;
    private String number;
    private String user;
    private String position;
    private String status;
    private String LastModified;
}
