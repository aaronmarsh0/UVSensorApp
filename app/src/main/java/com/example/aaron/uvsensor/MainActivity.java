package com.example.aaron.uvsensor;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    public byte[] mdatabyte ;
    public String mdata = "";

    //private static final UUID UV_SERVICE = UUID.fromString("UUID OF THIS SERVICE");
    private final UUID UV_SERVICE = convertFromInteger(0x1810);
    //private static final UUID UV_DATA_CHARACTERISTIC = UUID.fromString("UUID OF CHARACTERISTIC");
    private final UUID UV_DATA_CHARACTERISTIC = convertFromInteger(0x2A49);
    //private static final UUID UV_CONFIG_CHARACTERISTIC = UUID.fromString("UUID OF CHARACTERISTIC");
    private final UUID  UV_CONFIG_CHARACTERISTIC = convertFromInteger(0x2A39);
    private final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = convertFromInteger(0x2902);


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private DataFragment datafragment;



    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

    public UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.main_container);

        datafragment = new DataFragment();

        //side navigation bar
        mToolbar =(Toolbar) findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                switch(menuItem.getItemId()){
                    case R.id.weather:
//                        mDrawerLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        mViewPager.setCurrentItem(1);
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.data:
//                        mDrawerLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        //setFragment(datafragment);
                        mViewPager.setCurrentItem(2);
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.home:
                        mViewPager.setCurrentItem(0);
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.settings:
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        //https://stackoverflow.com/questions/33064159/how-to-start-an-activity-on-material-navigation-drawer-menu-item-clicked
                        startActivity(intent);
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });


        setupViewPager(mViewPager);

        //PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences,false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref.getBoolean(SettingsActivity.KEY_PREF_EXAMPLE_SWITCH, false);
        Toast.makeText(this,switchPref.toString(), Toast.LENGTH_SHORT).show();

        //bluetooth connection
        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes Bluetooth adapter
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();



    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        Toast.makeText(this, "In onResume", Toast.LENGTH_SHORT).show();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<ScanFilter>();
            }
            scanLeDevice(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "In onpause", Toast.LENGTH_SHORT).show();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }

    @Override
    protected void onDestroy() {
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
        super.onDestroy();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawerLayout, fragment, "MY_FRAGMENT");
        fragmentTransaction.commit();
    }

    private void setupBluetooth(){
        BluetoothAdapter mBluetoothAdapter;
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //could be error
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanLeDevice(final boolean enable) {
        Toast.makeText(this, "In scanLeDevice", Toast.LENGTH_SHORT).show();
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mLEScanner.stopScan(mScanCallback);

                    }
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(mScanCallback);
            }
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.e("callbackType", "MAX " + String.valueOf(callbackType));
            Log.e("result", "MAX " + result.toString());
            BluetoothDevice btDevice = result.getDevice();
            connectToDevice(btDevice);
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.e("ScanResult - Results", "MAX " + sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "MAX " + "Error Code: " + errorCode);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("onLeScan", "MAX " + device.toString());
                            connectToDevice(device);
                        }
                    });
                }
            };

    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false);// will stop after first device detection
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.e("onConnectionStateChange", "MAX " + "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.e("gattCallback", "MAX " + "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "MAX " + "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "MAX " + "STATE_OTHER");
            }

        }

//        @Override
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            List<BluetoothGattService> services = gatt.getServices();
//            Log.e("onServicesDiscovered", "MAX " + services.toString());
//
//            /*
//            for(int i = 0; i < services.size(); i++){
//                for (int j = 0; j < services.get(i).getCharacteristics().size(); j++){
//                    Log.e("MAX", "i= " + i + " j= " + j);
//                    gatt.readCharacteristic(services.get(i).getCharacteristics().get
//                            (j));
//                }
//            }
//            */
//
//            gatt.readCharacteristic(services.get(0).getCharacteristics().get(0));
//        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){
            Toast.makeText(getApplicationContext(), "In onservicesdescoverd", Toast.LENGTH_SHORT).show();
            BluetoothGattCharacteristic characteristic = gatt.getService(UV_SERVICE).getCharacteristic(UV_CONFIG_CHARACTERISTIC);
            gatt.setCharacteristicNotification(characteristic, true);

            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);

            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            gatt.writeDescriptor(descriptor);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.e("onCharacteristicRead", "MAX " + characteristic.toString());
            Log.e("onCharacteristicRead", "MAX " +characteristic.getStringValue(0));
            //gatt.disconnect();
        }
//        public void getData( BluetoothGatt gatt ){
//            BluetoothGattCharacteristic characterstic;
//            characterstic = gatt.getService(UV_SERVICE).getCharacteristic(UV_DATA_CHARACTERISTIC);
//
//            mGatt.setCharacteristicNotification(characterstic,true);
//            List<BluetoothGattDescriptor> descriptors = characterstic.getDescriptors();
//            BluetoothGattDescriptor descriptor = descriptors.get(1);
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mGatt.writeDescriptor(descriptor);
//
//
//        }
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            mdata = characteristic.getStringValue(0);
            mdatabyte = characteristic.getValue();

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
            BluetoothGattCharacteristic characteristic = gatt.getService(UV_SERVICE).getCharacteristic(UV_DATA_CHARACTERISTIC);

            characteristic.setValue(new byte[]{1,1});
            gatt.writeCharacteristic(characteristic);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "home_fragment");
        adapter.addFragment(new WeatherFragment(), "Weather_Fragment");
        adapter.addFragment(new DataFragment(), "data_Fragment");
        viewPager.setAdapter(adapter);
    }

}
