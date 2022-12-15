package com.example.appcarservicing;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupCarWorkshopOwnerActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupName,signupEmail,signupUsername, signupPassword,signupPhone,signupWorkshop;

    private Button signupButton;
    private TextView loginRedirectText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_car_workshop_owner);
        auth = FirebaseAuth.getInstance();

        signupName= findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupPhone = findViewById(R.id.signup_phone);
        signupWorkshop = findViewById(R.id.signup_workshop);

        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();
                String phone = signupPhone.getText().toString();
                String workshop = signupWorkshop.getText().toString();


                if (email.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                if (password.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                } else{
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String user_id = auth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("CarWorkshopOwner");
                                HelperClass1 user = new HelperClass1(name,  email,  username,  password,  phone,  workshop);

                                //current_user_db.setValue(email);
                                current_user_db.child(user_id).setValue(user);

                                Toast.makeText(SignupCarWorkshopOwnerActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupCarWorkshopOwnerActivity.this,LoginCarWorkshopOwnerActivity.class));

                            } else {
                                Toast.makeText(SignupCarWorkshopOwnerActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();



                            }
                        }
                    });
                }
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupCarWorkshopOwnerActivity.this,LoginCarWorkshopOwnerActivity.class));
            }
        });
    }
}