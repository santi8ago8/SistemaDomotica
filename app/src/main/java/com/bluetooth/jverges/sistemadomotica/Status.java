package com.bluetooth.jverges.sistemadomotica;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jverges on 7/27/16.
 */
public class Status {

    public boolean hasStatus = false;
    public HashMap<String, MutableDateTime> map;
    public ArrayList<StatusUpdate> notificaciones;
    public MutableDateTime hora;
    public MutableDateTime hora_luces_on;
    public MutableDateTime hora_luces_off;
    public MutableDateTime hora_riego_1_on;
    public MutableDateTime hora_riego_1_off;
    public MutableDateTime hora_riego_2_on;
    public MutableDateTime hora_riego_2_off;

    public Status() {
        map = new HashMap<>(9);
        notificaciones = new ArrayList<>();
        map.put("T", hora = new MutableDateTime());
        map.put("TL1", hora_luces_on = new MutableDateTime());
        map.put("TL0", hora_luces_off = new MutableDateTime());
        map.put("TR10", hora_riego_1_off = new MutableDateTime());
        map.put("TR11", hora_riego_1_on = new MutableDateTime());
        map.put("TR20", hora_riego_2_off = new MutableDateTime());
        map.put("TR21", hora_riego_2_on = new MutableDateTime());
    }

    public Status parseStatus(String status) {
        this.hasStatus = true;
        String[] lines = status.split("\n");
        for (String l : lines) {
            this.parseLine(l);
        }
        this.notificar();
        return this;
    }

    private void notificar() {
        for (StatusUpdate s : notificaciones) {
            Log.d("tag", "notificados");
            if (s != null)
                s.Update();
        }
    }

    private Status parseLine(String l) {
        Log.d("tag", l);

         l = l.replace(";","");

        String[] line = l.split("-");

        if (line.length > 0) {
            //aca tiene que venir el comando a setear
            String key = line[0];
            //aca tiene que venir la hora y fecha
            String[] data = line[1].split(":");
            Log.d("tag", line[1]);
            Log.d("tag", line[0]);
            int[] ints = new int[9];


            MutableDateTime tiempo = map.get(key);
            if (key.equals("T")) {
                for (int i = 0; i < 5; i++) {
                    ints[i] = Integer.parseInt(data[i]);
                }

                tiempo.setYear(ints[0]);
                tiempo.setMonthOfYear(ints[1]);
                tiempo.setDayOfMonth(ints[2]);
                tiempo.setHourOfDay(ints[3]);
                tiempo.setMinuteOfHour(ints[4]);
            } else {

                for (int i = 0; i < 2; i++) {
                    ints[i] = Integer.parseInt(data[i]);
                }

                tiempo.setHourOfDay(ints[0]);
                tiempo.setMinuteOfHour(ints[1]);
            }
        }

        return this;
    }

    public void addToNotify(StatusUpdate i) {
        this.notificaciones.add(i);
        if (hasStatus) {
            notificar();
        }
    }
}
