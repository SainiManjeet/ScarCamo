package com.scarcamo.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.scarcamo.R;
import com.scarcamo.custom.CTextViewHeavy;
import com.scarcamo.pojo.OnListFragmentInteractionListener;
import com.scarcamo.pojo.Result;

import java.util.List;


public class MyNotificationAdapter extends RecyclerView.Adapter<MyNotificationAdapter.ViewHolder> {

    private final List<Result> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyNotificationAdapter(List<Result> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).getMessage());
        holder.mContentView.setText(mValues.get(position).getMessage());
        holder.mContentView.setTypeface(holder.mContentView.getTypeface(), Typeface.ITALIC);

        holder.deleteImageView.setTag(position);
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                   int pos = Integer.parseInt(holder.deleteImageView.getTag().toString());
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem,pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();


    }
    public void restoreItem(Result item, int position) {
        mValues.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mValues.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final CTextViewHeavy mContentView;
        private final ImageView deleteImageView;
        public Result mItem;
        public RelativeLayout viewBackground, viewForeground;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (CTextViewHeavy) view.findViewById(R.id.content);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
            deleteImageView = (ImageView)view.findViewById(R.id.deleteImageView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
