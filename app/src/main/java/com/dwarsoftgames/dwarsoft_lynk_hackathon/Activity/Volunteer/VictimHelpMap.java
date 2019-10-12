package com.dwarsoftgames.dwarsoft_lynk_hackathon.Activity.Volunteer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dwarsoftgames.dwarsoft_lynk_hackathon.Database.AppDatabase;
import com.dwarsoftgames.dwarsoft_lynk_hackathon.Models.VictimsHelpModel;
import com.dwarsoftgames.dwarsoft_lynk_hackathon.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dwarsoftgames.dwarsoft_lynk_hackathon.Utils.Constants.AREA;
import static com.dwarsoftgames.dwarsoft_lynk_hackathon.Utils.Constants.CITY;
import static com.dwarsoftgames.dwarsoft_lynk_hackathon.Utils.Constants.GET_HELP_TYPES;
import static com.dwarsoftgames.dwarsoft_lynk_hackathon.Utils.Constants.GET_VICTIM_DETAILS;
import static com.dwarsoftgames.dwarsoft_lynk_hackathon.Utils.Constants.SHAREDPREF;
import static com.dwarsoftgames.dwarsoft_lynk_hackathon.Utils.Constants.STATES;
import static com.dwarsoftgames.dwarsoft_lynk_hackathon.Utils.Utilities.UTCToIST;

public class VictimHelpMap extends AppCompatActivity implements OnMapReadyCallback {

    private MaterialSpinner spState, spCity, spArea;

    private RequestQueue requestQueue;
    private AppDatabase db;

    private String areaID;

    private ArrayAdapter<String> spinnerAdapter_state;
    private ArrayAdapter<String> spinnerAdapter_city;
    private ArrayAdapter<String> spinnerAdapter_area;

    private ArrayList<String> states = new ArrayList<>();
    private ArrayList<String> city = new ArrayList<>();
    private ArrayList<String> area = new ArrayList<>();

    private final List<Object> itemsList = new ArrayList<>();

    private GoogleMap mMap;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victim_help_map);

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.white));

        init();
        initMaps();
        setAdapters();
        setSpinnerListeners();

        post_state();
        post_city("1");
        post_area("1");
    }

    private void init() {
        spState = findViewById(R.id.spState);
        spCity = findViewById(R.id.spCity);
        spArea = findViewById(R.id.spArea);

        requestQueue = Volley.newRequestQueue(getApplicationContext(),null);
        db = AppDatabase.getAppDatabase(getApplicationContext());

        spState.setHint("Select State");
        spCity.setHint("Select City");
        spArea.setHint("Select Area");

        states.clear();
        city.clear();
        area.clear();

        areaID = db.userDao().getAreaID();
    }

    private void initMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void setAdapters() {
        spinnerAdapter_state = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAdapter_city = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAdapter_area = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter_area.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void clearAdapters() {
        spinnerAdapter_city = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAdapter_area = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter_area.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spCity.setAdapter(spinnerAdapter_city);
        spArea.setAdapter(spinnerAdapter_area);
    }

    private void setSpinnerListeners() {
        spState.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                clearAdapters();
                post_city(states.get(position));
            }
        });

        spCity.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                post_area(city.get(position));
            }
        });

        spArea.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                areaID = area.get(position);
                post_getVictimDetails();
            }
        });
    }

    private void initMapDetails() {

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        });
    }

    private void post_state() {

        Map<String, String> params = new HashMap<>();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                STATES, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseStates(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),getString(R.string.api_fail), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        jsonObjReq.setTag("States");
        requestQueue.add(jsonObjReq);
    }

    private void parseStates(JSONObject jsonObject) {
        try {
            if (jsonObject.getBoolean("isSuccess")) {
                states.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    states.add(jsonObject1.getString("StateID"));
                    spinnerAdapter_state.add(jsonObject1.getString("StateName"));
                }

                spState.setAdapter(spinnerAdapter_state);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void post_city(String stateID) {

        Map<String, String> params = new HashMap<>();
        params.put("StateID",stateID);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                CITY, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseCity(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),getString(R.string.api_fail), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        jsonObjReq.setTag("City");
        requestQueue.add(jsonObjReq);
    }

    private void parseCity(JSONObject jsonObject) {
        try {
            if (jsonObject.getBoolean("isSuccess")) {
                city.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    city.add(jsonObject1.getString("CityID"));
                    spinnerAdapter_city.add(jsonObject1.getString("CityName"));
                }
                if (city.size() > 0) {
                    post_area(city.get(0));
                }
                spCity.setAdapter(spinnerAdapter_city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void post_area(String cityID) {

        Map<String, String> params = new HashMap<>();
        params.put("CityID",cityID);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                AREA, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseArea(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),getString(R.string.api_fail), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        jsonObjReq.setTag("Area");
        requestQueue.add(jsonObjReq);
    }

    private void parseArea(JSONObject jsonObject) {
        try {
            if (jsonObject.getBoolean("isSuccess")) {
                area.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    area.add(jsonObject1.getString("AreaID"));
                    spinnerAdapter_area.add(jsonObject1.getString("AName"));
                }

                spArea.setAdapter(spinnerAdapter_area);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void post_getVictimDetails() {

        itemsList.clear();

        Map<String, String> params = new HashMap<>();
        params.put("AreaID",areaID);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GET_VICTIM_DETAILS, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseVictims(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),getString(R.string.api_fail), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        jsonObjReq.setTag("VictimDetails");
        requestQueue.add(jsonObjReq);
    }

    private void parseVictims(JSONObject jsonObject) {
        try {
            if (jsonObject.getBoolean("isSuccess")) {
                area.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    VictimsHelpModel victimsHelpModel = new VictimsHelpModel();
                    victimsHelpModel.setVHMapID(jsonObject1.getInt("VHMapID"));
                    victimsHelpModel.setVictimID(jsonObject1.getInt("VictimID"));
                    victimsHelpModel.setHelpID(jsonObject1.getInt("HelpID"));
                    victimsHelpModel.setAreaID(jsonObject1.getInt("AreaID"));
                    victimsHelpModel.setDescription(jsonObject1.getString("Description"));
                    victimsHelpModel.setMembers(jsonObject1.getString("Members"));
                    victimsHelpModel.setMale(jsonObject1.getString("Male"));
                    victimsHelpModel.setFemale(jsonObject1.getString("Female"));
                    victimsHelpModel.setChildren(jsonObject1.getString("Children"));
                    victimsHelpModel.setLatitude(jsonObject1.getString("latitude"));
                    victimsHelpModel.setLongitude(jsonObject1.getString("longitude"));
                    victimsHelpModel.setPhoneNo(jsonObject1.getString("PhoneNo"));
                    victimsHelpModel.setCreatedOn(jsonObject1.getString("createdOn"));
                    victimsHelpModel.setUpdatedOn(jsonObject1.getString("updatedOn"));
                    itemsList.add(victimsHelpModel);
                }
                setMapMarkers();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setMapMarkers() {
        for (int i=0; i<itemsList.size(); i++) {
            VictimsHelpModel victimsHelpModel = (VictimsHelpModel) itemsList.get(i);

            int helpID = victimsHelpModel.getHelpID();
            String helpName;
            if (helpID == 1) {
                helpName = "Food";
            } else if (helpID == 2) {
                helpName = "Clothes";
            } else {
                helpName = "Shelter";
            }
            BitmapDescriptor bitmapDescriptor = null;
            if (helpName.equalsIgnoreCase("food")) {
                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            } else if (helpName.equalsIgnoreCase("clothes")) {
                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
            } else {
                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            }

            LatLng latlng = new LatLng(Double.parseDouble(victimsHelpModel.getLatitude()),Double.parseDouble(victimsHelpModel.getLongitude()));
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .title("Required - " + helpName)
                    .snippet(
                            "Description - " + victimsHelpModel.getDescription()+"\n"
                            + "Members - " + victimsHelpModel.getMembers()+"\n"
                            + "Male - " + victimsHelpModel.getMale()+"\n"
                            + "Female - " + victimsHelpModel.getFemale()+"\n"
                            + "Children - " + victimsHelpModel.getChildren()+"\n"
                            + "Date - " + UTCToIST(victimsHelpModel.getCreatedOn())+"\n"
                            + "Phone No - " + victimsHelpModel.getPhoneNo()
                    )
                    .position(latlng)
                    .icon(bitmapDescriptor)
            );
            marker.setTag(victimsHelpModel.getVHMapID());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMaps();
        initMapDetails();
        post_getVictimDetails();
    }

    private void setMaps() {
        mMap.setMyLocationEnabled(true);
        CameraUpdate center = CameraUpdateFactory.newLatLng(
                new LatLng(Double.parseDouble(db.userDao().getLatitude()),
                        Double.parseDouble(db.userDao().getLongitude())
                )
        );
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "You need to install Google Play Services to use the App properly", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }
            return false;
        }
        return true;
    }
}