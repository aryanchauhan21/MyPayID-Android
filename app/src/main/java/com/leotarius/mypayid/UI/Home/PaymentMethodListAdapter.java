package com.leotarius.mypayid.UI.Home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.leotarius.mypayid.Models.PaymentMethodPack;
import com.leotarius.mypayid.R;

import java.util.ArrayList;

public class PaymentMethodListAdapter extends RecyclerView.Adapter<PaymentMethodListAdapter.MethodViewHolder> {
    private ArrayList<PaymentMethodPack> methodList;
    private LifecycleOwner lifecycleOwner;
    private Context context;

    public PaymentMethodListAdapter(Context context, LifecycleOwner lifecycleOwner, ArrayList<PaymentMethodPack> methodList) {
        this.lifecycleOwner = lifecycleOwner;
        this.methodList = methodList;
        this.context = context;
    }

    public void updateList(ArrayList<PaymentMethodPack> newMethodList){
        methodList.clear();
        methodList.addAll(newMethodList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_element, parent, false);
        MethodViewHolder holder = new MethodViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MethodViewHolder holder, int position) {
        holder.username.setText(methodList.get(position).getPaymentMethod().getUserName());
        holder.phone.setText(methodList.get(position).getPaymentMethod().getPhone());
        String providerName = methodList.get(position).getPaymentMethod().getPaymentProvider();
        holder.provider.setText(providerName);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentDashboard.Companion.getDashboardViewModel().deletePaymentMethod(methodList.get(position).getKey(), methodList.get(position).getPaymentMethod().getGlobalKey());
                FragmentDashboard.Companion.getDashboardViewModel().isDeleted().observe(lifecycleOwner, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        switch (integer){
                            case 0 : {
                                holder.delete.setVisibility(View.GONE);
                                holder.progress.setVisibility(View.VISIBLE);
                                break;
                            }

                            case 1: {
                                // Do nothing since the card will be deleted by now.
                                break;
                            }

                            case -1: {
                                // It means we couldn't delete the method.
                                // Make the delete option visible again and show message
                                holder.delete.setVisibility(View.VISIBLE);
                                holder.progress.setVisibility(View.GONE);
                                Toast.makeText(context, "Error occurred! Couldn't delete.", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                });
            }
        });

        Drawable drawable;
        Resources resources = holder.itemView.getResources();

        switch(providerName){
            case "BHIM" : drawable = resources.getDrawable(R.drawable.bhim); break;
            case "PhonePe" : drawable = resources.getDrawable(R.drawable.phonepe); break;
            case "Google Pay" : drawable = resources.getDrawable(R.drawable.gpay); break;
            case "PayTm" : drawable = resources.getDrawable(R.drawable.paytm); break;
            default: drawable = resources.getDrawable(R.drawable.paytm);
        }
        holder.icon.setImageDrawable(drawable);

        switch(position%5){
            case 0 : drawable = resources.getDrawable(R.drawable.gradient_1); break;
            case 1 : drawable = resources.getDrawable(R.drawable.gradient_2); break;
            case 2 : drawable = resources.getDrawable(R.drawable.gradient_3); break;
            case 3 : drawable = resources.getDrawable(R.drawable.gradient_4); break;
            default: drawable = resources.getDrawable(R.drawable.gradient_5); break;
        }
        holder.cardBack.setBackground(drawable);

        // Adjusting padding of last item.
        if(methodList.size()-1 == position){
            float scale = holder.itemView.getResources().getDisplayMetrics().density;
            int top=20, bottom=40, left=15, right=15;

            // converting in Pixels from DP
            top = (int) (top*scale + 0.5f);
            bottom = (int) (bottom*scale + 0.5f);
            left = (int) (left*scale + 0.5f);
            right = (int) (right*scale + 0.5f);

            holder.base.setPadding(left, top, right, bottom);
        }
    }

    @Override
    public int getItemCount() {
        return methodList.size();
    }

    public class MethodViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView username, phone, provider;
        ConstraintLayout cardBack;
        LinearLayout base;
        ImageButton delete;
        ProgressBar progress;
        public MethodViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            username = itemView.findViewById(R.id.username);
            provider = itemView.findViewById(R.id.provider);
            phone = itemView.findViewById(R.id.phone);
            cardBack = itemView.findViewById(R.id.card_back);
            base = itemView.findViewById(R.id.base);
            delete = itemView.findViewById(R.id.delete);
            progress = itemView.findViewById(R.id.progress);
        }
    }
}
