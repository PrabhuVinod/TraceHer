package com.srkrit.traceher;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Transition;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayMap extends AppCompatActivity implements OnMapReadyCallback,LocationListener {

    Context mContext;

    Session session;
    double latitude;
    double longitude;
    public Location myLocation;
    Marker marker = null;
    String services_url = "";
    RequestQueue mRequestQueue;
    Map<String, String> userIdParam = new HashMap<String, String>();
    LatLngBounds.Builder builder;
    int builder_counter=0;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        mContext = this;

        mRequestQueue = Volley.newRequestQueue(mContext);
        services_url = getResources().getString(R.string.services_url);


        session = SessionManager.getInstance(mContext);

//        Toast.makeText(mContext, "oncreate", Toast.LENGTH_SHORT).show();
//        Log.e("mapAct","created");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition enterTrans = new Explode();
            getWindow().setEnterTransition(enterTrans);

            Transition returnTrans = new Explode();
            getWindow().setReturnTransition(returnTrans);
        }



//        Log.e("userno in DM:", session.get("userno"));
        userIdParam.put("userno", session.get("userno"));


//        setUpMapIfNeeded();


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

//        Log.e("map","1");
        mapFragment.getMapAsync(this);
//        Log.e("map","2");

    }






//
//    private Location getMyLocation() {
//        // Get location from GPS if it's available
//        Log.e("map","getLoc");
//        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        Log.e("map","getLoc1");
//
//        // Location wasn't found, check the next most accurate place for the current location
//        if (myLocation == null) {
//            Log.e("map","getLocNULL");
//
//            Criteria criteria = new Criteria();
//            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//            // Finds a provider that matches the criteria
//            String provider = lm.getBestProvider(criteria, true);
//            // Use the provider to get the last known location
//            myLocation = lm.getLastKnownLocation(provider);
//        }
//
//        return myLocation;
//    }




    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.mMap=googleMap;


//        Log.e("userno in DMR:", session.get("userno"));
        userIdParam.put("userno", session.get("userno"));

//        Toast.makeText(DisplayMap.this, "map ready", Toast.LENGTH_SHORT).show();
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(true);


        plantMarkers(googleMap);

    }


    public void plantMarkers(final GoogleMap googleMap){
//        Location myLocation=getMyLocation();

//        Log.e("myLocation", String.valueOf(myLocation));

        builder = new LatLngBounds.Builder();
        MarkerOptions markeroptions;
        if (myLocation!=null){
            markeroptions = new MarkerOptions().position(
                    new LatLng(myLocation.getLatitude(),myLocation.getLongitude())).draggable(false);


            markeroptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            marker = googleMap.addMarker(markeroptions);
            marker.setTitle("You are here");

//            Log.e("map","plantMark");


            builder.include(marker.getPosition());
            builder_counter++;
        }
        else{
            markeroptions = new MarkerOptions().position(
                    new LatLng(Double.valueOf(session.get("latitude")),Double.valueOf(session.get("longitude")))).draggable(false);


            markeroptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            marker = googleMap.addMarker(markeroptions);
            marker.setTitle("You are here");

//            Log.e("map","plantMark");


            builder.include(marker.getPosition());
            builder_counter++;
        }



        String getAllPrivateCategories_url = services_url + "get_related_users";
        StringRequest strRequest = new StringRequest(Request.Method.POST, getAllPrivateCategories_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


//                            Log.e("response: ",response);
                            JSONObject responseObj = new JSONObject(response);

                            JSONArray dataArray = responseObj.getJSONArray("data");


//                            LatLngBounds.Builder builder = new LatLngBounds.Builder();


//                            if (dataArray.length()>0) {
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject user = new JSONObject(dataArray.getString(i)).getJSONObject("User");

                                LatLng userLatLng = new LatLng(Double.valueOf(user.getString("latitude")), Double.valueOf(user.getString("longitude")));

                                String name = user.getString("username");
                                String userNumber = user.getString("userno");

//                                    Log.e("markers: " + String.valueOf(userLatLng), userNumber + " " + name);


                                MarkerOptions markeroptions = new MarkerOptions().position(
                                        userLatLng).draggable(false);



                                markeroptions.icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                                marker = googleMap.addMarker(markeroptions);
                                marker.setTitle(name);


                                    if (user.getString("address") != "") {
                                        marker.setSnippet(user.getString("address"));
                                    } else {
                                        marker.setSnippet(user.getString("latitude") + "," + user.getString("latitude"));
                                    }



                                builder.include(marker.getPosition());
                                builder_counter++;

                            }
//                            }
//                            else{
//                                builder.include(new LatLng(0.0,0.0));
//                            }

                            LatLngBounds bounds = builder.build();

//                            Log.e("bounds:", bounds.toString());
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                            googleMap.animateCamera(cu);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("res1: ", String.valueOf(error));
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
//                Log.e("userIdParam:", String.valueOf(userIdParam));
                return userIdParam;
            }
        };

//        if (builder_counter>0) {
//            LatLngBounds bounds = builder.build();
//
//            Log.e("bounds:", bounds.toString());
//            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
//            googleMap.animateCamera(cu);
//        }

        mRequestQueue.add(strRequest);

    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation=location;
    }

}
