package com.scarcamo.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scarcamo.R;
import com.scarcamo.custom.CTextViewHeavy;
import com.scarcamo.pojo.AboutPojo;
import com.scarcamo.retrofit.ScarCamoServices;
import com.scarcamo.utilities.Constants;
import com.scarcamo.utilities.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {


    private CTextViewHeavy aboutText;

    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpView();

        getDataFromServer();

    }


    private void setUpView() {

        aboutText = (CTextViewHeavy)getView().findViewById(R.id.aboutText);
        aboutText.setTypeface(aboutText.getTypeface(), Typeface.ITALIC);

    }


    private void getDataFromServer() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Utils.showDialog();

            ScarCamoServices service = retrofit.create(ScarCamoServices.class);
            Call<AboutPojo> shadesRequest = service.aboutUs();
            shadesRequest.enqueue(new Callback<AboutPojo>() {
                @Override
                public void onResponse(Call<AboutPojo> call, Response<AboutPojo> response) {
                    if (response.body()!=null ) {
                        if(response.body().getCode().equals("1")) {
                            aboutText.setText(response.body().getResult().getDescription());
                        }else {

                                //Utils.showAlertMessage(getActivity(), response.body().getMessage());

                        }

                    } else {
                        Utils.showAlertMessage(getActivity(), "Server not responding. Please try after sometime.");

                    }
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Call<AboutPojo> call, Throwable t) {
                    Utils.hideDialog();
                    Utils.showAlertMessage(getActivity(), t.getLocalizedMessage());

                }
            });
        }


}
