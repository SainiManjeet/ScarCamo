package com.scarcamo.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.scarcamo.R;
import com.scarcamo.custom.CButtonBold;

public class ContactUsFragment extends Fragment{


    private String message;

    public ContactUsFragment() {
        // Required empty public constructor
    }

    private EditText yourName;
    private EditText yourMessage,yourEmail;
    private CButtonBold sendBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        return inflater.inflate(R.layout.fragment_contact_us, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpLayout();
    }

    private void setUpLayout() {
        WebView webView = (WebView) getView().findViewById(R.id.webView);
        webView.setWebViewClient(new ourViewClient());

        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl("http://18.219.134.84/pages/contactus.php");
    }

    public class ourViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            return true; }

    }


}
