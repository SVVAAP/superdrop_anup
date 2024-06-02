package com.svvaap.superdrop2.adapter;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.svvaap.superdrop2.Bills_Activity;
import com.svvaap.superdrop2.R;
import com.svvaap.superdrop2.methods.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class customers_adapter extends RecyclerView.Adapter<customers_adapter.ViewHolder> {
    private List<Order> orderList;
    private Context context;

    public customers_adapter(List<Order> OrderList, Context context) {
        this.orderList = OrderList;
        this.context = context;

    }

    @NonNull
    @Override
    public customers_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item_v_2, parent, false);
        return new ViewHolder(view, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull customers_adapter.ViewHolder holder, int position) {
        Order order = orderList.get(position);
        // Set default values to prevent NullPointerException
        holder.fooditemadapter = new foodItemAdapter(order.getItems(), context);
        holder.itemRecyclerView.setAdapter(holder.fooditemadapter);
        String gtotal = "₹" + order.getGrandTotal();
        holder.date.setText(order.getDate());
        holder.time.setText(order.getTime());
        holder.orderid.setText(order.getOrderId());
        holder.status.setText(order.getStatus());

        holder.cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatus("Cancled", order.getOrderId(),order.getUserId(),order.getRestId());
            }
        });

        holder.bills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,Bills_Activity.class);
                context.startActivity(intent);
            }
        });


        // Set initial visibility
        holder.processing.setVisibility(View.INVISIBLE);
        holder.accepted.setVisibility(View.INVISIBLE);
        holder.delivered.setVisibility(View.INVISIBLE);
        holder.delivering.setVisibility(View.INVISIBLE);
        holder.cooking.setVisibility(View.INVISIBLE);
        holder.textcancle.setVisibility(View.GONE);
        String currentStatus = order.getStatus();
        assert currentStatus != null;

        holder.cancle.setEnabled(true);

        if (currentStatus.equals("Ordering")) {
            holder.progressBar.setProgress(0);
            holder.processing.setVisibility(View.VISIBLE);
            holder.accepted.setVisibility(View.INVISIBLE);
            holder.cooking.setVisibility(View.INVISIBLE);
            holder.delivering.setVisibility(View.INVISIBLE);
            holder.delivered.setVisibility(View.INVISIBLE);
        } else if (currentStatus.equals("Orderplaced")) {
            holder.progressBar.setProgress(25);
            holder.processing.setVisibility(View.VISIBLE);
            holder.accepted.setVisibility(View.VISIBLE);
            holder.cooking.setVisibility(View.INVISIBLE);
            holder.delivering.setVisibility(View.INVISIBLE);
            holder.delivered.setVisibility(View.INVISIBLE);
        } else if (currentStatus.equals("Cooking")) {
            holder.progressBar.setProgress(50);
            holder.processing.setVisibility(View.VISIBLE);
            holder.accepted.setVisibility(View.VISIBLE);
            holder.cooking.setVisibility(View.VISIBLE);
            holder.delivering.setVisibility(View.INVISIBLE);
            holder.delivered.setVisibility(View.INVISIBLE);
            holder.cancle.setEnabled(false);
        } else if (currentStatus.equals("Delivering")) {
            holder.progressBar.setProgress(75);
            holder.processing.setVisibility(View.VISIBLE);
            holder.accepted.setVisibility(View.VISIBLE);
            holder.cooking.setVisibility(View.VISIBLE);
            holder.delivering.setVisibility(View.VISIBLE);
            holder.delivered.setVisibility(View.INVISIBLE);
            holder.cancle.setEnabled(false);
        } else if (currentStatus.equals("Delivered")) {
            holder.progressBar.setProgress(100);
            holder.processing.setVisibility(View.VISIBLE);
            holder.accepted.setVisibility(View.VISIBLE);
            holder.cooking.setVisibility(View.VISIBLE);
            holder.delivering.setVisibility(View.VISIBLE);
            holder.delivered.setVisibility(View.VISIBLE);
            holder.cancle.setEnabled(false);
        } else if (currentStatus.equals("Cancled")) {
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.processing.setVisibility(View.INVISIBLE);
            holder.accepted.setVisibility(View.INVISIBLE);
            holder.delivered.setVisibility(View.INVISIBLE);
            holder.delivering.setVisibility(View.INVISIBLE);
            holder.cooking.setVisibility(View.INVISIBLE);
            holder.itembackground.setBackgroundResource(R.drawable.red_rounded_edge);
            holder.textcancle.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.INVISIBLE);
            holder.cancle.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView itemRecyclerView;
        private foodItemAdapter fooditemadapter;
        private Button cancle, bills;
        private ImageView processing, cooking, delivering, delivered, accepted, toogleimg;
        private ProgressBar progressBar;
        private ConstraintLayout moreinfo, itembackground, statua_bg;
        private LinearLayout linearLayout;
        private TextView toggltext, date, time, textcancle, orderid, status;

        public ViewHolder(@NonNull View itemView, ViewGroup parent) {
            super(itemView);
            itemRecyclerView = itemView.findViewById(R.id.cfoodItemsRecyclerView);
            progressBar = itemView.findViewById(R.id.c_progressBar);
            processing = itemView.findViewById(R.id.processing_img);
            cooking = itemView.findViewById(R.id.cooking_img);
            delivered = itemView.findViewById(R.id.delivered_img);
            delivering = itemView.findViewById(R.id.delivering_img);
            accepted = itemView.findViewById(R.id.accept_img);
            moreinfo = itemView.findViewById(R.id.more_item);
            toogleimg = itemView.findViewById(R.id.toogle_img);
            toggltext = itemView.findViewById(R.id.toogle_text);
            date = itemView.findViewById(R.id.cdate);
            time = itemView.findViewById(R.id.ctime);
            textcancle = itemView.findViewById(R.id.cancle_text);
            itembackground = itemView.findViewById(R.id.citem_background);
            linearLayout = itemView.findViewById(R.id.linear_status);
            orderid = itemView.findViewById(R.id.order_id_txt);
            statua_bg = itemView.findViewById(R.id.status_constraint);
            status = itemView.findViewById(R.id.status_text);
            cancle = itemView.findViewById(R.id.cancle_bt);
            bills = itemView.findViewById(R.id.bills_bt);
            // Set up layout animation for sliding down
            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING,
                    ObjectAnimator.ofFloat(null, "translationY", -moreinfo.getMeasuredHeight(), 0));
            layoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING,
                    ObjectAnimator.ofFloat(null, "translationY", 0, -moreinfo.getMeasuredHeight()));

            moreinfo.setLayoutTransition(layoutTransition);

            toogleimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle visibility of more_item layout
                    moreinfo.setVisibility(moreinfo.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    statua_bg.setVisibility(statua_bg.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
                    // Rotate the toggle image
                    float rotation = moreinfo.getVisibility() == View.VISIBLE ? 180 : 0;
                    toogleimg.animate().rotation(rotation).start();
                    toggltext.setText(moreinfo.getVisibility() == View.VISIBLE ? "View Less" : "View More");
                }
            });

            // Set up the layout manager for the nested RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            itemRecyclerView.setLayoutManager(layoutManager);

            fooditemadapter = new foodItemAdapter(new ArrayList<>(), itemView.getContext()); // Initialize cartAdapter with an empty list
            itemRecyclerView.setAdapter(fooditemadapter);
        }
    }

    private void updateStatus(String newStatus, String orderId, String userid, String restId) {
        try {
            // Perform your database updates here
            DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference("restaurant_orders").child(restId);
            DatabaseReference custOrderDatabaseReference = FirebaseDatabase.getInstance().getReference("cust_orders").child(userid);
            DatabaseReference udorderDatabaseReference = FirebaseDatabase.getInstance().getReference("cdelivery_orders").child(userid);

            orderDatabaseReference.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("status").setValue(newStatus);
                            if (Objects.equals(newStatus, "Delivered") || Objects.equals(newStatus, "Cancled")) {
                                snapshot.getRef().child("orderStatus").setValue("Done");
                            }
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
                            if (Objects.equals(newStatus, "Delivered") || Objects.equals(newStatus, "Cancled")) {
                                snapshot.getRef().child("orderStatus").setValue("Done");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database read error
                }
            });

            udorderDatabaseReference.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("status").setValue(newStatus);
                            if (Objects.equals(newStatus, "Delivered") || Objects.equals(newStatus, "Cancled")) {
                                snapshot.getRef().child("orderStatus").setValue("Done");
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database read error
                }
            });

            // Simulate a delay to ensure UI reflects the status change

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


}