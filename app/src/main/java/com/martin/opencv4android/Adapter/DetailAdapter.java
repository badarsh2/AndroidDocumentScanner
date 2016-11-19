package com.martin.opencv4android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.martin.opencv4android.DetailItem;
import com.martin.opencv4android.ImageActivity;
import com.martin.opencv4android.R;

import java.util.ArrayList;

/**
 * Created by Adarsh on 9/16/16.
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

    Context mContext;
    ArrayList<DetailItem> mItems;



    public DetailAdapter(Context context, ArrayList<DetailItem> items) {
        mContext = context;
        mItems = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView contactPic;
        public ViewHolder(View itemView) {
            super(itemView);
            contactPic = (ImageView)itemView.findViewById(R.id.iv1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;
        layout = R.layout.item_detail;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.contactPic.setImageBitmap(mItems.get(position).getBitmap());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ImageActivity.class);
                i.putExtra("pathkey", mItems.get(position).getPath());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });
    }



    @Override
    public int getItemCount() {
        return mItems.size();
    }


}