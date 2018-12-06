package com.example.aaron.uvsensor;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    private static final String TAG = "weather_Fragment";

    private TextView day1;
    private TextView day2;
    private TextView day3;
    private TextView day4;
    private TextView day5;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather,container,false);
        day1 = (TextView) view.findViewById(R.id.uvindex1);
        day2 = (TextView) view.findViewById(R.id.uvindex2);
        day3 = (TextView) view.findViewById(R.id.uvindex3);
        day4 = (TextView) view.findViewById(R.id.uvindex4);
        day5 = (TextView) view.findViewById(R.id.uvindex5);
        Log.d(TAG, "onCreateView: started.");

//        day1.setText(MainActivity.list_of_uv[0]);
//        day2.setText(MainActivity.list_of_uv[1]);
//        day3.setText(MainActivity.list_of_uv[2]);
//        day4.setText(MainActivity.list_of_uv[3]);
//        day5.setText(MainActivity.list_of_uv[4]);

        find_weather();
        //return inflater.inflate(R.layout.fragment_weather, container, false);
        return view;
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

        RequestQueue queue = Volley.newRequestQueue(((MainActivity)getActivity()).getApplicationContext());
        queue.add(jar);

    }

}
