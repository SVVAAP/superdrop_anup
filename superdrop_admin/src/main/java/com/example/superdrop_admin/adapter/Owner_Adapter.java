package com.example.superdrop_admin.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private Context context;
    private String orderID,cToken;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private String userid;
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
    private class UpdateStatusTask implements Callable<Void> {
        private final Button button;
        private final String newStatus;
        private final String orderId;

        public UpdateStatusTask(Button button, String newStatus, String orderId) {
            this.button = button;
            this.newStatus = newStatus;
            this.orderId = orderId;
        }

        @Override
        public Void call() {
            // Perform your database updates here
            DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");
            DatabaseReference custOrderDatabaseReference = FirebaseDatabase.getInstance().getReference("cust_orders").child(userid);

            orderDatabaseReference.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("status").setValue(newStatus);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database read error
                }
            });

            custOrderDatabaseReference.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("status").setValue(newStatus);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database read error
                }

            });


            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.order = order;
        String orderId=order.getOrderId();
         userid=order.getUserId();
        String currentStatus = order.getStatus();
        // Set default values to prevent NullPointerException
        holder.name.setText(order.getShippingName() != null ? order.getShippingName() : "N/A");
        holder.phone.setText(order.getContactInstructions() != null ? order.getContactInstructions() : "N/A");
        holder.city.setText(order.getShippingCity() != null ? order.getShippingCity() : "N/A");
        holder.address.setText(order.getShippingAddress() != null ? order.getShippingAddress() : "N/A");
        holder.payment.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A");
        holder.note.setText(order.getNote() != null ? order.getNote() : "N/A");
        holder.fooditemadapter = new foodItemAdapter(order.getItems(), context);
        holder.itemRecyclerView.setAdapter(holder.fooditemadapter);
        holder.orderid.setText(orderId);
        String gtotal="₹"+order.getGrandTotal();
        holder.total.setText(gtotal);
        cToken=order.getToken();

        if (currentStatus != null && currentStatus.equals("Ordering"))  {
            holder.acceptButton.setText("Accept");
            holder.acceptButton.getLayoutParams().width = holder.itemView.getWidth() / 2;  // Set half width
            holder.cancelButton.setVisibility(View.VISIBLE);  // Show the cancel button
            holder.cancelButton.setEnabled(true);
            holder.cancelButton.setClickable(true);
            holder.acceptButton.setEnabled(true);
            holder.acceptButton.setClickable(true);
            int greenColor = ContextCompat.getColor(context, android.R.color.holo_green_light);
            holder.acceptButton.setBackgroundColor(greenColor);
            int redColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
            holder.cancelButton.setBackgroundColor(redColor);
        } else if (currentStatus.equals("Orderplaced")) {
            holder.acceptButton.setText("Order Processing");
            holder.cancelButton.setVisibility(View.GONE);
        } else if (currentStatus.equals("Cooking")) {
            holder.acceptButton.setText("Order Delivering");
            holder.cancelButton.setVisibility(View.GONE);
        } else if (currentStatus.equals("Delivering")) {
            holder.acceptButton.setText("Order Delivered");
            holder.cancelButton.setVisibility(View.GONE);
        }else if(currentStatus.equals("Delivered")) {
            holder.acceptButton.setText("Done");
            holder.cancelButton.setVisibility(View.GONE);
            updateButtonAppearance(holder,holder.acceptButton);
            int orangeColor = ContextCompat.getColor(context, android.R.color.holo_orange_light);
            holder.acceptButton.setBackgroundColor(orangeColor);
            holder.acceptButton.setClickable(false);
            holder.acceptButton.setEnabled(false);
        } else if (currentStatus.equals("Cancled")) {
            holder.cancelButton.setText("Cancled");
            updateButtonAppearance(holder,holder.cancelButton);
            int orangeColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
            holder.cancelButton.setBackgroundColor(orangeColor);
            holder.cancelButton.setClickable(false);
            holder.cancelButton.setEnabled(false);
   //         holder.cancelButton.getTranslationX(-25dp);
        }

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    // No internet connection, display a toast message
                    Toast.makeText(context, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                } else {
                    // Show progress bar
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.cancelButton.setEnabled(false);
                    // Update status using ExecutorService and Future
                    Future<Void> future = executor.submit(new UpdateStatusTask(holder.cancelButton, "Cancled", order.getOrderId()));
                    sendNotification(cToken, "Cancled", orderId);
                    // Wait for the background task to complete
                    new Handler().postDelayed(() -> {
                        try {
                            future.get();
                            holder.progressBar.setVisibility(View.GONE); // Hide progress bar
                            holder.cancelButton.setEnabled(true);
                            // Update UI as needed
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 2500); // Delay for the same amount as the background operation
                }
            }
        });

// Set click listener for the "Accept" button
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    // No internet connection, display a toast message
                    Toast.makeText(context, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                } else {
                    // Show progress bar
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.acceptButton.setEnabled(false);

                    // Determine new status
                    String newStatus;
                    if (order.getStatus().equals("Ordering")) {
                        newStatus = "Orderplaced";
                    } else if (order.getStatus().equals("Orderplaced")) {
                        newStatus = "Cooking";
                    } else if (order.getStatus().equals("Cooking")) {
                        newStatus = "Delivering";
                    } else if (order.getStatus().equals("Delivering")) {
                        newStatus = "Delivered";
                    } else {
                        return;
                    }

                    // Update status using ExecutorService and Future
                    Future<Void> future = executor.submit(new UpdateStatusTask(holder.acceptButton, newStatus, order.getOrderId()));
                    sendNotification(cToken, newStatus, orderId);
                    // Wait for the background task to complete
                    new Handler().postDelayed(() -> {
                        try {
                            future.get();
                            holder.progressBar.setVisibility(View.GONE); // Hide progress bar
                            holder.acceptButton.setEnabled(true);
                            // Update UI as needed
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 2500); // Delay for the same amount as the background operation
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView itemRecyclerView;
        private foodItemAdapter fooditemadapter;
        private ProgressBar progressBar;
        private TextView name,city,address,phone,payment,note,orderid,total;
        private Button acceptButton,cancelButton;
        private Order order;
        public ViewHolder(@NonNull View itemView,ViewGroup parent) {
            super(itemView);
            itemRecyclerView = itemView.findViewById(R.id.ofoodItemsRecyclerView);
            name=itemView.findViewById(R.id.oshippingNameTextView);
            city=itemView.findViewById(R.id.oshippingCityTextView);
            phone=itemView.findViewById(R.id.oshippingphoneTextView);
            address=itemView.findViewById(R.id.oshippingAddressTextView);
            payment=itemView.findViewById(R.id.opaymentMethodContentTextView);
            note=itemView.findViewById(R.id.onoteContentTextView);
            acceptButton = itemView.findViewById(R.id.oacceptButton);
            orderid=itemView.findViewById(R.id.shippingorderid);
            cancelButton=itemView.findViewById(R.id.ocancelButton);
            total=itemView.findViewById(R.id.oGrandTotal);
            progressBar=itemView.findViewById(R.id.progressBar);


            // Set up the layout manager for the nested RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            itemRecyclerView.setLayoutManager(layoutManager);

            fooditemadapter = new foodItemAdapter(new ArrayList<>(), itemView.getContext()); // Initialize cartAdapter with an empty list
            itemRecyclerView.setAdapter(fooditemadapter);



        }

    }
     private void updateButtonAppearance(ViewHolder holder,Button abutton) {
         // Set the width of the accept button to match the parent's width
         animateButtonWidthChange(abutton, abutton.getWidth(),
                 holder.itemView.getWidth());
         int orangeColor = ContextCompat.getColor(context, android.R.color.holo_orange_dark);
         abutton.setBackgroundColor(orangeColor);

     }
     private void animateButtonWidthChange(Button button, int startWidth, int endWidth) {
         ValueAnimator anim = ValueAnimator.ofInt(startWidth, endWidth);
         anim.addUpdateListener(valueAnimator -> {
             int animatedValue = (int) valueAnimator.getAnimatedValue();
             ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
             layoutParams.width = animatedValue;
             button.setLayoutParams(layoutParams);
         });

         anim.setDuration(400); // Set the duration of the animation in milliseconds
         anim.start();
     }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        // Shut down the executor service
//        if (executor != null && !executor.isShutdown()) {
//            executor.shutdown();
//        }
private void sendNotification(String tokens,String status,String id) {
    SendNotificationTask task = new SendNotificationTask(tokens,status,id);
    new Thread(task).start();
}
    private class SendNotificationTask implements Runnable {
        private String tokens;
        private String status;
        private String id;
        public SendNotificationTask(String tokens,String status,String id) {
            this.tokens = tokens;
            this.status =status;
            this.id =id;
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");

                JSONObject notification = new JSONObject();
                JSONObject body = new JSONObject();

                try {
                    notification.put("title", "Your Order:" + id);
                    notification.put("body", "Your order is being "+status);
                    body.put("to", tokens);
                    body.put("notification", notification);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Error", e.toString());
                }

                RequestBody requestBody = RequestBody.create(mediaType, body.toString());
                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(requestBody)
                        .addHeader("Authorization", "key=AAAAiMxksdE:APA91bFlTJqkD8AVZ36SbzIKPjILBIJOPLYTqgnnXFj4F7xAaO-Qi9ddV7OYxY-Me3zzMDvZC9UXrSfNi54OMfBELA_0RFcHGchf9egUoDjQFQspRCGA-ornfL_mNsXQ7W3QvViIgMtL") // Replace with your server key
                        .addHeader("Content-Type", "application/json")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        // Notification sent successfully
                        Log.d("Notification", "Notification sent successfully");
                    } else {
                        // Notification sending failed
                        Log.d("Notification", "Notification sending failed");
                    }
                } catch (IOException e) {
                    Log.d("error", e.toString());
                }
            }

    }
//    OkHttpClient client=new OkHttpClient();
//    MediaType mediaType=MediaType.parse("application/json");
//        JSONObject notification = new JSONObject();
//        JSONObject body = new JSONObject();
//        try {
//            notification.put("title", "Your Order:" + orderID);
//                    notification.put("body", "Your order is being "+status);
//                   body.put("to",token);
//                   body.put("notification", notification);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.d("Error",e.toString());
//        }
//        RequestBody requestBody=RequestBody.create(mediaType,body.toString());
//        okhttp3.Request request=new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
//                .post(requestBody)
//                .addHeader("Authorization","key=AAAAiMxksdE:APA91bFlTJqkD8AVZ36SbzIKPjILBIJOPLYTqgnnXFj4F7xAaO-Qi9ddV7OYxY-Me3zzMDvZC9UXrSfNi54OMfBELA_0RFcHGchf9egUoDjQFQspRCGA-ornfL_mNsXQ7W3QvViIgMtL")
//                .addHeader("Content-Type","application/json").build();
//        try {
//            Response response = client.newCall(request).execute();
//        }catch (IOException e){
//            Log.d("error",e.toString());
//        }
//    }
private boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
    return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
}
}
