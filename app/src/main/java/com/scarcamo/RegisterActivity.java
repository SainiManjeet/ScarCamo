package com.scarcamo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.scarcamo.custom.CButtonBold;
import com.scarcamo.custom.CTextViewThin;
import com.scarcamo.pojo.RegistrationPojo;
import com.scarcamo.pojo.User;
import com.scarcamo.retrofit.ScarCamoServices;
import com.scarcamo.utilities.Constants;
import com.scarcamo.utilities.SharedPreferenceHelper;
import com.scarcamo.utilities.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterActivity extends Fragment implements View.OnClickListener{


    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private CButtonBold registerBtn;
    private LinearLayout rootLayout;
    private CTextViewThin loginLink;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_register, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpView();
    }


    private void setUpView() {
        rootLayout = (LinearLayout) getView().findViewById(R.id.rootLayout);
        usernameEditText = (EditText)getView().findViewById( R.id.username_EditText );
        passwordEditText = (EditText)getView().findViewById( R.id.password_EditText );
        emailEditText = (EditText)getView().findViewById( R.id.email_EditText );
        registerBtn = (CButtonBold)getView().findViewById( R.id.registerBtn );
        loginLink = (CTextViewThin)getView().findViewById(R.id.loginHere);
        initializeClickListeners();
    }


    private void initializeClickListeners() {
        rootLayout.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        loginLink.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == rootLayout) {
            Utils.hideKeyboard(getActivity());

        }else if(v == registerBtn)
        {
            if(usernameEditText.getText().toString().equals("") ||passwordEditText.getText().toString().equals("") || emailEditText.getText().toString().equals("")){
                Utils.showAlertMessage(getActivity(),"All fields required.");

            }else if(!Utils.isValidEmail(emailEditText.getText().toString())){
                Utils.showAlertMessage(getActivity(),"Please enter valid email address.");

            }
            else
                registerUser();

        }else  if (v == loginLink) {
            getActivity().onBackPressed();

        }
    }

    private void registerUser() {
        String android_id = FirebaseInstanceId.getInstance().getToken();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Utils.showDialog();
        ScarCamoServices service = retrofit.create(ScarCamoServices.class);
        User mUser = new User(usernameEditText.getText().toString(),emailEditText.getText().toString(),passwordEditText.getText().toString(),"2",android_id);
        //Call<ShadePojo> shadesRequest = service.createUser(mUser.getUsername(),mUser.getPassword(),mUser.getEmail(),mUser.getDeviceToken(),mUser.getDeviceType());
        Call<RegistrationPojo> shadesRequest = service.createUser(mUser.getUsername(),mUser.getPassword(),mUser.getEmail(),mUser.getDeviceType(),mUser.getDeviceToken());

        shadesRequest.enqueue(new Callback<RegistrationPojo>() {
            @Override
            public void onResponse(Call<RegistrationPojo> call, Response<RegistrationPojo> response) {
                if (response.body()!=null ) {
                    if(response.body().getCode().equals("1")) {
                        SharedPreferenceHelper.setSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_NAME, usernameEditText.getText().toString());
                        SharedPreferenceHelper.setSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_PASSWORD, passwordEditText.getText().toString());
                        Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }else
                    {
                        if(response.body().getMessage()!=null) {
                            Utils.showAlertMessage(getActivity(), response.body().getMessage());
                        }else{
                            Utils.showAlertMessage(getActivity(), "Please try after sometime.");

                        }
                    }
                }else {
                    Utils.showAlertMessage(getActivity(), "Server not responding. Please try after sometime.");

                }
                Utils.hideDialog();

            }

            @Override
            public void onFailure(Call<RegistrationPojo> call, Throwable t) {
                Utils.hideDialog();
                Utils.showAlertMessage(getActivity(), "Registration Failed");

            }
        });

            }
}
