package com.scarcamo.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scarcamo.R;
import com.scarcamo.adapter.MyNotificationAdapter;
import com.scarcamo.pojo.NotificationPojo;
import com.scarcamo.pojo.OnListFragmentInteractionListener;
import com.scarcamo.pojo.Result;
import com.scarcamo.pojo.ShadePojo;
import com.scarcamo.retrofit.ScarCamoServices;
import com.scarcamo.utilities.Constants;
import com.scarcamo.utilities.SharedPreferenceHelper;
import com.scarcamo.utilities.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NotificationItemFragment extends Fragment implements OnListFragmentInteractionListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private TextView noItem;
    private ArrayList<Result> notificationArrayList;
    private MyNotificationAdapter notiPagerAdapter;
    private RelativeLayout parent;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotificationItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NotificationItemFragment newInstance(int columnCount) {
        NotificationItemFragment fragment = new NotificationItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        View view = inflater.inflate(R.layout.notification_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
           //recyclerView.setAdapter(new MyNotificationAdapter(Result, mListener));
        }
        return view;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpLayout();

        getNotifications();
    }


    private void setUpLayout() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.list);
        parent = (RelativeLayout)getView().findViewById(R.id.parent);
       /* ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        */
       noItem = (TextView) getView().findViewById(R.id.noItem);
        noItem.setVisibility(View.VISIBLE);

    }
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //Toast.makeText(MyActivity.this, "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                final View foregroundView = ((MyNotificationAdapter.ViewHolder) viewHolder).viewForeground;

                getDefaultUIUtil().onSelected(foregroundView);
            }
        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
            final View foregroundView = ((MyNotificationAdapter.ViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final View foregroundView = ((MyNotificationAdapter.ViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().clearView(foregroundView);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
            final View foregroundView = ((MyNotificationAdapter.ViewHolder) viewHolder).viewForeground;

            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            //Remove swiped item from list and notify the RecyclerView
            final int position = viewHolder.getAdapterPosition();


            Snackbar snackbar = Snackbar
                    .make(parent,  " One item removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    notiPagerAdapter.restoreItem(notificationArrayList.get(position), position);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            deleteFromServer(position);

        }
    };



    private void getNotifications() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ScarCamoServices service = retrofit.create(ScarCamoServices.class);
        Call<ShadePojo> shadesRequest;
        String uID = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_ID, "");

        if (uID != null && !uID.equals("")) {
            shadesRequest = service.getPNotification(uID);
        } else {
            shadesRequest = service.getPNotification("1");

        }
        shadesRequest.enqueue(new Callback<ShadePojo>() {
            @Override
            public void onResponse(Call<ShadePojo> call, Response<ShadePojo> response) {

                if (response.body() != null) {
                    if (response.body().getCode().equals("1")) {
                        notificationArrayList = new ArrayList<>();
                        notificationArrayList.addAll(Arrays.asList(response.body().getResult()));
                        noItem.setVisibility(View.INVISIBLE);
                         notiPagerAdapter = new MyNotificationAdapter( notificationArrayList, NotificationItemFragment.this);
                        recyclerView.setAdapter(notiPagerAdapter);
                    } else {
                        if (response.body().getMessage() != null) {
                            if(response.body().getMessage().equalsIgnoreCase("Error occurred!"))
                            {
                                /* Alert box removed from here*/
                               // Utils.showAlertMessage(getActivity(), "No Record Found");
                                noItem.setVisibility(View.VISIBLE);
                            }else {
                                Utils.showAlertMessage(getActivity(), response.body().getMessage());
                            }
                        } else {
                            Utils.showAlertMessage(getActivity(), "Please try after sometime.");

                        }
                    }
                } else {
                    Utils.showAlertMessage(getActivity(), "Server not responding. Please try after sometime.");

                }
            }

            @Override
            public void onFailure(Call<ShadePojo> call, Throwable t) {

            }
        });
    }


    private void deleteFromServer(final int position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ScarCamoServices service = retrofit.create(ScarCamoServices.class);
        Call<NotificationPojo> shadesRequest;
        String uID = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_ID, "");


            shadesRequest = service.deleteNotification(notificationArrayList.get(position).getId());

        shadesRequest.enqueue(new Callback<NotificationPojo>() {
            @Override
            public void onResponse(Call<NotificationPojo> call, Response<NotificationPojo> response) {
                if (response.body() != null) {
                    if (response.body().getCode().equals("1")) {
                        notiPagerAdapter.removeItem(position);
                        notiPagerAdapter.notifyDataSetChanged();
                        //recyclerView.notifyAll();
                    } else {
                        if (response.body().getMessage() != null) {
                            Utils.showAlertMessage(getActivity(), response.body().getMessage());
                        } else {
                            Utils.showAlertMessage(getActivity(), "Please try after sometime.");

                        }
                    }
                } else {
                    Utils.showAlertMessage(getActivity(), "Server not responding. Please try after sometime.");

                }
            }

            @Override
            public void onFailure(Call<NotificationPojo> call, Throwable t) {

            }
        });

    }

    @Override
    public void onListFragmentInteraction(Result item,int pos) {
        deleteFromServer(pos);
    }
}
