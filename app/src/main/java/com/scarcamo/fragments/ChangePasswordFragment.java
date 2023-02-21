package com.scarcamo.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.scarcamo.R;
import com.scarcamo.custom.CButtonBold;
import com.scarcamo.pojo.ShadePojo;
import com.scarcamo.retrofit.ScarCamoServices;
import com.scarcamo.utilities.Constants;
import com.scarcamo.utilities.SharedPreferenceHelper;
import com.scarcamo.utilities.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private CButtonBold submitBtn;
    private EditText confirmPasswordEditText;
    private String message;
    private Long userIdIndex;
    private ImageView close_activityImageView;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpView();
    }

    private void setUpView() {

        oldPasswordEditText = (EditText) getView().findViewById(R.id.oldPassword);
        newPasswordEditText = (EditText) getView().findViewById(R.id.new_password);
        confirmPasswordEditText = (EditText) getView().findViewById(R.id.confirm_password);
        submitBtn = (CButtonBold) getView().findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == submitBtn) {
            if (validate()) {
               callService();
                }else {
                Utils.showAlertMessage(getActivity(), message);
            }
        }
    }


    private boolean validate() {
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String userName = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_NAME, "");


        if (oldPassword.equals("") || newPassword.equals("") || confirmPassword.equals("")) {
            message = "All fields required.";
            return false;
        } else if (!newPassword.equals(confirmPassword)) {
            message = "Password mismatch.";
            return false;
        } else if (!confirmPassword.equals(newPassword)) {
            message = "Wrong password.";
            return false;
        } else if (confirmPassword.equals(newPassword)) {
            return true;
        } else {
            message = "Please Create Account.";
            return false;
        }


    }

    private void callService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        String uID = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_ID, "");

        Utils.showDialog();
        ScarCamoServices service = retrofit.create(ScarCamoServices.class);
        Call<ShadePojo> shadesRequest = service.changePassword(uID,oldPasswordEditText.getText().toString(), newPasswordEditText.getText().toString());
        shadesRequest.enqueue(new Callback<ShadePojo>() {
            @Override
            public void onResponse(Call<ShadePojo> call, Response<ShadePojo> response) {
                if (response.body()!=null ) {
                    if(response.body().getCode().equals("1")) {
                        Toast.makeText(getActivity(), "Password Successfully changed.", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();


                    }else
                    {
                        if(response.body().getMessage()!=null) {
                            Utils.showAlertMessage(getActivity(), response.body().getMessage());
                        }else{
                            Utils.showAlertMessage(getActivity(), "Please try after sometime.");

                        }
                    }
                } else {
                    Utils.showAlertMessage(getActivity(), "Server not responding. Please try after sometime.");

                }
                Utils.hideDialog();
            }

            @Override
            public void onFailure(Call<ShadePojo> call, Throwable t) {
                Utils.hideDialog();
                Utils.showAlertMessage(getActivity(), "Login Failed");

            }
        });

    }

}
