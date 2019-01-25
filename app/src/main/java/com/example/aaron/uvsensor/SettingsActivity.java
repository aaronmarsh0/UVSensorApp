package com.example.aaron.uvsensor;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SettingsActivity extends AppCompatActivity {
    private Button startScanbutton;
    private Button updateButton;
    private TextView value;
    public static final String KEY_PREF_EXAMPLE_SWITCH = "example_switch";
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2;
    private boolean mScanning;
    private Map<String, BluetoothDevice> mScanResults;
    private ScanCallback mScanCallback;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    private BluetoothGatt mGatt;
    private static final int  SCAN_PERIOD = 10000;
    private final UUID SERVICE_UUID = UUID.fromString("12345678-9012-3456-7890-1234567890FF");
    private final UUID CHARACTERISTIC_UUID = UUID.fromString("12345678-9012-3456-7890-123456789022");
    private boolean mConnected;
    private boolean mInitialised;
    private static final String TAG = "BluetoothConnection ";
    BluetoothDevice mBluetoothDevice;
    BluetoothGattCharacteristic mCharacteristic;

    private ViewPager mviewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acvivity_settings);
//        Intent intent = getIntent();
//        getSupportFragmentManager().beginTransaction()
//                .replace(android.R.id.content, new SettingsFragment())
//                .commit();
//        mviewPager = (ViewPager) findViewById(R.id.settingspage);
        startScanbutton = (Button) findViewById(R.id.Start_Scan_button);
        updateButton = (Button) findViewById(R.id.update_value);
        value = (TextView) findViewById(R.id.tempdisplay);
        startScanbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if (hasPermissions()) {


                    mScanResults = new HashMap<>();
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice("00:1E:C0:7C:0B:DB");
                    mScanResults.put("UV_Device", mBluetoothDevice);
                    connectDevice(mScanResults.get("UV_Device"));
                    Log.d(TAG, "bluetooth name: " + mScanResults.get("UV_Device").getName());
                    Log.d(TAG, "start button pressed");
                } else {
                    Log.d(TAG, "Permission denied");
                }

            }
    });

        updateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mGatt.readCharacteristic(mCharacteristic);
            }
        });



        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        Log.d(TAG, "got bluetooth adapter" + mBluetoothAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            finish();
        }

    }

    private void startScan(){
        if(!hasPermissions() || mScanning){
            return;
        }
        Log.d(TAG,"Started scanning...");
        mScanResults = new HashMap<>();
        mScanCallback = new BtleScanCallback(mScanResults);
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        //mBluetoothLeScanner.startScan(filters,settings,mScanCallback);
        mBluetoothLeScanner.startScan(mScanCallback);
        mScanning = true;
        mHandler = new Handler();
        mHandler.postDelayed(this::stopScan, SCAN_PERIOD);
    }

    private void stopScan(){
        if (mScanning && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothLeScanner != null){
            mBluetoothLeScanner.stopScan(mScanCallback);
            scanComplete();
        }

        mScanCallback = null;
        mScanning = false;
        mHandler = null;
        Log.d(TAG, "stopped scanning...");
    }

    private void scanComplete(){
        Log.d(TAG,"Scan complete...");
        Log.d(TAG,mScanResults.toString());
        if (mScanResults.isEmpty()){
            Log.d(TAG,"Scan results are empty..");
            return;
        }
        for (String deviceAddress : mScanResults.keySet()){
            Log.d(TAG,"Found Device: " + deviceAddress);
        }
    }

    private class BtleScanCallback extends ScanCallback {

        //private Map<String, BluetoothDevice> mScanResults;

        BtleScanCallback(Map<String, BluetoothDevice> scanResults) {
            mScanResults = scanResults;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result){
            addScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results){
            for (ScanResult result : results){
                addScanResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode){
            Log.e(TAG, "BLE Scan Failed with code " + errorCode);
        }
//        private void addScanResult(ScanResult result){
//            BluetoothDevice device = result.getDevice();
//            String deviceAddress = device.getAddress();
//            mScanResults.put(deviceAddress,device);
//        }
        private void addScanResult(ScanResult result){
            stopScan();
            BluetoothDevice bluetoothDevice = result.getDevice();
            connectDevice(bluetoothDevice);
        }
    }

    private void connectDevice(BluetoothDevice device){
        GattClientCallback gattClientCallback = new GattClientCallback();
        mGatt = device.connectGatt(this,false,gattClientCallback);
        Log.d(TAG, "Connecting device: " +mGatt.toString());
    }

    private class GattClientCallback extends BluetoothGattCallback{
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){
            if (status == BluetoothGatt.GATT_FAILURE){
                disconnectGattServer();
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS){
                disconnectGattServer();
                return;
            }
            if (newState == BluetoothProfile.STATE_CONNECTED){
                mConnected = true;
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                disconnectGattServer();
            }
        }
        public void disconnectGattServer(){
            mConnected = false;
            if(mGatt != null){
                mGatt.disconnect();
                mGatt.close();
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status){
            super.onServicesDiscovered(gatt,status);
            if (status != BluetoothGatt.GATT_SUCCESS){
                Log.d(TAG, "GATT unsuccessful");
                return;
            }

            BluetoothGattService service = gatt.getService(SERVICE_UUID);
            Log.d(TAG, "onServiceDiscovered: " +service);
            mCharacteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
//            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
//            mInitialised = gatt.setCharacteristicNotification(characteristic, true);
            if (gatt.readCharacteristic(mCharacteristic)) {
                Log.d(TAG, "readCharacteristic returned true, uuid: " + mCharacteristic.getUuid());

            }else {
                Log.d(TAG, "readCharacteristic returned false");
            }
            Log.d(TAG, "Value: " + mCharacteristic.getValue());
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
            Log.d(TAG, "onCharacteristic changed");
            super.onCharacteristicChanged(gatt,characteristic);
            byte[] messageBytes = characteristic.getValue();
            String messageString = null;
            try{
                messageString = new String(messageBytes,"ISO-8859-1");
            } catch(UnsupportedEncodingException e){
                Log.e(TAG, "unable to convert message from bytes to string");
            }
            Log.d(TAG,"Received message: " + messageString);
        }

        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status){

            if (status == BluetoothGatt.GATT_SUCCESS){
                Log.d(TAG, "onCharacteristicRead = " + characteristic.getUuid() + ", " + characteristic.getValue());
                byte[] messageBytes = characteristic.getValue();
                String messageString = null;
                try{
                    messageString = new String(messageBytes,"US-ASCII");
                } catch(UnsupportedEncodingException e){
                    Log.e(TAG, "unable to convert message from bytes to string");
                }
                Log.d(TAG,"Received message: " + messageString);
                value.setText(messageString);

            }
        }
    }
    //potentially not needed
//    private class GattServerCallback extends BluetoothGattServerCallback {
//        @Override
//        public void onConnectedStateChange(BluetoothDevice device, int status, int newStatus){
//            super.onConnectionStateChange(device,status,newStatus);
//            if(newStatus == BluetoothProfile.STATE_CONNECTED){
//                mDevices.add(device));
//            } else if (newStatus == BluetoothProfile.STATE_DISCONNECTED){
//                mDevices.remove(device));
//            }
//        }
//    }
    private boolean hasPermissions(){
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            requestBluetoothEnable();
            return false;
        }else if (!hasLocationPermissions()){
            requestLocationPermission();
            return false;
        }
        return true;
    }

    private void requestBluetoothEnable(){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
    }

    private boolean hasLocationPermissions(){
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission(){
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
    }

    public UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

    public void readvalue(BluetoothGattCharacteristic gattCharacteristic){
        byte[] messageBytes = gattCharacteristic.getValue();
        String messageString = null;
        try{
            messageString = new String(messageBytes,"UTF-8");
        } catch(UnsupportedEncodingException e){
            Log.e(TAG, "unable to convert message from bytes to string");
        }
        Log.d(TAG,"Received message: " + messageString);
    }
}