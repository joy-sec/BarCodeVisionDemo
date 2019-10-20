package me.thejoy.barcodedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mapbox.android.core.permissions.PermissionsManager;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private Button btnZBar,btnGV,btnMap,btnMapBox;
    private static final int REQUEST_CAMERA_PERMISSION = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnZBar=findViewById(R.id.btn_zbar);
        btnGV=findViewById(R.id.btn_google);
        btnMap=findViewById(R.id.btn_map);
        btnMapBox=findViewById(R.id.btn_map_box);

        btnZBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ZBarActivity.class));
            }
        });

        btnGV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,GVisionActivity.class));
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MapActivity.class));
            }
        });

        btnMapBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MapBoxActivity.class));
            }
        });



        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //nothing to do here
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new
                    String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v("TAG","Permission is granted");
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.v("TAG","Permission is granted");
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 105);
        }

    }
}
