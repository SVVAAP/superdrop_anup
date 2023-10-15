package com.svvaap.superdrop_admin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop_admin.R;

import java.util.List;

public class Owner_Adapter extends RecyclerView.Adapter<Owner_Adapter.ViewHolder> {
    private List<Order> orderList;
    private static Context context;

    private MediaPlayer mediaPlayer;
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
        holder.status.setText(currentStatus);


        // Check if the status is "processing"
        if ("processing".equalsIgnoreCase(currentStatus)) {
            // Start playing music if it's not already playing
            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                startMusicPlayback();
            }
        } else {
            // Stop music if the status changes
            stopMusicPlayback();
        }

        // Set click listeners for location and call
        holder.ringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stop the music when the ring button is clicked
                stopMusicPlayback();
                // Handle other actions related to the button click
            }
        });


        // Set click listeners for location and call
        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGoogleMaps(order.getShippingAddress(), holder.mapWebView);
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



    // Method to start playing music
    private void startMusicPlayback() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.addtocart_music); // Replace with your music resource
            mediaPlayer.setLooping(true); // Loop the music
        }
        mediaPlayer.start();
    }

    // Method to stop music playback
    private void stopMusicPlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private foodItemAdapter fooditemadapter;
        private TextView name, phone, orderid, total, status;
        private ImageView location, call, ringButton;
        public WebView mapWebView;  // Store the WebView as a member variable


        @SuppressLint("SetJavaScriptEnabled")
        public ViewHolder(@NonNull View itemView, ViewGroup parent) {
            super(itemView);
            name = itemView.findViewById(R.id.oshippingNameTextView);
            phone = itemView.findViewById(R.id.oshippingphoneTextView);
            orderid = itemView.findViewById(R.id.shippingorderid);
            total = itemView.findViewById(R.id.oGrandTotal);
            location = itemView.findViewById(R.id.location);
            call = itemView.findViewById(R.id.call);
            //  mapWebView = itemView.findViewById(R.id.map_web);
            status = itemView.findViewById(R.id.ostatus_text);
            // Initialize mapWebView here

            // Set up a WebViewClient to handle Google Maps URL
            // Set up a WebViewClient to handle Google Maps URL
//            mapWebView.setWebViewClient(new WebViewClient() {
//                @Override
//                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                    super.onReceivedError(view, errorCode, description, failingUrl);
//                    // Handle the error here
//                    Log.e("WebViewError", description);
//                }
//            });


// Enable JavaScript


        }
    }
    // Open Google Maps with the specified address
    private void openGoogleMaps(String address, WebView mapWebView) {
        if (mapWebView != null) {
            mapWebView.getSettings().setJavaScriptEnabled(true);
            mapWebView.setVisibility(View.VISIBLE);
            mapWebView.loadUrl("https://maps.google.com/maps?q=" + Uri.encode(address));
        }
    }
    // Make a phone call to the specified phone number
    private void makePhoneCall(String phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        context.startActivity(dialIntent);

    }
}
