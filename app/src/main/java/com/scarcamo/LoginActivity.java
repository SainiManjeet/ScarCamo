package com.scarcamo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.iid.FirebaseInstanceId;
import com.scarcamo.custom.CButtonBold;
import com.scarcamo.custom.CTextViewHeavy;
import com.scarcamo.custom.CTextViewThin;
import com.scarcamo.fragments.AddressFragment;
import com.scarcamo.fragments.ChangePasswordFragment;
import com.scarcamo.pojo.Data;
import com.scarcamo.pojo.Result;
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


public class LoginActivity extends Fragment implements View.OnClickListener {


    private ViewSwitcher loginViewpager;
    private ImageView appName;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private CTextViewThin forgotPassword;
    private CButtonBold loginBtn;
    private CTextViewThin registerHere;
    private CTextViewHeavy myUsernameText;
    private CTextViewHeavy myEmailText;
    //private EditText myEmailEditText;
    private EditText myAddressEditText;
    private CButtonBold updateBtn;
    private LinearLayout rootLayout;
    private String message;
    private TextView selectedColor;
    private CTextViewHeavy changeAddressText;
    private CTextViewHeavy changePasswordText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.activity_login, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpView();
    }



    private void setUpView() {


        rootLayout = (LinearLayout) getView().findViewById(R.id.rootLayout);
        loginViewpager = (ViewSwitcher) getView().findViewById(R.id.loginViewpager);
        //appName = (ImageView) getView().findViewById(R.id.app_name);
        usernameEditText = (EditText) getView().findViewById(R.id.username_EditText);
        passwordEditText = (EditText) getView().findViewById(R.id.password_EditText);
        forgotPassword = (CTextViewThin) getView().findViewById(R.id.forgot_password);
        loginBtn = (CButtonBold) getView().findViewById(R.id.loginBtn);
        registerHere = (CTextViewThin) getView().findViewById(R.id.registerHere);
        myUsernameText = (CTextViewHeavy) getView().findViewById(R.id.user_name);
        myEmailText = (CTextViewHeavy) getView().findViewById(R.id.myPassword_EditText);
        selectedColor = (TextView) getView().findViewById(R.id.selectedColor);
        //myAddressEditText = (EditText) getView().findViewById(R.id.myAddress_EditText);
        //updateBtn = (CButtonBold) getView().findViewById(R.id.updateBtn);
        changeAddressText = (CTextViewHeavy)getView().findViewById( R.id.changeAddressText );
        changePasswordText = (CTextViewHeavy)getView().findViewById( R.id.changePasswordText );
        initializeClickListeners();

        if (SharedPreferenceHelper.getSharedPreferenceBoolean(getActivity(), SharedPreferenceHelper.IS_LOGGED_IN, false)) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            if(!SharedPreferenceHelper.getSharedPreferenceString(getActivity(),SharedPreferenceHelper.FULL_NAME,"").equalsIgnoreCase("")) {
                myUsernameText.setText(SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.FULL_NAME, ""));
                myEmailText.setText(SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.EMAIL, ""));
            }
            loginViewpager.setDisplayedChild(1);
            if(!SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.ADDRESS, "").equalsIgnoreCase("")){
                String addressString = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.ADDRESS, "").replaceAll("!", "<br>");

                selectedColor.setText(Html.fromHtml(addressString));

            }
           /* if(Constants.SelectedColor != 0){
                //String[] rgbArray = Constants.SelecteResult.getColor_code().split(",");
                int color = Constants.SelectedColor;
                int red=0,green=0,blue=0;

                red = Color.red(color);
                green = Color.green(color);
                blue = Color.blue(color);
                if (red != 0 && green != 0 && blue != 0) {
                    selectedColor.setBackgroundColor(Color.rgb(red, green, blue));
                    selectedColor.setText("Your Skin Tone");
                }
            }*/
        }else
        {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
    }

    private void initializeClickListeners() {
        rootLayout.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        registerHere.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        changeAddressText.setOnClickListener(this);
        changePasswordText.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == rootLayout) {
            Utils.hideKeyboard(getActivity());

        } else if (v == loginBtn) {
            if (validate()) {
                loginService();

            } else {
                Utils.showAlertMessage(getActivity(), message);
            }
        } else if (v == registerHere) {
            RegisterActivity mRegisterActivity = new RegisterActivity();
            Utils.changeFragment(getActivity(), mRegisterActivity);
        }else if(v== forgotPassword)
        {
            showAlert();
//            ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
//            Utils.changeFragment(getActivity(), forgotPasswordFragment);
        }else if(v== changeAddressText)
        {
            AddressFragment addressFragment = new AddressFragment();
            Utils.changeFragment(getActivity(), addressFragment);
        }else if(v== changePasswordText)
        {
            ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
            Utils.changeFragment(getActivity(), changePasswordFragment);
        }

    }

    private void showAlert() {

        final Dialog mView = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        mView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView.setCancelable(true);
        mView.setContentView(R.layout.custom_dialog);
        Button btnNO = (Button)mView.findViewById(R.id.btn_popup_NO);
        Button btnYES = (Button)mView.findViewById(R.id.btn_popup_YES);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.et_input);

        btnNO.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mView.cancel();


            }
        });

        btnYES.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(validate(userInputDialogEditText.getText().toString()))
                {
                    callService(userInputDialogEditText.getText().toString());
                }else
                {
                    Utils.showSnackBar(getActivity(),getView().findViewById(R.id.rootLayout),message);

                }
                mView.cancel();

            }
        });

        mView.show();


    }

    private void loginService() {
        String notificationToken = FirebaseInstanceId.getInstance().getToken();
        String android_id = notificationToken;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Utils.showDialog();
        ScarCamoServices service = retrofit.create(ScarCamoServices.class);
        Call<ShadePojo> shadesRequest = service.loginUser(usernameEditText.getText().toString(), passwordEditText.getText().toString(),"2",android_id);
        shadesRequest.enqueue(new Callback<ShadePojo>() {
            @Override
            public void onResponse(Call<ShadePojo> call, Response<ShadePojo> response) {
                if (response.body()!=null ) {
                    if(response.body().getData()!=null && response.body().getCode().equals("1")) {
                        Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                        Data userData = response.body().getData();
                        SharedPreferenceHelper.setSharedPreferenceBoolean(getActivity(), SharedPreferenceHelper.IS_LOGGED_IN, true);
                        SharedPreferenceHelper.setSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_ID, userData.getId());
                        SharedPreferenceHelper.setSharedPreferenceString(getActivity(), SharedPreferenceHelper.FULL_NAME, userData.getUsername());

                        myEmailText.setText(response.body().getData().getEmail());
                        myUsernameText.setText(response.body().getData().getUsername());

                        getUserDetails();
                        SharedPreferenceHelper.setSharedPreferenceString(getActivity(),SharedPreferenceHelper.EMAIL,response.body().getData().getEmail());
                        loginViewpager.showNext();
                        ((TabbedActivity)getActivity()).userLoggedIn();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
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
    private void getUserDetails() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ScarCamoServices service = retrofit.create(ScarCamoServices.class);
        Call<ShadePojo> shadesRequest;
        String uID = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_ID, "");
        if (uID != null && !uID.equals("")) {

            shadesRequest = service.getUserDetail(uID);


            shadesRequest.enqueue(new Callback<ShadePojo>() {
                @Override
                public void onResponse(Call<ShadePojo> call, Response<ShadePojo> response) {
                    if (response.body().getResult() != null && response.body().getCode().equals("1")) {
                        SharedPreferenceHelper.setSharedPreferenceString(getActivity(),SharedPreferenceHelper.ADDRESS_ID,response.body().getResult()[0].getId());
                        Result result = response.body().getResult()[0];
                        /*if(result.getFull_name()!=null && !result.getFull_name().equalsIgnoreCase(""))
                        {
                            SharedPreferenceHelper.setSharedPreferenceString(getActivity(), SharedPreferenceHelper.FULL_NAME, result.getFull_name());
                            myUsernameText.setText(result.getFull_name());
                        }*/

                        if(result.getAddress_line1()!=null && !result.getAddress_line1().equalsIgnoreCase("")) {
                            Constants.Address = result.getFull_name()+"!"+result.getAddress_line1() + "!" + result.getAddress_line2() + "! " + result.getCity() + ", " + result.getState() + ", " + result.getPin_code();
                            SharedPreferenceHelper.setSharedPreferenceString(getActivity(), SharedPreferenceHelper.ADDRESS, Constants.Address);
                            String addressString = Constants.Address.replaceAll("!", "<br>");

                            selectedColor.setText(Html.fromHtml(addressString));
                        }else
                        {
                            selectedColor.setText("No Address found");
                        }

                        goToBuyScreen();
                    }else
                    {
                        AddressFragment addressFragment = new AddressFragment();
                        Utils.changeFragment(getActivity(), addressFragment);
                    }
                }

                @Override
                public void onFailure(Call<ShadePojo> call, Throwable t) {

                }
            });
        }
    }
    private boolean validate() {
        String userName = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (userName.equals("") || password.equals("")) {
            message = "All fields required.";
            return false;
        }
        return true;

    }

    private void goToBuyScreen()
    {
        String uID = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_ID, "");

        if(Constants.isBuyClicked && !uID.equalsIgnoreCase(""))
        {
            Constants.isBuyClicked = false;
            AddressFragment addressFragment = new AddressFragment();
            Bundle b = new Bundle();
            b.putString("from", "productScreen");
            b.putSerializable("item", Constants.userSelectedItem);
            addressFragment.setArguments(b);
            Utils.changeFragment(getActivity(), addressFragment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        goToBuyScreen();
    }

    private boolean validate(String email) {



        if ( email.equals("") ) {
            message = "All fields required.";
            return false;
        }else if(!Utils.isValidEmail(email))
        {
            message = "Please enter a valid email.";
            return false;
        }
        return true;

    }

    private void callService(String email) {
        Utils.hideKeyboard(getActivity());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Utils.showDialog();
        ScarCamoServices service = retrofit.create(ScarCamoServices.class);
        Call<ShadePojo> shadesRequest = service.forgetPassword(email);

        shadesRequest.enqueue(new Callback<ShadePojo>() {
            @Override
            public void onResponse(Call<ShadePojo> call, Response<ShadePojo> response) {
                if (response.body()!=null ) {
                    if(response.body().getCode().equals("1")) {
                        Utils.showSnackBar(getActivity(), getView().findViewById(R.id.rootLayout), "Your password has been sent to your inbox. Please check your mail.");
                       // getActivity().getSupportFragmentManager().popBackStack();
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
