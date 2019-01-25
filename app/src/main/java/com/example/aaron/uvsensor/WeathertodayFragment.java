package com.example.aaron.uvsensor;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aaron.uvsensor.Model.OpenWeatherMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeathertodayFragment extends Fragment implements LocationListener {

    TextView txtCity, txtLastUpdate, txtDescription, txtHumidity, txtTime, txtCelsius;
    ImageView imageview;

    LocationManager locationManager;
    String provider;
    static double lat, lng;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    int MY_PERMISSION = 0;

    public WeathertodayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        txtCity = (TextView)  view.findViewById(R.id.txtCit);
        //txtLastUpdate = (TextView)  view.findViewById(R.id.txtLastUpdate);
        txtDescription = (TextView)  view.findViewById(R.id.txtDescription);
        txtHumidity = (TextView)  view.findViewById(R.id.txtHumidity);
        txtTime = (TextView)  view.findViewById(R.id.txtTime);
        txtCelsius = (TextView)  view.findViewById(R.id.txtCelsius);
        imageview = (ImageView) view.findViewById(R.id.imageView);


        //Get Coordinates
        //this line might cause some errors: https://stackoverflow.com/questions/24427414/getsystemservices-is-undefined-when-called-in-a-fragment
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);


        if (ActivityCompat.checkSelfPermission((MainActivity)getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((MainActivity)getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((MainActivity)getActivity(),new String[] {

                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            },MY_PERMISSION);
        }
        Location location = locationManager.getLastKnownLocation(provider);


        return view;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
