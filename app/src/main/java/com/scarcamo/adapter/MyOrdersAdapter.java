package com.scarcamo.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scarcamo.R;
import com.scarcamo.custom.CTextViewHeavy;
import com.scarcamo.custom.CTextViewThin;
import com.scarcamo.pojo.Result;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ravinder on 2/28/2018.
 */

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {

    private final FragmentActivity activity;
    private final ArrayList<Result> productList;

    public MyOrdersAdapter(FragmentActivity activity, ArrayList<Result> productArrayList) {
    this.activity = activity;
    this.productList = productArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myorders_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.productDescription.setText(productList.get(position).getDescription());
        holder.productName.setText(productList.get(position).getName());
        holder.productPrice.setText("$"+productList.get(position).getPrice());
        String status = productList.get(position).getStatus();
        if(!status.equalsIgnoreCase(""))
        {
            if(status.equalsIgnoreCase("1"))
            {
                holder.productStatus.setText(productList.get(position).getCreated_date()+" - Processing");
            }else if(status.equalsIgnoreCase("2"))
            {
                holder.productStatus.setText(productList.get(position).getCreated_date()+" - Dispatched");
            }else
            {
                holder.productStatus.setText(productList.get(position).getCreated_date()+" - Delivered");
            }
        }

        /*Glide.with(activity)
                .load(productList.get(position).getImage())
                .into(holder.productImage);*/
        if (productList.get(position).getImage() != null && !productList.get(position).getImage().equals("")) {

            Picasso.with(activity)
                    .load(productList.get(position).getImage())
                    .into(holder.productImage);
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        private ImageView productImage;
        private CTextViewHeavy productName;
        private CTextViewThin productPrice;
        private CTextViewThin productDescription,productStatus;

        public MyViewHolder(View view) {
            super(view);


            productImage = (ImageView)view.findViewById( R.id.productImage );
            productName = (CTextViewHeavy)view.findViewById( R.id.product_name );
            productPrice = (CTextViewThin)view.findViewById( R.id.product_price );
            productDescription = (CTextViewThin)view.findViewById( R.id.product_description );
            productStatus = (CTextViewThin)view.findViewById( R.id.product_status );
        }
    }
}
