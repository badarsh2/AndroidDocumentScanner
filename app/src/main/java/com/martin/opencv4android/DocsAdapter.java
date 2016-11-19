package com.martin.opencv4android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by Adarsh on 9/16/16.
 */
public class DocsAdapter extends RecyclerView.Adapter<DocsAdapter.ViewHolder> {

    Context mContext;
    ArrayList<DocItem> mItems;



    public DocsAdapter(Context context, ArrayList<DocItem> items) {
        mContext = context;
        mItems = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,tstext;
        ImageView contactPic;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.docname);
            tstext = (TextView)itemView.findViewById(R.id.doctimestamp);
            contactPic = (ImageView)itemView.findViewById(R.id.iv1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;
        layout = R.layout.item_gallery;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(mItems.get(position).getName());
        holder.tstext.setText(mItems.get(position).getTimestamp());
        holder.contactPic.setImageBitmap(mItems.get(position).getBitmap());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, DetailActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("folder", mItems.get(position).getName());
                mContext.startActivity(i);
            }
        });
    }



    @Override
    public int getItemCount() {
        return mItems.size();
    }


}