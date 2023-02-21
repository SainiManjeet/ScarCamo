package com.scarcamo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.scarcamo.custom.CTextViewHeavy;
import com.scarcamo.fragments.AboutUsFragment;
import com.scarcamo.fragments.AddressFragment;
import com.scarcamo.fragments.ContactUsFragment;
import com.scarcamo.fragments.HistoryFragment;
import com.scarcamo.fragments.MyOrdersFragment;
import com.scarcamo.fragments.NotificationItemFragment;
import com.scarcamo.fragments.ProductFragment;
import com.scarcamo.fragments.ScanFragment;
import com.scarcamo.utilities.Constants;
import com.scarcamo.utilities.SharedPreferenceHelper;
import com.scarcamo.utilities.Utils;

public class TabbedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selectFragment(item);
            return true;
        }
    };
    private int mSelectedItem;
    private BottomNavigationView navigation;
    private ActionBar actionbar;
    private Toolbar toolbar;
    private CTextViewHeavy profileNav;
    private CTextViewHeavy ordersNav;
    private CTextViewHeavy historyNav;
    private CTextViewHeavy notificationNav;
    private CTextViewHeavy productNav;
    private CTextViewHeavy aboutNav;
    private CTextViewHeavy contactNav;
    private CTextViewHeavy videoNav, logOutNav, addressNav;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private boolean isCalled = false;
    private ScrollView scrollNav;
    private TextView headerTextView;
    private ImageView closeDrawer;
    private String productTag;
    private Fragment productFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /*actionbar.setHomeButtonEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);*/

        setUpLayout();

    }

    private void setUpLayout() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = navigation.findViewById(R.id.scan);
                view.performClick();
            }
        }, 200);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment frag = new ProductFragment();
        ft.add(R.id.container, frag, frag.getTag());
        ft.addToBackStack(frag.getTag());
        ft.commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            // set your height here
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
            // set your width here
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }
        headerTextView = (TextView) findViewById(R.id.headerTextView);
        scrollNav = (ScrollView) findViewById(R.id.scroll);
        profileNav = (CTextViewHeavy) findViewById(R.id.profileNav);
        ordersNav = (CTextViewHeavy) findViewById(R.id.ordersNav);
        historyNav = (CTextViewHeavy) findViewById(R.id.historyNav);
        notificationNav = (CTextViewHeavy) findViewById(R.id.notificationNav);
        productNav = (CTextViewHeavy) findViewById(R.id.productNav);
        aboutNav = (CTextViewHeavy) findViewById(R.id.aboutNav);
        contactNav = (CTextViewHeavy) findViewById(R.id.contactNav);
        //addressNav = (CTextViewHeavy) findViewById(R.id.addressNav);
        //videoNav = (CTextViewHeavy) findViewById(R.id.videoNav);
        logOutNav = (CTextViewHeavy) findViewById(R.id.logOutNav);
        closeDrawer = (ImageView) findViewById(R.id.closeDrawer);
        initializeListener();
        userLoggedIn();
    }

    private void initializeListener() {
        profileNav.setOnClickListener(this);
        ordersNav.setOnClickListener(this);
        historyNav.setOnClickListener(this);
        notificationNav.setOnClickListener(this);
        aboutNav.setOnClickListener(this);
        contactNav.setOnClickListener(this);
        //videoNav.setOnClickListener(this);
        logOutNav.setOnClickListener(this);
        productNav.setOnClickListener(this);
        //addressNav.setOnClickListener(this);
        closeDrawer.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        drawer.closeDrawer(Gravity.START);
        if (v == contactNav) {
            ContactUsFragment mContactUsFragment = new ContactUsFragment();
            Utils.changeFragment(TabbedActivity.this, mContactUsFragment);
        } else if (v == aboutNav) {
            AboutUsFragment aboutUsFragment = new AboutUsFragment();
            Utils.changeFragment(TabbedActivity.this, aboutUsFragment);

        } else if (v == logOutNav) {
            onBackPressed();
            SharedPreferenceHelper.clearSharedPrefs(TabbedActivity.this);
            SharedPreferenceHelper.setSharedPreferenceBoolean(TabbedActivity.this,SharedPreferenceHelper.IS_TUTORIAL_SHOWN,true);
            headerTextView.setText("");
            View view = navigation.findViewById(R.id.user);
            view.performClick();
            userLoggedIn();
        } else if (v == profileNav) {
            onBackPressed();
            View view = navigation.findViewById(R.id.user);
            view.performClick();
        } else if (v == productNav) {
            onBackPressed();
            View view = navigation.findViewById(R.id.product);
            view.performClick();
        } else if (v == ordersNav) {

            MyOrdersFragment myOrdersFragment = new MyOrdersFragment();
            Utils.changeFragment(TabbedActivity.this, myOrdersFragment);

        } else if (v == notificationNav) {

            NotificationItemFragment itemFragment = new NotificationItemFragment();
            Utils.changeFragment(TabbedActivity.this, itemFragment);

        } else if (v == historyNav) {

            HistoryFragment historyFragment = new HistoryFragment();
            Utils.changeFragment(TabbedActivity.this, historyFragment);

        } else if (v == addressNav) {

            AddressFragment addressFragment = new AddressFragment();
            Utils.changeFragment(TabbedActivity.this, addressFragment);

        }
    }

    public void selectFragment(MenuItem item) {
        Fragment frag = null;
        if(!SharedPreferenceHelper.getSharedPreferenceString(TabbedActivity.this,SharedPreferenceHelper.FULL_NAME,"").equalsIgnoreCase(""))
        {
            headerTextView.setText(SharedPreferenceHelper.getSharedPreferenceString(TabbedActivity.this,SharedPreferenceHelper.FULL_NAME,""));
        }

        if (Constants.SelecteResult != null) {
            String[] rgbArray = Constants.SelecteResult.getColor_code().split(",");
            int color = Constants.SelectedColor;
            int red = 0, green = 0, blue = 0;

            red = Integer.parseInt(rgbArray[0]);
            green = Integer.parseInt(rgbArray[1]);
            blue = Integer.parseInt(rgbArray[2]);
            if (red != 0 && green != 0 && blue != 0) {
               // scrollNav.setBackgroundColor(Color.rgb(red, green, blue));
                //headerTextView.setTextColor(Color.rgb(red + 20, green + 15, blue + 10));

            }
        }


        switch (item.getItemId()) {
            case R.id.product:
                frag = productFragment;

                if (productFragment == null) {
                    frag = new ProductFragment();
                    productFragment = frag;
                    productTag ="ProductFragment";
                }

                break;
            case R.id.scan:

                frag = new ScanFragment();
                productTag ="ScanFragment";
                break;
            case R.id.user:

                frag = new LoginActivity();
                productTag ="LoginActivity";
                break;

        }


        // update selected item
        mSelectedItem = item.getItemId();

        // uncheck the other items.
        for (int i = 0; i < navigation.getMenu().size(); i++) {
            MenuItem menuItem = navigation.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
        }

        updateToolbarText(item.getTitle());
        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, frag, frag.getTag());
            ft.commit();
        }

    }

    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }


    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count == 1) {

                TabbedActivity.this.finish();
                //additional code
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void showProductScreen()
    {
        navigation.postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = navigation.findViewById(R.id.product);
                view.performClick();
            }
        },1000);
    }

    public void showLoginScreen()
    {
        navigation.postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = navigation.findViewById(R.id.user);
                view.performClick();
            }
        },1000);
    }

    public void userLoggedIn()
    {
        String uID = SharedPreferenceHelper.getSharedPreferenceString(TabbedActivity.this, SharedPreferenceHelper.USER_ID, "");
        if(!SharedPreferenceHelper.getSharedPreferenceString(TabbedActivity.this,SharedPreferenceHelper.FULL_NAME,"").equalsIgnoreCase(""))
        {
            headerTextView.setText(SharedPreferenceHelper.getSharedPreferenceString(TabbedActivity.this,SharedPreferenceHelper.FULL_NAME,""));
        }
        if(!uID.equalsIgnoreCase("")) {
            logOutNav.setVisibility(View.VISIBLE);
        }else
        {
            logOutNav.setVisibility(View.INVISIBLE);
        }
    }
}
