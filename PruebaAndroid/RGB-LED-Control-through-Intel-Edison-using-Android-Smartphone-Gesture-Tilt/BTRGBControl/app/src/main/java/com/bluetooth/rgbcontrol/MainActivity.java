package com.bluetooth.rgbcontrol;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;

    private boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(DEBUG) Log.d(TAG, "MainActivity() onCreate()");

        buildGoogleApiClient();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            BluetoothFragment fragment = new BluetoothFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();

        }
    }

    /*
     * Creating GoogleApiClient Object for Analytics
     */
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mGoogleApiClient.isConnected())
        {
            Log.d(TAG, "MainActivity() onStart() GoogleAPiClient connected");
            mGoogleApiClient.connect();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(DEBUG) Log.d(TAG, "MainActivity() onResume()");

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(DEBUG) Log.d(TAG, "MainActivity() onStop()");

    }

    @Override
    protected void onDestroy() {

        if(DEBUG) Log.d(TAG, "MainActivity() onDestroy()");

        if(mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
            Log.d(TAG, "MainActivity() onStart() GoogleAPiClient disconnected");
        }
        super.onDestroy();

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
