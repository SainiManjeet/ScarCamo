package com.scarcamo.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.scarcamo.ColorPickerActivity;
import com.scarcamo.R;
import com.scarcamo.custom.CButtonBold;
import com.scarcamo.custom.CTextViewHeavy;
import com.scarcamo.pojo.Result;
import com.scarcamo.sugarDB.UserData;
import com.scarcamo.utilities.Constants;
import com.scarcamo.utilities.Utils;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedSkinToneFragment extends AppCompatActivity implements View.OnClickListener{


    public SelectedSkinToneFragment() {
        // Required empty public constructor
    }
    private LinearLayout parent;
    private ImageView selectedColor;
    private CButtonBold confirmBtn;
    private CButtonBold chooseBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_selected_skin_tone);
        setUpLayout();
        }


    private void setUpLayout() {
        parent = (LinearLayout)findViewById(R.id.parent);
        selectedColor = (ImageView)findViewById( R.id.selectedColor );
        CTextViewHeavy colorPercentage = (CTextViewHeavy) findViewById(R.id.colorPercentage);
        if(Constants.SelecteResult!=null){
            String[] rgbArray = Constants.SelecteResult.getColor_code().split(",");
            int red=0,green=0,blue=0;
            if(rgbArray.length==3)
            {
                red = Integer.parseInt(rgbArray[0]);
                green = Integer.parseInt(rgbArray[1]);
                blue = Integer.parseInt(rgbArray[2]);


            }
            selectedColor.setBackgroundResource(R.drawable.colored_border);
            GradientDrawable drawable = (GradientDrawable) selectedColor.getBackground();
            drawable.setColor(Color.rgb(red,green,blue));
            //selectedColor.setBackgroundColor(Color.rgb(red,green,blue));

            int color = Constants.SelectedColor;
            int red2 = Color.red(color);
            int green2 = Color.green(color);
            int blue2 = Color.blue(color);
            if (red != 0 && green != 0 && blue != 0) {
                parent.setBackgroundColor(Color.rgb(red2, green2, blue2));
            }

            int diffRed   = Math.abs(red - red2);
            int diffGreen = Math.abs(green - green2);
            int diffBlue  = Math.abs(blue - blue2);

            float pctDiffRed   = (float)diffRed   / 255;
            float pctDiffGreen = (float)diffGreen / 255;
            float pctDiffBlue   = (float)diffBlue  / 255;
            float percentage = (pctDiffRed + pctDiffGreen + pctDiffBlue) / 3 * 100;
            int p = 100 - (((int) (percentage*100))/100);
            colorPercentage.setText("Your Skin Tone Matched "+p+"% With This Color.");
        }
        confirmBtn = (CButtonBold)findViewById( R.id.confirmBtn );
        chooseBtn = (CButtonBold)findViewById( R.id.chooseBtn );
        initializeListener();
    }

    private void initializeListener() {
        confirmBtn.setOnClickListener(this);
        chooseBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v==confirmBtn)
        {
            saveCardOnServer(Constants.SelecteResult);

        }else
        {
            final Intent intentColorPickerActivity = new Intent(SelectedSkinToneFragment.this, ColorPickerActivity.class);
            startActivity(intentColorPickerActivity);
            SelectedSkinToneFragment.this.finish();
        }

    }

    private void saveCardOnServer(final Result selecteResult) {

        List<UserData> userDataList = UserData.listAll(UserData.class);
        if(userDataList.size()>10){
            UserData userData = UserData.findById(UserData.class, userDataList.get(userDataList.size()-1).getId());
            userData.delete();
        }
        UserData mUserData = new UserData(selecteResult.getId(),selecteResult.getColor_code(),Utils.parseDate(selecteResult.getModify_date()));
        mUserData.save();
        SelectedSkinToneFragment.this.finish();

    }

}
