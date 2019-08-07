package com.android.eatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.eatit.Interface.ItemOnClickListener;
import com.android.eatit.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName;
    public ImageView menuImageView;

    private ItemOnClickListener itemOnClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);

        txtMenuName = (TextView) itemView.findViewById(R.id.menu_name);
        menuImageView = (ImageView) itemView.findViewById(R.id.menu_image);

        itemView.setOnClickListener(this);

    }

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    @Override
    public void onClick(View view) {
        itemOnClickListener.onClick(view, getAdapterPosition(), false);
    }
}
