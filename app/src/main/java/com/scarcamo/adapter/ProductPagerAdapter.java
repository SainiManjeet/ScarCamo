package com.scarcamo.adapter;

import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scarcamo.R;
import com.scarcamo.custom.CButtonBold;
import com.scarcamo.custom.CTextViewHeavy;
import com.scarcamo.pojo.OnListFragmentInteractionListener;
import com.scarcamo.pojo.Result;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ravinder on 2/28/2018.
 */

public class ProductPagerAdapter extends RecyclerView.Adapter<ProductPagerAdapter.MyViewHolder> {

    private final FragmentActivity activity;
    private final ArrayList<Result> productList;
    private final OnListFragmentInteractionListener listener;

    public ProductPagerAdapter(FragmentActivity activity, ArrayList<Result> productArrayList, OnListFragmentInteractionListener onListFragmentInteractionListener) {
        this.activity = activity;
        this.productList = productArrayList;
        this.listener = onListFragmentInteractionListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_item_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.productDescription.setText(productList.get(position).getDescription());
        holder.productName.setText(productList.get(position).getName());
        holder.productName.setTypeface(holder.productName.getTypeface(), Typeface.BOLD);

        holder.productPrice.setText("$" + productList.get(position).getPrice());
        holder.productPrice.setTypeface(holder.productPrice.getTypeface(), Typeface.BOLD_ITALIC);

        holder.buyBtn.setTag(position);
        holder.buyBtn.setTypeface(holder.buyBtn.getTypeface(), Typeface.ITALIC);

        Log.e("Product Img=","IMGGG"+productList.get(position).getImage());



        if (productList.get(position).getImage() != null  && !productList.get(position).getImage().equalsIgnoreCase("")) {
            Picasso.with(activity)
                    .load(productList.get(position).getImage())
                    .into(holder.productImage);
        }
        holder.buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onListFragmentInteraction(productList.get(Integer.parseInt(v.getTag().toString())), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        private ImageView productImage;
        private CTextViewHeavy productName;
        private CTextViewHeavy productPrice;
        private CTextViewHeavy productDescription;
        private CButtonBold buyBtn;

        public MyViewHolder(View view) {
            super(view);


            productImage = (ImageView) view.findViewById(R.id.productImage);
            productName = (CTextViewHeavy) view.findViewById(R.id.product_name);
            productPrice = (CTextViewHeavy) view.findViewById(R.id.product_price);
            productDescription = (CTextViewHeavy) view.findViewById(R.id.product_description);
            buyBtn = (CButtonBold) view.findViewById(R.id.buyBtn);
        }
    }
}
