package com.quy.hc05;

public class History {
    String time;
    String temp;
    String humidity;
    String maxTemp;
    String minTemp;
    String maxHumidity;
    String minHumidity;

    public History(String time, String temp, String humidity, String maxTemp, String minTemp, String maxHumidity, String minHumidity) {
        this.time = time;
        this.temp = temp;
        this.humidity = humidity;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.maxHumidity = maxHumidity;
        this.minHumidity = minHumidity;
    }
}
