package com.eilims.danielb.foodfinder;

import android.Manifest;
import android.animation.Animator;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.yelp.clientlib.entities.Business;


public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //When app is instantiated we will...
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Use the activity layout to generate the app UI
        checkPermission(); // Checks if we have access to needs "dangerous" tools
        Button startButton = (Button) findViewById(R.id.start);
        mGoogleApiClient = new GoogleApiClient.Builder(this) // Create the all important googleplay API access port
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect(); //Checks for connection (Go to onConnected method)
        startButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                LocationRequest request = new LocationRequest().create();
                startLocationUpdates(request);
            }
        });

    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                ){//Can add more as per requirement
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, 123);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 123);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("onConnected","Successful Google connection");
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w("onConnectionFailed", "Google API failed to connect");
    }

    public void startLocationUpdates(LocationRequest request){ //Starts location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
    }

    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.i("onLocationChanged", "Location sucessfully changed");
        selectBusiness();
    }

    public void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void selectBusiness(){
        BusinessSelector selectBusiness = new BusinessSelector(mCurrentLocation);
        Business business = selectBusiness.returnBusiness();
        updateUI(business);
        //TODO display stuff
    }

    private void updateUI(Business business){ //Updates the textViews on the main screen
        //TODO Add no GPS signal
        fadeInText();
        TextView name = (TextView) findViewById(R.id.businessName);
        TextView address = (TextView) findViewById(R.id.businessAddress);
        TextView type = (TextView) findViewById(R.id.restaurantType);
        if(business != null) {

            name.setText(business.name());
            String addressString = "";
            for(int i = 0; i< business.location().displayAddress().size(); i++){
                addressString = addressString + " "  + business.location().displayAddress().get(i);
            }
            address.setText(addressString);
            type.setText(business.categories().get(0).name());
        } else {
            name.setText("No GPS Signal :(");
        }

    }

    private void fadeInText(){
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f);
        fadeIn.setDuration(500);
        TextView name = (TextView) findViewById(R.id.textView5);
        TextView address = (TextView) findViewById(R.id.textView6);
        TextView type = (TextView) findViewById(R.id.textView7);

        name.setVisibility(1);

        address.setVisibility(1);
        type.startAnimation(fadeIn);
        type.setVisibility(1);

    }
}
