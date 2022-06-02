package com.example.weatherapp;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.Locale;


public class MainWeatherFragment extends Fragment {
    EditText searchCity;
    TextView result;
    TextView location;
    TextView sky;

    DecimalFormat decimalFormat = new DecimalFormat("#.#");
    View view;

    //weather parameters for this fragment
    double temperatureCelsius;
    double temperatureFahrenheit;
    String units = "metric";
    String temperature;
    String description;
    String cityName;
    String country;
    boolean internet;


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

        if(!isNetworkAvailable(requireContext().getApplicationContext())){
            Toast.makeText(requireActivity().getApplicationContext(),
                    "NO INTERNET CONNECTION!",
                    Toast.LENGTH_SHORT).show();
        }


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
                getWeatherDetails();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(" File crash");
            }
        });


        // refresh button
        int refreshID = getResources().getIdentifier("btnRefresh", "id", requireContext().getPackageName());
        View refreshButtonView = view.findViewById(refreshID);
        refreshButtonView.setOnClickListener(this::refreshButton);

        // switch units button
        int unitID = getResources().getIdentifier("switchButton", "id", requireContext().getPackageName());
        View unitButtonView = view.findViewById(unitID);
        unitButtonView.setOnClickListener(this::unitButton);


        return view;
    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }


    public void unitButton(View view){

        if(this.units.equals("metric"))
            this.units = "imperial";
        else if(this.units.equals("imperial"))
            this.units = "metric";

        try {
            getWeatherFromJSON(readFromFile("current"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void refreshButton(View view){
        downloadWeatherData(true);
    }


    public void writeToFile(String fileName, String content) throws IOException{
        File path = requireActivity().getApplicationContext().getFilesDir();
        FileOutputStream writer = new FileOutputStream(new File(path, fileName));
        writer.write(content.getBytes());
        writer.close();
    }

    public String readFromFile(String fileName) throws IOException {
        File path = requireActivity().getApplicationContext().getFilesDir();
        File readFrom = new File(path, fileName);
        FileInputStream stream = new FileInputStream(readFrom);
        byte[] content = new byte[(int)readFrom.length()];
        stream.read(content);
        return new String(content);
    }

    public void downloadWeatherData(boolean isRefresh){

        if(!isRefresh)
            cityName = searchCity.getText().toString().trim();
        String url = "https://api.openweathermap.org/data/2.5/weather";
        String key = "ddad710ec049bf1fc59002002f69963d";
        String units = "metric";
        String tempUrl = url + "?q=" + cityName + "&appid=" + key + "&units=" + units;

        //complete a url with a city
        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, tempUrl, response -> {
                    try{

                        JSONObject jsonResponse = new JSONObject(response);
                        cityName = jsonResponse.getString("name");


                        writeToFile(cityName.toLowerCase(Locale.ROOT), response);
                        writeToFile("current", response);
                        getWeatherFromJSON(response);
                        Toast.makeText(requireActivity().getApplicationContext(),
                                "Location Saved",
                                Toast.LENGTH_SHORT).show();

                    }catch (IOException | JSONException e){
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
            temperatureFahrenheit = this.temperatureCelsius * 1.8 + 32;

            JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
            country = jsonObjectSys.getString("country");

            //if(!temp.contains("Voivodeship") || cityName == null)
            cityName = jsonResponse.getString("name");

            //putting retrieved data into textViews
            if(this.units.equals("metric"))
                temperature = decimalFormat.format(temperatureCelsius) + "°C";
            else if(this.units.equals("imperial"))
                temperature = decimalFormat.format(temperatureFahrenheit) + "F";

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
    public void getWeatherDetails()throws IOException{
        //showAllFiles();
        downloadWeatherData(false);
        getWeatherFromJSON(readFromFile("current"));
    }
}