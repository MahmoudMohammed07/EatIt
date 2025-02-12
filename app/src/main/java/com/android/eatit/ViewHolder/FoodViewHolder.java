package com.android.eatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.eatit.Interface.ItemOnClickListener;
import com.android.eatit.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtFoodName, foodPrice;
    public ImageView foodImageView, favImage, shareImage, quickCart;

    private ItemOnClickListener itemOnClickListener;

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public FoodViewHolder(View itemView) {
        super(itemView);

        txtFoodName = (TextView) itemView.findViewById(R.id.food_name);
        foodPrice = (TextView) itemView.findViewById(R.id.food_price);
        foodImageView = (ImageView) itemView.findViewById(R.id.food_image);
        favImage = (ImageView) itemView.findViewById(R.id.fav);
        shareImage = (ImageView) itemView.findViewById(R.id.btnShare);
        quickCart = (ImageView) itemView.findViewById(R.id.btn_quick_cart);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        itemOnClickListener.onClick(view, getAdapterPosition(), false);
    }
}
