package com.example.tunisiepromoclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PublicationActivity extends AppCompatActivity implements RecyclerViewInterface {
        private FirebaseAuth mAuth;
        private FirebaseFirestore fstore;
        private RecyclerView recyclerView;
        private PublicationAdapter pubAdapter;
        private List<publication> pubList;
        private Button add;
        private ImageView profile;
        String imageURL;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_publication);
            mAuth= FirebaseAuth.getInstance();
            fstore=FirebaseFirestore.getInstance();
            recyclerView = findViewById(R.id.recyclerView);
            add=findViewById(R.id.buttonAddPub);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            profile=findViewById(R.id.profileCompany);
            profile.setOnClickListener(view -> {
                Intent intent=new Intent(PublicationActivity.this,FirstProfileCompanyActivity.class);
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
            pubList = new ArrayList<>();
            pubAdapter = new PublicationAdapter(pubList, this,this);
            recyclerView.setAdapter(pubAdapter);
            add.setOnClickListener(view -> {
                Intent intent=new Intent(PublicationActivity.this,AddPubActivity.class);
                startActivity(intent);});

// Fetch product data from Firebase Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/publications/"+mAuth.getCurrentUser().getUid().toString()+"/");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pubList.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        publication product = dataSnapshot.getValue(publication.class);
                        pubList.add(product);
                    }

                    pubAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });

// ...

// Fetch image URLs from Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("pub_images/"+mAuth.getCurrentUser().getUid().toString()+"/");
            for (publication product : pubList) {
                storageReference.child(product.getImageUrl()).getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            product.setImageUrl(uri.toString());
                            pubAdapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            // Handle error
                        });
            }


        }

    @Override
    public void onItemClick(int position) {
          Intent intent=new Intent(PublicationActivity.this,ProductActivity.class)  ;
          intent.putExtra("pub",pubList.get(position).getPubId());
          startActivity(intent);
    }
}

