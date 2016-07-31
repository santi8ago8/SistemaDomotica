package com.bluetooth.jverges.sistemadomotica;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

/**
 * Created by jverges on 7/29/16.
 */
public class StatusItem {

    public int keyText;
    public String keyPlaca;
    public MutableDateTime time;
    public Boolean onlyTime = true;

    public StatusItem(int keyText, String keyPlaca, MutableDateTime time, Boolean onlyTime) {
        this.keyText = keyText;
        this.keyPlaca = keyPlaca;
        this.time = time;
        this.onlyTime = onlyTime;
    }

    @Override
    public String toString() {
        return "StatusItem{" +
                "keyText=" + keyText +
                ", keyPlaca='" + keyPlaca + '\'' +
                ", time=" + time +
                ", onlyTime=" + onlyTime +
                '}';
    }
}
