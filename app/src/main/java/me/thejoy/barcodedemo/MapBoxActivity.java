package me.thejoy.barcodedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.Constants;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.core.constants.Constants.PRECISION_6;

public class MapBoxActivity extends AppCompatActivity {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private double lat,lon;
    private FeatureCollection dashedLineDirectionsFeatureCollection;
    private String TAG = "TAG: ";
    private DirectionsRoute currentRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_box);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {

                MapBoxActivity.this.mapboxMap = mapboxMap;


                ArrayList<LatLng> markerPoints = new ArrayList<>();
                markerPoints.add(new LatLng(23.8163566156554,90.4292010515928));
                markerPoints.add(new LatLng(23.801423132818,90.423213019967));
                markerPoints.add(new LatLng(23.7802951075657,90.4215349629521));
                markerPoints.add(new LatLng(23.8011906079439,90.4244961217045));
                markerPoints.add(new LatLng(23.7808577970747,90.422443896532));
                markerPoints.add(new LatLng(23.7727289369093,90.4085939750075));
                markerPoints.add(new LatLng(23.7794305122882,90.4068066179752));
                markerPoints.add(new LatLng(23.7419249264593,90.410323664546));
                markerPoints.add(new LatLng(23.7529152054911,90.3832893446087));

                for (int i=0;i<markerPoints.size();i++){
                    mapboxMap.addMarker(new MarkerOptions().position(markerPoints.get(i)));
                }


                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .zoom(12)
                        .build());

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });



                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {

                        IconFactory mIconFactory = IconFactory.getInstance(MapBoxActivity.this);
                        Icon icon = mIconFactory.fromResource(R.drawable.m);

                        marker.setIcon(icon);

                        getRoute(Point.fromLngLat(marker.getPosition().getLongitude(),marker.getPosition().getLatitude()));
                        return true;
                    }
                });

                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {

//                        mapboxMap.addMarker(new MarkerOptions()
//                                .position(point));
//
//                        ArrayList<LatLng> points = new ArrayList<>();
//                        points.add(new LatLng(lat, lon));
//                        points.add(point);
//
//                        mapboxMap.addPolyline(new PolylineOptions()
//                                .addAll(points)
//                                .color(Color.parseColor("#3bb2d0"))
//                                .width(2));
//
//                        getRoute(Point.fromLngLat(point.getLongitude(),point.getLatitude()));

                        return true;
                    }
                });
            }
        });
    }

    private void getRoute(Point destination) throws ServicesException {

        MapboxDirections client = MapboxDirections.builder()
                .origin(Point.fromLngLat(lon,lat))
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(getResources().getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Timber.d("Response code: " + response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e(TAG, "No routes found");
                    return;
                }

                // Print some info about the route
                currentRoute = response.body().routes().get(0);
                Timber.d("Distance: " + currentRoute.distance());
//                Toast.makeText(
//                        DirectionsActivity.this,
//                        "Route is " + currentRoute.getDistance() + " meters long.",
//                        Toast.LENGTH_SHORT).show();

                // Draw the route on the map
                drawRoute(currentRoute);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Timber.e("Error: %s", throwable.getMessage());
                Toast.makeText(MapBoxActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRoute(DirectionsRoute route) {
        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
        List<Point> coordinates = lineString.coordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).latitude(),
                    coordinates.get(i).longitude());
        }

        // Draw Points on MapView
        mapboxMap.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#009688"))
                .width(5));
    }

    private void enableLocationComponent(Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this,loadedMapStyle).build());

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);

            locationComponent.setRenderMode(RenderMode.COMPASS);

            lat=locationComponent.getLastKnownLocation().getLatitude();
            lon=locationComponent.getLastKnownLocation().getLongitude();
        }
    }


    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
