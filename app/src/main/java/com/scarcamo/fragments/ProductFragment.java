package com.scarcamo.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scarcamo.R;
import com.scarcamo.TabbedActivity;
import com.scarcamo.adapter.ProductPagerAdapter;
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


public class ProductFragment extends Fragment implements OnListFragmentInteractionListener {
    private ArrayList<Result> productArrayList;
    private RecyclerView recyclerView;
    private TextView selectedColor;
    private boolean isButtonClicked = false;
    private boolean isInflateLayout = false;

    public ProductFragment() {
        // Required empty public constructor
    }


    View _rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        if (Constants.isToneChanged) {
            _rootView = null;
            Constants.isToneChanged = false;
        }
        if (_rootView == null) {
            // Inflate the layout for this fragment
            _rootView = inflater.inflate(R.layout.fragment_product, container, false);
            isInflateLayout = true;
        } else {
            isInflateLayout = false;
        }
        return _rootView;
    }

    @Override
    public void onDestroyView() {
        if (_rootView.getParent() != null) {
            ((ViewGroup) _rootView.getParent()).removeView(_rootView);
        }
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isInflateLayout) {
            setUpLayout();

        }
    }

    private void setUpLayout() {
        selectedColor = (TextView) getView().findViewById(R.id.selectedColor);
        if (Constants.SelecteResult != null) {
            String[] rgbArray = Constants.SelecteResult.getColor_code().split(",");
            int color = Constants.SelectedColor;
            int red = 0, green = 0, blue = 0;

            red = Integer.parseInt(rgbArray[0]);
            green = Integer.parseInt(rgbArray[1]);
            blue = Integer.parseInt(rgbArray[2]);
            if (red != 0 && green != 0 && blue != 0) {
                selectedColor.setBackgroundColor(Color.rgb(red, green, blue));
                selectedColor.setText("Your Skin Tone");
            }
        }
        recyclerView = (RecyclerView) getView().findViewById(R.id.productList);

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);

        getProducts();
    }

    private void getProducts() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ScarCamoServices service = retrofit.create(ScarCamoServices.class);
        Call<ShadePojo> shadesRequest;
        if (Constants.SelecteResult != null) {
            shadesRequest = service.getProducts(Constants.SelecteResult.getId());
        } else {
            shadesRequest = service.getProducts("1");

        }
        shadesRequest.enqueue(new Callback<ShadePojo>() {
            @Override
            public void onResponse(Call<ShadePojo> call, Response<ShadePojo> response) {
                if (response.body() != null) {
                    if (response.body().getCode().equals("1")) {
                        productArrayList = new ArrayList<>();
                        productArrayList.addAll(Arrays.asList(response.body().getResult()));
                        if (productArrayList.get(0).getImage().equalsIgnoreCase("")) {
                            productArrayList.get(0).setImage(response.body().getProd_image());
                        }
                        ProductPagerAdapter mProductPagerAdapter = new ProductPagerAdapter(getActivity(), productArrayList, ProductFragment.this);
                        recyclerView.setAdapter(mProductPagerAdapter);
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
            public void onFailure(Call<ShadePojo> call, Throwable t) {

            }
        });
    }


    @Override
    public void onListFragmentInteraction(Result item, int pos) {
        String uID = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_ID, "");

       if(selectedColor.getText().toString().equalsIgnoreCase("Your skin tone not selected"))
       {
           Utils.showAlertMessage(getActivity(), "Your skin tone not selected.");

           return;
       }
        if (uID.equals("")) {

            Constants.isBuyClicked = true;
            Constants.userSelectedItem = item;
            ((TabbedActivity) getActivity()).showLoginScreen();
        } else {
            if(Constants.SelecteResult!=null) {
                AddressFragment addressFragment = new AddressFragment();
                Bundle b = new Bundle();
                b.putString("from", "productScreen");
                b.putSerializable("item", item);
                addressFragment.setArguments(b);
                Utils.changeFragment(getActivity(), addressFragment);
            }else
            {
                Utils.showAlertMessage(getActivity(), "Scan or Choose tone before selecting product.");

            }

        }
    }


}
