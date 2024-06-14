package com.example.tunisiepromoclient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Map;


public class AddPubActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore fstore;
    private FirebaseUser user;
    private long numberOfChildren;
    private EditText CompanyNameEditText;
    private DatePicker startDate;
    private DatePicker endDate;
    private ImageView productImageView;
    private Uri selectedImageUri;
    private long numberOfImages;
    EditText editTextPromoName;
    String selectedDateStart;
    String selectedDateEnd;
    String CompanyName;
    String PromoName;
    String pubId;
    String imageURL;

    private ImageView profile;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_pub);
            fstore=FirebaseFirestore.getInstance();
            mAuth=FirebaseAuth.getInstance();
            productImageView = findViewById(R.id.imageViewProduct);
            editTextPromoName=findViewById(R.id.editTextPromoName);
            startDate=findViewById(R.id.Startdate);
            endDate=findViewById(R.id.Enddate);
            profile=findViewById(R.id.profileCompany);
            profile.setOnClickListener(view -> {
                Intent intent=new Intent(AddPubActivity.this,FirstProfileCompanyActivity.class);
                startActivity(intent);
            });
            String userid=mAuth.getCurrentUser().getUid();
            DocumentReference dr=fstore.collection("Companies").document(userid);

            // Retrieve the document
            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@android.support.annotation.NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> userInfo = document.getData();

                            // Now you can access specific fields in the map
                            if (userInfo != null) {
                                imageURL = (String) userInfo.get("Image Url");

                                if(imageURL!=null){
                                    Picasso.get().load(imageURL).resize(40,50).into(profile);}
                            }
                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        Log.w("Firestore", "Error getting document", task.getException());
                    }
                }});
            Button selectImageButton = findViewById(R.id.buttonSelectImage);
            selectImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    getContent.launch(galleryIntent);
                }
            });

            Button addProductButton = findViewById(R.id.buttonAddPub);
            addProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pubId=uploadProductToFirebase();
                    Toast.makeText(AddPubActivity.this, "PUB ADDED.", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(AddPubActivity.this,PublicationActivity.class);
                    startActivity(intent);
                }
            });
        }


    private String uploadProductToFirebase() {
        String userid=mAuth.getCurrentUser().getUid();
        DocumentReference dr=fstore.collection("Companies").document(userid);

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
                            String CompanyName = (String) userInfo.get("Company Name");
                            if (selectedImageUri != null) {

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference("pub_images/" +mAuth.getCurrentUser().getUid().toString()+"/");
                                storageRef.listAll()
                                        .addOnSuccessListener(listResult -> {
                                            numberOfImages = listResult.getItems().size();
                                            StorageReference storageRef2 = FirebaseStorage.getInstance().getReference("pub_images/" +mAuth.getCurrentUser().getUid().toString()+"/publication_images("+(++numberOfImages)+").jpg");
                                            UploadTask uploadTask = storageRef2.putFile(selectedImageUri);
                                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                                storageRef2.getDownloadUrl().addOnSuccessListener(uri -> {
                                                    String imageUrl = uri.toString();
                                                    pubId="pub"+numberOfImages;
                                                    int day = startDate.getDayOfMonth();
                                                    int month = startDate.getMonth() + 1; // Month is 0-based, so add 1
                                                    int year = startDate.getYear();

                                                    // Handle the selected date
                                                    String selectedDateStart = day + "/" + month + "/" + year;
                                                    // You can use the selectedDate as needed
                                                    Log.d("DatePicker", "Selected date: " + selectedDateStart);
                                                    day = endDate.getDayOfMonth();
                                                    month = endDate.getMonth() + 1;
                                                    year = endDate.getYear();

                                                    // Handle the selected date
                                                    String selectedDateEnd = day + "/" + month + "/" + year;
                                                    // You can use the selectedDate as needed
                                                    Log.d("DatePicker", "Selected date: " + selectedDateEnd);
                                                    PromoName=editTextPromoName.getText().toString().trim();
                                                    publication pub = new publication(pubId,userid,CompanyName,PromoName,selectedDateStart,selectedDateEnd,imageUrl);
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("publications/"+mAuth.getCurrentUser().getUid().toString()+"/");

                                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            numberOfChildren = dataSnapshot.getChildrenCount();
                                                            database.getReference("publications").child(mAuth.getCurrentUser().getUid().toString()).child("pub"+(++numberOfChildren)).setValue(pub);
                                                            pubId="pub"+numberOfChildren;
                                                            Log.d("Firebase", "pub"+numberOfChildren);
                                                            Log.d("Firebase", "Number of children: " + numberOfChildren);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            // Handle errors here
                                                        }
                                                    });
                                                    finish();
                                                });
                                            }).addOnFailureListener(e -> {
                                                // Handle unsuccessful uploads
                                                // ...
                                            });
                                            Log.d("FirebaseStorage", "Number of Images: " + numberOfImages);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle errors
                                            Log.e("FirebaseStorage", "Error getting number of images", e);
                                        });


                            }
                            }}
                    else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.w("Firestore", "Error getting document", task.getException());
                }
            }});

return pubId;
    }


    private final ActivityResultLauncher<Intent> getContent =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imageUri = data.getData();
                        selectedImageUri = imageUri;
                        Picasso.get().load(imageUri).resize(150,150).into(productImageView);
                    }
                }
            });
}