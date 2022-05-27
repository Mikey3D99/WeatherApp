package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Objects;


public class MainWeatherFragment extends Fragment {
    EditText searchCity;
    TextView result;
    TextView location;
    TextView sky;


    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String key = "ddad710ec049bf1fc59002002f69963d";
    private String units = "metric";
    DecimalFormat decimalFormat = new DecimalFormat("#.#");
    String cityName;
    View view;


    @Override
    public void onStart(){
        super.onStart();
        if(searchCity != null && !searchCity.getText().toString().isEmpty()){
            System.out.println(searchCity.getText().toString());
            cityName = searchCity.getText().toString();
            getWeatherDetails(view);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_main_weather, container, false);

        searchCity = view.findViewById(R.id.search_bar);
        result = view.findViewById(R.id.result);
        location = view.findViewById(R.id.location);
        sky = view.findViewById(R.id.sky);




        int idView = getResources().getIdentifier("btnGet", "id", getContext().getPackageName());
        View buttonView = view.findViewById(idView);
        buttonView.setOnClickListener(this::getWeatherDetails);

        return view;
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
            searchCity.setText(savedInstanceState.getString("searchCity"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putString("searchCity", searchCity.getText().toString());
        outState.putString("temp", result.getText().toString());
        super.onSaveInstanceState(outState);
    }*/

    @SuppressLint("SetTextI18n")
    public void getWeatherDetails(View view) {
        String tempUrl = "";
        String city = searchCity.getText().toString().trim();
        if (city.equals(""))
            result.setText("City field cannot be empty!");

        //complete a url with a city
        tempUrl = url + "?q=" + city + "&appid=" + key + "&units=" + units;
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

                            double temperatureCelcius = jsonObjectMain.getDouble("temp");
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
                            sky.setText(description);



                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                        error -> result.setText("Location does not exist!"));

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

}