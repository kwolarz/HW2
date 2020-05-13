package com.kwolarz.hw2;

import androidx.fragment.app.FragmentActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, SensorEventListener {

    private static final String FILE_NAME = "storage.json";

    private GoogleMap mMap;
    private SensorManager sensorManager;
    private Points pointsOBJ = new Points();

    private TextView accTextView;
    private FloatingActionButton accFab;
    private FloatingActionButton pointFab;

    private double accX, accY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        readJSON();

        accTextView = findViewById(R.id.accTextView);
        accTextView.setVisibility(View.INVISIBLE);

        accFab = findViewById(R.id.accFab);
        accFab.setVisibility(View.INVISIBLE);
        pointFab = findViewById(R.id.pointFab);
        pointFab.setVisibility(View.INVISIBLE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener((GoogleMap.OnMapLongClickListener) this);
        mMap.setOnMarkerClickListener(this);

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

        this.findViewById(R.id.zoomInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        this.findViewById(R.id.zoomOutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

        accFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accText = "Acceleration: \nx: " + accX + " y: " + accY;
                accTextView.setText(accText);

                if(accTextView.getVisibility() == View.INVISIBLE)
                    accTextView.setVisibility(View.VISIBLE);
                else accTextView.setVisibility(View.INVISIBLE);
            }
        });

        pointFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accFab.setVisibility(View.INVISIBLE);
                pointFab.setVisibility(View.INVISIBLE);
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

    private double roundACC(double number) {
        number = Math.round(number * 10000);
        number /= 10000;

        return number;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.d("MARKER", ""+marker.getPosition());
        marker.showInfoWindow();
//
//        double x = roundACC(marker.getPosition().latitude);
//        double y = roundACC(marker.getPosition().longitude);
//        String accText = "Acceleration: \nx: " + x + " y: " + y;
//
//        accTextView.setText(accText);
//        accTextView.setVisibility(View.VISIBLE);

        accFab.setVisibility(View.VISIBLE);
        pointFab.setVisibility(View.VISIBLE);

        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accX = event.values[0];
            accY = event.values[1];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
