package com.scarcamo.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.scarcamo.R;
import com.scarcamo.custom.CButtonBold;
import com.scarcamo.pojo.ShadePojo;
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
public class ForgotPasswordFragment extends Fragment implements View.OnClickListener{


    private String message;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    private EditText yourEmail;
    private CButtonBold sendBtn;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpLayout();
    }

    private void setUpLayout() {

        yourEmail = (EditText)getView().findViewById( R.id.yourEmail );

        sendBtn = (CButtonBold)getView().findViewById( R.id.sendBtn );
        initializeListener();
    }

    private void initializeListener() {
        sendBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(getActivity());
        if(v == sendBtn){

            if(validate())
            {
                callService();
            }else
            {
                Utils.showSnackBar(getActivity(),getView().findViewById(R.id.parent),message);

            }
        }
    }

    private boolean validate() {

        String emailString = yourEmail.getText().toString();

        if ( emailString.equals("") ) {
            message = "All fields required.";
            return false;
        }else if(!Utils.isValidEmail(emailString))
        {
            message = "Please enter a valid email.";
            return false;
        }
        return true;

    }

    private void callService() {
        Utils.hideKeyboard(getActivity());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Utils.showDialog();
        ScarCamoServices service = retrofit.create(ScarCamoServices.class);
        Call<ShadePojo> shadesRequest = service.forgetPassword(yourEmail.getText().toString());

        shadesRequest.enqueue(new Callback<ShadePojo>() {
            @Override
            public void onResponse(Call<ShadePojo> call, Response<ShadePojo> response) {
                if (response.body()!=null ) {
                    if(response.body().getCode().equals("1")) {
                        Utils.showSnackBar(getActivity(), getView().findViewById(R.id.parent), "Your password has been sent to your inbox. Please check your mail.");
                        getActivity().getSupportFragmentManager().popBackStack();
                    }else
                    {

                    }
                }else {
                    if(response.body().getMessage()!=null) {
                        Utils.showAlertMessage(getActivity(), response.body().getMessage());
                    }else{
                        Utils.showAlertMessage(getActivity(), "Please try after sometime.");

                    }
                }
                Utils.hideDialog();

            }

            @Override
            public void onFailure(Call<ShadePojo> call, Throwable t) {
                Utils.hideDialog();
                Utils.showAlertMessage(getActivity(), "Something went wrong.");

            }
        });


    }

}
