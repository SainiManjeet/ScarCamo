package com.scarcamo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scarcamo.R;
import com.scarcamo.pojo.Result;

import java.util.ArrayList;


/**
 * Created by Ravinder on 2/21/2018.
 */

    public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {

    private final int layout;
    private Context mContext;
        private ArrayList<Result> mResources;



    public DeckAdapter(Activity introActivity, ArrayList<Result> mlayoutResources, int item_layout) {
            this.mContext = introActivity;
            this.mResources = mlayoutResources;
            this.layout = item_layout;

        }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String color = mResources.get(position).getColor_code();
        String[] rgbArray = color.split(",");
        int red=0,green=0,blue=0;
        if(rgbArray.length==3)
        {
            red = Integer.parseInt(rgbArray[0]);
            green = Integer.parseInt(rgbArray[1]);
            blue = Integer.parseInt(rgbArray[2]);


        }
        holder.image.setBackgroundColor(Color.rgb(red,green,blue));
        /*Glide.with(holder.itemView.getContext())
                .load(iconTint)
                .listener(new TintOnLoad(holder.image, iconTint))
                .into(holder.image);*/


    }



    @Override
    public int getItemCount() {
        return mResources.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private View overlay;
        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.city_image);

        }


    }


}



