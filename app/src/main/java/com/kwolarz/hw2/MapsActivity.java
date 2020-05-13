package com.kwolarz.hw2;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String FILE_NAME = "storage.json";

    private GoogleMap mMap;
    private Points pointsOBJ = new Points();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        readJSON();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener((GoogleMap.OnMapLongClickListener) this);

        for (Point point : pointsOBJ.points) {
            LatLng markerLL = new LatLng(point.getX(), point.getY());
            String title = "Position:(" + point.getX() + ", " + point.getY() + ")";
            mMap.addMarker(new MarkerOptions().position(markerLL).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLL));
        }

        this.findViewById(R.id.clearButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointsOBJ.points.clear();
                mMap.clear();
            }
        });
    }

    private void readJSON() {
        Gson gson = new Gson();
        String text = "";

        try {

            String path = this.getFilesDir() + "/" + FILE_NAME;
            File file = new File(path);

            InputStream is = new FileInputStream(file);
            StringBuilder sb = new StringBuilder();

            if (is != null) {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String receiveString = "";
                while ((receiveString = br.readLine()) != null) {
                    sb.append(receiveString);
                }

                is.close();
                text = sb.toString();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Type pointsListType = new TypeToken<Points>(){}.getType();
        pointsOBJ = gson.fromJson(text, pointsListType);
    }
    

    @Override
    protected void onStop() {
        saveToJSON();
        super.onStop();
    }

    private void saveToJSON() {

        Gson gson = new Gson();
        String pointJSON = gson.toJson(pointsOBJ);

        String path = this.getFilesDir() + "/" + FILE_NAME;
        File file = new File(path);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            fos.write(pointJSON.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private double round(double number) {
        number = Math.round(number * 100);
        number /= 100;

        return number;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        double x = round(latLng.latitude);
        double y = round(latLng.longitude);

        String title = "Position:(" + x + ", " + y + ")";

        mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        pointsOBJ.points.add(new Point(x, y));
    }
}
