package com.example.appcarservicing;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button buttonforcarOwner;
    private Button buttonforWorkshopOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonforcarOwner = findViewById(R.id.button_for_car_Owner);
        buttonforWorkshopOwner=findViewById(R.id.button_for_Workshop_owner);
        buttonforcarOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginCarOwnerActivity.class));
            }

        });
        buttonforWorkshopOwner.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                startActivity(new Intent(MainActivity.this,LoginCarWorkshopOwnerActivity.class));

            }
        });



    }
}