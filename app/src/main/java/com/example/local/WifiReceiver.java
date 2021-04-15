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
    private String filename = "SampleFile.txt";
    private String filepath = "MyFileStorage";
    File myExternalFile;


    public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList) {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            sb = new StringBuilder();
            List<ScanResult> wifiList = wifiManager.getScanResults();
            for (ScanResult scanResult : wifiList) {
                sb.append("\n").append("BSSID : ").append(scanResult.BSSID).append("\n").append("SSID : ").append(scanResult.SSID).append("\n")
                        .append("Frequency : ").append(scanResult.frequency).append("\n").append("Level : ").append(scanResult.level)
                        .append("\n").append("CenterFreq0 : ").append(scanResult.centerFreq0).append("\n");
                deviceList.add(scanResult.SSID + " - " + scanResult.capabilities);
            }

        }
    }

    public String Show(){
        System.out.println(sb);
        return sb.toString();
    }



}