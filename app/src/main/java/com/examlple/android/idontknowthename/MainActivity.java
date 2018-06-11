package com.examlple.android.idontknowthename;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,LocationEngineListener,PermissionsListener,MapboxMap.OnMapClickListener {
    private MapView mapView;
    private MapboxMap map;
    private Button startButton;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private Point originPosition;
    private Point destinationPosition;
    private Marker destinationMarker;
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView=findViewById(R.id.mapView);
        startButton=findViewById(R.id.startbutton);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
            map=mapboxMap;
            enableLocation();
    }
    private void enableLocation(){
        if(permissionsManager.areLocationPermissionsGranted(this)){
            initializeLocationEngine();
            initializeLocationLayer();
        }
        else{
            permissionsManager=new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
        Log.d(TAG,"enable Location");
    }
    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine(){

        locationEngine= new LocationEngineProvider(this).obtainBestLocationEngineAvailable();


        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        Location lastLocation=locationEngine.getLastLocation();
            if(lastLocation!=null){
                originLocation=lastLocation;
                setCameraPosition(lastLocation);
            }
            else{
                locationEngine.addLocationEngineListener(this);
            }
            Log.d(TAG,"initialize location Engine");
    }


    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer(){
            locationLayerPlugin=new LocationLayerPlugin(mapView,map,locationEngine);
            locationLayerPlugin.setLocationLayerEnabled(true);
            locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
            locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
        ;
    }

    private void setCameraPosition(Location location){
        Log.d(TAG,"settingcamers ");
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),100.0));
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
            destinationMarker=map.addMarker(new MarkerOptions().position(point));

            destinationPosition=Point.fromLngLat(point.getLongitude(),point.getLatitude());
            originPosition=Point.fromLngLat(originLocation.getLongitude(),originLocation.getLatitude());

            startButton.setEnabled(true);
            startButton.setBackgroundResource(R.color.mapboxBlue);
        Log.d(TAG,"clicking on the map");
    }

    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }


    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
            originLocation=location;
            Log.d(TAG,"on location changed");
            setCameraPosition(location);
        }

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocation();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onStart(){
        super.onStart();
        mapView.onStart();
        Log.d(TAG,"onstart");
    }
    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
        Log.d(TAG,"onResume");
    }
    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
        Log.d(TAG,"onPause");
    }
    @Override
    protected void onStop(){
        super.onStop();
        mapView.onStop();
        Log.d(TAG,"onStop");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        Log.d(TAG,"onSavedInstance");
    }
    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
        Log.d(TAG,"OnLowmemory");
    }
    protected void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
        Log.d(TAG,"OnDestroy");
    }



}
