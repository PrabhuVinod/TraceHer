package com.srkrit.traceher;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Aboutus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        TextView about_us=(TextView)findViewById(R.id.dev_details);
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/prabhuvinod"));
                startActivity(browserIntent);
            }
        });
    }

    public void openSrkr(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.srkrec.ac.in/"));
        startActivity(browserIntent);
    }

    public void openSrkrIt(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.srkrec.ac.in/Web/Pages/Fordstan/DepartmentHome.aspx?OrganizationId=2&MasterPage=DepartmentMasterPage&PageLayout=anything&Theme=Fordstan&SubSite=DEPARTMENT&SubSiteId=10&WebMode=PUBLIC&NodeId=20"));
        startActivity(browserIntent);
    }
}
