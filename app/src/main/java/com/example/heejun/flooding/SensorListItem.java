package com.example.heejun.flooding;

/**
 * Created by HEEJUN on 2017-01-10.
 */

public class SensorListItem {

    private String name = null;
    private String location = null;
    private String serialNumber = null;
    private String condition = "1";

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
