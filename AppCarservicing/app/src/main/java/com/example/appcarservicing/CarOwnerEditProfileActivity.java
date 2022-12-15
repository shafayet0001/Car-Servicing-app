package com.example.appcarservicing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.appcarservicing.databinding.ActivityCarWorkshopOwnerEditProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class CarOwnerEditProfileActivity extends AppCompatActivity {

    ActivityCarWorkshopOwnerEditProfileBinding binding;
    DatabaseReference databaseReference;
    private String currentUserId;
    private FirebaseAuth auth;
    Button save;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCarWorkshopOwnerEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameuser = binding.editName.getText().toString();
                String usernameuser = binding.editUsername.getText().toString();
                String phoneuser = binding.editPhone.getText().toString();
                String caruser = binding.editWorkshop.getText().toString();


                carWorkshopOwnerEditProfile(nameuser,  usernameuser, phoneuser, caruser);

            }

        });
        back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(CarOwnerEditProfileActivity.this,CarOwnerProfileActivity.class));

            }
        });

    }

    private void carWorkshopOwnerEditProfile(String nameuser,String usernameuser, String phoneuser,  final String caruser) {

        HashMap User = new HashMap();


        User.put("name",nameuser);
        User.put("username",usernameuser);
        User.put("phone",phoneuser);
        User.put("car",caruser);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("CarOwner");

        databaseReference.child(currentUserId).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {


            @Override
            public void onComplete(@NonNull Task task) {


                if (task.isSuccessful()){

                    binding.editName.setText("");

                    binding.editUsername.setText("");

                    binding.editPhone.setText("");
                    binding.editWorkshop.setText("");


                    Toast.makeText(CarOwnerEditProfileActivity.this,"Saved",Toast.LENGTH_SHORT).show();

                }else {

                    Toast.makeText(CarOwnerEditProfileActivity.this,"Failed to Saved",Toast.LENGTH_SHORT).show();

                }

            }
        });



    }



}

