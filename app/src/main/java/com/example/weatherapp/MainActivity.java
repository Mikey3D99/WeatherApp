package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    EditText searchCity;
    TextView result;
    TextView location;

    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String key = "ddad710ec049bf1fc59002002f69963d";
    DecimalFormat decimalFormat = new DecimalFormat("#.#");
    View myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchCity = findViewById(R.id.search_bar);
        result = findViewById(R.id.result);
        location = findViewById(R.id.location);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this,  R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    public void getWeatherDetails(View view) {
        String tempUrl = "";
        String city = searchCity.getText().toString().trim();
        if (city.equals(""))
            result.setText("City field cannot be empty!");

        //complete a url with a city
        tempUrl = url + "?q=" + city + "&appid=" + key;
        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, tempUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String output = "";
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                            String description = jsonObjectWeather.getString("description");
                            JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");

                            double temperatureCelcius = jsonObjectMain.getDouble("temp") - 273.15;
                            float pressure = jsonObjectMain.getInt("pressure");
                            int humidity = jsonObjectMain.getInt("humidity");

                            JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                            String wind = jsonObjectWind.getString("speed");
                            JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                            String clouds = jsonObjectClouds.getString("all");
                            JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                            String countryName = jsonObjectSys.getString("country");
                            String cityName = jsonResponse.getString("name");

                            //putting retrieved data into textViews
                            String temperature = decimalFormat.format(temperatureCelcius) + "°C";
                            result.setText(temperature);
                            location.setText(cityName);



                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                        error -> result.setText(error.toString().trim()));

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}