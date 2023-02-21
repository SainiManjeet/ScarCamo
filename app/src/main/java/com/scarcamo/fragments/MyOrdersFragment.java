package com.scarcamo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scarcamo.R;
import com.scarcamo.TabbedActivity;
import com.scarcamo.adapter.MyOrdersAdapter;
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


public class MyOrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView noItem;
    private ArrayList<Result> myOrderArrayList;

    public MyOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpLayout();
    }

    private void setUpLayout() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        noItem = (TextView) getView().findViewById(R.id.noItem);

        
        getOrdersFromServer();

    }

    private void getOrdersFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ScarCamoServices service = retrofit.create(ScarCamoServices.class);
        Call<ShadePojo> shadesRequest;
        String uID = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_ID, "");

        if (uID != null && !uID.equals("")) {
            shadesRequest = service.getOrders(uID);

            shadesRequest.enqueue(new Callback<ShadePojo>() {
                @Override
                public void onResponse(Call<ShadePojo> call, Response<ShadePojo> response) {
                    if (response.body()!=null ) {
                        if(response.body().getCode().equals("1")) {
                            myOrderArrayList = new ArrayList<>();
                            myOrderArrayList.addAll(Arrays.asList(response.body().getResult()));

                            MyOrdersAdapter mProductPagerAdapter = new MyOrdersAdapter(getActivity(), myOrderArrayList);
                            recyclerView.setAdapter(mProductPagerAdapter);
                        }else
                        {
                            if(response.body().getMessage()!=null) {

                               /* Alert box removed from here*/
                               // Utils.showAlertMessage(getActivity(), response.body().getMessage());
                            }else{
                                Utils.showAlertMessage(getActivity(), "Please try after sometime.");

                            }
                        }
                    } else

                    {
                        noItem.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<ShadePojo> call, Throwable t) {
                    noItem.setVisibility(View.VISIBLE);
                }
            });
        }else
        {
            //Utils.showSnackBar(getActivity(),getView().findViewById(R.id.parent),"Create account first.");
            ((TabbedActivity)getActivity()).showLoginScreen();
        }
    }

}
