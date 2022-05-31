package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;


public class MainWeatherFragment extends Fragment {
    EditText searchCity;
    TextView result;
    TextView location;
    TextView sky;

    DecimalFormat decimalFormat = new DecimalFormat("#.#");
    View view;

    //weather parameters for this fragment
    double temperatureCelsius;
    String temperature;
    String description;
    String cityName;
    String country;


    @Override
    public void onStart(){
        super.onStart();
        if(description!=null && cityName!=null && temperature!=null){
            setWeatherDetails();
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


        try {
            getWeatherFromJSON(readFromFile("current"));

        } catch (IOException e) {
            Toast.makeText(requireActivity().getApplicationContext(),
                    "None locations are saved yet!",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // search button
        int idView = getResources().getIdentifier("btnGet", "id", requireContext().getPackageName());
        View buttonView = view.findViewById(idView);
        buttonView.setOnClickListener(view1 -> {
            try {
                getWeatherDetails(view1);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(" File crash");
            }
        });


        return view;
    }


    public void writeToFile(String fileName, String content) throws IOException{
        File path = requireActivity().getApplicationContext().getFilesDir();
        FileOutputStream writer = new FileOutputStream(new File(path, fileName));
        writer.write(content.getBytes());
        writer.close();
        Toast.makeText(requireActivity().getApplicationContext(),
                "Location Saved",
                Toast.LENGTH_SHORT).show();
    }

    public String readFromFile(String fileName) throws IOException {
        File path = requireActivity().getApplicationContext().getFilesDir();
        File readFrom = new File(path, fileName);
        FileInputStream stream = new FileInputStream(readFrom);
        byte[] content = new byte[(int)readFrom.length()];
        stream.read(content);
        return new String(content);
    }

    public void downloadWeatherData(View view){

        cityName = searchCity.getText().toString().trim();
        String url = "https://api.openweathermap.org/data/2.5/weather";
        String key = "ddad710ec049bf1fc59002002f69963d";
        String units = "metric";
        String tempUrl = url + "?q=" + cityName + "&appid=" + key + "&units=" + units;

        //complete a url with a city
        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, tempUrl, response -> {
                    try{
                        writeToFile(cityName.toLowerCase(Locale.ROOT), response);
                        writeToFile("current", response);
                        getWeatherFromJSON(response);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                },
                        error -> result.setText(R.string.location_error));

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void setWeatherDetails(){
        result.setText(temperature);
        location.setText(cityName);
        sky.setText(description);
    }

    private void getWeatherFromJSON(String jsonString) throws IOException{
        try {

            JSONObject jsonResponse = new JSONObject(jsonString);

            JSONArray jsonArray = jsonResponse.getJSONArray("weather");
            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);

            description = jsonObjectWeather.getString("description");
            JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");

            temperatureCelsius = jsonObjectMain.getDouble("temp");

            JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
            country = jsonObjectSys.getString("country");

            //if(!temp.contains("Voivodeship") || cityName == null)
            cityName = jsonResponse.getString("name");

            //putting retrieved data into textViews
            temperature = decimalFormat.format(temperatureCelsius) + "°C";
            setWeatherDetails();

        }
        catch(JSONException e){
            e.printStackTrace();
            System.out.println("Something wrong with json");
        }
    }

    private void showAllFiles(){
        File path = requireActivity().getApplicationContext().getFilesDir();
        File [] all = path.listFiles();
        assert all != null;
        for(File x: all) {
            System.out.println(x.getName());
            System.out.println(x.getParent());
        }

    }

    @SuppressLint("SetTextI18n")
    public void getWeatherDetails(View view)throws IOException{
        //showAllFiles();
        downloadWeatherData(view);
        getWeatherFromJSON(readFromFile("current"));
    }
}