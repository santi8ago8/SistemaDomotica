package com.bluetooth.jverges.sistemadomotica;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
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
    public static DateTimeFormatter formatterTime = DateTimeFormat.forPattern("HH:mm 'hs.'");
    public static Handler handler;
    private ProgressDialog dialog = null;
    private static BluetoothSocket socket = null;

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
                    String s = MainActivity.mainActivity.toNotify.get(i);
                    MainActivity.mainActivity.toNotify.remove(i--);
                    MainActivity.mainActivity.status.parseStatus(s);
                }
            }
        };
        initWithThread();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Log.d("tag", "back bt");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("tag", "on pause");
        if (dialog != null)
            dialog.onDetachedFromWindow();

    }

    @Override
    public void onResume() {
        super.onResume();
        this.write("S");
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
        /*if (id == R.id.action_settings) {
            return true;
        }*/

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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (dialog == null)
                    dialog = ProgressDialog.show(MainActivity.this, "",
                            getString(R.string.int_conn_disp), true);
                else
                    dialog.show();
            }
        });

        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Log.d("tag", "bt enabled");


                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
                Log.d("tag", "devices: " + bondedDevices.size());
                if (bondedDevices.size() > 0) {

                    Object[] devices = (Object[]) bondedDevices.toArray();
                    int pos = -1;
                    for (int i = 0; i < devices.length; i++) {
                        BluetoothDevice bd = (BluetoothDevice) devices[i];
                        Log.d("tag", bd.getName());
                        Log.d("tag", bd.getAddress());
                        if (bd.getName().equals("JSJS")){ // bd.getAddress().equals("20:15:06:03:15:58")) {
                            pos = i;
                        }

                    }
                    if (pos == -1) {
                        showDeviceNotConected();
                    } else {
                        BluetoothDevice device = (BluetoothDevice) devices[pos];
                        Log.d("tag", "JSJS encontrado");
                        ParcelUuid[] uuids = device.getUuids();

                        try {

                            Log.d("tag", "Socket:");
                            Log.d("tag", socket == null ? "null" : socket.toString());
                            if (socket == null) {
                                socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());

                                socket.connect();
                            }
                            inStream = socket.getInputStream();
                            outputStream = socket.getOutputStream();
                            Log.d("tag", "Socket conectado");

                            onClick3(null);
                            write("S");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog != null) {
                                        dialog.hide();
                                    }


                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                            showDeviceOutOfRange();
                            socket = null;
                            Log.d("tag", "Esta vinculado pero no se puede abrir el socket al dispositivo, comprobar el rango");

                        }

                        new Thread(new Runnable() {
                            @TargetApi(Build.VERSION_CODES.KITKAT)
                            public void run() {
                                Log.d("bt domotica", "Init thread");
                                int bytes;
                                int availableBytes = 0;

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
                                                    ArrayList<String> begins = new ArrayList<>(7);
                                                    begins.add("T");
                                                    begins.add("A");
                                                    begins.add("E");
                                                    begins.add("R");
                                                    begins.add("S");
                                                    begins.add("F");
                                                    begins.add("M");

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
                    }
                } else {
                    Log.e("tag", "dispositivo no vinculado");
                    showDeviceNotConected();
                }

            } else {
                Log.e("error", "Bluetooth is disabled, open dialog and settings.");
                btDissabled();
            }
        }
    }

    private void showDeviceOutOfRange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("tag", "device out of range");
                if (dialog != null)
                    dialog.hide();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.mainActivity);
                builder.setMessage(R.string.mgs_not_connection)
                        .setPositiveButton(R.string.reintentar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.d("tag", "positive");
                                initWithThread();
                            }
                        })
                        .setNegativeButton(R.string.salir, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                Log.d("tag", "salir");
                                System.exit(0);
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Log.d("tag", "back dialogo");
                                System.exit(0);
                            }
                        });
                builder.create().show();
            }
        });
    }


    private void showDeviceNotConected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("tag", "device not paired");
                if (dialog != null)
                    dialog.hide();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.mainActivity);
                builder.setMessage(R.string.disp_no_vinculado)
                        .setPositiveButton(R.string.vincular, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.d("tag", "positive");
                                Intent settingsIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                                startActivityForResult(settingsIntent, 1);
                            }
                        })
                        .setNegativeButton(R.string.salir, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                Log.d("tag", "salir");
                                System.exit(0);
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Log.d("tag", "back dialogo");
                                System.exit(0);
                            }
                        });
                builder.create().show();
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            initWithThread();

        }

        Log.d("tag", "finish bt activate");
    }

    private void btDissabled() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null)
                    dialog.hide();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.mainActivity);
                builder.setMessage(R.string.msg_activar_bt)
                        .setPositiveButton(R.string.activar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.d("tag", "positive");
                                Intent settingsIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                                settingsIntent.putExtra("bt", "asdas");
                                startActivityForResult(settingsIntent, 1);
                                Log.d("tag", "bluetoothhhh");
                            }
                        })
                        .setNegativeButton(R.string.salir, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                Log.d("tag", "salir");
                                System.exit(0);
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Log.d("tag", "back dialogo");
                                System.exit(0);
                            }
                        });
                builder.create().show();
            }
        });


    }

    private void initWithThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {


    }

    public void eventoLuz(final boolean prendida) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int i = 1;
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(getString(prendida ? R.string.luces_prendidas : R.string.luces_apagadas))
                                .setVibrate(new long[]{1000, 1000})
                                .setLights(Color.CYAN, 1000, 3000)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setAutoCancel(true);

                mBuilder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0,
                        new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(i, mBuilder.build());

            }
        });
    }

    public void eventoRiego(final boolean prendido) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int i = 2;
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(getString(prendido ? R.string.regando : R.string.no_regando))
                                .setVibrate(new long[]{1000, 1000})
                                .setLights(Color.CYAN, 1000, 3000)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setAutoCancel(true);

                mBuilder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0,
                        new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(i, mBuilder.build());

            }
        });
    }

    public void eventoAlarma() {
        Log.d("tag", "evento alarma");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int i = 3;
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(getString(R.string.alarma_panico))
                                .setVibrate(new long[]{1000, 1000})
                                .setLights(Color.CYAN, 1000, 3000)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setAutoCancel(true);

                mBuilder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0,
                        new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(i, mBuilder.build());
            }
        });
    }

    ProgressDialog dialogBloq = null;

    public void bloquearPantalla(boolean bloquear) {
        Log.d("tag", "bloqueo del teclado: " + Boolean.toString(bloquear));
        if (bloquear && dialogBloq == null) {
            Log.d("tag","bloquear pantalla");
            dialogBloq = ProgressDialog.show(MainActivity.this, "",
                    getString(R.string.espera_teclado), true);
            dialogBloq.show();
        }
        if (!bloquear && dialogBloq != null) {
            dialogBloq.hide();
            dialogBloq = null;
        }

    }
}
