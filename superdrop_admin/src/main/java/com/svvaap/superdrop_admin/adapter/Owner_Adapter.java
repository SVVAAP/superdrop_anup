package com.svvaap.superdrop_admin.adapter;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop_admin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.svvaap.superdrop_admin.BackgroundMusicService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Owner_Adapter extends RecyclerView.Adapter<Owner_Adapter.ViewHolder> {
    private List<Order> orderList;
    private static Context context;

    private static boolean isMusicServiceBound = false;
    private OnItemClickListener mListener; // Add a listener field

    public interface OnItemClickListener {
        void onItemClick(String stringToPass); // Define the onItemClick method
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public Owner_Adapter(List<Order> OrderList, Context context) {
        this.orderList = OrderList;
        this.context = context;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_item_v, parent, false);
        return new ViewHolder(view,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        String orderId=order.getOrderId();
        String currentStatus = order.getStatus();
        // Set default values to prevent NullPointerException
        holder.name.setText(order.getShippingName() != null ? order.getShippingName() : "N/A");
        holder.phone.setText(order.getContactInstructions() != null ? order.getContactInstructions() : "N/A");
        holder.orderid.setText(orderId);
        String gtotal="₹"+order.getGrandTotal();
        holder.total.setText(gtotal);

        // Set click listeners for location and call
        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGoogleMaps(order.getShippingAddress());
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall(order.getContactInstructions());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null){
                    mListener.onItemClick(order.getOrderId());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private foodItemAdapter fooditemadapter;
        private TextView name,phone,orderid,total;
        private ImageView location,call;
        //private Button acceptButton, cancelButton;

        public ViewHolder(@NonNull View itemView,ViewGroup parent) {
            super(itemView);
            name=itemView.findViewById(R.id.oshippingNameTextView);
            phone=itemView.findViewById(R.id.oshippingphoneTextView);
            orderid=itemView.findViewById(R.id.shippingorderid);
            total=itemView.findViewById(R.id.oGrandTotal);
            location=itemView.findViewById(R.id.location);
            call=itemView.findViewById(R.id.call);

        }

    }

    // Open Google Maps with the specified address
    private void openGoogleMaps(String address) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // Ensure it opens in Google Maps
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            // Handle the case where Google Maps is not installed
            // You can show a message to the user or open a web-based map service.
            Toast.makeText(context, "Google Maps is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Make a phone call to the specified phone number
    private void makePhoneCall(String phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        if (dialIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(dialIntent);
        } else {
            // Handle the case where there's no dialer app installed
            // You can display a message or take an alternative action.
            Toast.makeText(context, "No dialer app is available.", Toast.LENGTH_SHORT).show();
        }
    }
}
