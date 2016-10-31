package de.uni_bremen.comnets.abdelhay.resourcemonitor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ConfigActivity extends Activity {
    public static final String LOG_TAG = "ConfigActivity";

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


// Broadcast to detect the actual time to turn on or off the Bluetooth


    private BroadcastReceiver batteryReceiver = null;  // Base class for code that will receive intents sent by sendBroadcast().
    private boolean batteryReceiverRegistered = false;
    private IntentFilter batteryReceiverFilter = null;




    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {

        int bluetooth_Hour_conn;
        int bluetooth_Minutes_conn;
        int bluetooth_Second_conn;
        int bluetooth_Day_conn;
        int bluetooth_Month_conn;

        int bluetooth_Hour_dis_conn;
        int bluetooth_Minutes_dis_conn;
        int bluetooth_Second_dis_conn;
        int bluetooth_Day_dis_conn;
        int bluetooth_Month_dis_conn;


        int bluetooth_Hour_Duration;
        int bluetooth_Minutes_Duration;
        int bluetooth_Second_Duration;
        int bluetooth_Day_Duration;

        boolean  Bluetooth_still_conn = true;

        String Wifi_Initiation_Duration = "null";
        String Screen_Initiation_Duration = "null";
        String RxBytes ="null";
        String TxBytes = " null";
        String isCharging ="null";
        String acCharge ="null";
        String usbCharge ="null";
        String level ="null";
        String isWifi ="null";
        String ConnWifi ="null";
        String isScreenOn ="null";
        String mobileDataEnabled ="null";
        String Radio_Technology ="null";
        String Network_Speed ="null";
        String Data_State ="null";
        String Call_Technology ="null";
        String Network_State ="null";



        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();


            // to check Bluetooth Enabled or not
            BluetoothAdapter Bluetooth = BluetoothAdapter.getDefaultAdapter();
            boolean isBluetooth = Bluetooth.isEnabled();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {


                    case BluetoothAdapter.STATE_ON:
                        while (Bluetooth_still_conn) {
                            long connectedTime = System.currentTimeMillis();  // Returns the current time in milliseconds.
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(connectedTime);    // Sets this Calendar's current time from the given long value.
                            bluetooth_Hour_conn = cal.get(Calendar.HOUR_OF_DAY);
                            bluetooth_Minutes_conn = cal.get(Calendar.MINUTE);
                            bluetooth_Second_conn = cal.get(Calendar.SECOND);
                            bluetooth_Day_conn = cal.get(Calendar.DAY_OF_MONTH);
                            bluetooth_Month_conn = cal.get(Calendar.MONTH);
                            // because calendar.Month give the month -1 " I don't know why !! " ex: if we are on October the result will be 9 not 10 !!
                            bluetooth_Month_conn = bluetooth_Month_conn +1;

                            Bluetooth_still_conn = false;
                            Toast toast4 = Toast.makeText(ConfigActivity.this, " Bluetooth is Connected  " + bluetooth_Month_conn, Toast.LENGTH_LONG);
                            toast4.show();





                            String Bluetooth_Initiation_Duration = "null";






                            ContentValues values = new ContentValues();
                            values.put(EnergyDbAdapter.COLUMN_NAME_CHARGING, isCharging);
                            values.put(EnergyDbAdapter.COLUMN_NAME_TIMESTAMP, connectedTime);
                            values.put(EnergyDbAdapter.COLUMN_NAME_CHG_AC, acCharge);
                            values.put(EnergyDbAdapter.COLUMN_NAME_CHG_USB, usbCharge);
                            values.put(EnergyDbAdapter.COLUMN_NAME_PERCENTAGE, level);
                            values.put(EnergyDbAdapter.COLUMN_NAME_WIFI, isWifi);
                            values.put(EnergyDbAdapter.COLUMN_NAME_Conn_WIFI, ConnWifi);
                            values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_DURATION, Wifi_Initiation_Duration);
                            values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_RECEIVE,  RxBytes);
                            values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_TRANSMIT,  TxBytes);
                            values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH, isBluetooth);
                            values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH_DURATION, Bluetooth_Initiation_Duration);
                            values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN, isScreenOn);
                            values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN_DURATION,Screen_Initiation_Duration );
                            values.put(EnergyDbAdapter.COLUMN_NAME_MOBILE_DATA, mobileDataEnabled);
                            values.put(EnergyDbAdapter.COLUMN_NAME_RADIO_TECHNOLOGY, Radio_Technology);
                            values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_SPEED, Network_Speed);
                            values.put(EnergyDbAdapter.COLUMN_NAME_DATA_STATE, Data_State);
                            values.put(EnergyDbAdapter.COLUMN_NAME_CALL_TECHNOLOGY, Call_Technology);
                            values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_STATE, Network_State);


                            if (dbAdapter.appendData(values) == -1) {
                                Log.e(LOG_TAG, "Cannot insert data into database!");
                            }
                            listAdapter.notifyDataSetChanged();

                        }
                        break;

                    case BluetoothAdapter.STATE_OFF:
                        while (!Bluetooth_still_conn) {
                            long disconnectedTime = System.currentTimeMillis();
                            Calendar call = Calendar.getInstance();
                            call.setTimeInMillis(disconnectedTime);
                            bluetooth_Hour_dis_conn = call.get(Calendar.HOUR_OF_DAY);
                            bluetooth_Minutes_dis_conn = call.get(Calendar.MINUTE);
                            bluetooth_Second_dis_conn = call.get(Calendar.SECOND);
                            bluetooth_Day_dis_conn = call.get(Calendar.DAY_OF_MONTH);
                            bluetooth_Month_dis_conn = call.get(Calendar.MONTH);

                            // because calendar.Month give the month -1 " I don't know why !! " ex: if we are on October the result will be 9 not 10 !!
                            bluetooth_Month_dis_conn = bluetooth_Month_dis_conn +1;
                            Bluetooth_still_conn = true;
                            Toast toast5 = Toast.makeText(ConfigActivity.this, " Bluetooth is disconnected  " + bluetooth_Hour_dis_conn + " hours, " + bluetooth_Minutes_dis_conn + " minutes, " + bluetooth_Second_dis_conn + " seconds ", Toast.LENGTH_LONG);
                            toast5.show();


                            if (bluetooth_Second_dis_conn >= bluetooth_Second_conn) {
                                bluetooth_Second_Duration = bluetooth_Second_dis_conn - bluetooth_Second_conn;
                            } else {

                                bluetooth_Second_dis_conn = bluetooth_Second_dis_conn + 60;
                                bluetooth_Minutes_dis_conn = bluetooth_Minutes_dis_conn - 1;
                                bluetooth_Second_Duration = bluetooth_Second_dis_conn - bluetooth_Second_conn;

                            }
                            if (bluetooth_Minutes_dis_conn >= bluetooth_Minutes_conn) {
                                bluetooth_Minutes_Duration = bluetooth_Minutes_dis_conn - bluetooth_Minutes_conn;
                            } else {
                                bluetooth_Minutes_dis_conn = bluetooth_Minutes_dis_conn + 60;
                                bluetooth_Hour_dis_conn = bluetooth_Hour_dis_conn - 1;
                                bluetooth_Minutes_Duration = bluetooth_Minutes_dis_conn - bluetooth_Minutes_conn;

                            }
                            if (bluetooth_Hour_dis_conn >= bluetooth_Hour_conn) {
                                bluetooth_Hour_Duration = bluetooth_Hour_dis_conn - bluetooth_Hour_conn;
                            } else {
                                bluetooth_Hour_dis_conn = bluetooth_Hour_dis_conn + 24;
                                bluetooth_Day_dis_conn = bluetooth_Day_dis_conn - 1;
                                bluetooth_Hour_Duration = bluetooth_Hour_dis_conn - bluetooth_Hour_conn;
                            }
                            if (bluetooth_Day_dis_conn >= bluetooth_Day_conn) {
                                bluetooth_Day_Duration = bluetooth_Day_dis_conn - bluetooth_Day_conn;
                            } else {
                                if((bluetooth_Month_conn== 1) || (bluetooth_Month_conn == 3) || (bluetooth_Month_conn == 5) || (bluetooth_Month_conn == 7) || (bluetooth_Month_conn == 8) || (bluetooth_Month_conn == 10)|| (bluetooth_Month_conn == 12)  ){
                                    bluetooth_Day_dis_conn = bluetooth_Day_dis_conn + 31;
                                    bluetooth_Day_Duration = bluetooth_Day_dis_conn - bluetooth_Day_conn;
                                }
                                if((bluetooth_Month_conn== 4) || (bluetooth_Month_conn == 6) || (bluetooth_Month_conn == 9) || (bluetooth_Month_conn == 11)   ){
                                    bluetooth_Day_dis_conn = bluetooth_Day_dis_conn + 30;
                                    bluetooth_Day_Duration = bluetooth_Day_dis_conn - bluetooth_Day_conn;
                                }
                                if(bluetooth_Month_conn== 2   ){
                                    bluetooth_Day_dis_conn = bluetooth_Day_dis_conn + 28;
                                    bluetooth_Day_Duration = bluetooth_Day_dis_conn - bluetooth_Day_conn;
                                }


                            }







                            ContentValues values = new ContentValues();
                            values.put(EnergyDbAdapter.COLUMN_NAME_CHARGING, isCharging);
                            values.put(EnergyDbAdapter.COLUMN_NAME_TIMESTAMP, disconnectedTime);
                            values.put(EnergyDbAdapter.COLUMN_NAME_CHG_AC, acCharge);
                            values.put(EnergyDbAdapter.COLUMN_NAME_CHG_USB, usbCharge);
                            values.put(EnergyDbAdapter.COLUMN_NAME_PERCENTAGE, level);
                            values.put(EnergyDbAdapter.COLUMN_NAME_WIFI, isWifi);
                            values.put(EnergyDbAdapter.COLUMN_NAME_Conn_WIFI, ConnWifi);
                            values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_DURATION, Wifi_Initiation_Duration);
                            values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_RECEIVE,  RxBytes);
                            values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_TRANSMIT,  TxBytes);
                            values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH, isBluetooth);
                            values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH_DURATION,bluetooth_Day_Duration + ":" + bluetooth_Hour_Duration + ":" + bluetooth_Minutes_Duration + ":" + bluetooth_Second_Duration);
                            values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN, isScreenOn);
                            values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN_DURATION,Screen_Initiation_Duration );
                            values.put(EnergyDbAdapter.COLUMN_NAME_MOBILE_DATA, mobileDataEnabled);
                            values.put(EnergyDbAdapter.COLUMN_NAME_RADIO_TECHNOLOGY, Radio_Technology);
                            values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_SPEED, Network_Speed);
                            values.put(EnergyDbAdapter.COLUMN_NAME_DATA_STATE, Data_State);
                            values.put(EnergyDbAdapter.COLUMN_NAME_CALL_TECHNOLOGY, Call_Technology);
                            values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_STATE, Network_State);


                            if (dbAdapter.appendData(values) == -1) {
                                Log.e(LOG_TAG, "Cannot insert data into database!");
                            }
                            listAdapter.notifyDataSetChanged();


                            Toast toast6 = Toast.makeText(ConfigActivity.this, " Bluetooth Connected Time    " + bluetooth_Hour_Duration + " hours, " + bluetooth_Minutes_Duration + " minutes, " + bluetooth_Second_Duration + " seconds ", Toast.LENGTH_LONG);
                            toast6.show();
                        }
                        break;
                }






                }

            }

    };



    // Broadcast to detect the actual time to turn on or off the Wifi

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        boolean  Wifi_still_conn = true;
        int wifi_Hour_conn;
        int wifi_Minutes_conn;
        int wifi_Second_conn;
        int wifi_Day_conn;
        int wifi_Month_conn;

        int wifi_Hour_dis_conn;
        int wifi_Minutes_dis_conn;
        int wifi_Second_dis_conn;
        int wifi_Day_dis_conn;
        int wifi_Month_dis_conn;

        int wifi_Hour_Duration;
        int wifi_Minutes_Duration;
        int wifi_Second_Duration;
        int wifi_Day_Duration;


        double mStartRX;
        double mStartTX;

        double mEndRx;
        double mEndTx;

        double RxBytes;
        double TxBytes;

        String Screen_Initiation_Duration="null";
        String Bluetooth_Initiation_Duration ="null";
        String isBluetooth ="null";
        String isCharging ="null";
        String acCharge ="null";
        String usbCharge ="null";
        String level ="null";
        String isScreenOn ="null";
        String Radio_Technology ="null";
        String Network_Speed ="null";
        String Data_State ="null";
        String Call_Technology ="null";
        String Network_State ="null";
        String mobileDataEnabled ="null";


        @Override
        public void onReceive(Context context, Intent intent) {


            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            Log.i("Wi-Fi network state", info.getDetailedState().toString());


           // to check Wifi is enable or not
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            boolean isWifi = wifi.isWifiEnabled();

            // to check wifi connected to any network or not
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean ConnWifi = mWifi.isConnected();







            if(isWifi) {

                while (Wifi_still_conn) {


                    long connectedTime = System.currentTimeMillis();  // Returns the current time in milliseconds.
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(connectedTime);    // Sets this Calendar's current time from the given long value.
                    wifi_Hour_conn = cal.get(Calendar.HOUR_OF_DAY);
                    wifi_Minutes_conn = cal.get(Calendar.MINUTE);
                    wifi_Second_conn = cal.get(Calendar.SECOND);
                    wifi_Day_conn = cal.get(Calendar.DAY_OF_MONTH);
                    wifi_Month_conn = cal.get(Calendar.MONTH);
                    // because calendar.Month give the month -1 " I don't know why !! " ex: if we are on October the result will be 9 not 10 !!
                    wifi_Month_conn = wifi_Month_conn +1;
                    Wifi_still_conn = false;


                    Toast toast1 = Toast.makeText(ConfigActivity.this, " Wifi is Connected  " + wifi_Hour_conn + " hours, " + wifi_Minutes_conn + " minutes, " + wifi_Second_conn + " seconds ", Toast.LENGTH_LONG);
                    toast1.show();


                    // to store the amount of TX and RX data that used by the cellphone before wifi is connected
                      mStartRX = TrafficStats.getTotalRxBytes();
                      mStartTX = TrafficStats.getTotalTxBytes();




                    String Wifi_Initiation_Duration ="null";
                    String RxBytes ="null";
                    String TxBytes = " null";





                    ContentValues values = new ContentValues();                     //bysglha fi el data bas table
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHARGING, isCharging);
                    values.put(EnergyDbAdapter.COLUMN_NAME_TIMESTAMP, connectedTime);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHG_AC, acCharge);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHG_USB, usbCharge);
                    values.put(EnergyDbAdapter.COLUMN_NAME_PERCENTAGE, level);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI, isWifi);
                    values.put(EnergyDbAdapter.COLUMN_NAME_Conn_WIFI, ConnWifi);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_DURATION, Wifi_Initiation_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_RECEIVE,  RxBytes);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_TRANSMIT,  TxBytes);
                    values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH, isBluetooth);
                    values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH_DURATION,Bluetooth_Initiation_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN, isScreenOn);
                    values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN_DURATION,Screen_Initiation_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_MOBILE_DATA, mobileDataEnabled);
                    values.put(EnergyDbAdapter.COLUMN_NAME_RADIO_TECHNOLOGY,  Radio_Technology);
                    values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_SPEED,  Network_Speed);
                    values.put(EnergyDbAdapter.COLUMN_NAME_DATA_STATE,  Data_State);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CALL_TECHNOLOGY,  Call_Technology);
                    values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_STATE,  Network_State);




                    if (dbAdapter.appendData(values) == -1) {
                        Log.e(LOG_TAG, "Cannot insert data into database!");
                    }
                    listAdapter.notifyDataSetChanged();


                }

            }


            else {

                while (!Wifi_still_conn) {


                    long disconnectedTime = System.currentTimeMillis();
                    Calendar call = Calendar.getInstance();
                    call.setTimeInMillis(disconnectedTime);
                    wifi_Hour_dis_conn = call.get(Calendar.HOUR_OF_DAY);
                    wifi_Minutes_dis_conn = call.get(Calendar.MINUTE);
                    wifi_Second_dis_conn = call.get(Calendar.SECOND);
                    wifi_Day_dis_conn = call.get(Calendar.DAY_OF_MONTH);
                    wifi_Month_dis_conn = call.get(Calendar.MONTH);
                    // because calendar.Month give the month -1 " I don't know why !! " ex: if we are on October the result will be 9 not 10 !!
                    wifi_Month_dis_conn = wifi_Month_dis_conn +1;
                    Wifi_still_conn = true;

                    Toast toast2 = Toast.makeText(ConfigActivity.this, " Wifi is disconnected  " + wifi_Hour_dis_conn + " hours, " + wifi_Minutes_dis_conn + " minutes, " + wifi_Second_dis_conn + " seconds ", Toast.LENGTH_LONG);
                    toast2.show();


                    // to calculate the amount of data after wifi is disconnected
                     mEndRx = TrafficStats.getTotalRxBytes();
                     mEndTx = TrafficStats.getTotalTxBytes();
                     RxBytes = (mEndRx- mStartRX)/1000; //  divid by 1000 to get the data with Kilo Byte
                     TxBytes = (mEndTx- mStartTX)/1000;




                    if (wifi_Second_dis_conn >= wifi_Second_conn) {
                        wifi_Second_Duration = wifi_Second_dis_conn - wifi_Second_conn;
                    } else {

                        wifi_Second_dis_conn = wifi_Second_dis_conn + 60;
                        wifi_Minutes_dis_conn = wifi_Minutes_dis_conn - 1;
                        wifi_Second_Duration = wifi_Second_dis_conn - wifi_Second_conn;

                    }
                    if (wifi_Minutes_dis_conn >= wifi_Minutes_conn) {
                        wifi_Minutes_Duration = wifi_Minutes_dis_conn - wifi_Minutes_conn;
                    } else {
                        wifi_Minutes_dis_conn = wifi_Minutes_dis_conn + 60;
                        wifi_Hour_dis_conn = wifi_Hour_dis_conn - 1;
                        wifi_Minutes_Duration = wifi_Minutes_dis_conn - wifi_Minutes_conn;

                    }
                    if (wifi_Hour_dis_conn >= wifi_Hour_conn) {
                        wifi_Hour_Duration = wifi_Hour_dis_conn - wifi_Hour_conn;
                    } else {
                        wifi_Hour_dis_conn = wifi_Hour_dis_conn + 24;
                        wifi_Day_dis_conn = wifi_Day_dis_conn - 1;
                        wifi_Hour_Duration = wifi_Hour_dis_conn - wifi_Hour_conn;
                    }
                    if (wifi_Day_dis_conn >= wifi_Day_conn) {
                        wifi_Day_Duration = wifi_Day_dis_conn - wifi_Day_conn;
                    } else {
                        if((wifi_Month_conn== 1) || (wifi_Month_conn == 3) || (wifi_Month_conn == 5) || (wifi_Month_conn == 7) || (wifi_Month_conn == 8) || (wifi_Month_conn == 10)|| (wifi_Month_conn == 12)  ){
                            wifi_Day_dis_conn = wifi_Day_dis_conn + 31;
                            wifi_Day_Duration = wifi_Day_dis_conn - wifi_Day_conn;
                        }
                        if((wifi_Month_conn== 4) || (wifi_Month_conn == 6) || (wifi_Month_conn == 9) || (wifi_Month_conn == 11)   ){
                            wifi_Day_dis_conn = wifi_Day_dis_conn + 30;
                            wifi_Day_Duration = wifi_Day_dis_conn - wifi_Day_conn;
                        }
                        if(wifi_Month_conn== 2   ){
                            wifi_Day_dis_conn = wifi_Day_dis_conn + 28;
                            wifi_Day_Duration = wifi_Day_dis_conn - wifi_Day_conn;
                        }


                    }


                    Toast toast3 = Toast.makeText(ConfigActivity.this, " Wifi Connected Time    " + wifi_Hour_Duration + " hours, " + wifi_Minutes_Duration + " minutes, " + wifi_Second_Duration + " seconds ", Toast.LENGTH_LONG);
                    toast3.show();



                    ContentValues values = new ContentValues();
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHARGING, isCharging);
                    values.put(EnergyDbAdapter.COLUMN_NAME_TIMESTAMP, disconnectedTime);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHG_AC, acCharge);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHG_USB, usbCharge);
                    values.put(EnergyDbAdapter.COLUMN_NAME_PERCENTAGE, level);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI, isWifi);
                    values.put(EnergyDbAdapter.COLUMN_NAME_Conn_WIFI, ConnWifi);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_DURATION,wifi_Day_Duration + ":" + wifi_Hour_Duration + ":" + wifi_Minutes_Duration + ":" + wifi_Second_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_RECEIVE,  RxBytes + "KB");
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_TRANSMIT,  TxBytes + "KB");
                    values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH, isBluetooth);
                    values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH_DURATION,Bluetooth_Initiation_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN, isScreenOn);
                    values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN_DURATION,Screen_Initiation_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_MOBILE_DATA, mobileDataEnabled);
                    values.put(EnergyDbAdapter.COLUMN_NAME_RADIO_TECHNOLOGY,  Radio_Technology);
                    values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_SPEED,  Network_Speed);
                    values.put(EnergyDbAdapter.COLUMN_NAME_DATA_STATE,  Data_State);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CALL_TECHNOLOGY,  Call_Technology);
                    values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_STATE,  Network_State);








                    if (dbAdapter.appendData(values) == -1) {
                        Log.e(LOG_TAG, "Cannot insert data into database!");
                    }
                    listAdapter.notifyDataSetChanged();



                }

            }



        }
    };



    // Broadcast to detect the actual time to turn on or off the Screen
    public  class ScreenReceiver extends BroadcastReceiver {

        int Screen_Hour_conn;
        int Screen_Minutes_conn;
        int Screen_Second_conn;
        int Screen_Day_conn;
        int Screen_Month_conn;

        int Screen_Hour_dis_conn;
        int Screen_Minutes_dis_conn;
        int Screen_Second_dis_conn;
        int Screen_Day_dis_conn;
        int Screen_Month_dis_conn;

        int Screen_Hour_Duration;
        int Screen_Minutes_Duration;
        int Screen_Second_Duration;
        int Screen_Day_Duration;
        public  boolean wasScreenOn = true;



        String Wifi_Initiation_Duration = "null";
        String isBluetooth ="null";
        String Bluetooth_Initiation_Duration ="null";

        String RxBytes ="null";
        String TxBytes = " null";
        String isCharging ="null";
        String acCharge ="null";
        String usbCharge ="null";
        String level ="null";
        String isWifi ="null";
        String ConnWifi ="null";

        String mobileDataEnabled ="null";
        String Radio_Technology ="null";
        String Network_Speed ="null";
        String Data_State ="null";
        String Call_Technology ="null";
        String Network_State ="null";

        @Override
        public void onReceive(Context context, Intent intent) {






              // to check the screen is on or off
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = pm.isScreenOn();


            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                  while (wasScreenOn) {


                    long connectedTime = System.currentTimeMillis();  // Returns the current time in milliseconds.
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(connectedTime);    // Sets this Calendar's current time from the given long value.
                    Screen_Hour_conn = cal.get(Calendar.HOUR_OF_DAY);
                    Screen_Minutes_conn = cal.get(Calendar.MINUTE);
                    Screen_Second_conn = cal.get(Calendar.SECOND);
                      Screen_Day_conn = cal.get(Calendar.DAY_OF_MONTH);
                      Screen_Month_conn = cal.get(Calendar.MONTH);
                      // because calendar.Month give the month -1 " I don't know why !! " ex: if we are on October the result will be 9 not 10 !!
                      Screen_Month_conn = Screen_Month_conn +1;
                    wasScreenOn = false;


                   // Toast toast1 = Toast.makeText(ConfigActivity.this, " Screen is ON  " + Screen_Hour_conn + " hours, " + Screen_Minutes_conn + " minutes, " + Screen_Second_conn + " seconds ", Toast.LENGTH_LONG);
                    //toast1.show();









                    String Screen_Initiation_Duration="null";


                    ContentValues values = new ContentValues();
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHARGING, isCharging);
                    values.put(EnergyDbAdapter.COLUMN_NAME_TIMESTAMP, connectedTime);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHG_AC, acCharge);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHG_USB, usbCharge);
                    values.put(EnergyDbAdapter.COLUMN_NAME_PERCENTAGE, level);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI, isWifi);
                    values.put(EnergyDbAdapter.COLUMN_NAME_Conn_WIFI, ConnWifi);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_DURATION, Wifi_Initiation_Duration );
                      values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_RECEIVE,  RxBytes);
                      values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_TRANSMIT,  TxBytes);
                    values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH, isBluetooth);
                    values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH_DURATION,Bluetooth_Initiation_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN, isScreenOn);
                    values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN_DURATION,Screen_Initiation_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_MOBILE_DATA, mobileDataEnabled);
                    values.put(EnergyDbAdapter.COLUMN_NAME_RADIO_TECHNOLOGY,  Radio_Technology);
                    values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_SPEED,  Network_Speed);
                    values.put(EnergyDbAdapter.COLUMN_NAME_DATA_STATE,  Data_State);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CALL_TECHNOLOGY,  Call_Technology);
                    values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_STATE,  Network_State);






                    if (dbAdapter.appendData(values) == -1) {
                        Log.e(LOG_TAG, "Cannot insert data into database!");
                    }
                    listAdapter.notifyDataSetChanged();


                }

            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {


                while (!wasScreenOn) {


                    long disconnectedTime = System.currentTimeMillis();
                    Calendar call = Calendar.getInstance();
                    call.setTimeInMillis(disconnectedTime);
                    Screen_Hour_dis_conn = call.get(Calendar.HOUR_OF_DAY);
                    Screen_Minutes_dis_conn = call.get(Calendar.MINUTE);
                    Screen_Second_dis_conn = call.get(Calendar.SECOND);
                    Screen_Day_dis_conn = call.get(Calendar.DAY_OF_MONTH);
                    Screen_Month_dis_conn = call.get(Calendar.MONTH);
                    // because calendar.Month give the month -1 " I don't know why !! " ex: if we are on October the result will be 9 not 10 !!
                    Screen_Month_dis_conn = Screen_Month_dis_conn +1;

                    wasScreenOn = true;

                    // Toast toast2 = Toast.makeText(ConfigActivity.this, " Screen is OFF  " + Screen_Hour_dis_conn + " hours, " + Screen_Minutes_dis_conn + " minutes, " + Screen_Second_dis_conn + " seconds ", Toast.LENGTH_LONG);
                    // toast2.show();


                    if (Screen_Second_dis_conn >= Screen_Second_conn) {
                        Screen_Second_Duration = Screen_Second_dis_conn - Screen_Second_conn;
                    } else {

                        Screen_Second_dis_conn = Screen_Second_dis_conn + 60;
                        Screen_Minutes_dis_conn = Screen_Minutes_dis_conn - 1;
                        Screen_Second_Duration = Screen_Second_dis_conn - Screen_Second_conn;

                    }
                    if (Screen_Minutes_dis_conn >= Screen_Minutes_conn) {
                        Screen_Minutes_Duration = Screen_Minutes_dis_conn - Screen_Minutes_conn;
                    } else {
                        Screen_Minutes_dis_conn = Screen_Minutes_dis_conn + 60;
                        Screen_Hour_dis_conn = Screen_Hour_dis_conn - 1;
                        Screen_Minutes_Duration = Screen_Minutes_dis_conn - Screen_Minutes_conn;

                    }
                    if (Screen_Hour_dis_conn >= Screen_Hour_conn) {
                        Screen_Hour_Duration = Screen_Hour_dis_conn - Screen_Hour_conn;
                    } else {
                        Screen_Hour_dis_conn = Screen_Hour_dis_conn + 24;
                        Screen_Day_dis_conn = Screen_Day_dis_conn - 1;
                        Screen_Hour_Duration = Screen_Hour_dis_conn - Screen_Hour_conn;
                    }
                    if (Screen_Day_dis_conn >=Screen_Day_conn) {
                        Screen_Day_Duration = Screen_Day_dis_conn - Screen_Day_conn;
                    } else {
                        if((Screen_Month_conn== 1) || (Screen_Month_conn == 3) || (Screen_Month_conn == 5) || (Screen_Month_conn == 7) || (Screen_Month_conn == 8) || (Screen_Month_conn == 10)|| (Screen_Month_conn == 12)  ){
                            Screen_Day_dis_conn = Screen_Day_dis_conn + 31;
                            Screen_Day_Duration = Screen_Day_dis_conn - Screen_Day_conn;
                        }
                        if((Screen_Month_conn== 4) || (Screen_Month_conn == 6) || (Screen_Month_conn == 9) || (Screen_Month_conn == 11)   ){
                            Screen_Day_dis_conn = Screen_Day_dis_conn + 30;
                            Screen_Day_Duration = Screen_Day_dis_conn - Screen_Day_conn;
                        }
                        if(Screen_Month_conn== 2   ){
                            Screen_Day_dis_conn = Screen_Day_dis_conn + 28;
                            Screen_Day_Duration = Screen_Day_dis_conn - Screen_Day_conn;
                        }


                    }

                    Toast toast3 = Toast.makeText(ConfigActivity.this, " Screen ON Time    " + Screen_Hour_Duration + " hours, " + Screen_Minutes_Duration + " minutes, " + Screen_Second_Duration + " seconds ", Toast.LENGTH_LONG);
                    toast3.show();






                    ContentValues values = new ContentValues();
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHARGING, isCharging);
                    values.put(EnergyDbAdapter.COLUMN_NAME_TIMESTAMP, disconnectedTime);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHG_AC, acCharge);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHG_USB, usbCharge);
                    values.put(EnergyDbAdapter.COLUMN_NAME_PERCENTAGE, level);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI, isWifi);
                    values.put(EnergyDbAdapter.COLUMN_NAME_Conn_WIFI, ConnWifi);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_DURATION, Wifi_Initiation_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_RECEIVE,  RxBytes);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_TRANSMIT,  TxBytes);
                    values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH, isBluetooth);
                    values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH_DURATION,Bluetooth_Initiation_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN, isScreenOn);
                    values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN_DURATION,Screen_Day_Duration+ ":" + Screen_Hour_Duration + ":" + Screen_Minutes_Duration + ":" + Screen_Second_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_MOBILE_DATA, mobileDataEnabled);
                    values.put(EnergyDbAdapter.COLUMN_NAME_RADIO_TECHNOLOGY,  Radio_Technology);
                    values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_SPEED,  Network_Speed);
                    values.put(EnergyDbAdapter.COLUMN_NAME_DATA_STATE,  Data_State);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CALL_TECHNOLOGY,  Call_Technology);
                    values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_STATE,  Network_State);






                    if (dbAdapter.appendData(values) == -1) {
                        Log.e(LOG_TAG, "Cannot insert data into database!");
                    }
                    listAdapter.notifyDataSetChanged();



                }
            }
        }

    }





    private EnergyDbAdapter dbAdapter = null;  //deh class ma3omlah yadawy w mawgoda fe file lw7daha

    // TODO: CHECK
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView; //A view that shows items in a vertically scrolling two-level list. This differs from the ListView by allowing two levels: groups
    // which can individually be expanded to show its children. The items come from the ExpandableListAdapter associated with this view.
    List<String> listDataHeader; // The user of this interface has precise control over where in the list each element is inserted.
    // The user can access elements by their integer index (position in the list), and search for elements in the list.

    HashMap<String, List<String>> listDataChild; // This class implements a hash table, which maps keys to values. Any non-null object can be used as a key or as a value.
    // To successfully store and retrieve objects from a hashtable, the objects used as keys must implement the hashCode method and the equals method.
    // The HashMap class is roughly equivalent to Hashtable, except that it is unsynchronized and permits nulls.
    // A HashMap contains values based on the key. It implements the Map interface and extends AbstractMap class.
    //It contains only unique elements.
    //It may have one null key and multiple null values.
    //It maintains no order.
    // keys is the input to the table
// TODO: End check

    public Boolean isMobileDataEnabled(){
        Object connectivityService = getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        try {
            Class<?> c = Class.forName(cm.getClass().getName());
            Method m = c.getDeclaredMethod("getMobileDataEnabled");
            m.setAccessible(true);
            return (Boolean)m.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);



        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(receiver, intentFilter);





        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, filter1);










        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);







        // DB Related stuff
        try {
            if (dbAdapter == null) {
                dbAdapter = new EnergyDbAdapter(this);
                dbAdapter.open();
            }
        } catch (SQLException e) {    // SQL is used to communicate with a database.
            // SQL statements are used to perform tasks such as update data on a database, or retrieve data from a database.
            // the standard SQL commands such as "Select", "Insert", "Update", "Delete", "Create", and "Drop" can be used to accomplish almost everything that one needs to do with a database.
            // An SQLException can occur both in the driver and the database. When such an exception occurs, an object of type SQLException will be passed to the catch clause.
            e.printStackTrace();
            dbAdapter = null;
            // Todo: Handle?
        }
        Log.e(LOG_TAG, "dbAdapter id: " + dbAdapter.toString());

        // List View start

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandableListView);

        listAdapter = new ExpandableListAdapter(this, dbAdapter);

        // setting list adapter
        expListView.setAdapter(listAdapter);




        // List View end


        if (batteryReceiver == null) {


            batteryReceiver = new BroadcastReceiver() {



                @Override
                public void onReceive(Context context, Intent intent) { // bygeeb el m3lomat 2aly m7tagha

                    long currentTime = System.currentTimeMillis();  // bytla3 el sa3a kame men el gehaz

                    int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scaling = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    int level = -1;
                    if (currentLevel >= 0 && scaling > 0) {// calculates the Level in Percent
                        level = (currentLevel * 100) / scaling;
                    }
                    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                    boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL;

                    int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                    boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                    boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

                    // to check wifi Enabled or not
                    WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    boolean isWifi = wifi.isWifiEnabled();



                    // to check wifi connected to any network or not
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    boolean ConnWifi = mWifi.isConnected();







                   // to check Bluetooth Enabled or not
                    BluetoothAdapter Bluetooth = BluetoothAdapter.getDefaultAdapter();
                    boolean isBluetooth = Bluetooth.isEnabled();

                    // to check Screen on or off
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    boolean isScreenOn = pm.isScreenOn();


                    // to check Mobile Data is Enabled or not
                    boolean mobileDataEnabled ;
                    Object connectivityService = getSystemService(CONNECTIVITY_SERVICE);
                    ConnectivityManager cm = (ConnectivityManager) connectivityService;

                    try {
                        Class<?> c = Class.forName(cm.getClass().getName());
                        Method m = c.getDeclaredMethod("getMobileDataEnabled");
                        m.setAccessible(true);
                        mobileDataEnabled = (Boolean)m.invoke(cm);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mobileDataEnabled = false;
                    }


                    // Get the type of the  Radio Tehcnology you are connected with



                      TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                      int NetworkType = tm.getNetworkType();
                      String Radio_Technology = "";
                      String Network_Speed = "";
                    if (mobileDataEnabled) {

                      switch (NetworkType) {

                          case TelephonyManager.NETWORK_TYPE_1xRTT:
                              Radio_Technology = "1xRTT";
                              Network_Speed = "Low Speed";
                              break;// ~ 50-100 kbps
                          case TelephonyManager.NETWORK_TYPE_CDMA:
                              Radio_Technology = "CDMA";
                              Network_Speed = "Low Speed";
                              break; // ~ 14-64 kbps
                          case TelephonyManager.NETWORK_TYPE_EDGE:
                              Radio_Technology = "EDGE";
                              Network_Speed = "Low Speed";
                              break; // ~ 120Kbps to 384Kbps
                          case TelephonyManager.NETWORK_TYPE_EVDO_0:
                              Radio_Technology = "EVDO_0";
                              Network_Speed = "Normal Speed";
                              break; // ~ 400-1000 kbps
                          case TelephonyManager.NETWORK_TYPE_EVDO_A:
                              Radio_Technology = "EVDO_A";
                              Network_Speed = "Normal Speed";
                              break; // ~ 600-1400 kbps
                          case TelephonyManager.NETWORK_TYPE_GPRS:
                              Radio_Technology = "GPRS";
                              Network_Speed = "Low Speed";
                              break; // ~ 35Kbps to 171kbps
                          case TelephonyManager.NETWORK_TYPE_HSDPA:
                              Radio_Technology = "HSDPA";
                              Network_Speed = "High Speed";
                              break; // ~ 2-14 Mbps
                          case TelephonyManager.NETWORK_TYPE_HSPA:
                              Radio_Technology = "HSPA";
                              Network_Speed = "High Speed";
                              break; // ~ 700-1700 kbps
                          case TelephonyManager.NETWORK_TYPE_HSUPA:
                              Radio_Technology = "HSUPA";
                              Network_Speed = "High Speed";
                              break; // ~ 1-23 Mbps
                          case TelephonyManager.NETWORK_TYPE_UMTS:
                              Radio_Technology = "UMTS";
                              Network_Speed = "Normal Speed";
                              break; // ~ 384Kbps to 2Mbps
			/*
			 * Above API level 7, make sure to set android:targetSdkVersion
			 * to appropriate level to use these
			 */
                          case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                              Radio_Technology = "EHRPD";
                              Network_Speed = "Normal Speed";
                              break; // ~ 1-2 Mbps
                          case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                              Radio_Technology = "EVDO_B";
                              Network_Speed = "High Speed";
                              break; // ~ 5 Mbps
                          case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                              Radio_Technology = "HSPAP";
                              Network_Speed = "Very High Speed";
                              break; // ~ 10-20 Mbps
                          case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                              Radio_Technology = "IDEN";
                              Network_Speed = "Low Speed";
                              break; // ~25 kbps
                          case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                              Radio_Technology = "LTE";
                              Network_Speed = "Very High Speed";
                              break; // ~ 3Mbps to 10Mbps average, 20Mbps+ peak download speeds
                          // Unknown
                          case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                              Radio_Technology = " UNKNOWN";
                              Network_Speed = "UNKNOWN";
                              break;

                      }
                  }
                   else{
                        Radio_Technology ="No Data";
                        Network_Speed="0";

                  }

                    // check the Data state

                    int NetworkState = tm.getDataState ();
                    String Data_State="";
                    boolean Internet_State=false;

                    switch (NetworkState){

                        case TelephonyManager.DATA_CONNECTING:
                            Data_State=" Connecting ";
                            Internet_State= false;
                            break;
                        case TelephonyManager.DATA_CONNECTED:
                            Data_State=" Connected ";
                            Internet_State= true;
                            break;
                        case TelephonyManager.DATA_DISCONNECTED:
                            Data_State=" DisConnected ";
                            Internet_State= false;
                            break;
                        case TelephonyManager.DATA_SUSPENDED:
                            Data_State=" Suspended ";
                            Internet_State= false;
                            break;


                    }

                    int CallState = tm.getPhoneType ();
                    String Call_Technology="";
                    boolean Call_State=false;

                    switch (CallState){
                        case TelephonyManager.PHONE_TYPE_GSM:
                            Call_Technology=" GSM ";
                            Call_State= true;
                            break;
                        case TelephonyManager.PHONE_TYPE_CDMA:
                            Call_Technology=" CDMA ";
                            Call_State= true;
                            break;
                        case TelephonyManager.PHONE_TYPE_SIP:
                            Call_Technology=" VOIP ";
                            Call_State= true;
                            break;
                        case TelephonyManager.PHONE_TYPE_NONE:
                            Call_Technology=" No phone radio ";
                            Call_State= false;
                            break;

                    }
                    String  Network_State="";

                    if(Internet_State && Call_State ){
                        Network_State = " Calls & Data";
                    }
                    else if(!Internet_State && Call_State){
                        Network_State = " Calls Only";
                    }
                    else if(!Call_State){
                        Network_State = " NO Coverage";
                    }



                    String Bluetooth_Initiation_Duration ="null";
                       String Wifi_Initiation_Duration ="null";
                       String Screen_Initiation_Duration ="null";
                    String RxBytes ="null";
                    String TxBytes = " null";

                    ContentValues values = new ContentValues();                     //bysglha fi el data bas table
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHARGING, isCharging);
                    values.put(EnergyDbAdapter.COLUMN_NAME_TIMESTAMP, currentTime);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHG_AC, acCharge);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CHG_USB, usbCharge);
                    values.put(EnergyDbAdapter.COLUMN_NAME_PERCENTAGE, level);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI, isWifi);
                    values.put(EnergyDbAdapter.COLUMN_NAME_Conn_WIFI, ConnWifi);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_DURATION,Wifi_Initiation_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_RECEIVE,  RxBytes);
                    values.put(EnergyDbAdapter.COLUMN_NAME_WIFI_TRANSMIT,  TxBytes);
                    values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH, isBluetooth);
                    values.put(EnergyDbAdapter.COLUMN_NAME_BLUETOOTH_DURATION,Bluetooth_Initiation_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN, isScreenOn);
                    values.put(EnergyDbAdapter.COLUMN_NAME_SCREEN_DURATION,Screen_Initiation_Duration );
                    values.put(EnergyDbAdapter.COLUMN_NAME_MOBILE_DATA, mobileDataEnabled);
                    values.put(EnergyDbAdapter.COLUMN_NAME_RADIO_TECHNOLOGY,  Radio_Technology);
                    values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_SPEED,  Network_Speed);
                    values.put(EnergyDbAdapter.COLUMN_NAME_DATA_STATE,  Data_State);
                    values.put(EnergyDbAdapter.COLUMN_NAME_CALL_TECHNOLOGY,  Call_Technology);
                    values.put(EnergyDbAdapter.COLUMN_NAME_NETWORK_STATE,  Network_State);






                    if (dbAdapter.appendData(values) == -1) {    // check law el data etkatabat fe el database law matkatabtesh hatb2a el value be -1
                        // w haysagel fe el log beta3 el error an howa ma3refsh ye insert el data
                        // appendData deh function beda5l beha data gwah el data base w hatla2y el function deh fe el class 2aly asmaha EnergyDbAdapter w dbAdapter dah object men el class dah
                        // el -1 deh hatla2eha gwah el fuction SQLdatabase  w ma3naha law el insert naga7 matrga3esh 7aga law manga7esh raga3 -1 3alshan keda howa hena 7atet el -1  be7esh law fe3lan el function deh raga3t -1 yb2ah fashal fe insert el data w katbteloh ba3ed keda any can't insert el data
                        Log.e(LOG_TAG, "Cannot insert data into database!");
                    }
                    listAdapter.notifyDataSetChanged();

                }
            };

            if (batteryReceiverFilter == null) {
                batteryReceiverFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            }
        }

        // Enable measurements on startup
        setMeasurementsEnable(true);
    }

    /**
     * App is exited. Stopp all measurements, unregister everything
     */
    @Override
    protected void onDestroy() {
        setMeasurementsEnable(false);
        unregisterReceiver(receiver);
        unregisterReceiver(mBroadcastReceiver1);

        super.onDestroy();
    }

    /**
     * App is in the background. Remove unneeded (cached) data from the memory
     */
    @Override
    public void onStop() {
        dbAdapter.tidyup();
        super.onStop();
    }

    /**
     * Executed when a certain permission is requested
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new ExportDb().execute();

                } else {
                    notifyUser(R.string.no_write_access);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Used to request permissions out of an onClick handler
     *
     * @param permissions
     * @param requestCode
     */
    public void myRequestPermissions(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }


    /**
     * Export the database
     *
     * @param view
     */

    public void exportDbClickHandler(View view) {          // eh ely by7sl lma btdoos 3la export Db
        Log.e(LOG_TAG, "export");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e(LOG_TAG, "request");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.write_access_message)
                        .setTitle(R.string.write_access_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myRequestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                Log.e(LOG_TAG, "requested");
                myRequestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        } else {
            new ExportDb().execute();
        }
    }

    /**
     * Remove everything from the database
     *
     * @param view
     */
    public void flushDbClickHandler(View view) {                      // eh ely by7sl lma btdoos 3la flush Db
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.flush_db_message)
                .setTitle(R.string.flush_db_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbAdapter.flushDb();
                listAdapter.notifyDataSetChanged();
                notifyUser(R.string.db_flushed);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notifyUser(R.string.aborted);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void toggleCheckBox(View view) {                   // el function ely btkhly el check box y toggle lma tdos 3leeh
        CheckBox checkBox = (CheckBox) findViewById(R.id.measurementStatusCheckBox);  // by3rf object esmha check box we byrbotha bel check box fi el layout
        setMeasurementsEnable(checkBox.isChecked());                                 //we by3rf howa checked wala l2a (mt3lm 3leeh wala l2) mn el method is checked we bypasseha lel function set...
    }

    /**
     * Enable / disable measurements
     *
     * @param measurementsEnable
     */
    private void setMeasurementsEnable(boolean measurementsEnable) {
        if (!measurementsEnable) {
            if (batteryReceiverRegistered) {  // law kan el mesaurments sha3'alah w bawa2fha
                unregisterReceiver(batteryReceiver);
                batteryReceiverRegistered = false;
                notifyUser(R.string.measurements_stopped);
            } else {  // law kant mesh sha3'alah  ( el box mesh checked ) w ana ba2fel el application ( men el onDestroy )
                notifyUser(R.string.measurements_already_stopped);
            }
        } else {  // law kant hyah mesh sha3'alah w basha3'alha
            if (!batteryReceiverRegistered) {  // law hyah aslan kant mesh Registered w a5leha Regitered w a5leha sha3'alah
                registerReceiver(batteryReceiver, batteryReceiverFilter);
                batteryReceiverRegistered = true;
                notifyUser(R.string.measurements_started);
            } else {
                notifyUser(R.string.measurements_already_running);
            }
        }

        CheckBox checkBox = (CheckBox) findViewById(R.id.measurementStatusCheckBox);
        if (batteryReceiverRegistered) {
            checkBox.setText(R.string.measurement_status_running);
            checkBox.setChecked(true);
        } else {
            checkBox.setText(R.string.measurement_status_stopped);
            checkBox.setChecked(false);
        }
    }


    /**
     * Debug output: Output given text in text view and as a toast
     *
     * @param text The Text itself
     */
    public void notifyUser(String text) {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Wrapper for notifyUser: Accepts id from string resources
     *
     * @param id The string id
     */
    public void notifyUser(int id) {
        notifyUser(getResources().getString(id));
    }

    /**
     * Add a new item to the expandable list view
     *
     * @param title Title of new item
     * @param items The corresponding List including all items
     */
    private void addNewDataToListView(String title, List<String> items) {
        listDataHeader.add(title);
        listDataChild.put(title, items);
        listAdapter.notifyDataSetChanged();
    }


    public class ExportDb extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... voids) {
            String lastFilename = "";

            SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.output_file_format));

            if (!isExternalStorageWritable()) {
                Log.e(LOG_TAG, "Directory not accessible");
                return null;
            }


            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            if (!(dir.exists() && dir.isDirectory())) {
                dir.mkdirs();
            }

            File output = new File(dir, "Energy-Table_" + sdf.format(new Date()) + ".csv");

            if (output == null) {
                Log.e(LOG_TAG, "Cannot create output file.");
                return null;
            }

            if (!output.exists()) {
                Log.e(LOG_TAG, "File does not exists, will try to create it: " + output.toString());
                try {
                    output.createNewFile();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Cannot create new file!");
                    e.printStackTrace();
                }

            }


            if (dbAdapter.writeToFile(output) != 0) {
                return null;
            }

            return output.getAbsolutePath().toString();
        }

        protected void onPostExecute(String lastFile) {
            if (lastFile != null) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(lastFile)));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_csv_to)));
            } else {
                notifyUser(R.string.no_write_access);
                Log.e(LOG_TAG, "lastFile is null.");
            }
            return;
        }

        /**
         * Check if the external storage is mounted and writable
         *
         * @return true if writable
         */
        private boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            return false;
        }

    }

}


