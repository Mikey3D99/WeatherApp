package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
import android.widget.Toast;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class MainActivity extends FragmentActivity {

    MainWeatherFragment main = new MainWeatherFragment();
    AdditionalInfoFragment additional = new AdditionalInfoFragment();
    MySettingsFragment settings = new MySettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showInfoAboutOldData();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this,  R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


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

    public void showInfoAboutOldData(){
        String test = null;
        try {
            test = readFromFile("current");
        }catch (IOException e){
            e.printStackTrace();
        }
        if (test != null){
            Toast.makeText(getApplicationContext(),
                    "Saved data loaded - click refresh to get new data!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public String readFromFile(String fileName) throws IOException {
        File path = getApplicationContext().getFilesDir();
        File readFrom = new File(path, fileName);
        FileInputStream stream = new FileInputStream(readFrom);
        byte[] content = new byte[(int)readFrom.length()];
        stream.read(content);
        return new String(content);
    }

}