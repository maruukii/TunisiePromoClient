package com.example.tunisiepromoclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextInputLayout passwordLayout;
    private TextInputLayout emailLayout;
    private ImageView back;
    private TextView loginButton;
    private Button createAccountButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        back=findViewById(R.id.back);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        passwordLayout=findViewById(R.id.passLayout);
        emailLayout=findViewById(R.id.emailLayout);
        createAccountButton=findViewById(R.id.createAccountButton);
        loginButton=findViewById(R.id.loginButton);
        back.setOnClickListener(view -> {
            Intent intent=new Intent(RegisterActivity.this,LoginClientActivity.class);
            startActivity(intent);
        });
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String ema=charSequence.toString();
            if (Patterns.EMAIL_ADDRESS.matcher(ema).matches()){
                emailLayout.setHelperText("Correct Email Format");
                emailLayout.setError("");
                }else{
                emailLayout.setHelperText("");
                emailLayout.setError("Wrong Email Format");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pass=charSequence.toString();
                if (pass.length()>=8){
                    Pattern pattern= Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
                    Matcher matcher=pattern.matcher(pass);
                    boolean isPassStrong=matcher.find();
                    if(isPassStrong){
                        passwordLayout.setHelperText("Strong Password");
                        passwordLayout.setError("");
                    }
                    else {
                        passwordLayout.setHelperText("");
                        passwordLayout.setError("Weak Password\n** Password must contain lowercase Letters, uppercase Letters and at least one Numeric and one Special characters");
                    }
                }else {passwordLayout.setHelperText("Enter Minimum 8 characters");
                    passwordLayout.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        createAccountButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(this, "Username, E-mail and Password are Required", Toast.LENGTH_SHORT).show();}
            else {
                if (password.length() < 8) {
                    // Password must be at least 8 characters
                    Toast.makeText(RegisterActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean hasUppercase = false;
                boolean hasLowercase = false;
                boolean hasDigit = false;

                for (char ch : password.toCharArray()) {
                    if (Character.isUpperCase(ch)) {
                        hasUppercase = true;
                    } else if (Character.isLowerCase(ch)) {
                        hasLowercase = true;
                    } else if (Character.isDigit(ch)) {
                        hasDigit = true;
                    }
                }

                if (!hasUppercase || !(hasLowercase && hasDigit)) {

                    Toast.makeText(RegisterActivity.this, "Password must contain lowercase and uppercase letters and numeric characters", Toast.LENGTH_LONG).show();
                }
                else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,
                                    password)
                            .addOnCompleteListener(this, task -> {

                            if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Account created.", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(RegisterActivity.this,FirstProfileClientActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Account creation failed.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

        });
        loginButton.setOnClickListener(view -> {
            Intent intent=new Intent(RegisterActivity.this,LoginClientActivity.class);
            startActivity(intent);

        });
    }}