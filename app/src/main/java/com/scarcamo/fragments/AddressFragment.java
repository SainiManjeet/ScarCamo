package com.scarcamo.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scarcamo.R;
import com.scarcamo.custom.CButtonBold;
import com.scarcamo.custom.CTextViewHeavy;
import com.scarcamo.pojo.Result;
import com.scarcamo.pojo.ShadePojo;
import com.scarcamo.retrofit.ScarCamoServices;
import com.scarcamo.stripe.StripeCardActivity;
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
public class AddressFragment extends Fragment implements View.OnClickListener {

    private LinearLayout parent;
    private CTextViewHeavy title;
    private TextView divider;
    private EditText yourName;
    private EditText mobileNumberEditText;
    private EditText addressLine1ET;
    private EditText addressLine2ET;
    private EditText cityET;
    private EditText pinET;
    private EditText stateET;
    private CButtonBold sendBtn;
    private String addressId;
    private String fromScreen;
    private Result item;

    public AddressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(((AppCompatActivity) getActivity()).getSupportActionBar()!=null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }

        return inflater.inflate(R.layout.fragment_address, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle b = getArguments();
        if(b != null) {
            fromScreen = b.getString("from");
            item = (Result)b.getSerializable("item");
        }
        setUpLayout();

        getUserDetails();
    }

    private void setUpLayout() {
        parent = (LinearLayout) getView().findViewById(R.id.parent);
        yourName = (EditText) getView().findViewById(R.id.yourName);
        mobileNumberEditText = (EditText) getView().findViewById(R.id.mobileNumberEditText);
        addressLine1ET = (EditText) getView().findViewById(R.id.addressLine1ET);
        addressLine2ET = (EditText) getView().findViewById(R.id.addressLine2ET);
        cityET = (EditText) getView().findViewById(R.id.cityET);
        pinET = (EditText) getView().findViewById(R.id.pinET);
        stateET = (EditText) getView().findViewById(R.id.stateET);
        sendBtn = (CButtonBold) getView().findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
         addressId = SharedPreferenceHelper.getSharedPreferenceString(getActivity(),SharedPreferenceHelper.ADDRESS_ID,"");
        if(!addressId.equalsIgnoreCase(""))
        {

            sendBtn.setText("UPDATE");
        }
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

                        setDataToViews(response.body().getResult()[0]);
                    }
                }

                @Override
                public void onFailure(Call<ShadePojo> call, Throwable t) {

                }
            });
        } else {
            Utils.showSnackBar(getActivity(), parent, "Create account first.");

        }
    }

    private void setDataToViews(Result result) {

        yourName.setText(result.getFull_name());
        addressLine1ET.setText(result.getAddress_line1());
        addressLine2ET.setText(result.getAddress_line2());
        mobileNumberEditText.setText(result.getMobile_number());
        cityET.setText(result.getCity());
        pinET.setText(result.getPin_code());
        stateET.setText(result.getState());
        Constants.Address = result.getFull_name()+"!"+result.getAddress_line1() + "!" + result.getAddress_line2() + "!" + result.getCity() + ", " + result.getState() + ", " + result.getPin_code();

    }

    private void callService(int status) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ScarCamoServices service = retrofit.create(ScarCamoServices.class);
        Call<ShadePojo> shadesRequest;
        String uID = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_ID, "");
        if (uID != null && !uID.equals("")) {
            final String userNameString = yourName.getText().toString();
            String mobileString = mobileNumberEditText.getText().toString();
            final String address1String = addressLine1ET.getText().toString();
            final String address2String = addressLine2ET.getText().toString();
            final String pinCodeString = pinET.getText().toString();
            final String cityString = cityET.getText().toString();
            final String stateString = stateET.getText().toString();

                shadesRequest = service.editAddress(uID,userNameString,mobileString,pinCodeString,address1String,address2String,cityString,stateString);


            shadesRequest.enqueue(new Callback<ShadePojo>() {
                @Override
                public void onResponse(Call<ShadePojo> call, Response<ShadePojo> response) {
                    if (response.body().getResult() != null && response.body().getCode().equals("1")) {
                        //SharedPreferenceHelper.setSharedPreferenceString(getActivity(),SharedPreferenceHelper.FULL_NAME,yourName.getText().toString());
                        sendBtn.setText("UPDATE");

                        Constants.Address = userNameString+"!"+address1String+"!"+address2String+"!"+cityString+", "+stateString+", "+pinCodeString;
                        SharedPreferenceHelper.setSharedPreferenceString(getActivity(), SharedPreferenceHelper.ADDRESS, Constants.Address);
                        SharedPreferenceHelper.setSharedPreferenceString(getActivity(),SharedPreferenceHelper.ADDRESS_ID,response.body().getResult()[0].getId());
                        //SharedPreferenceHelper.setSharedPreferenceString(getActivity(), SharedPreferenceHelper.FULL_NAME, response.body().getResult()[0].getUsername());

                        Utils.showSnackBar(getActivity(), parent, "Address Saved Successfully.");
                       if(fromScreen!=null && fromScreen.equalsIgnoreCase("productScreen")){
                           StripeCardActivity stripeCardActivity = new StripeCardActivity();
                           Bundle b = new Bundle();
                           b.putSerializable("item",item);
                           stripeCardActivity.setArguments(b);
                           getActivity().getSupportFragmentManager().popBackStack();
                           Utils.changeFragment(getActivity(),stripeCardActivity);
                           /*Intent paymentIntent = new Intent(getActivity(), StripeCardActivity.class);
                           paymentIntent.putExtra("item", item);
                           startActivity(paymentIntent);*/
                       }else{
                           getActivity().getSupportFragmentManager().popBackStack();
                       }

                    }
                }

                @Override
                public void onFailure(Call<ShadePojo> call, Throwable t) {

                }
            });
        } else {
            //Utils.showSnackBar(getActivity(), parent, "Create account first.");
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sendBtn) {
            if (validate()) {
                Utils.hideKeyboard(getActivity());
                callService(0);
            }else
            {
                Toast.makeText(getActivity(),getActivity().getString(R.string.error_blank),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validate() {

        if (!yourName.getText().toString().equalsIgnoreCase("") &&
                !mobileNumberEditText.getText().toString().equalsIgnoreCase("")
                && !addressLine1ET.getText().toString().equalsIgnoreCase("")
                && !addressLine2ET.getText().toString().equalsIgnoreCase("")
                && !pinET.getText().toString().equalsIgnoreCase("")
                && !cityET.getText().toString().equalsIgnoreCase("")
                && !stateET.getText().toString().equalsIgnoreCase("")) {
            return true;
        } else {
            return false;
        }
    }
}
