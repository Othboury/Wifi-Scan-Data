package com.example.local;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.LogPrinter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class WifiReceiver extends BroadcastReceiver {
    WifiManager wifiManager;
    StringBuilder sb;
    ListView wifiDeviceList;
    ArrayList<String> deviceList = new ArrayList<>();
    int batimentId = 1;
    String salleId = "N/A";
    String floorId= "Empty";
    int positionId = 0;


    public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList) {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            positionId++;
            sb = new StringBuilder();
            List<ScanResult> wifiList = wifiManager.getScanResults();
            for (ScanResult scanResult : wifiList) {
                sb.append("\n").append(batimentId).append(",").append(salleId).append(",").append(floorId).append(",").append(positionId)
                        .append(",").append(scanResult.SSID).append(",").append(scanResult.BSSID).append(",").append(scanResult.level)
                        .append(",").append(scanResult.centerFreq0).append(",").append(scanResult.frequency);
            }
        }
    }

    public String ShowString(){
        return sb.toString();
    }



}