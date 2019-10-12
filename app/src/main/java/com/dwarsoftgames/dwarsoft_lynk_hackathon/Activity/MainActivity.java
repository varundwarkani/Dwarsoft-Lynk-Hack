package com.dwarsoftgames.dwarsoft_lynk_hackathon.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dwarsoftgames.dwarsoft_lynk_hackathon.Activity.Volunteer.VolunteerAuth;
import com.dwarsoftgames.dwarsoft_lynk_hackathon.Activity.Volunteer.VolunteerAuthDetails;
import com.dwarsoftgames.dwarsoft_lynk_hackathon.Activity.Volunteer.VolunteerDashboard;
import com.dwarsoftgames.dwarsoft_lynk_hackathon.Database.AppDatabase;
import com.dwarsoftgames.dwarsoft_lynk_hackathon.R;
import com.google.android.material.button.MaterialButton;

import static com.dwarsoftgames.dwarsoft_lynk_hackathon.Utils.Constants.SHAREDPREF;
import static com.dwarsoftgames.dwarsoft_lynk_hackathon.Utils.Permissions.checkCoarseLocation;
import static com.dwarsoftgames.dwarsoft_lynk_hackathon.Utils.Permissions.checkFineLocation;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btVolunteer;
    private MaterialButton btVictim;
    private MaterialButton btOrganization;

    private AppDatabase db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.white));

        init();
        setOnClicks();
        checkPermissions();
    }

    private void init() {
        btVictim = findViewById(R.id.btVictim);
        btVolunteer = findViewById(R.id.btVolunteer);
        btOrganization = findViewById(R.id.btOrganization);

        db = AppDatabase.getAppDatabase(getApplicationContext());
        sharedPreferences = getSharedPreferences(SHAREDPREF,MODE_PRIVATE);
    }

    private void setOnClicks() {
        btVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    checkVolunteer();
                }
            }
        });
    }

    private boolean checkPermissions() {
        return checkCoarseLocation(MainActivity.this) && checkFineLocation(MainActivity.this);

    }

    private void checkVolunteer() {
        if (db.userDao().getVolunteerID() == 0) {
            //new User
            Intent intent = new Intent(MainActivity.this, VolunteerAuth.class);
            startActivity(intent);
        } else {
            //existing user
            if (sharedPreferences.getBoolean("volunteerDetails",false)) {
                Intent intent = new Intent(MainActivity.this, VolunteerDashboard.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, VolunteerAuthDetails.class);
                startActivity(intent);
            }
        }
    }
}