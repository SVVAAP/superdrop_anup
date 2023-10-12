package com.svvaap.superdrop_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.superdrop_admin.R;

public class Order_Activity extends AppCompatActivity {
private TextView name,phone,optionalphone,address,paymentmethord,landmark,total;
private Button accept,cancle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        name=findViewById(R.id.order_Name);
        phone=findViewById(R.id.order_phone);
        optionalphone=findViewById(R.id.order_phone_optional);
        address=findViewById(R.id.order_Address);
        paymentmethord=findViewById(R.id.order_paymentMethod);
        landmark=findViewById(R.id.order_Landmark);
        total=findViewById(R.id.oredr_GrandTotal);
        accept=findViewById(R.id.order_acceptButton);
        cancle=findViewById(R.id.order_cancelButton);

    }
}