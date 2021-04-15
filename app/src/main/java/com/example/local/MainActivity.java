package com.example.local;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;

import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;
    private ArrayList<String> listWifi;
    EditText textRoom;
    EditText textFloor;
    Button buttonScan;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textRoom = findViewById(R.id.roomText);
        buttonScan = findViewById(R.id.scanBtn);
        textFloor = findViewById(R.id.floorText);
        buttonScan.setEnabled(false);
        textFloor.setEnabled(false);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        textRoom.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                String roomNumber = textRoom.getText().toString();
                enableTxtFloor();
                launchScan();
                Toast.makeText(getApplicationContext(), "Salle: " + roomNumber, Toast.LENGTH_LONG).show();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        textFloor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String floorNumber = textFloor.getText().toString();
                enableBtnStartScan();
                launchScan();
                Toast.makeText(getApplicationContext(), "Etage: " + floorNumber, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void enableBtnStartScan(){
        buttonScan.setEnabled(true);
    }

    public void enableTxtFloor(){textFloor.setEnabled(true);}

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public void launchScan(){
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
                } else {
                    String roomNumber = textRoom.getText().toString();
                    String floorNumber = textFloor.getText().toString();
                    wifiManager.startScan();

                    if(receiverWifi.ShowString() == null){
                        Toast.makeText(MainActivity.this, "EMPTY", Toast.LENGTH_SHORT).show();
                    }else{
                        String sb =  receiverWifi.ShowString();
                        StringBuilder header = new StringBuilder();
                        header.append("batimentid").append(",").append("salleid").append(",").append("floorid").append(",")
                                .append("positionid").append(",").append("ssid").append(",")
                                .append("bssid").append(",").append("level").append(",").append("centrefrequence0")
                                .append(",").append("frequency");

                        String fullRoomNumber = "";
                        if(roomNumber.length() == 1){
                            fullRoomNumber = "00"+ roomNumber;
                        }else if(roomNumber.length() ==2){
                            fullRoomNumber = "0" + roomNumber;
                        }else{
                            fullRoomNumber = roomNumber;
                        }

                        filename = "salle-U" + fullRoomNumber + ".txt";
                        String dataToReplace = "N/A";
                        Pattern pattern = Pattern.compile(dataToReplace);
                        Matcher matcher = pattern.matcher(sb);
                        String result = matcher.replaceAll(roomNumber);

                        String ToReplace = "Empty";
                        Pattern patternFloor = Pattern.compile(ToReplace);
                        Matcher matcherFloor = patternFloor.matcher(result);
                        String resultFloor = matcherFloor.replaceAll(floorNumber);
                        if(fileExists(MainActivity.this, filename)){
                            try {
                                FileOutputStream fOut = openFileOutput(filename,  MODE_APPEND);
                                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                                osw.write(resultFloor);
                                osw.flush();
                                osw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            try {
                                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE));
                                outputStreamWriter.write(header+resultFloor);
                                outputStreamWriter.close();
                            } catch (IOException e) {
                                Log.e("Exception", "File write failed: " + e.toString());
                            }
                        }
                    }
                    Toast.makeText(MainActivity.this, "Wifi Data Collected and Stored...", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "File name:" + filename, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        receiverWifi = new WifiReceiver(wifiManager, wifiList);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }

    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Toast.makeText(MainActivity.this, "version> = marshmallow", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "location turned off", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                Toast.makeText(MainActivity.this, "location turned on", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            }
        } else {
            Toast.makeText(MainActivity.this, "scanning", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                launchScan();
            } else {
                Toast.makeText(MainActivity.this, "permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
            break;
        }
    }
}