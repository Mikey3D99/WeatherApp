package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    MainWeatherFragment main = new MainWeatherFragment();
    AdditionalInfoFragment additional = new AdditionalInfoFragment();
    MySettingsFragment settings = new MySettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, main).commit();
        bottomNavigationView.setSelectedItemId(R.id.my_nav);


        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch(item.getItemId()){
                case R.id.mainWeatherFragment:
                    getSupportFragmentManager().beginTransaction().replace(R.id.body_container,main).commit();
                    break;
                case R.id.additionalInfoFragment:
                    getSupportFragmentManager().beginTransaction().replace(R.id.body_container,additional).commit();
                    break;
                case R.id.mySettingsFragment:
                    getSupportFragmentManager().beginTransaction().replace(R.id.body_container,settings).commit();
                    break;
            }
            return true;
        });


        //NavController navController = Navigation.findNavController(this,  R.id.fragment);
       // NavigationUI.setupWithNavController(bottomNavigationView, navController);

        /*FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment main = fragmentManager.findFragmentByTag("MainWeatherFragment");
        Fragment additional = fragmentManager.findFragmentByTag("AdditionalInfoFragment");
        Fragment.SavedState mainSaved = fragmentManager.saveFragmentInstanceState(main);
        Fragment.SavedState additionalSaved = fragmentManager.saveFragmentInstanceState(additional);

        main.setInitialSavedState(mainSaved);
        additional.setInitialSavedState(additionalSaved);*/

    }

}