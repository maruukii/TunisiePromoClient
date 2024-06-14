package com.example.tunisiepromoclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button client;
    private Button company;
    private TextView help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        help=findViewById(R.id.help);
        client = findViewById(R.id.clientButton);
        company = findViewById(R.id.companyButton);
        client.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this,LoginClientActivity.class);
            startActivity(intent);

        });
        company.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this,LoginCompanyActivity.class);
            startActivity(intent);

        });
        help.setOnClickListener(view -> {
            String url = "https://support.google.com/firebase/?hl=en#topic=6399725";

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);});

}}