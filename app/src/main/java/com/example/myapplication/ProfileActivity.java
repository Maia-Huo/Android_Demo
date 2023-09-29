package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button home = findViewById(R.id.navigation_home);
        Button upload = findViewById(R.id.navigation_upload);
        Button profile = findViewById(R.id.navigation_profile);

        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //跳转到uppload
                Intent intent = new Intent(ProfileActivity.this, UploadActivity.class);
                startActivity(intent);
                finish();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "profile", Toast.LENGTH_SHORT).show();
            }
        });



    }
}