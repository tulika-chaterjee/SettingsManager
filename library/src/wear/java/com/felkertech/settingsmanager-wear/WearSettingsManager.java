package com.felkertech.settingsmanager-wear;

/**
 * Created by guest1 on 1/2/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.felkertech.settingsmanager.SettingsManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Version 1.1
 * Created by N on 14/9/2014.
 * Last Edited 13/5/2015
 *   * Support for syncing data to wearables
 */
public class WearSettingsManager extends SettingsManager {
    public WearSettingsManager(Activity activity) {
        super(activity);
    }
    public WearSettingsManager(Context context) {
        super(context);
    }
    public String setString(int resId, String val) {
        String out = super.setString(resId, val);
        pushData();
        return out;
    }
    public String setString(String key, String val) {
        String out = super.setString(key, val);
        pushData();
        return out;
    }
    public boolean setBoolean(int resId, boolean val) {
        boolean out = super.setBoolean(mContext.getString(resId), val);
        pushData();
        return out;
    }
    public boolean setBoolean(String key, boolean val) {
        boolean out = super.setBoolean(key, val);
        pushData();
        return out;
    }
    public int setInt(int resId, int val) {
        int out = super.setInt(mContext.getString(resId), val);
        pushData();
        return out;
    }
    public int setInt(String key, int val) {
        int out = super.setInt(key, val);
        pushData();
        return out;
    }
    public long setLong(int resId, long val) {
        long out = super.setLong(mContext.getString(resId), val);
        pushData();
        return out;
    }
    public long setLong(String key, long val) {
        long out = super.setLong(key, val);
        pushData();
        return out;
    }

    /* SYNCABLE SETTINGS MANAGER */
    private boolean syncEnabled = false;
    private GoogleApiClient syncClient;

    public boolean setSyncableSettingsManager(GoogleApiClient gapi) {
        syncClient = gapi;
        if(gapi != null && gapi.isConnected()) {
            ConnectionResult wearable = gapi.getConnectionResult(Wearable.API);
            if (wearable.isSuccess()) {
                //Sync enabled
                syncEnabled = true;
                return true;
            } else {
                Log.e(TAG, "Wear API is disabled");
            }
        }
        return false;
    }

    /**
     * Pushes all settings to other devices
     */
    public void pushData() {
        if(syncClient == null || !syncClient.isConnected()) {
            Log.e(TAG, "Can't sync");
            throw new NullPointerException("GoogleApiClient is null or not connected");
        }
        Iterator<String> keys = sharedPreferences.getAll().keySet().iterator();
        PutDataMapRequest dataMap = PutDataMapRequest.create("/prefs");
        while(keys.hasNext()) {
            String key = keys.next();
            Object v = sharedPreferences.getAll().get(key);
            Log.d(TAG, "Push "+key+" = "+v.getClass().getSimpleName()+", "+v);
            if(v.getClass().toString().contains("Boolean")) {
                dataMap.getDataMap().putBoolean(key, (Boolean) v);
                //Log.d(TAG, "Sending boolean "+key+" = "+v);
            } else if(v.getClass().toString().contains("String")) {
                dataMap.getDataMap().putString(key, (String) v);
                //Log.d(TAG, "Sending string "+key+" = "+v);
            } else if (v.getClass().toString().contains("Integer")) {
                dataMap.getDataMap().putInt(key, (int) v);
                //Log.d(TAG, "Sending int "+key+" = "+v);
            } else if (v.getClass().toString().contains("Long")) {
                dataMap.getDataMap().putLong(key, (long) v);
                // Log.d(TAG, "Sending long "+key+" = "+v);
            }
        }
        PutDataRequest request = dataMap.asPutDataRequest();
        //Log.d(TAG, "Pending intent");
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(syncClient, request);
    }

    /**
     * Retrieves any changes in settings and applies them to the correct key
     * @param dataEvents DataEventBuffer from the service callback
     */
    public void pullData(DataEventBuffer dataEvents) {
//        Iterator<DataEvent> eventIterator = dataEvents.iterator();
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
                DataMap dataMap = DataMap.fromByteArray(event.getDataItem().getData());
                Iterator<String> stringIterator = dataMap.keySet().iterator();
                while(stringIterator.hasNext()) {
                    String key = stringIterator.next();
                    Object v = dataMap.get(key);
                    if(v.getClass().toString().contains("Boolean")) {
                        setBoolean(key, (Boolean) v);
                    } else if(v.getClass().toString().contains("String")) {
                        setString(key, (String) v);
                    } else if (v.getClass().toString().contains("Integer")) {
                        setInt(key, (int) v);
                    } else if (v.getClass().toString().contains("Long")) {
                        setLong(key, (long) v);
                    }
                }
            }
        }
    }
}

