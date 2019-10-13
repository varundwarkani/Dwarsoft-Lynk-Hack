package com.dwarsoftgames.dwarsoft_lynk_hackathon.Activity.Organization;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dwarsoftgames.dwarsoft_lynk_hackathon.Activity.Volunteer.VictimHelpMap;
import com.dwarsoftgames.dwarsoft_lynk_hackathon.R;
import com.google.android.material.button.MaterialButton;

public class OrganizationDashboard extends AppCompatActivity {

    private MaterialButton btLive, btEvents, btFunds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_dashboard);

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.white));

        init();
        setOnClicks();
    }

    private void init() {
        btLive = findViewById(R.id.btLive);
        btEvents = findViewById(R.id.btEvents);
        btFunds = findViewById(R.id.btFunds);
    }

    private void setOnClicks() {
        btLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizationDashboard.this, VictimHelpMap.class);
                startActivity(intent);
            }
        });

        btEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizationDashboard.this, RaiseFunds.class);
                startActivity(intent);
            }
        });
    }
}