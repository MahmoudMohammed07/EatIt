package com.android.eatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.eatit.Interface.ItemOnClickListener;
import com.android.eatit.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtFoodName;
    public ImageView foodImageView, favImage, shareImage;

    private ItemOnClickListener itemOnClickListener;

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public FoodViewHolder(View itemView) {
        super(itemView);

        txtFoodName = (TextView) itemView.findViewById(R.id.food_name);
        foodImageView = (ImageView) itemView.findViewById(R.id.food_image);
        favImage = (ImageView) itemView.findViewById(R.id.fav);
        shareImage = (ImageView) itemView.findViewById(R.id.btnShare);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        itemOnClickListener.onClick(view, getAdapterPosition(), false);
    }
}
