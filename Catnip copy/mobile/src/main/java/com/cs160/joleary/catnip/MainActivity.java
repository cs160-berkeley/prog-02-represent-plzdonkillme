package com.cs160.joleary.catnip;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //there's not much interesting happening. when the buttons are pressed, they start
    //the PhoneToWatchService with the cat name passed in.

    private GoogleApiClient mGoogleApiClient;
    private String mLatitude;
    private String mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    protected void onStart() {
        Log.v("Connecting", "Connecting");
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v("CONNECTED", "CONNECTED");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = String.valueOf(mLastLocation.getLatitude());
            mLongitude = String.valueOf(mLastLocation.getLongitude());
            Log.v("LAT", mLatitude);
            Log.v("LONG", mLongitude);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void useCurrentLocation(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra(getPackageName() + ".input", 0);
        intent.putExtra(getPackageName() + ".lat" , mLatitude);
        intent.putExtra(getPackageName() + ".long", mLongitude);
        startActivity(intent);
    }

    public void useZipCode(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra(getPackageName() + ".input", 1);
        EditText zip_code = (EditText) findViewById(R.id.zip);
        intent.putExtra(getPackageName() + ".zip", zip_code.getText().toString());
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
