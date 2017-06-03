package com.bluetooth.rgbcontrol;

import android.support.annotation.Nullable;
import android.widget.Switch;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothFragment extends Fragment {

    private static final String TAG = "BluetoothFragment";
    private boolean DEBUG = true;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    // Gesture related members
    private Switch mGesture;
    private TextView mAccel;
    private boolean mTimeToSendData = false;
    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;
    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;
    /**
     * Member object for the Bluetooth services
     */
    private BluetoothService mBtService = null;

    //Sensors specific
    private SensorManager mSensorManager = null;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    boolean haveAccelerometer = false;
    boolean haveMagnetometer = false;

    // Data specific
    private int mRoll = 0, mPreRoll = 0, mNextRoll = 0; // degree
    float[] gData = new float[3]; // accelerometer
    float[] mData = new float[3]; // magnetometer
    float[] rMat = new float[9];
    float[] iMat = new float[9];
    float[] orientation = new float[3];


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (DEBUG) Log.d(TAG, "BluetoothFragment() onCreate()");

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            if (DEBUG) Log.d(TAG, "BluetoothFragment() mBluetoothAdapter() == null");

            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();

        }
    }

    /**
     * Event Listener for Sensors
     */
    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    synchronized (getActivity()) {
                        gData = event.values.clone();
                        mAccel.setText("X: " + event.values[0] + "\nY: " + event.values[1] + "\nZ: " + event.values[2]);
                    }
                    // Log.d(TAG, "BluetoothFragment() accel: " + event.values[0] + ", " + event.values[1] + ", " + event.values[2]);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    synchronized (getActivity()) {
                        mData = event.values.clone();
                    }
                    // Log.d(TAG, "BluetoothFragment() magneto: " + event.values[0] + ", " + event.values[1] + ", " + event.values[2]);
                    break;

                default:
                    return;
            }
            if (mTimeToSendData) {
                if (mBtService.getState() == BluetoothService.STATE_CONNECTED) {
                    mNextRoll = doCalculation();
                    if (mNextRoll != mPreRoll)
                        sendMessage(String.valueOf(mNextRoll));
                    mPreRoll = mNextRoll;
                }
            }
        }
    };

    /*
     * Get accelerometer data
     */
    public synchronized float[] getAccelData() {
        return gData;
    }

    /*
     * Get magnetometer data
     */
    public synchronized float[] getMagnetoData() {
        return mData;
    }


    /**
     * Calculate roll angle from accelerometer and magnetometer data
     *
     * @return roll angle or -1 if we get an error while creating RotationMatrix
     */
    public synchronized int doCalculation() {
        if (SensorManager.getRotationMatrix(rMat, iMat, getAccelData(), getMagnetoData())) {
            mRoll = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[2]) + 360) % 360;
            return (mRoll);
        }
        return -1;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG) Log.d(TAG, "BluetoothFragment() onStart()");
        // If BT is not on, request that it be enabled.
        // setupConnection() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the bluetooth connection session
        } else if (mBtService == null) {
            setupConnection();
        }

    }

    @Override
    public void onStop() {
        mSensorManager.unregisterListener(mSensorEventListener);
        super.onStop();
        if (DEBUG) Log.d(TAG, "BluetoothFragment() onStop()");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG) Log.d(TAG, "BluetoothFragment() onDestroy()");

        if (mBtService != null) {
            mBtService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) Log.d(TAG, "BluetoothFragment() onResume()");

        haveAccelerometer = mSensorManager.registerListener(mSensorEventListener, this.mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        haveMagnetometer = mSensorManager.registerListener(mSensorEventListener, this.mMagnetometer, SensorManager.SENSOR_DELAY_UI);

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBtService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBtService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth services
                mBtService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "BluetoothFragment() onCreateView()");

        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mAccel = (TextView) view.findViewById(R.id.accelvalues);
        mGesture = (Switch) view.findViewById(R.id.switch1);
        if (DEBUG) Log.d(TAG, "BluetoothFragment() onViewCreated()");

    }

    /**
     * Set up the connection, call bluetooth service class
     */
    private void setupConnection() {
        if (DEBUG) Log.d(TAG, "BluetoothFragment() setupChat()");

        mGesture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTimeToSendData = isChecked;
            }
        });
        // Initialize the BluetoothService to perform bluetooth connections
        mBtService = new BluetoothService(getActivity(), mHandler);
    }


    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        if (DEBUG) Log.d(TAG, "BluetoothFragment() sendMessage()");
        // Check that we're actually connected before trying anything
        if (mBtService.getState() != BluetoothService.STATE_CONNECTED) {

            if (DEBUG) Log.d(TAG, "BluetoothFragment() sendMessage() not connected");

            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send = message.getBytes();
            mBtService.write(send);

        }
    }

    /**
     * The Handler that gets information back from the BluetoothService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (DEBUG) Log.d(TAG, "BluetoothFragment() mHandler)");

            FragmentActivity activity = getActivity();
            switch (msg.what) {

                case Constants.MESSAGE_DEVICE_NAME:
                    if (DEBUG)
                        Log.d(TAG, "BluetoothFragment() BluetoothService.MESSAGE_DEVICE_NAME)");

                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (DEBUG)
                    Log.d(TAG, "BluetoothFragment() onActivityResult (REQUEST_CONNECT_DEVICE)");

                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;

            case REQUEST_ENABLE_BT:
                if (DEBUG) Log.d(TAG, "BluetoothFragment() onActivityResult (REQUEST_ENABLE_BT)");

                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    if (DEBUG)
                        Log.d(TAG, "BluetoothFragment() onActivityResult, calling setupChat()");

                    // Bluetooth is now enabled, so set up a connection
                    setupConnection();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    if (DEBUG)
                        Log.d(TAG, "BluetoothFragment() onActivityResult (REQUEST_ENABLE_BT, not enabled)");

                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     */
    private void connectDevice(Intent data) {
        if (DEBUG) Log.d(TAG, "BluetoothFragment() connectDevice()");

        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBtService.connect(device);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (DEBUG) Log.d(TAG, "BluetoothFragment() onCreateOptionsMenu()");

        inflater.inflate(R.menu.bluetooth_scan, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect_scan: {
                if (DEBUG)
                    Log.d(TAG, "BluetoothFragment() onOptionsItemSelected( secure_connect_scan)");
                if(mGesture.isChecked())
                    mGesture.setChecked(false);

                if ((mBtService.getState() == BluetoothService.STATE_CONNECTED) || (mBtService.getState() == BluetoothService.STATE_CONNECTING)) {
                    mBtService.stop();
                }
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            }
        }
        return false;
    }

}