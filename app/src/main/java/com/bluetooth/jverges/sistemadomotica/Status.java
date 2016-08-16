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
    public MutableDateTime hora_riego_2_on;
    public boolean Sriego;
    public boolean Sluz;

    public String alarma = "";

    /**
     * eventos:
     * E-L1 luz activada.
     * E-L0 luz desactivada.
     * R-1 riego activado
     * R-0 riego desactivado
     * P panico alarma
     */

    public Status() {
        map = new HashMap<>(9);
        notificaciones = new ArrayList<>();
        map.put("T", hora = new MutableDateTime());
        map.put("TL1", hora_luces_on = new MutableDateTime());
        map.put("TL0", hora_luces_off = new MutableDateTime());
        map.put("TR1", hora_riego_1_on = new MutableDateTime());
        map.put("TR2", hora_riego_2_on = new MutableDateTime());
    }

    public Status parseStatus(String status) {
        this.hasStatus = true;
        String[] lines = status.split("\n");
        for (String l : lines) {
            try {
                this.parseLine(l);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.notificar();
        return this;
    }

    private void notificar() {
        Log.d("tag", "notificaciones len: " + notificaciones.size());
        for (StatusUpdate s : notificaciones) {
            Log.d("tag", "notificados");
            if (s != null)
                s.Update();
        }
    }

    private Status parseLine(String l) {
        Log.d("tag", l);

        l = l.replace(";", "");

        String[] line = l.split("-");

        if (line.length > 0) {
            //aca tiene que venir el comando a setear
            String key = line[0];
            //aca tiene que venir la hora y fecha
            String[] data = new String[2];
            if (line.length > 1) {
                data = line[1].split(":");
            }
            int[] ints = new int[9];


            if (key.startsWith("A")) {
                this.alarma = line.length > 1 ? line[1] : "d";
            } else if (key.startsWith("S")) {
                try {
                    this.getClass().getField(key).setBoolean(this, line[1].equals("1"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            } else if (key.startsWith("T")) {
                MutableDateTime tiempo = map.get(key);
                if (key.equals("T")) {
                    for (int i = 0; i < 6; i++) {
                        ints[i] = Integer.parseInt(data[i]);
                    }
                    tiempo.setYear(ints[0]);
                    tiempo.setMonthOfYear(ints[1]);
                    tiempo.setDayOfMonth(ints[2]);
                    tiempo.setHourOfDay(ints[3]);
                    tiempo.setMinuteOfHour(ints[4]);
                    tiempo.setSecondOfMinute(ints[5]);
                } else {
                    for (int i = 0; i < 2; i++) {
                        ints[i] = Integer.parseInt(data[i]);
                    }
                    tiempo.setHourOfDay(ints[0]);
                    tiempo.setMinuteOfHour(ints[1]);
                }
            } else if (key.startsWith("E")) { //evento de luz
                MainActivity.mainActivity.eventoLuz(line[1].equals("L1"));
            } else if (key.startsWith("R")) {
                MainActivity.mainActivity.eventoRiego(line[1].equals("1"));
            } else if (key.startsWith("F")) {
                MainActivity.mainActivity.eventoAlarma();
            } else if (key.startsWith("M")) {
                MainActivity.mainActivity.bloquearPantalla(line[1].equals("1"));
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
