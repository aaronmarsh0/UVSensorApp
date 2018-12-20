package com.example.aaron.uvsensor;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private TextView display;
    private Button refresh_button;
    private TextView bytesdata;
    private TextView Stringdata;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        display = (TextView) view.findViewById(R.id.textView);
        bytesdata = (TextView) view.findViewById(R.id.uv_bytes);
        Stringdata = (TextView) view.findViewById(R.id.uv_string);

        refresh_button = (Button) view.findViewById(R.id.refbutton);

        final MainActivity mainActivity = (MainActivity)getActivity();

        refresh_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Random r = new Random();
                int i1 = r.nextInt(45 - 28) + 28;
                int i2 = r.nextInt(12 - 0) + 12;
                bytesdata.setText(String.valueOf(i1));

//                if(mainActivity.mdata.equals("")){
//                    Stringdata.setText(String.valueOf(i2));
////                    Stringdata.setText("00");
//                }
//                else{
//                    Stringdata.setText(mainActivity.mdata);
//                }
            }
        });

        return view;
}}
