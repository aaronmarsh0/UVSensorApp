package com.example.aaron.uvsensor;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Toolbar mToolbar;

    private WeatherFragment weatherfragment;
    private DataFragment datafragment;
    private HomeFragment homefragment;

    TextView day1,day2,day3,day4,day5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        day1 = (TextView) findViewById(R.id.uvindex1);
        day2 = (TextView) findViewById(R.id.uvindex2);
        day3 = (TextView) findViewById(R.id.uvindex3);
        day4 = (TextView) findViewById(R.id.uvindex4);
        day5 = (TextView) findViewById(R.id.uvindex5);

        find_weather();

        homefragment = new HomeFragment();
        weatherfragment = new WeatherFragment();
        datafragment = new DataFragment();

        //setFragment(homefragment);

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
                        setFragment(weatherfragment);
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.data:
//                        mDrawerLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        setFragment(datafragment);
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.homepage:
                        setFragment(homefragment);
                        mDrawerLayout.closeDrawers();
                        return true;


                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawerLayout, fragment, "MY_FRAGMENT");
        fragmentTransaction.commit();
    }

    public void find_weather(){

        String url  = "http://api.openweathermap.org/data/2.5/uvi/forecast?appid=00c5a0b19994d9e5770682e74074f710&lat=51.2365&lon=-0.5703&cnt=5";

        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                JSONObject arr = response.getJSONObject(0);
                day1.setText(String.valueOf(arr.getDouble("value")));

                JSONObject arr2 = response.getJSONObject(1);
                day2.setText(String.valueOf(arr2.getDouble("value")));

                JSONObject arr3 = response.getJSONObject(2);
                day3.setText(String.valueOf(arr3.getDouble("value")));

                JSONObject arr4 = response.getJSONObject(3);
                day4.setText(String.valueOf(arr4.getDouble("value")));

                JSONObject arr5 = response.getJSONObject(4);
                day5.setText(String.valueOf(arr5.getDouble("value")));

            }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
