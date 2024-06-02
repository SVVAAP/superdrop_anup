package com.svvaap.superdrop2.adapter;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.svvaap.superdrop2.R;
import com.svvaap.superdrop2.methods.Order;

import java.util.ArrayList;
import java.util.List;

public class customers_adapter_2 extends RecyclerView.Adapter<customers_adapter_2.ViewHolder>{
    private List<Order> orderList;
    private Context context;
    public customers_adapter_2(List<Order> OrderList, Context context) {
        this.orderList = OrderList;
        this.context = context;

    }
    @NonNull
    @Override
    public customers_adapter_2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item_v, parent, false);
        return new ViewHolder(view, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull customers_adapter_2.ViewHolder holder, int position) {
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
        holder.time.setText(order.getTime());
        holder.orderid.setText(order.getOrderId());
        holder.status.setText(order.getStatus());
       String currentStatus = order.getStatus();

        // Set initial visibility

     if(currentStatus.equals("Cancled")) {
            holder.itembackground.setBackgroundResource(R.drawable.red_rounded_edge);
          //  holder.textcancle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView itemRecyclerView;
        private foodItemAdapter fooditemadapter;
        private ImageView toogleimg;
        private ConstraintLayout moreinfo,itembackground,statua_bg;
        private TextView name,city,address,phone,payment,note,total,toggltext,date,time,textcancle,orderid,status;
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
            moreinfo=itemView.findViewById(R.id.more_item);
            toogleimg=itemView.findViewById(R.id.toogle_img);
            toggltext=itemView.findViewById(R.id.toogle_text);
            date=itemView.findViewById(R.id.cdate);
            time=itemView.findViewById(R.id.ctime);
            textcancle=itemView.findViewById(R.id.cancle_text);
            itembackground=itemView.findViewById(R.id.citem_background);
            orderid=itemView.findViewById(R.id.order_id_txt);
            statua_bg=itemView.findViewById(R.id.status_constraint);
            status=itemView.findViewById(R.id.status_text);
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
                    statua_bg.setVisibility(statua_bg.getVisibility()== View.VISIBLE ? View.VISIBLE : View.GONE);
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