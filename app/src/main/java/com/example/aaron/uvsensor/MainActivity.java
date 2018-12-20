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

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;

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

//    private static final int REQUEST_ENABLE_BT = 1;
//    private BluetoothAdapter mBluetoothAdapter;
//    private Handler mHandler;
//    private static final long SCAN_PERIOD = 10000;
//    private BluetoothLeScanner mLEScanner;
//    private ScanSettings settings;
//    private BluetoothDevice myDevice;
//    private List<ScanFilter> filters;
//    private BluetoothGatt mGatt;
//    public byte[] mdatabyte ;
//    public String mdata = "";
//    private String BLUETOOTH_SENSOR_ADDRESS;
//
//    //private static final UUID UV_SERVICE = UUID.fromString("UUID OF THIS SERVICE");
//    private final UUID UV_SERVICE = convertFromInteger(0x1810);
//    //private static final UUID UV_DATA_CHARACTERISTIC = UUID.fromString("UUID OF CHARACTERISTIC");
//    private final UUID UV_DATA_CHARACTERISTIC = convertFromInteger(0x2A49);
//    //private static final UUID UV_CONFIG_CHARACTERISTIC = UUID.fromString("UUID OF CHARACTERISTIC");
//    private final UUID  UV_CONFIG_CHARACTERISTIC = convertFromInteger(0x2A39);
//    private final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = convertFromInteger(0x2902);



    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private DataFragment datafragment;



    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

//    public UUID convertFromInteger(int i) {
//        final long MSB = 0x0000000000001000L;
//        final long LSB = 0x800000805f9b34fbL;
//        long value = i & 0xFFFFFFFF;
//        return new UUID(MSB | (value << 32), LSB);
//    }


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


//        // Initializes Bluetooth adapter
//        final BluetoothManager bluetoothManager =
//                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent =
//                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//
//        BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
//            @Override
//            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//                if (device.getAddress().equals(BLUETOOTH_SENSOR_ADDRESS)) {
//                    myDevice = device;
//                }
//
//
//            }
//        };
//        mBluetoothAdapter.startLeScan(scanCallback);
//
//        BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
//            @Override
//            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//                super.onConnectionStateChange(gatt, status, newState);
//                if(newState == STATE_CONNECTED){
//                    gatt.discoverServices();
//                }
//            }
//
//            @Override
//            public void onServicesDiscovered(BluetoothGatt gatt, int status){
//
//                BluetoothGattCharacteristic characteristic =
//                        gatt.getService(UV_SERVICE)
//                                .getCharacteristic(UV_DATA_CHARACTERISTIC);
//
//                BluetoothGattDescriptor descriptor =
//                        characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
//
//                descriptor.setValue(
//                        BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt.writeDescriptor(descriptor);
//            }
//
//        };

    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        // Ensures Bluetooth is available on the device and it is enabled. If not,
//        // displays a dialog requesting user permission to enable Bluetooth.
//        Toast.makeText(this, "In onResume", Toast.LENGTH_SHORT).show();
//
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        } else {
//            if (Build.VERSION.SDK_INT >= 21) {
//                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
//                settings = new ScanSettings.Builder()
//                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                        .build();
//                filters = new ArrayList<ScanFilter>();
//            }
//            scanLeDevice(true);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Toast.makeText(this, "In onpause", Toast.LENGTH_SHORT).show();
//        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
//            scanLeDevice(false);
//        }
//    }

//    @Override
//    protected void onDestroy() {
//        if (mGatt == null) {
//            return;
//        }
//        mGatt.close();
//        mGatt = null;
//        super.onDestroy();
//    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawerLayout, fragment, "MY_FRAGMENT");
        fragmentTransaction.commit();
    }


    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "home_fragment");
        adapter.addFragment(new WeatherFragment(), "Weather_Fragment");
        adapter.addFragment(new DataFragment(), "data_Fragment");
        viewPager.setAdapter(adapter);
    }

}
