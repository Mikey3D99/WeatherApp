package com.example.weatherapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.MainWeatherFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class AdditionalInfoFragment extends Fragment {
    View view;
    TextView country;
    TextView location;
    TextView wind;
    TextView humidity;
    TextView pressure;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_additional_info, container, false);

        country = view.findViewById(R.id.country);
        location = view.findViewById(R.id.location);
        wind = view.findViewById(R.id.wind);
        humidity = view.findViewById(R.id.humidity);
        pressure = view.findViewById(R.id.pressure);

        try {
            JSONObject jsonResponse = new JSONObject(readFromFile("current"));

            JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
            JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
            JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");

            String countryName = jsonObjectSys.getString("country");
            String pressure = jsonObjectMain.getString("pressure");
            String humidity = jsonObjectMain.getString("humidity");
            String wind = jsonObjectWind.getString("speed");
            String cityName = jsonResponse.getString("name");

            this.location.setText(cityName);
            this.country.setText(countryName);
            this.pressure.setText("pressure\n" + pressure + "HPa");
            this.humidity.setText("humidity\n" + humidity + "%");
            this.wind.setText("wind speed\n" + wind + "m/s");

        } catch (IOException e) {
            Toast.makeText(requireActivity().getApplicationContext(),
                    "Error while trying to find last file",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (JSONException e) {
            Toast.makeText(requireActivity().getApplicationContext(),
                    "Error while trying to find last file - JSON object",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return view;

    }


    public String readFromFile(String fileName) throws IOException {
        File path = requireActivity().getApplicationContext().getFilesDir();
        File readFrom = new File(path, fileName);
        FileInputStream stream = new FileInputStream(readFrom);
        byte[] content = new byte[(int)readFrom.length()];
        stream.read(content);
        return new String(content);
    }

}