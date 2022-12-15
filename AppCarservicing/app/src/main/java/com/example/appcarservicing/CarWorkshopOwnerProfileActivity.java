package com.example.appcarservicing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.appcarservicing.databinding.ActivityCarOwnerProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CarWorkshopOwnerProfileActivity extends AppCompatActivity {
    private TextView profileName, profileEmail, profileUsername, profilePhone,profileWorkshop;
    private TextView titleName, titleUsername;
    private DatabaseReference ProfileUserRef;

    Button editProfile, Search;
    private FirebaseAuth auth;
    TextView dataTextView;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_car_workshop_owner_profile);
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        ProfileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child("CarWorkshopOwner").child(currentUserId);


        profileName = (TextView) findViewById(R.id.profileName);
        profileEmail = (TextView) findViewById(R.id.profileEmail);
        profileUsername = (TextView) findViewById(R.id.profileUsername);
        profilePhone = (TextView) findViewById(R.id.profilePhone);
        profileWorkshop = (TextView) findViewById(R.id.profileworkshop);
        titleName = (TextView) findViewById(R.id.titleName);
        titleUsername = (TextView) findViewById(R.id.titleUsername);

        editProfile = findViewById(R.id.EditProfileButton);
        Search = findViewById(R.id.SearchButton);

        ProfileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                    //String TitleName = snapshot.child("name").getValue().toString();
                    // String TitleUsername = snapshot.child("username").getValue().toString();
                    String ProfileName = snapshot.child("name").getValue().toString();
                    String ProfileUsername = snapshot.child("username").getValue().toString();
                    String ProfilePhone = snapshot.child("phone").getValue().toString();
                    String ProfileEmail = snapshot.child("email").getValue().toString();
                    String ProfileWorkshop = snapshot.child("workshop").getValue().toString();

                    titleName.setText(ProfileName);
                    titleUsername.setText(ProfileUsername);
                    profileName.setText( ProfileName);
                    profileEmail.setText(  ProfileEmail);
                    profileUsername.setText( ProfileUsername);
                    profilePhone.setText(ProfilePhone);
                    profileWorkshop.setText( ProfileWorkshop);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(CarWorkshopOwnerProfileActivity.this,CarWorkshopOwnerEditProfileActivity.class));

            }
        });

        Search = findViewById(R.id.SearchButton);
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(CarWorkshopOwnerProfileActivity.this,MapsActivity.class));

            }
        });



    }


}

