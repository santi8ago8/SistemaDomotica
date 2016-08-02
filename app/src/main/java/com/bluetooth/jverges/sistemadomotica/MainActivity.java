package com.bluetooth.jverges.sistemadomotica;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import layout.Alarma;
import layout.Creditos;
import layout.Luces;
import layout.PickTime;
import layout.Riego;
import layout.StatusFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, StatusFragment.OnFragmentInteractionListener, Riego.OnFragmentInteractionListener, Luces.OnFragmentInteractionListener, Creditos.OnFragmentInteractionListener, Alarma.OnFragmentInteractionListener {

    public Status status;
    private String readBuffer = "";
    public static MainActivity mainActivity = null;
    public static ArrayList<String> toNotify = new ArrayList<>(9);
    public static DateTimeFormatter formatterTime = DateTimeFormat.forPattern("k:m");
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        this.status = new Status();


        MainActivity.mainActivity = this;
        Fragment frag = new StatusFragment();
        StatusFragment.mainActivity = this;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, frag);
        ft.commit();
        //status.parseStatus("T-2016:5:30:12:32:15");
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                for (int i = 0; i < MainActivity.mainActivity.toNotify.size(); i++) {
                    Log.d("tag", "run on ui thread");
                    MainActivity.mainActivity.status.parseStatus(MainActivity.mainActivity.toNotify.get(i));
                    MainActivity.mainActivity.toNotify.remove(i--);
                }
            }
        };
        try {
            init();
            onClick3(null);
            onClick2(null);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment frag = null;

        if (id == R.id.nav_estado) {
            // Handle the camera action
            frag = new StatusFragment();
            this.getSupportActionBar().setTitle(R.string.app_name);
        } else if (id == R.id.nav_luces) {
            frag = new Luces();
            this.getSupportActionBar().setTitle(R.string.luces);

        } else if (id == R.id.nav_riego) {
            frag = new Riego();
            this.getSupportActionBar().setTitle(R.string.riego);

        } else if (id == R.id.nav_alarma) {
            frag = new Alarma();
            this.getSupportActionBar().setTitle(R.string.alarma);

        } else if (id == R.id.nav_creditos) {
            frag = new Creditos();
            this.getSupportActionBar().setTitle(R.string.creditos);

        }

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, frag);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private OutputStream outputStream;
    private InputStream inStream;

    private void init() throws IOException {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Log.d("tag", "bt enabled");


                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
                Log.d("tag", "devices: " + bondedDevices.size());
                if (bondedDevices.size() > 0) {

                    Object[] devices = (Object[]) bondedDevices.toArray();
                    int pos = 0;
                    for (int i = 0; i < devices.length; i++) {
                        BluetoothDevice bd = (BluetoothDevice) devices[i];
                        Log.d("tag", bd.getName());
                        Log.d("tag", bd.getAddress());
                        if (bd.getAddress().equals("20:15:06:03:15:58")) {
                            pos = i;
                        }

                    }
                    BluetoothDevice device = (BluetoothDevice) devices[pos];
                    ParcelUuid[] uuids = device.getUuids();
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    try {
                        socket.connect();
                        inStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                    } catch (IOException e) {
                        Log.d("tag", "Esta vinculado pero no se puede abrir el socket al dispositivo, comprobar el rango");
                    }

                    new Thread(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.KITKAT)
                        public void run() {
                            Log.d("bt domotica", "Init thread");
                            int bytes;
                            int availableBytes = 0;
                            // Keep listening to the InputStream until an exception occurs
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });

                            boolean needRun = true;
                            while (needRun) {
                                if (inStream != null)
                                    try {
                                        availableBytes = inStream.available();
                                        if (availableBytes > 0) {
                                            byte[] buffer = new byte[availableBytes];  // buffer store for the stream
                                            // Read from the InputStream

                                            bytes = inStream.read(buffer, 0, availableBytes);

                                            StringBuilder s = new StringBuilder();

                                            for (int i = 0; i < buffer.length; i++) {
                                                if (buffer[i] > 0) {
                                                    Character c = (char) buffer[i];
                                                    s.append(c.toString());
                                                }
                                            }

                                            Log.d("buffer", s.toString().replace("\r\n", ""));//, Character.getName(buffer[0])));

                                            readBuffer += s.toString();
                                            Log.d("read buffer", readBuffer);

                                            String[] lines = readBuffer.split("\r\n");


                                            readBuffer = "";

                                            for (int i = 0; i < lines.length; i++) {
                                                String l = lines[i];
                                                ArrayList<String> begins = new ArrayList<String>(4);
                                                begins.add("T");
                                                begins.add("A");
                                                begins.add("E");
                                                begins.add("R");
                                                begins.add("S");

                                                //agregar P, y E para manejar eventos.

                                                if (l.length() > 0 && begins.contains(l.substring(0, 1)) && l.endsWith(";")) {
                                                    toNotify.add(l);
                                                } else {
                                                    if (l.endsWith(";"))
                                                        readBuffer += l + "\r\n";
                                                    else {
                                                        readBuffer += l;
                                                    }
                                                }
                                            }
                                            handler.sendEmptyMessage(1);

                                        }
                                    } catch (IOException e) {
                                        Log.d("Error reading", e.getMessage());
                                        e.printStackTrace();
                                        break;
                                    }
                            }

                        }
                    }).start();
                } else {
                    Log.e("tag", "dispositivo no vinculado");
                }
            } else {
                Log.e("error", "Bluetooth is disabled, open dialog and settings.");
            }
        }
    }

    public void write(String s) {
        try {
            outputStream.write(s.getBytes());
        } catch (IOException e) {
            Log.d("tag", "fail write in bt, try to reconnect");
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("tag", "fail, ex");

            e.printStackTrace();
        }
    }

    public void onClick2(View v) {

        Log.d("tag", "enviar");
        write("S");

    }

    public void onClick3(View v) {

        Log.d("tag", "enviar comando");
        DateTime t = new DateTime();
        String s = String.format(
                "T-%s:%s:%s:%s:%s:%s;",
                Integer.toString(t.getYear()).substring(2, 4),
                String.format("%02d", t.getMonthOfYear()),
                String.format("%02d", t.getDayOfMonth()),
                String.format("%02d", t.getHourOfDay()),
                String.format("%02d", t.getMinuteOfHour()),
                String.format("%02d", t.getSecondOfMinute()));
        Log.d("tag", s);
        write(s);

    }

    private static StatusItem current = null;

    public void onClickSetTime(View v) {

        Log.d("tag", "click set timers");

        PickTime pt = new PickTime();
        if (v.getTag() instanceof StatusItem) {
            pt.statusItem = (StatusItem) v.getTag();
            MainActivity.current = (StatusItem) v.getTag();
        }
        pt.show(this.getFragmentManager(), "TimePicker");

    }

    public void onClickDesactivarRiego(View v) {
        Log.d("tag", "Desactivar riego");
        write("F");
    }

    public void onClickActivarRiego(View v) {
        Log.d("tag", "Activar riego");
        write("E");
    }


    public void onClickPrenderLuces(View v) {
        Log.d("tag", "Prender Luces");
        write("G");
    }

    public void onClickApagarLuces(View v) {
        Log.d("tag", "Apagar Luces");
        write("H");
    }

    public void onClickPanicoAlarma(View v) {
        Log.d("tag", "Panico alarma");
        write("p");
    }

    public void onClickActivarAlarma(View v) {
        Log.d("tag", "Activar Alarma");
        write("a");
    }

    public void onClickDesactivarAlarma(View v) {
        Log.d("tag", "Desactivar alarma");
        write("d");
    }

    public void setTime(int hour, int minutes) {
        //el date time no esta mutado, por eso lo transformo..
        MainActivity.current.time.setHourOfDay(hour);
        MainActivity.current.time.setMinuteOfHour(minutes);
        String s = String.format(
                "%s-%s:%s;",
                MainActivity.current.keyPlaca,
                String.format("%02d", hour),
                String.format("%02d", minutes));
        Log.d("tag", "Send time: " + s);
        //hay que enviarlo a la placa por bluetooth para que se modifique.
        write(s);


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
