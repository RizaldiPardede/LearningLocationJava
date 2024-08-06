package com.example.tugaspraktikumlocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    String addressLines;
    private TextView latitude, longitude, altitude, akurasi, alamat;
    private Button btnFind;
    private FusedLocationProviderClient locationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        altitude = findViewById(R.id.altitude);
        akurasi = findViewById(R.id.akurasi);
        alamat = findViewById(R.id.alamat);

        btnFind = findViewById(R.id.btn_find);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        btnFind.setOnClickListener(v -> {

            getLocation();
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Izin lokasi tidak di aktifkan!", Toast.LENGTH_SHORT).show();
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getLocation();
                }
            }
        }
    }
    private void getLocation() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        final List<Address>[] addressList = new List[]{null};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
// get Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION
                }, 10);
            }
        }else {
// get Location
            locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location!=null) {
                    latitude.setText(String.valueOf(location.getLatitude()));
                    longitude.setText(String.valueOf(location.getLongitude()));
                    altitude.setText(String.valueOf(location.getAltitude()));
                    akurasi.setText(location.getAccuracy() + "%");
                    try {
                        addressList[0] = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        if (addressList[0] != null){
                            Address returnAdd = addressList[0].get(0);
                            StringBuilder stringBuilder = new StringBuilder("");
                            for (int i = 0;i<returnAdd.getMaxAddressLineIndex();i++){
                                stringBuilder.append(returnAdd.getAddressLine(i)).append("\n");

                            }
                            Log.w("My location Address", stringBuilder.toString());
                        }
                        else{
                            Log.w("My location Address", "no address");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    addressLines = addressList[0].get(0).getAddressLine(0);
                    alamat.setText(addressLines);
                }else{
                    Toast.makeText(getApplicationContext(), "Lokasi tidak aktif!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}