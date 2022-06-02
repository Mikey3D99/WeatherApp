package com.example.weatherapp;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MySettingsFragment extends Fragment {
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       view = inflater.inflate(R.layout.fragment_my_settings, container, false);

            // Find the ScrollView


            // Create a LinearLayout element
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));



            // Add Buttons

        try {
            ArrayList<String> allLocations = getLocations();
            for(String location: allLocations){

                LinearLayout oneRow = new LinearLayout(getContext());
                oneRow.setOrientation(LinearLayout.HORIZONTAL);
                oneRow.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                Button temp = new Button(getContext());
                temp.setText(location);
                temp.setOnClickListener(v -> changeCurrentFile(location));

                Button delete = new Button(getContext());
                delete.setText("delete");
                delete.setOnClickListener(v-> deleteLocation(linearLayout, location, oneRow));

                oneRow.addView(temp);
                oneRow.addView(delete);

                linearLayout.addView(oneRow);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
            // Add the LinearLayout element to the ScrollView
            ScrollView scrollView = view.findViewById(R.id.favList);
            scrollView.addView(linearLayout);
        return view;
    }

    public void deleteFile(String filename) throws IOException {
        File path = requireActivity().getApplicationContext().getFilesDir();
        File file = new File(path, filename);
        file.delete();
        if(file.exists()){
            file.getCanonicalFile().delete();
            if(file.exists()){
                requireActivity().getApplicationContext().deleteFile(file.getName());
            }
        }
    }

    public void deleteLocation(LinearLayout view, String location, LinearLayout fieldToRemove){
        try {
            deleteFile(location);
            File path = requireActivity().getApplicationContext().getFilesDir();
            File [] all = path.listFiles();
            String content = null;
            if (all != null) {
                content = readFromFile(all[0].getName());
            }
            if (content != null) {
                writeToFile("current", content);
            }

            view.removeView(fieldToRemove);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeCurrentFile(String newFileName){
        try {
            System.out.println(newFileName);
            String newCurrentContent = readFromFile(newFileName);
            System.out.println(newCurrentContent);
            writeToFile("current", newCurrentContent);

            Toast.makeText(requireActivity().getApplicationContext(),
                    "Changed current location",
                    Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String fileName, String content) throws IOException{
        File path = requireActivity().getApplicationContext().getFilesDir();
        FileOutputStream writer = new FileOutputStream(new File(path, fileName));
        writer.write(content.getBytes());
        writer.close();
    }

    public ArrayList<String> getLocations() throws IOException {
        ArrayList<String> allLocations = new ArrayList<>();
        File path = requireActivity().getApplicationContext().getFilesDir();
        File[] allFiles = path.listFiles();
        if (allFiles != null) {
            for(File x : allFiles){
                if(!x.getName().equals("current"))
                    allLocations.add(x.getName());
            }
        }

        return allLocations;
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