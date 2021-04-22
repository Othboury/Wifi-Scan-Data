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

import com.google.gson.JsonObject;

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

/**
 *
 * This class extends from BroadcastReceiver
 *
 */

class WifiReceiver extends BroadcastReceiver {
    WifiManager wifiManager;
    StringBuilder sb;
    ListView wifiDeviceList;
    String batimentId = "NoBat";
    String salleId = "N/A";
    String floorId= "Empty";
    int positionId = 0;
    JsonObject combined= new JsonObject();

    public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList) {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
    }

    /***
     *
     * Get Wifi Scan Result and store them in both, a StringBuilder and a JsonObject. The first
     * will serve when writing the into a txt file, while the later will be the one to send
     * (Client/Server) into the machine learning model.
     *
     * @author Othmane
     */

    public void onReceive(Context context, Intent intent) {
        //int i =0;
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            positionId++;
            sb = new StringBuilder();
            List<ScanResult> wifiList = wifiManager.getScanResults();
            for (ScanResult scanResult : wifiList) {
                sb.append("\n").append(batimentId).append(",").append(salleId).append(",").append(floorId).append(",").append(positionId)
                        .append(",").append(scanResult.SSID).append(",").append(scanResult.BSSID).append(",").append(scanResult.level)
                        .append(",").append(scanResult.centerFreq0).append(",").append(scanResult.frequency);

                JsonObject postData= new JsonObject();
                //postData.addProperty("ssid",scanResult.SSID);
                //postData.addProperty("bssid",scanResult.BSSID);
                postData.addProperty("level",scanResult.level);
                //postData.addProperty("centerFreq0",scanResult.centerFreq0);
                //postData.addProperty("frequency",scanResult.frequency);
                combined.add(scanResult.BSSID, postData);
                //i++;
            }
        }
    }

    public String ShowString(){
        return sb.toString();
    }

    public JsonObject jsList(){
        return combined;
    }



}