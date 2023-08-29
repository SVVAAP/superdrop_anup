package com.example.superdrop2.adapter;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop2.R;
import com.example.superdrop2.methods.Order;

import java.util.ArrayList;
import java.util.List;

public class customers_adapter extends RecyclerView.Adapter<customers_adapter.ViewHolder>{
    private List<Order> orderList;
    private Context context;
    public customers_adapter(List<Order> OrderList, Context context) {
        this.orderList = OrderList;
        this.context = context;

    }
    @NonNull
    @Override
    public customers_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item_v, parent, false);
        return new customers_adapter.ViewHolder(view,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull customers_adapter.ViewHolder holder, int position) {
        Order order = orderList.get(position);
     // Set default values to prevent NullPointerException
        holder.name.setText(order.getShippingName() != null ? order.getShippingName() : "N/A");
        holder.phone.setText(order.getContactInstructions() != null ? order.getContactInstructions() : "N/A");
        holder.city.setText(order.getShippingCity() != null ? order.getShippingCity() : "N/A");
        holder.address.setText(order.getShippingAddress() != null ? order.getShippingAddress() : "N/A");
        holder.payment.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A");
        holder.note.setText(order.getNote() != null ? order.getNote() : "N/A");
        holder.fooditemadapter = new foodItemAdapter(order.getItems(), context);
        holder.itemRecyclerView.setAdapter(holder.fooditemadapter);
        String gtotal="₹"+order.getGrandTotal();
        holder.total.setText(gtotal);
        holder.date.setText(order.getDate());

        // Set initial visibility
        holder.processing.setVisibility(View.INVISIBLE);
        holder.accepted.setVisibility(View.INVISIBLE);
        holder.delivered.setVisibility(View.INVISIBLE);
        holder.delivering.setVisibility(View.INVISIBLE);
        holder.cooking.setVisibility(View.INVISIBLE);


        String currentStatus=order.getStatus();
        if (currentStatus != null && currentStatus.equals("Ordering"))  {
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
        } else if (currentStatus.equals("Processing")) {
            holder.progressBar.setProgress(50);
            holder.processing.setVisibility(View.VISIBLE);
            holder.accepted.setVisibility(View.VISIBLE);
            holder.cooking.setVisibility(View.VISIBLE);
            holder.delivering.setVisibility(View.INVISIBLE);
            holder.delivered.setVisibility(View.INVISIBLE);
        } else if (currentStatus.equals("Delivering")) {
            holder.progressBar.setProgress(75);
            holder.processing.setVisibility(View.VISIBLE);
            holder.accepted.setVisibility(View.VISIBLE);
            holder.cooking.setVisibility(View.VISIBLE);
            holder.delivering.setVisibility(View.VISIBLE);
            holder.delivered.setVisibility(View.INVISIBLE);
        }else if(currentStatus.equals("Delivered")) {
            holder.progressBar.setProgress(100);
            holder.processing.setVisibility(View.VISIBLE);
            holder.accepted.setVisibility(View.VISIBLE);
            holder.cooking.setVisibility(View.VISIBLE);
            holder.delivering.setVisibility(View.VISIBLE);
            holder.delivered.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView itemRecyclerView;
        private foodItemAdapter fooditemadapter;
        private ImageView processing,cooking,delivering,delivered,accepted,toogleimg;
        private ProgressBar progressBar;
        private ConstraintLayout moreinfo;
        private TextView name,city,address,phone,payment,note,total,toggltext,date;
        public ViewHolder(@NonNull View itemView,ViewGroup parent) {
            super(itemView);
            Order order;
            itemRecyclerView = itemView.findViewById(R.id.cfoodItemsRecyclerView);
            name=itemView.findViewById(R.id.cshippingNameTextView);
            city=itemView.findViewById(R.id.cshippingCityTextView);
            phone=itemView.findViewById(R.id.cshippingphoneTextView);
            address=itemView.findViewById(R.id.cshippingAddressTextView);
            payment=itemView.findViewById(R.id.cpaymentMethodContentTextView);
            note=itemView.findViewById(R.id.cnoteContentTextView);
            total=itemView.findViewById(R.id.ctotal);
            progressBar=itemView.findViewById(R.id.c_progressBar);
            processing=itemView.findViewById(R.id.processing_img);
            cooking=itemView.findViewById(R.id.cooking_img);
            delivered=itemView.findViewById(R.id.delivered_img);
            delivering=itemView.findViewById(R.id.delivering_img);
            accepted=itemView.findViewById(R.id.accept_img);
            moreinfo=itemView.findViewById(R.id.more_item);
            toogleimg=itemView.findViewById(R.id.toogle_img);
            toggltext=itemView.findViewById(R.id.toogle_text);
            date=itemView.findViewById(R.id.cdate);
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
                    moreinfo.setVisibility(
                            moreinfo.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
                    );

                    // Rotate the toggle image
                    float rotation = moreinfo.getVisibility() == View.VISIBLE ? 180 : 0;
                    toogleimg.animate().rotation(rotation).start();
                    toggltext.setText(moreinfo.getVisibility()==View.VISIBLE ? "View Less" : "View More");
                }
            });

            // Set up the layout manager for the nested RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            itemRecyclerView.setLayoutManager(layoutManager);

            fooditemadapter = new foodItemAdapter(new ArrayList<>(), itemView.getContext()); // Initialize cartAdapter with an empty list
            itemRecyclerView.setAdapter(fooditemadapter);
        }
    }

}
