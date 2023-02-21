package com.scarcamo.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scarcamo.ColorPickerActivity;
import com.scarcamo.FindYourToneActivity;
import com.scarcamo.R;
import com.scarcamo.TabbedActivity;
import com.scarcamo.custom.CButtonBold;
import com.scarcamo.utilities.Constants;
import com.scarcamo.utilities.SharedPreferenceHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanFragment extends Fragment implements View.OnClickListener{

    private static final int SELECT_IMAGE = 1001;
    private static final int CAMERA_REQUEST = 1002;
    private CButtonBold scanBtn;
    private CButtonBold chooseBtn;
    private RelativeLayout rootLayout;
    private boolean isButtonClicked=false;
    private boolean isFromScan=false;

    public ScanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpLayout();
    }

    private void setUpLayout() {
        rootLayout = (RelativeLayout) getView().findViewById(R.id.rootLayout);

        scanBtn = (CButtonBold) getView().findViewById(R.id.scanBtn);
        chooseBtn = (CButtonBold) getView().findViewById(R.id.chooseBtn);

        initializeListener();
    }

    private void initializeListener() {
        scanBtn.setOnClickListener(this);
        chooseBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isButtonClicked){
            isButtonClicked = false;
            Constants.isToneChanged = true;
            String uID = SharedPreferenceHelper.getSharedPreferenceString(getActivity(), SharedPreferenceHelper.USER_ID,"");
            /*if (uID.equals("")) {
                ((TabbedActivity) getActivity()).showLoginScreen();
            }else {*/
                ((TabbedActivity) getActivity()).showProductScreen();
            //}
        }
    }

    @Override
    public void onClick(View v) {
        isButtonClicked =true;
        if (v == scanBtn) {
            isFromScan = true;

        } else if (v == chooseBtn) {
            isFromScan = false;

        }
        openCamera();
    }


    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        } else {
            if(isFromScan) {
                Intent intentColorPickerActivity = new Intent(getActivity(), ColorPickerActivity.class);
                startActivity(intentColorPickerActivity);
            }else
            {
                Intent intentColorPickerActivity = new Intent(getActivity(), FindYourToneActivity.class);
                startActivity(intentColorPickerActivity);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {


         if (requestCode == CAMERA_REQUEST ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intentColorPickerActivity = new Intent(getActivity(), ColorPickerActivity.class);
                startActivity(intentColorPickerActivity);

            } else {
                Toast.makeText(getActivity(), "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
            }
        }

    }
}


