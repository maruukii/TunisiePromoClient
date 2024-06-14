package com.example.tunisiepromoclient;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class FirstProfileClientActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private TextView logout;
    private EditText FnameEditText;
    private EditText LnameEditText;
    private EditText PhoneEditText;
    private ImageView image;
    private EditText emailEditText;
    private String imageURL;
    private Uri selectedImageUri;
    private TextInputLayout emailLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_profile_client);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        logout=findViewById(R.id.logout);
        fstore=FirebaseFirestore.getInstance();
        emailEditText = findViewById(R.id.editTextEmail);
        emailLayout=findViewById(R.id.emailLayout);
        FnameEditText=findViewById(R.id.editTextFname);
        LnameEditText=findViewById(R.id.editTextLname);
        PhoneEditText=findViewById(R.id.editTextPhone);
        image=findViewById(R.id.imageClient);
        Button selectImageButton = findViewById(R.id.uploadButton);
        Button updateProfile=findViewById(R.id.saveProfileButton);
        String userid=mAuth.getCurrentUser().getUid();
        DocumentReference dr=fstore.collection("users").document(userid);
        logout.setOnClickListener(view -> {
            Intent intent=new Intent(FirstProfileClientActivity.this,MainActivity.class);
            startActivity(intent);
        });
        // Retrieve the document
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> userInfo = document.getData();

                        // Now you can access specific fields in the map
                        if (userInfo != null) {
                            String firstName = (String) userInfo.get("First Name");
                            String lastName = (String) userInfo.get("Last Name");
                            String email=(String) userInfo.get("Email");
                            imageURL = (String) userInfo.get("Image Url");
                            long number =Long.parseLong(userInfo.get("Phone Number").toString());
                            FnameEditText.setText(firstName);
                            LnameEditText.setText(lastName);
                            if(number!=0){
                                PhoneEditText.setText(String.valueOf(number));
                            }

                            emailEditText.setText(email);
                            emailEditText.setEnabled(false);
                            emailEditText.setFocusable(false);
                            emailEditText.setFocusableInTouchMode(false);
                            emailEditText.setTextColor(Color.BLACK);
                            if(imageURL!=null){
                            Picasso.get().load(imageURL).resize(200,200).into(image);}
                        }
                    } else {
                        emailEditText.setText(mAuth.getCurrentUser().getEmail().toString().trim());
                        emailEditText.setEnabled(false);
                        emailEditText.setFocusable(false);
                        emailEditText.setFocusableInTouchMode(false);
                        emailEditText.setTextColor(Color.BLACK);
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.w("Firestore", "Error getting document", task.getException());
                }
            }});
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfiletoFirestore();
                Toast.makeText(FirstProfileClientActivity.this, "PROFILE MODIFIED.", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(FirstProfileClientActivity.this,HomeClientActivity.class);
                startActivity(intent);
            }
        });
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                getContent.launch(galleryIntent);
            }
        });



    }

    private void updateProfiletoFirestore() {
        String FName = FnameEditText.getText().toString().trim();
        String LName = LnameEditText.getText().toString().trim();
        String phone=PhoneEditText.getText().toString().trim();
        String email;
        long phoneNumber;
        email=mAuth.getCurrentUser().getEmail().toString().trim();
        if(phone.isEmpty()){phoneNumber=0;}
        else {
        phoneNumber=Long.parseLong(PhoneEditText.getText().toString());}
        String userid=mAuth.getCurrentUser().getUid();
        DocumentReference dr=fstore.collection("users").document(userid);
        if (!email.isEmpty()&&selectedImageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("imageClient/" +userid+"/image.jpg");
            UploadTask uploadTask = storageRef.putFile(selectedImageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    HashMap<String, Object> user = new HashMap<>();
                    user.put("First Name",FName);
                    user.put("Last Name", LName);
                    user.put("Email",email);
                    user.put("Phone Number",phoneNumber);
                    user.put("Image Url",imageUrl);
                    dr.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG,"Profile Updated with Success for User: "+userid);
                        }
                    });

                    finish();
                });
            }).addOnFailureListener(e -> {
                // Handle unsuccessful uploads
                // ...
            });
        }
        else if (!email.isEmpty()) {
                    HashMap<String, Object> user = new HashMap<>();
                    user.put("First Name",FName);
                    user.put("Last Name", LName);
                    user.put("Email",email);
                    user.put("Phone Number",phoneNumber);
                    user.put("Image Url",imageURL);
                    dr.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG,"Profile Updated with Success for User: "+userid);
                        }
                    });

                    finish();
                }
            }

    private final ActivityResultLauncher<Intent> getContent =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imageUri = data.getData();
                        selectedImageUri = imageUri;
                        Picasso.get().load(imageUri).resize(200,200).into(image);
                    }
                }
            });
}