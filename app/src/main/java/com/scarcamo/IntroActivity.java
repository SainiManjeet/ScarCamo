package com.scarcamo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scarcamo.adapter.ViewPagerAdapter;
import com.scarcamo.custom.CButtonBold;


public class IntroActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private TextView txtTopBar; //@Added
    private ViewPager intro_images;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;
    private LinearLayout pager_indicator;
   /* private int[] mlayoutResources = {
            R.layout.intro_one,
            R.layout.intro_two,

    };*/

    private int[] mlayoutResources = {
            R.layout.intro_one,
            R.layout.intro_two,
            R.layout.intro_three,
            R.layout.intro_four,
            R.layout.intro_five,
            R.layout.intro_six,


    };

    private CButtonBold btnLogin;
    private CButtonBold btnRegister;
    private CButtonBold skipTipsCButtonBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        setUpView();
    }

    private void setUpView() {

        txtTopBar=(TextView)findViewById(R.id.txt_top_bar);//Added

        intro_images = (ViewPager) findViewById(R.id.pager_introduction);
        mAdapter = new ViewPagerAdapter(IntroActivity.this, mlayoutResources);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        skipTipsCButtonBold = (CButtonBold) findViewById(R.id.skipBtn);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);
        setUiPageViewController();

        initalizeClickListeners();

    }

    private void initalizeClickListeners() {


        skipTipsCButtonBold.setOnClickListener(this);
    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {

        Intent loginIntent = new Intent(IntroActivity.this, TabbedActivity.class);
        startActivity(loginIntent);

        IntroActivity.this.finish();
    }
}
