package com.scarcamo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scarcamo.custom.CButtonBold;
import com.scarcamo.utilities.Constants;
import com.scarcamo.utilities.Utils;

import java.io.IOException;


public class ScanActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_IMAGE = 1001;
    private static final int CAMERA_REQUEST = 1002;
    private CButtonBold scanBtn;
    private CButtonBold chooseBtn;
    private RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        setUpLayout();
    }

    private void setUpLayout() {
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        scanBtn = (CButtonBold) findViewById(R.id.scanBtn);
        chooseBtn = (CButtonBold) findViewById(R.id.chooseBtn);

        initializeListener();
    }

    private void initializeListener() {
        scanBtn.setOnClickListener(this);
        chooseBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == scanBtn) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);

        } else if (v == chooseBtn) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);

        } else {
            Utils.hideKeyboard(ScanActivity.this);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(ScanActivity.this.getContentResolver(), data.getData());


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(ScanActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");

        }
        if (bitmap != null) {
            Intent yourToneIntent = new Intent(ScanActivity.this, FindYourToneActivity.class);
            Constants.SELECTED_IMAGE = bitmap;
            /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] byteArray = stream.toByteArray();
            yourToneIntent.putExtra("BitmapImage", byteArray);*/
            startActivity(yourToneIntent);
        }
    }
}
