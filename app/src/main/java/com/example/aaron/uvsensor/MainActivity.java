package com.example.aaron.uvsensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    private static final int REQUEST_ENABLE_BT = 0;
    //private static final int REQUEST_DSIABLE_BT = 0;
    public static String[] list_of_uv;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    //initialise fragments for pages
   // private WeatherFragment weatherfragment;
    private DataFragment datafragment;
    //private HomeFragment homefragment;
    private BluetoothAdapter mBluetoothAdapter;


    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;
//    TextView day1,day2,day3,day4,day5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        day1 = (TextView) findViewById(R.id.uvindex1);
//        day2 = (TextView) findViewById(R.id.uvindex2);
//        day3 = (TextView) findViewById(R.id.uvindex3);
//        day4 = (TextView) findViewById(R.id.uvindex4);
//        day5 = (TextView) findViewById(R.id.uvindex5);

//        find_weather();

        mViewPager = (ViewPager) findViewById(R.id.main_container);

//        homefragment = new HomeFragment();
//        weatherfragment = new WeatherFragment();
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
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
//        find_weather();
//        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
//            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
//        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            //could be error
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }

        setupViewPager(mViewPager);
        //queue = Volley.newRequestQueue(this);


    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawerLayout, fragment, "MY_FRAGMENT");
        fragmentTransaction.commit();
    }

//    public void find_weather(){
//
//        String url  = "http://api.openweathermap.org/data/2.5/uvi/forecast?appid=00c5a0b19994d9e5770682e74074f710&lat=51.2365&lon=-0.5703&cnt=5";
//
//        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                list_of_uv = new String[5];
//                try{
//                JSONObject arr = response.getJSONObject(0) ;
//                list_of_uv[0] = (String.valueOf(arr.getDouble("value")));
//
//                JSONObject arr2 = response.getJSONObject(1);
//                list_of_uv[1] = (String.valueOf(arr2.getDouble("value")));
//
//                JSONObject arr3 = response.getJSONObject(2);
//                list_of_uv[2] = (String.valueOf(arr3.getDouble("value")));
//
//                JSONObject arr4 = response.getJSONObject(3);
//                list_of_uv[3] = (String.valueOf(arr4.getDouble("value")));
//
//                JSONObject arr5 = response.getJSONObject(4);
//                list_of_uv[4] = (String.valueOf(arr5.getDouble("value")));
//
//            }catch(JSONException e){
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(jar);
//
//    }



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
//    public RequestQueue getRequestqueue(){
//        return queue;
//    }
}
