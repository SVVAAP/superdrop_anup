package com.example.superdrop_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Admin_Activity extends AppCompatActivity {
    Button add_rest,add_offer,add_bunontop,add_streetwok,add_bowlexpres,orders,owner_n;
    ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        add_rest=findViewById(R.id.add_rest);
        add_offer=findViewById(R.id.offr);
        add_bunontop=findViewById(R.id.bunontop_add);
        add_streetwok=findViewById(R.id.streetwok_add);
        add_bowlexpres=findViewById(R.id.bowlexpress_add);
        orders=findViewById(R.id.owners_bt);
        owner_n=findViewById(R.id.new_o);

        add_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Activity.this, DeleteActivity.class);
                startActivity(intent);
            }
        });

        add_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Activity.this, Offer_Add_Activity.class);
                startActivity(intent);
            }
        });
        add_bunontop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Activity.this, BunOnTopAdd_Activity.class);
                startActivity(intent);
            }
        });
        add_streetwok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Activity.this, StreetWokAdd_Activity.class);
                startActivity(intent);
            }
        });
        add_bowlexpres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Activity.this, BowlExpressAdd_Activity.class);
                startActivity(intent);
            }
        });
       orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Activity.this, OwnersActivity.class);
                startActivity(intent);
            }
        });

       //logout
        // Initialize the logout button
        logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Admin_Activity.this, OtpSendActivity.class);
                startActivity(intent);
                finish(); // Close the current activity after logout
            }
        });
       owner_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Admin_Activity.this, OwnersActivity2.class);
                startActivity(intent);
                finish(); // Close the current activity after logout
            }
        });
    }
}