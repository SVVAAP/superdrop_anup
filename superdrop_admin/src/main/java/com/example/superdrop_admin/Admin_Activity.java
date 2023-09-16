package com.example.superdrop_admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Admin_Activity extends AppCompatActivity {
    Button add_rest,add_offer,add_bunontop,add_streetwok,add_bowlexpres,orders,tab,Offeradd,add_vadapav,add_kfc;
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
        tab=findViewById(R.id.tab_bt);
        Offeradd=findViewById(R.id.offer_item_add);
        add_vadapav=findViewById(R.id.vadapav_add_bt);
        add_kfc=findViewById(R.id.kfc_add_bt);

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
        tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Activity.this, OwnersActivity.class);
                startActivity(intent);
            }
        });
        Offeradd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Activity.this, Offer_item_addActivity.class);
                startActivity(intent);
            }
        });
        add_vadapav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Activity.this, VadaPav_Add_Activity.class);
                startActivity(intent);
            }
        });
        add_kfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Activity.this, KFC_Add_Activity.class);
                startActivity(intent);
            }
        });

       //logout
        // Initialize the logout button
        logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    // No internet connection, display a toast message
                    Toast.makeText(Admin_Activity.this, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                } else {
                    // Create an AlertDialog.Builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(Admin_Activity.this);
                    builder.setTitle("Logout");
                    builder.setMessage("Are you sure you want to logout?");

                    // Add OK button
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked OK, perform logout
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(Admin_Activity.this, OtpSendActivity.class);
                            startActivity(intent);
                        }
                    });

                    // Add Cancel button
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked Cancel, do nothing
                            dialogInterface.dismiss();
                        }
                    });

                    // Create and show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}