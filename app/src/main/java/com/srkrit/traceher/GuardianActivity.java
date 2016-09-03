package com.srkrit.traceher;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Transition;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GuardianActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition enterTrans = new Explode();
            getWindow().setEnterTransition(enterTrans);

            Transition returnTrans = new Explode();
            getWindow().setReturnTransition(returnTrans);
        }


        Button track_btn=(Button)findViewById(R.id.track_btn);
        track_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(GuardianActivity.this);
                    startActivity(new Intent(GuardianActivity.this,DisplayMap.class),options.toBundle());
                }
                else{
                    startActivity(new Intent(GuardianActivity.this,DisplayMap.class));
                }

            }
        });
    }

}
