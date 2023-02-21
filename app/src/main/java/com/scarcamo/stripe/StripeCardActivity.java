package com.scarcamo.stripe;

import android.content.BroadcastReceiver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.scarcamo.R;
import com.scarcamo.controller.ErrorDialogHandler;
import com.scarcamo.controller.ProgressDialogController;
import com.scarcamo.custom.CButtonBold;
import com.scarcamo.custom.CTextViewHeavy;
import com.scarcamo.custom.CTextViewThin;
import com.scarcamo.fragments.AddressFragment;
import com.scarcamo.fragments.ThankyouFragment;
import com.scarcamo.pojo.Result;
import com.scarcamo.pojo.ShadePojo;
import com.scarcamo.retrofit.ScarCamoServices;
import com.scarcamo.utilities.Constants;
import com.scarcamo.utilities.SharedPreferenceHelper;
import com.scarcamo.utilities.Utils;
import com.squareup.picasso.Picasso;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Customer;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.view.CardMultilineWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.subscriptions.CompositeSubscription;


public class StripeCardActivity extends Fragment {
    private ProgressBar progressBar;

    public StripeCardActivity() {
        // Required empty public constructor
    }
    ProgressDialogController mProgressDialogController;


    CardMultilineWidget mCardMultilineWidget;
    CompositeSubscription mCompositeSubscription;
    private static final String PUBLISHABLE_KEY =
            "pk_test_DwkBrpGS1ZTZd4qZd4i8eKlk";
    private PaymentSessionData mPaymentSessionData;
    private SimpleAdapter mSimpleAdapter;
    private List<Map<String, String>> mCardSources = new ArrayList<>();
    private BroadcastReceiver mBroadcastReceiver;
    private Customer mCustomer;
    private ErrorDialogHandler mErrorDialogHandler;
    private PaymentSession mPaymentSession;
    private ProgressBar mProgressBar;
    private TextView mResultTextView;
    private TextView mResultTitleTextView;
    private Button mSelectPaymentButton;
    private Button mSelectShippingButton;
    private Button makePayment;
    private CardInputWidget mCardInputWidget;
    private RelativeLayout parent;
    private Result buyItem;
    private CTextViewHeavy addressText;
    private ImageView productImage;
    private CTextViewHeavy productName;
    private CTextViewHeavy productPrice;
    private CButtonBold changeAddressBtn;
    private FrameLayout container1;
    private boolean isCloseActivity= false;
    private CTextViewThin productDesc;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(!((AppCompatActivity) getActivity()).getSupportActionBar().isShowing()) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
        return inflater.inflate(R.layout.activity_stripe, container, false);


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle b = getArguments();
        if(b!=null){
            buyItem = (Result) b.getSerializable("item");
        }
        setUpView();
    }

    private void setUpView() {
        container1 = (FrameLayout) getView().findViewById(R.id.container1);
        parent = (RelativeLayout) getView().findViewById(R.id.parent);
        mCardInputWidget = (CardInputWidget) getView().findViewById(R.id.card_input_widget);
        makePayment = (Button) getView().findViewById(R.id.make_payment);
        addressText = (CTextViewHeavy) getView().findViewById(R.id.addressText);
        progressBar = (ProgressBar)getView().findViewById(R.id.progress);
        changeAddressBtn = (CButtonBold)getView().findViewById(R.id.changeAddressBtn);
        changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //container1.setVisibility(View.VISIBLE);
                AddressFragment addressFragment = new AddressFragment();
                Utils.changeFragment(getActivity(),addressFragment);

                /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container1, addressFragment, "addressFragment");
                ft.addToBackStack("addressFragment");
                ft.commit();*/
            }
        });
        if (SharedPreferenceHelper.getSharedPreferenceBoolean(getActivity(), SharedPreferenceHelper.IS_LOGGED_IN, false)) {

            if (Constants.Address != null && !Constants.Address.equalsIgnoreCase("")) {
                String addressString = Constants.Address.replaceAll("!", "<br>");
                addressText.setText(Html.fromHtml(addressString));
            }
        }
        productImage = (ImageView)getView().findViewById( R.id.productImage );
        productName = (CTextViewHeavy)getView().findViewById( R.id.product_name );
        productName.setTypeface(productName.getTypeface(), Typeface.BOLD);
        productPrice = (CTextViewHeavy)getView().findViewById( R.id.product_price );
        productPrice.setTypeface(productPrice.getTypeface(), Typeface.BOLD_ITALIC);

        productDesc = (CTextViewThin)getView().findViewById( R.id.product_descriptionText );
        productDesc.setTypeface(productDesc.getTypeface(), Typeface.ITALIC);
        productName.setText(buyItem.getName());
        if (buyItem.getImage() != null && !buyItem.getImage().equals("")) {

            Picasso.with(getActivity())
                    .load(buyItem.getImage())
                    .into(productImage);
        }
        productPrice.setText("Total: $" + buyItem.getPrice());
        productDesc.setText(buyItem.getDescription());
        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePayment.setEnabled(false);
                Utils.hideKeyboard(getActivity());
                Card card = mCardInputWidget.getCard();
                progressBar.setVisibility(View.VISIBLE);
                ErrorDialogHandler mErrorDialogHandler = new ErrorDialogHandler(getChildFragmentManager());
                if (card == null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    mErrorDialogHandler.showError("The card details that you entered are invalid");
                } else if (!card.validateNumber()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    mErrorDialogHandler.showError("The card number that you entered is invalid");
                } else if (!card.validateExpiryDate()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    mErrorDialogHandler.showError("The expiration date that you entered is invalid");
                } else if (!card.validateCVC()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    mErrorDialogHandler.showError("The CVC code that you entered is invalid");
                }  else {
                    Stripe stripe = new Stripe(getActivity(), PUBLISHABLE_KEY);
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server
                                    sendTokenToServer(token.getId());
                                }

                                public void onError(Exception error) {
                                    // Show localized error message
                                    makePayment.setEnabled(true);
                                    Toast.makeText(getActivity(),
                                            error.getLocalizedMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            });
                }

            }
        });
    }
   /* @Override
    public void onBackPressed() {

            if (container1.getVisibility() == View.VISIBLE) {
                if(isCloseActivity){
                    StripeCardActivity.this.finish();
                }
                container1.setVisibility(View.GONE);
                getSupportFragmentManager().popBackStack();

                //additional code
            } else {
                StripeCardActivity.this.finish();
            }

    }
    @Override
    protected void onResume() {
        super.onResume();
        //container1.setVisibility(View.GONE);
        if (SharedPreferenceHelper.getSharedPreferenceBoolean(StripeCardActivity.this, SharedPreferenceHelper.IS_LOGGED_IN, false)) {

            if (Constants.Address != null && !Constants.Address.equalsIgnoreCase("")) {
                addressText.setText(Constants.Address);
            }
        }
    }
*/
    private void sendTokenToServer(String id) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Utils.showDialog();
        String uID = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_ID, "");
        String addressId = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.ADDRESS_ID, "");
        if (addressId.equalsIgnoreCase("")) {
            Utils.showAlertMessage(getActivity(), "Please add shipping address before buying.");
            return;
        }
        if (uID != null && !uID.equals("") && buyItem!=null) {
            ScarCamoServices service = retrofit.create(ScarCamoServices.class);
            Call<ShadePojo> shadesRequest = service.stripeToken(id, uID, buyItem.getPrice(), buyItem.getId(), addressId, Constants.SelecteResult.getColor_code(), Constants.SelecteResult.getId());
            shadesRequest.enqueue(new Callback<ShadePojo>() {
                @Override
                public void onResponse(Call<ShadePojo> call, Response<ShadePojo> response) {
                    if (response.body().getCode().equals("1")) {
                        Utils.showSnackBar(getActivity(), parent, "Payment Successful.");
                        isCloseActivity = true;
                        progressBar.setVisibility(View.INVISIBLE);
                        //container1.setVisibility(View.VISIBLE);
                        ThankyouFragment thankyouFragment = new ThankyouFragment();
                        Utils.changeFragment(getActivity(),thankyouFragment);

                    } else {
                        Utils.showAlertMessage(getActivity(), response.body().getMessage());
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Call<ShadePojo> call, Throwable t) {
                    Utils.hideDialog();
                    makePayment.setEnabled(true);
                    Utils.showAlertMessage(getActivity(), t.getLocalizedMessage());
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            //Utils.showSnackBar(StripeCardActivity.this, parent, "Create account first.");
            getActivity().onBackPressed();
        }
    }


}