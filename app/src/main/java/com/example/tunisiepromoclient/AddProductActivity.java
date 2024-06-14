package com.example.tunisiepromoclient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class AddProductActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    private EditText productNameEditText;
    private EditText productPriceOldText;
    private EditText productPriceNewText;
    private ImageView productImageView;
    private Uri selectedImageUri;
    long numberOfImages;
    long numberOfChildren;
    String imageURL;
    ImageView profile;
    String pubId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        pubId=getIntent().getStringExtra("pub");
        mAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        productNameEditText = findViewById(R.id.editTextProductName);
        productImageView = findViewById(R.id.imageViewProduct);
        productPriceOldText=findViewById(R.id.editTextProductPriceOld);
        productPriceNewText=findViewById(R.id.editTextProductPriceNew);
        Button selectImageButton = findViewById(R.id.buttonSelectImage);
        profile=findViewById(R.id.profileCompany);
        profile.setOnClickListener(view -> {
            Intent intent=new Intent(AddProductActivity.this,FirstProfileCompanyActivity.class);
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
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                getContent.launch(galleryIntent);
            }
        });

        Button addProductButton = findViewById(R.id.buttonAddProduct);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProductToFirebase();
                Intent intent=new Intent(AddProductActivity.this,ProductActivity.class);
                Log.d("DatePicker", "Selected date: " + pubId);
                intent.putExtra("pub",pubId);
                startActivity(intent);
            }
        });
    }
    private void uploadProductToFirebase() {
        String productName = productNameEditText.getText().toString().trim();
        Log.d("DatePicker", "Selected date: " + pubId);
        if (!productName.isEmpty() && selectedImageUri != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("product_images/"+mAuth.getCurrentUser().getUid().toString() +"/"+pubId+"/");
            storageRef.listAll()
                    .addOnSuccessListener(listResult -> {
                        numberOfImages = listResult.getItems().size();
                        StorageReference storageRef2 = FirebaseStorage.getInstance().getReference("product_images/" +mAuth.getCurrentUser().getUid().toString()+"/"+pubId+"/product_image("+(++numberOfImages)+").jpg");
                        UploadTask uploadTask = storageRef2.putFile(selectedImageUri);
                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                            storageRef2.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                String productId="product"+numberOfImages;
                                double priceOld=Double.parseDouble(productPriceOldText.getText().toString());
                                double priceNew=Double.parseDouble(productPriceNewText.getText().toString());
                                Product product = new Product(productId,productName,priceOld,priceNew ,imageUrl);
                                // Assuming you have a "products" node in your database
                                database.getReference("publications").child(mAuth.getCurrentUser().getUid().toString()).child(pubId).child("products").child(productId).setValue(product);
                                finish();
                            });

            }).addOnFailureListener(e -> {
                // Handle unsuccessful uploads
                // ...
            });
        });
    }}


    private final ActivityResultLauncher<Intent> getContent =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imageUri = data.getData();
                        selectedImageUri = imageUri;
                        Picasso.get().load(imageUri).resize(700,700).into(productImageView);
                    }
                }
            });
}