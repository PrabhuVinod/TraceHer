package com.srkrit.traceher;

/**
 * Created by prabhu on 7/30/2016.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyService extends Service
{
    private static final String TAG = "MyService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;


    Map<String, String> params;
    public Session session;


    public Location mLastLocation;


    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
//        //Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);


        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */

                //Log.e("onShake:", String.valueOf(count));

                if (count==2){
                    //Log.e("onShake:", "DO IT NOW");

                    Bundle bundle=new Bundle();
                    //pr@bhu
                    bundle.putString("latitude", String.valueOf(mLastLocation.getLatitude()));
                    bundle.putString("longitude", String.valueOf(mLastLocation.getLongitude()));
                    //Log.e("latLng",mLastLocation.getLatitude()+" "+mLastLocation.getLongitude());
                    updateLocationInServer(bundle);


                }

            }
        });


        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);

        //Log.e("shake ","OK");



        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            //Log.e(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            //Log.e(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            //Log.e(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            //Log.e(TAG, "gps provider does not exist " + ex.getMessage());
        }







        return START_STICKY;
    }
    @Override
    public void onCreate()
    {
        //Log.e(TAG, "onCreate");


    }
    @Override
    public void onDestroy()
    {
        //Log.e(TAG, "onDestroy");
        super.onDestroy();

        mSensorManager.unregisterListener(mShakeDetector);


        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    //Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }



    private void initializeLocationManager() {
        //Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }






    private void updateLocationInServer(Bundle bundle) {

        String url ="http://www.srkrit.in/services/register";

        params=new HashMap<>();


        session = SessionManager.getInstance(getApplication());

        Log.e("session:", String.valueOf(session));


        params.put("id",session.get("id"));
        params.put("userno", session.get("userno"));
        params.put("guardianno", session.get("guardianno"));

        String latitude;
        String longitude;
        if (bundle.getString("latitude")==null){
            latitude="0.0";
            longitude="0.0";
        }
        else{
            latitude=bundle.getString("latitude");
            longitude=bundle.getString("longitude");
        }

        params.put("latitude",latitude);
        params.put("longitude",longitude);
        //Log.e("params update:", String.valueOf(params));

        String s ="In emergency. NEED HELP! at:"+session.get("address")+"("+latitude+","+longitude+")";
        //Log.e("string ",s);
        ArrayList<String> smsBody=new ArrayList<>();

        if (s.length()>150){
            smsBody.add(0, s.substring(0, 150));
            smsBody.add(1,s.substring(150));
        }
        else{
            smsBody.add(0, s);
        }


        SmsManager smsManager = SmsManager.getDefault();

		smsManager.sendMultipartTextMessage(session.get("guardianno"), null, smsBody, null, null);

//        Toast.makeText(getApplicationContext(), "sms sent", Toast.LENGTH_SHORT).show();

        //Log.e("sms","sent");
        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        params.clear();
                        //Log.e("response ", response);

//                        Toast.makeText(getApplicationContext(), "Successfully updated.", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        params.clear();
                        //Log.e("res1: ", String.valueOf(error));
//						Toast.makeText(getApplicationContext(), "ERROR "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };

        RequestQueue mRequestQueue;
        mRequestQueue= Volley.newRequestQueue(getApplication());
        mRequestQueue.add(strRequest);
    }



    private void onlyUpdateLocationInServer(Bundle bundle) {

        String url ="http://www.srkrit.in/services/register";

        params=new HashMap<>();


        session = SessionManager.getInstance(getApplication());

        //Log.e("session:", String.valueOf(session));

        params.put("id",session.get("id"));
        params.put("userno", session.get("userno"));
        params.put("guardianno", session.get("guardianno"));

        String latitude;
        String longitude;

        if (bundle.getString("latitude")==null){
            latitude="0.0";
            longitude="0.0";
            //Log.e("in","if");
        }
        else{
            latitude=bundle.getString("latitude");
            longitude=bundle.getString("longitude");
            //Log.e("in","else");

        }

        params.put("latitude",latitude);
        params.put("longitude",longitude);

        //Log.e("params update:", String.valueOf(params));

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        params.clear();
                        //Log.e("response ", response);

//                        Toast.makeText(getApplicationContext(), "Successfully updated.", Toast.LENGTH_SHORT).show();


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        params.clear();
                        //Log.e("res1: ", String.valueOf(error));
//						Toast.makeText(getApplicationContext(), "ERROR "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };

        RequestQueue mRequestQueue;
        mRequestQueue= Volley.newRequestQueue(getApplication());
        mRequestQueue.add(strRequest);
    }




    private class LocationListener implements android.location.LocationListener{

        public LocationListener(String provider)
        {
            //Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);

            if (mLastLocation.getLatitude()>0.0){
                Bundle bundle=new Bundle();
                bundle.putString("latitude", String.valueOf(mLastLocation.getLatitude()));
                bundle.putString("longitude", String.valueOf(mLastLocation.getLongitude()));
                //Log.e("from","lastLoc"+mLastLocation);
                onlyUpdateLocationInServer(bundle);
            }

        }
        @Override
        public void onLocationChanged(Location location)
        {
            //Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            if (mLastLocation.getLatitude()>0.0){
                Bundle bundle=new Bundle();
                bundle.putString("latitude", String.valueOf(mLastLocation.getLatitude()));
                bundle.putString("longitude", String.valueOf(mLastLocation.getLongitude()));
                //Log.e("from","changedLoc");
                onlyUpdateLocationInServer(bundle);
            }
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            //Log.e(TAG, "onProviderDisabled: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            //Log.e(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            //Log.e(TAG, "onStatusChanged: " + provider);
        }
    }


}
