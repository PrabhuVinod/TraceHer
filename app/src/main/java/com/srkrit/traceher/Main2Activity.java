package com.srkrit.traceher;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.transition.Explode;
import android.transition.Transition;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    protected static final String TAG = "MainActivity";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    Activity mActivity;
    String servicesUrl="";
    RequestQueue mRequestQueue;
    Session session;

    double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Trace Her");
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        buildGoogleApiClient();


        mActivity=this;


        Transition exitTrans = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            exitTrans = new Explode();
            getWindow().setExitTransition(exitTrans);

            Transition reenterTrans = new Explode();
            getWindow().setReenterTransition(reenterTrans);
        }


        servicesUrl=getResources().getString(R.string.services_url);

        mRequestQueue = Volley.newRequestQueue(this);

        session=SessionManager.getInstance(mActivity);
//		Toast.makeText(MainActivity.this, "id:"+session.get("id"), Toast.LENGTH_SHORT).show();

//        if ((session.get("firstRun")=="")||(session.get("firstRun")==null)){
//            session.set("firstRun","1");
//            showHelp();
//        }
//        else{
            if ((session.get("id")=="")||(session.get("id")==null)){
                openSettings();
            }
            else{
                Button register_btn=(Button)findViewById(R.id.register_btn);
                register_btn.setText("Edit your details");
            }
//        }


    }

    private void showHelp() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(new Intent(this,Helpscreen.class),options.toBundle());
        }
        else{
            startActivity(new Intent(this,Helpscreen.class));
        }
    }


    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    public void openChildMod(View view){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(new Intent(this,ChildActivity.class),options.toBundle());
        }
        else{
            startActivity(new Intent(this,ChildActivity.class));
        }

    }

    public void openGuardianMod(View view){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(new Intent(this,GuardianActivity.class),options.toBundle());
        }
        else{
            startActivity(new Intent(this,GuardianActivity.class));
        }

    }


    public void openChildMod(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(new Intent(this,ChildActivity.class),options.toBundle());
        }
        else{
            startActivity(new Intent(this,ChildActivity.class));
        }

    }

    public void openGuardianMod(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(new Intent(this,GuardianActivity.class),options.toBundle());
        }
        else{
            startActivity(new Intent(this,GuardianActivity.class));
        }

    }



    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

//			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//			try {
            latitude=mLastLocation.getLatitude();
            longitude=mLastLocation.getLongitude();


            getThisAddress();

//				Toast.makeText(this,mLastLocation.getLatitude()+","+mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();


//				List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
//				address = addresses.get(0);
//				Toast.makeText(this, "address: "+address, Toast.LENGTH_LONG).show();

//			} catch (IOException e) {
//				e.printStackTrace();
//			}



        } else {
//			Toast.makeText(this, "location_detected", Toast.LENGTH_LONG).show();
        }
    }




    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
//		Log.e(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    private void getThisAddress(){
        RGeocoder locationAddress = new RGeocoder();
        locationAddress.getAddressFromLocation(latitude, longitude,
                getApplicationContext(), new Main2Activity.GeocoderHandler());
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            session.set("address",locationAddress);
//			Toast.makeText(mActivity, "Address:"+locationAddress, Toast.LENGTH_SHORT).show();
        }
    }


    public void openSettings() {
//		Toast.makeText(MainActivity.this, "open settings", Toast.LENGTH_SHORT).show();

        AlertDialog.Builder alert=new AlertDialog.Builder(mActivity);


        LinearLayout formLayout=new LinearLayout(mActivity);


        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

        formLayout.setPadding(25,5,25,5);

        formLayout.setOrientation(LinearLayout.VERTICAL);
        formLayout.setLayoutParams(LLParams);

        final EditText yourName=new EditText(mActivity);
        yourName.setHint("Your Name");
        formLayout.addView(yourName);

        final EditText yourNo=new EditText(mActivity);
        yourNo.setHint("Your Number");
        yourNo.setInputType(InputType.TYPE_CLASS_PHONE);
        formLayout.addView(yourNo);

        final EditText guardianNo=new EditText(mActivity);
        guardianNo.setHint("Guardian Number");
        guardianNo.setInputType(InputType.TYPE_CLASS_PHONE);
        formLayout.addView(guardianNo);

        alert.setView(formLayout);

        alert.setTitle("Register")
                .setMessage("Please fill all details")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        if ((yourName.getText().toString()!="")&&(yourNo.getText().toString()!="")&&(guardianNo.getText().toString()!=""))
                        {

//							Toast.makeText(MainActivity.this, "save", Toast.LENGTH_SHORT).show();

                            Map<String,String> params=new HashMap<String, String>();
                            params.put("username",yourName.getText().toString());
                            params.put("guardianno",guardianNo.getText().toString());
                            params.put("userno",yourNo.getText().toString());
                            params.put("latitude", String.valueOf(latitude));
                            params.put("longitude", String.valueOf(longitude));
                            params.put("address", session.get("address"));


                            saveToServer(params);

                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//						Toast.makeText(MainActivity.this, "cancel", Toast.LENGTH_SHORT).show();

                    }
                })
                .setIcon(getResources().getDrawable(R.drawable.register_icon))
                .show();
    }





    public void openSettings(View view) {

        AlertDialog.Builder alert=new AlertDialog.Builder(mActivity);


        LinearLayout formLayout=new LinearLayout(mActivity);


        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

        formLayout.setPadding(25,5,25,5);

        formLayout.setOrientation(LinearLayout.VERTICAL);
        formLayout.setLayoutParams(LLParams);

        final EditText yourName=new EditText(mActivity);
        yourName.setHint("Your Name");
        formLayout.addView(yourName);

        final EditText yourNo=new EditText(mActivity);
        yourNo.setHint("Your Number");
        yourNo.setInputType(InputType.TYPE_CLASS_PHONE);
        formLayout.addView(yourNo);

        final EditText guardianNo=new EditText(mActivity);
        guardianNo.setHint("Guardian Number");
        guardianNo.setInputType(InputType.TYPE_CLASS_PHONE);
        formLayout.addView(guardianNo);

        alert.setView(formLayout);

        alert.setTitle("Register")
                .setMessage("Please fill all details")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        if ((yourName.getText().toString()!="")&&(yourNo.getText().toString()!="")&&(guardianNo.getText().toString()!=""))
                        {

//							Toast.makeText(MainActivity.this, "save", Toast.LENGTH_SHORT).show();

                            Map<String,String> params=new HashMap<String, String>();
                            params.put("username",yourName.getText().toString());
                            params.put("guardianno",guardianNo.getText().toString());
                            params.put("userno",yourNo.getText().toString());
                            params.put("latitude", String.valueOf(latitude));
                            params.put("longitude", String.valueOf(longitude));
                            params.put("address", session.get("address"));
                            saveToServer(params);

                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//						Toast.makeText(MainActivity.this, "cancel", Toast.LENGTH_SHORT).show();

                    }
                })
                .setIcon(getResources().getDrawable(R.drawable.register_icon))
                .show();
    }

    private void saveToServer(final Map<String, String> params) {

//		Log.e("params", String.valueOf(params));


        String url =getResources().getString(R.string.services_url)+"register";



        if (isInternetAvailable()){


            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {

//							Log.e("response ", response);

                            Toast.makeText(Main2Activity.this, "Successfully Saved", Toast.LENGTH_SHORT).show();


                            try {
                                JSONObject responseObj=new JSONObject(response);
                                session.setUserId(responseObj.getString("id"));
                                session.set("userno", params.get("userno"));
                                session.setName(params.get("username"));
                                session.set("guardianno", params.get("guardianno"));
                                session.set("latitude", params.get("latitude"));
                                session.set("longitude", params.get("longitude"));
                                session.set("address", session.get("address"));


//								Log.e("userno",session.get("userno"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            params.clear();

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            params.clear();
                            Toast.makeText(Main2Activity.this, "Unable to save. Check internet.", Toast.LENGTH_SHORT).show();

//							Log.e("res1: ", String.valueOf(error));
//							Toast.makeText(getApplicationContext(), "ERROR "+error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams()
                {
                    return params;
                }
            };

            mRequestQueue.add(strRequest);









        }
        else{

            new AlertDialog.Builder(mActivity)
                    .setTitle("Internet Needed")
                    .setMessage("Please enable internet")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                        }
                    })
//					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) {
//							// do nothing
//						}
//					})
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();


        }

    }


    @Override
    public void onConnectionFailed(@NonNull com.google.android.gms.common.ConnectionResult connectionResult) {

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_child) {
            openChildMod();
        } else if (id == R.id.nav_guardian) {
            openGuardianMod();
        } else if (id == R.id.nav_open_map) {
            startActivity(new Intent(this,DisplayMap.class));
        } else if (id == R.id.nav_register) {
            openSettings();
        } else if (id == R.id.nav_about_us) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
                startActivity(new Intent(this,Aboutus.class),options.toBundle());
            }
            else{
                startActivity(new Intent(this,Aboutus.class));
            }

        }else if (id == R.id.nav_help) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
                startActivity(new Intent(this,Helpscreen.class),options.toBundle());
            }
            else{
                startActivity(new Intent(this,Helpscreen.class));
            }

        }
        else if (id == R.id.nav_share) {

            Intent i=new Intent(android.content.Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Install Trace Her App");
            i.putExtra(android.content.Intent.EXTRA_TEXT, "Download Trace Her: https://play.google.com/store/apps/details?id=com.srkrit.traceher");
            startActivity(Intent.createChooser(i,"Share via"));


        } else if (id == R.id.nav_exit) {
            AlertDialog.Builder alert=new AlertDialog.Builder(mActivity);
            alert.setTitle("Exit")
                    .setMessage("Are you aure you want to exit?")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(getResources().getDrawable(R.drawable.exit_icon))
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
