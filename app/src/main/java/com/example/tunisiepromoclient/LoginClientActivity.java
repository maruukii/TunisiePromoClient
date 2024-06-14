package com.example.tunisiepromoclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LoginClientActivity extends AppCompatActivity {

        private FirebaseAuth mAuth;
        private FirebaseUser user;
        private FirebaseFirestore fstore;
        private EditText emailEditText;
        private EditText passwordEditText;
        private TextInputLayout emailLayout;
        private Button loginButton;
        private TextView createAccountButton;
        private ImageView back;
        private List<String> userEmails;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login_client);
            fstore=FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            back=findViewById(R.id.back);
            userEmails=new ArrayList<>();
            emailEditText = findViewById(R.id.editTextEmail);
            passwordEditText = findViewById(R.id.editTextPassword);
            Button loginButton=findViewById(R.id.loginButton);
            createAccountButton=findViewById(R.id.createAccountButton);
            emailLayout=findViewById(R.id.emailLayout);
            back.setOnClickListener(view -> {
                Intent intent=new Intent(LoginClientActivity.this,MainActivity.class);
                startActivity(intent);
            });

            emailEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String ema=editable.toString();
                    if (Patterns.EMAIL_ADDRESS.matcher(ema).matches()){
                        emailLayout.setHelperText("");
                        emailLayout.setError("");
                    }else{
                        emailLayout.setHelperText("");
                        emailLayout.setError("Wrong Email Format");
                    }
                }
            });
            loginButton.setOnClickListener(view -> {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            Toast.makeText(LoginClientActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(LoginClientActivity.this,HomeClientActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginClientActivity.this, "Wrong Email/Password.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Email and Password are required", Toast.LENGTH_SHORT).show();}
            });
            createAccountButton.setOnClickListener(view -> {
                Intent intent=new Intent(LoginClientActivity.this,RegisterActivity.class);
                startActivity(intent);

            });
        }}