package com.kwolarz.hw2;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    //private List<Point> pointList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       // pointList = new ArrayList<>();
        //pointsOBJ = new Points(new ArrayList<Point>());

        readJSON();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        mMap.setOnMapLongClickListener((GoogleMap.OnMapLongClickListener) this);

        for (Point point : pointsOBJ.points) {
            LatLng sydney = new LatLng(point.getX(), point.getY());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }

    private void readJSON() {
        //FileInputStream fis = null;
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
        //pointList = pointsOBJ.points;


    }
    

    @Override
    protected void onStop() {


        saveToJSON();

        Log.d("LISTA", "" + pointsOBJ.points.get(0).getX());
        super.onStop();
    }



    private void saveToJSON() {
        //Points points = new Points(pointList);

        Gson gson = new Gson();
        String pointJSON = gson.toJson(pointsOBJ);

        String path = this.getFilesDir() + "/" + FILE_NAME;
        File file = new File(path);

        FileOutputStream fos = null;

        Log.d("LISTA", "" + pointsOBJ.points.get(0).getX());

        try {
            //fos = openFileOutput(file);
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
        //Point p = new Point(x, y);
        pointsOBJ.points.add(new Point(x, y));
    }
}
