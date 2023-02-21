package com.scarcamo.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scarcamo.ColorPickerActivity;
import com.scarcamo.R;
import com.scarcamo.custom.CTextViewHeavy;
import com.scarcamo.custom.CTextViewThin;
import com.scarcamo.pojo.OnListFragmentInteractionListener;
import com.scarcamo.pojo.Result;

import java.util.ArrayList;

/**
 * Created by Ravinder on 2/28/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private final FragmentActivity activity;
    private final ArrayList<Result> productList;
    private final OnListFragmentInteractionListener listener;

    public HistoryAdapter(FragmentActivity activity, ArrayList<Result> productArrayList, OnListFragmentInteractionListener onListFragmentInteractionListener) {
        this.activity = activity;
        this.productList = productArrayList;
        this.listener = onListFragmentInteractionListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.previous_scanned_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvType.setText(productList.get(position).getModify_date());

        String color = productList.get(position).getColor_code();
        holder.tvPos.setText("" + (position + 1));
        holder.cvRoomCard.setTag(position);
        String[] rgbArray = color.split(",");
        int red = 0, green = 0, blue = 0;
        if (rgbArray.length == 3) {
            red = Integer.parseInt(rgbArray[0]);
            green = Integer.parseInt(rgbArray[1]);
            blue = Integer.parseInt(rgbArray[2]);
            holder.productImage.setBackgroundColor(Color.rgb(red, green, blue));
        }

        holder.cvRoomCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(activity, Integer.parseInt(v.getTag().toString()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView tvType, tvPos;
        public CardView cvRoomCard;
        public ImageView productImage;

        public MyViewHolder(View view) {
            super(view);

            cvRoomCard = (CardView) view;
            tvType = (TextView) view.findViewById(R.id.tvTypeName);
            tvPos = (TextView) view.findViewById(R.id.textViewPos);
            productImage = (ImageView) view.findViewById(R.id.img_roomType);
        }
    }


    public void showDialog(final Activity activity, final int pos) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        CTextViewHeavy headerText = (CTextViewHeavy)dialog.findViewById(R.id.txt_dia);
        headerText.setTypeface(headerText.getTypeface(), Typeface.BOLD_ITALIC);
        CTextViewThin text_message= (CTextViewThin)dialog.findViewById(R.id.text_message);
        text_message.setTypeface(text_message.getTypeface(), Typeface.ITALIC);

        Button dialogYesButton = (Button) dialog.findViewById(R.id.btn_yes);
        dialogYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onListFragmentInteraction(productList.get(pos), pos);
                dialog.dismiss();
            }
        });

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_no);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentColorPickerActivity = new Intent(activity, ColorPickerActivity.class);
                activity.startActivity(intentColorPickerActivity);
                dialog.dismiss();
                activity.onBackPressed();

            }
        });

        dialog.show();


    }
}
