package com.camt.redcab.Maps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.camt.redcab.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMapView;
    private ImageView mGeoCodingBtn;
    private EditText mAddressEditText;
    private GoogleApiClient mApiClient;
    private LocationRequest mRequest;
    private static final long UPDATE_INTERVAL = 1500;
    private static final long FASTEST_INTERVAL = 1000;
    private String mCurrentLocStr;
    public static int REQ_GEO_CODING_SEARCH = 1;
    private List<LatLng> listOfLatLng = new ArrayList<>();
    public Geocoder geocoder;
    public List<Address> addresses;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        bindWidget();
        onMapReady(mMapView);
        setEvent();
        addMarker();

        mApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        mRequest = LocationRequest.create();
                        mRequest.setInterval(UPDATE_INTERVAL);
                        mRequest.setFastestInterval(FASTEST_INTERVAL);
                        mRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                        if (ActivityCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getParent(),new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSION_REQUEST_CODE);

                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, mRequest, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                updateLocation(location);
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Connection is Failed!", Toast.LENGTH_LONG).show();
                    }
                }).build();
    }

    private void addMarker() {
        mMapView.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.marker_info_content, null);
                //สำคัญ
                TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
                if (marker.getTitle() != null && !marker.getTitle().equals("")) {
                    tvTitle.setText(marker.getTitle());
                    tvTitle.setVisibility(View.VISIBLE); // VISIBLE, INVISIBLE, GONE
                } else {
                    tvTitle.setVisibility(View.GONE);
                }
                LatLng latLng = marker.getPosition();
                TextView poistionTextView = (TextView) v.findViewById(R.id.position);
                DecimalFormat formatter = new DecimalFormat("#,###.000");

                String lat = formatter.format(latLng.latitude) + "°";
                String lng = formatter.format(latLng.longitude) + "°";
                poistionTextView.setText(lat + "," + lng);

                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });


        mMapView.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            }
        });

        mMapView.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                String address = null;
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    address = addresses.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                placeMarker(latLng.latitude,latLng.longitude,address);

            }
        });

    }

    private void updateLocation(Location location) {
        DecimalFormat formatter = new DecimalFormat("#,###.000000");
        final String lat = formatter.format(location.getLatitude());
        final String lng = formatter.format(location.getLongitude());

        mCurrentLocStr = String.format("Lat: %s°, \n Long: %s°", lat, lng);
        //Toast.makeText(getApplicationContext(), mCurrentLocStr , Toast.LENGTH_LONG).show();

    }

    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }
        mMapView.setMyLocationEnabled(true);
        mMapView.getUiSettings().setZoomControlsEnabled(true);
        mApiClient.connect();

    }


    @Override
    protected void onStop() {
        super.onStop();
        mApiClient.disconnect();
    }

    private void setEvent() {
        mGeoCodingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddressListActivity.class);
                i.putExtra("ADDRESS", mAddressEditText.getText().toString());
                startActivityForResult(i, REQ_GEO_CODING_SEARCH);
            }
        });


    }

    private void bindWidget() {
        SupportMapFragment mySupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mMapView);
        mMapView = mySupportMapFragment.getMap();
        mGeoCodingBtn = (ImageView) findViewById(R.id.mGeoCodingBtn);
        mAddressEditText = (EditText) findViewById(R.id.mAddressEditText);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMapView = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng CM = new LatLng(18.7853843, 98.9838654);
        // mMapView.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMapView.moveCamera(CameraUpdateFactory.newLatLng(CM));
        mMapView.moveCamera(CameraUpdateFactory.newLatLngZoom(CM, 13));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_GEO_CODING_SEARCH && resultCode == RESULT_OK) {
            double lat = data.getDoubleExtra("LAT", 0);
            double lng = data.getDoubleExtra("LNG", 0);
            String title = data.getStringExtra("TITLE");
            placeMarker(lat, lng, title);
        }
    }

    private void placeMarker(double lat, double lng, String title) {
        mMapView.clear();
        LatLng latLng = new LatLng(lat, lng);
        listOfLatLng.add(latLng);
        Marker marker = mMapView.addMarker(new MarkerOptions().position(latLng).title(title).snippet(""));
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_01)));
        //marker.remove(); // Deleting a marker
        mMapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        // mMapView.clear(); // Clear all overlay
    }

}
