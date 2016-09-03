package com.srkrit.traceher;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ChildActivity extends AppCompatActivity {


    Button enable_tracing;

    RequestQueue mRequestQueue;

    Session session;
    int running = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);


        enable_tracing=(Button) findViewById(R.id.enable_tracing);
        mRequestQueue = Volley.newRequestQueue(this);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition enterTrans = new Explode();
            getWindow().setEnterTransition(enterTrans);

            Transition returnTrans = new Explode();
            getWindow().setReturnTransition(returnTrans);
        }

        if (isMyServiceRunning(MyService.class)){
            running=1;
            enable_tracing.setText("Stop Tracing");
        }
        else{
            running=0;
        }

        session = SessionManager.getInstance(this);


        enable_tracing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				Toast.makeText(MainActivity.this, "clicked tracing" + running, Toast.LENGTH_SHORT).show();



                if ((session.get("id")!=null)&&(session.get("id")!=""))
                {
                    if (isMyServiceRunning(MyService.class)){
                        running=1;
                        stopService(new Intent(ChildActivity.this, MyService.class));
                        enable_tracing.setText("Start Tracing");

                    }
                    else{
                        running=0;

                        startService(new Intent(ChildActivity.this,MyService.class));
                        enable_tracing.setText("Stop Tracing");
                    }
                }
                else{
                    startActivity(new Intent(ChildActivity.this,Main2Activity.class));
                    finish();
                }



            }
        });

    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
