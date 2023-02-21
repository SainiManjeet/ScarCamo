package com.scarcamo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scarcamo.adapter.DeckAdapter;
import com.scarcamo.fragments.SelectedSkinToneFragment;
import com.scarcamo.pojo.Result;
import com.scarcamo.pojo.ShadePojo;
import com.scarcamo.retrofit.ScarCamoServices;
import com.scarcamo.skinTonePicker.ColorItems;
import com.scarcamo.utilities.Cameras;
import com.scarcamo.utilities.Constants;
import com.scarcamo.utilities.Utils;
import com.scarcamo.views.CameraColorPickerPreview;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * An {@link android.support.v7.app.AppCompatActivity} for picking colors by using the camera of the device.
 * <p/>
 * The user aims at a color with the camera of the device, when they click on the preview the color is selected.
 * An animation notifies the user of the selection.
 * <p/>
 * The last selected color can be saved by clicking the save button.
 * An animation notifies the user of the save.
 */
class ColorPickerBaseActivity extends AppCompatActivity
        implements CameraColorPickerPreview.OnColorSelectedListener, View.OnClickListener {

    /**
     * A tag used in the logs.
     */
    protected static final String TAG = ColorPickerBaseActivity.class.getSimpleName();

    /**
     * The name of the property that animates the 'picked color'.
     * <p/>
     * Used by {@link ColorPickerBaseActivity#mPickedColorProgressAnimator}.
     */
    protected static final String PICKED_COLOR_PROGRESS_PROPERTY_NAME = "pickedColorProgress";

    /**
     * The name of the property that animates the 'save completed'.
     * <p/>
     * Used by {@link ColorPickerBaseActivity#mSaveCompletedProgressAnimator}.
     */
    protected static final String SAVE_COMPLETED_PROGRESS_PROPERTY_NAME = "saveCompletedProgress";

    /**
     * The duration of the animation of the confirm save message. (in millis).
     */
    protected static final long DURATION_CONFIRM_SAVE_MESSAGE = 400;

    /**
     * The delay before the confirm save message is hidden. (in millis).
     * <p/>
     * 1000 + DURATION_CONFIRM_SAVE_MESSAGE = 1400
     * The confirm save message should stay on screen for 1 second.
     */
    protected static final long DELAY_HIDE_CONFIRM_SAVE_MESSAGE = 1400;
    private boolean isToneSelected = false;
    private LinearLayout timeLayout;
    private TextView markOne, markTwo, markThree;
    private boolean isColorFound = false;
    private boolean isActivityFinished=false;
    private boolean isAutoScrolled=false;


    /**
     * A safe way to get an instance of the back {@link android.hardware.Camera}.
     */
    private static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return c;
    }

    /**
     * An instance of the {@link android.hardware.Camera} used for displaying the preview.
     */
    protected Camera mCamera;

    /**
     * A boolean for knowing the orientation of the activity.
     */
    protected boolean mIsPortrait;

    /**
     * A simple {@link android.widget.FrameLayout} that contains the preview.
     */
    protected FrameLayout mPreviewContainer;


    protected CameraColorPickerPreview mCameraPreview;

    /**
     * A reference to the {@link ColorPickerBaseActivity.CameraAsyncTask} that gets the {@link android.hardware.Camera}.
     */
    protected CameraAsyncTask mCameraAsyncTask;

    /**
     * The color selected by the user.
     * <p/>
     * The user "selects" a color by pointing a color with the camera.
     */
    protected int mSelectedColor;

    private ArrayList<Result> shadesArrayList;
    /**
     * The last picked color.
     * <p/>
     * The user "picks" a color by clicking the preview.
     */
    protected int mLastPickedColor;

    /**
     * A simple {@link android.view.View} used for showing the picked color.
     */
    //protected View mPickedColorPreview;

    /**
     * A simple {@link android.view.View} used for animating the color being picked.
     */
    protected View mPickedColorPreviewAnimated;

    /**
     * An {@link android.animation.ObjectAnimator} used for animating the color being picked.
     */
    protected ObjectAnimator mPickedColorProgressAnimator;

    /**
     * The delta for the translation on the x-axis of the mPickedColorPreviewAnimated.
     */
    protected float mTranslationDeltaX;

    /**
     * The delta for the translation on the y-axis of the mPickedColorPreviewAnimated.
     */
    protected float mTranslationDeltaY;

    /**
     * A simple {@link android.widget.TextView} used for showing a human readable representation of the picked color.
     */
    //protected TextView mColorPreviewText;

    /**
     * A simple {@link android.view.View} used for showing the selected color.
     */
    protected View mPointerRing;

    /**
     * An icon representing the "save completed" state.
     */
    //protected View mSaveCompletedIcon;

    /**
     * The save button.
     */
    //protected View mSaveButton;

    /**
     * A float representing the progress of the "save completed" state.
     */
    protected float mSaveCompletedProgress;

    /**
     * An {@link android.animation.ObjectAnimator} used for animating the "save completed" state.
     */
    protected ObjectAnimator mSaveCompletedProgressAnimator;

    /**
     * A simple {@link android.widget.TextView} that confirms the user that the color has been saved successfully.
     */
    protected TextView mConfirmSaveMessage;

    /**
     * An {@link android.view.animation.Interpolator} used for showing the mConfirmSaveMessage.
     */
    protected Interpolator mConfirmSaveMessageInterpolator;

    /**
     * A {@link java.lang.Runnable} that hide the confirm save message.
     * <p/>
     * This runnable is posted with some delayed on mConfirmSaveMessage each time a color is successfully saved.
     */
    protected Runnable mHideConfirmSaveMessage;

    /**
     * A simple boolean for keeping track of the device's camera flash state.
     */
    protected boolean mIsFlashOn;

    /**
     * the intent {@link android.content.Intent#getAction action} that led to this activity.
     */
    protected String action = null;

    /**
     * <a href="http://www.openintents.org/action/org-openintents-action-pick-color/">
     * see openintents.org</a>
     */
    public static final String OI_COLOR_PICKER = "org.openintents.action.PICK_COLOR";
    public static final String OI_COLOR_DATA = "org.openintents.extra.COLOR";

    private DiscreteScrollView scrollView;
    private DeckAdapter adapter;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        initPickedColorProgressAnimator();
        initSaveCompletedProgressAnimator();
        initViews();
        initTranslationDeltas();
        getShades();
        Toast.makeText(ColorPickerBaseActivity.this, "Tap on screen to choose skin tone", Toast.LENGTH_LONG).show();
        Intent intent = getIntent();
        if (intent != null)
            action = intent.getAction();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Setup the camera asynchronously.
        mCameraAsyncTask = new CameraAsyncTask();
        mCameraAsyncTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Cancel the Camera AsyncTask.
        mCameraAsyncTask.cancel(true);

        // Release the camera.
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }

        // Remove the camera preview
        if (mCameraPreview != null) {
            mPreviewContainer.removeView(mCameraPreview);
        }
    }

    @Override
    protected void onDestroy() {
        // Remove any pending mHideConfirmSaveMessage.
        mConfirmSaveMessage.removeCallbacks(mHideConfirmSaveMessage);
        isActivityFinished =true;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isFlashSupported()) {
            getMenuInflater().inflate(R.menu.menu_color_picker, menu);
            final MenuItem flashItem = menu.findItem(R.id.menu_color_picker_action_flash);
            int flashIcon = mIsFlashOn ? R.drawable.ic_action_flash_off : R.drawable.ic_action_flash_on;
            flashItem.setIcon(flashIcon);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        boolean handled;
        switch (itemId) {
            case android.R.id.home:
                finish();
                handled = true;
                break;

            case R.id.menu_color_picker_action_flash:
                toggleFlash();
                handled = true;
                break;

            default:
                handled = super.onOptionsItemSelected(item);
        }

        return handled;
    }

    @Override
    public void onColorSelected(int color) {
        mSelectedColor = color;
        mPointerRing.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        if (!isToneSelected) {
            isToneSelected = true;
            timerTask();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mCameraPreview) {
            animatePickedColor(mSelectedColor);
            if (shadesArrayList != null) {
                if(isToneSelected = false){
                    isToneSelected= true;
                    getSimilarCodeFromServer();
                }
                //findYourTone(mSelectedColor);


            }
        }
    }

    /**
     * Initialize the views used in this activity.
     * <p/>
     * Internally find the view by their ids and set the click listeners.
     */

    protected void initViews() {
        scrollView = findViewById(R.id.picker);


        scrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.07f)
                .setMinScale(0.7f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                .build());
        scrollView.addScrollListener(new DiscreteScrollView.ScrollListener<RecyclerView.ViewHolder>() {
            @Override
            public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {
                if(!isAutoScrolled) {
                    ((CardView) newCurrent.itemView).getChildAt(0).setPadding(4, 12, 4, 4);
                    ((CardView) currentHolder.itemView).getChildAt(0).setPadding(0, 0, 0, 0);

                } }
        });
        mIsPortrait = getResources().getBoolean(R.bool.is_portrait);
        mPreviewContainer = (FrameLayout) findViewById(R.id.activity_color_picker_preview_container);
        mPickedColorPreviewAnimated = findViewById(R.id.activity_color_picker_animated_preview);
        mPointerRing = findViewById(R.id.activity_color_picker_pointer_ring);
        mConfirmSaveMessage = (TextView) findViewById(R.id.activity_color_picker_confirm_save_message);
        mHideConfirmSaveMessage = new Runnable() {
            @Override
            public void run() {
                mConfirmSaveMessage.animate()
                        .translationY(-mConfirmSaveMessage.getMeasuredHeight())
                        .setDuration(DURATION_CONFIRM_SAVE_MESSAGE)
                        .start();
            }
        };
        positionConfirmSaveMessage();
        mConfirmSaveMessageInterpolator = new DecelerateInterpolator();

        mLastPickedColor = ColorItems.getLastPickedColor(this);
        applyPreviewColor(mLastPickedColor);
        timeLayout = (LinearLayout) findViewById(R.id.timerLayout);
        markOne = (TextView) findViewById(R.id.mark1);
        markTwo = (TextView) findViewById(R.id.mark2);
        markThree = (TextView) findViewById(R.id.mark3);


    }

    private void timerTask() {
        counter = 0;
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (counter == 0) {
                            markThree.setBackgroundResource(R.drawable.circle_green);

                            timeLayout.setVisibility(View.VISIBLE);
                            markOne.setBackgroundResource(R.drawable.circle_white);
                        } else if (counter == 1) {
                            markOne.setBackgroundResource(R.drawable.circle_white);
                            markTwo.setBackgroundResource(R.drawable.circle_white);
                        } else if (counter == 2) {
                            markTwo.setBackgroundResource(R.drawable.circle_green);
                            markOne.setBackgroundResource(R.drawable.circle_green);
                            markThree.setBackgroundResource(R.drawable.circle_white);
                            timeLayout.setVisibility(View.GONE);
                            timer.cancel();
                            getSimilarCodeFromServer();
                        }
                        counter++;

                    }
                });
            }
        }, 1000, 3000);
    }


    /**
     * Position mConfirmSaveMessage.
     * <p/>
     * Set the translationY of mConfirmSaveMessage to - mConfirmSaveMessage.getMeasuredHeight() so that it is correctly placed before the first animation.
     */
    protected void positionConfirmSaveMessage() {
        ViewTreeObserver vto = mConfirmSaveMessage.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    ViewTreeObserver vto = mConfirmSaveMessage.getViewTreeObserver();
                    vto.removeOnPreDrawListener(this);
                    mConfirmSaveMessage.setTranslationY(-mConfirmSaveMessage.getMeasuredHeight());
                    return true;
                }
            });
        }
    }

    /**
     * Initialize the deltas used for the translation of the preview of the picked color.
     */
    @SuppressLint("NewApi")
    protected void initTranslationDeltas() {
        ViewTreeObserver vto = mPointerRing.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver vto = mPointerRing.getViewTreeObserver();
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                        vto.removeGlobalOnLayoutListener(this);
                    } else {
                        vto.removeOnGlobalLayoutListener(this);
                    }

                    final Rect pointerRingRect = new Rect();
                    final Rect colorPreviewAnimatedRect = new Rect();
                    mPickedColorPreviewAnimated.getGlobalVisibleRect(pointerRingRect);
                    //mPickedColorPreview.getGlobalVisibleRect(colorPreviewAnimatedRect);

                    mTranslationDeltaX = colorPreviewAnimatedRect.left - pointerRingRect.left;
                    mTranslationDeltaY = colorPreviewAnimatedRect.top - pointerRingRect.top;
                }
            });
        }
    }


    /**
     * Initialize the animator used for the progress of the picked color.
     */
    protected void initPickedColorProgressAnimator() {
        mPickedColorProgressAnimator = ObjectAnimator.ofFloat(this, PICKED_COLOR_PROGRESS_PROPERTY_NAME, 0f, 1f);
        mPickedColorProgressAnimator.setDuration(400);
        mPickedColorProgressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mPickedColorPreviewAnimated.setVisibility(View.VISIBLE);
                mPickedColorPreviewAnimated.getBackground().setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ColorItems.saveLastPickedColor(ColorPickerBaseActivity.this, mLastPickedColor);
                applyPreviewColor(mLastPickedColor);
                mPickedColorPreviewAnimated.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mPickedColorPreviewAnimated.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * Check if the device's camera supports flash.
     *
     * @return Returns true if the device's camera supports flash, false otherwise.
     */
    protected boolean isFlashSupported() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    /**
     * Toggle the device's camera flash.
     * {@link ColorPickerBaseActivity#isFlashSupported()} should be called before using this methods.
     */
    protected void toggleFlash() {
        if (mCamera != null) {
            final Camera.Parameters parameters = mCamera.getParameters();
            final String flashParameter = mIsFlashOn ? Camera.Parameters.FLASH_MODE_OFF : Camera.Parameters.FLASH_MODE_TORCH;
            parameters.setFlashMode(flashParameter);

            // Set the preview callback to null and stop the preview
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();

            // Change the parameters
            mCamera.setParameters(parameters);

            // Restore the preview callback and re-start the preview
            mCamera.setPreviewCallback(mCameraPreview);
            mCamera.startPreview();

            mIsFlashOn = !mIsFlashOn;
            invalidateOptionsMenu();
        }
    }

    /**
     * Initialize the animator used for the progress of the "save completed" state.
     */
    protected void initSaveCompletedProgressAnimator() {
        mSaveCompletedProgressAnimator = ObjectAnimator.ofFloat(this, SAVE_COMPLETED_PROGRESS_PROPERTY_NAME, 1f, 0f);
    }

    /**
     * Apply the preview color.
     * <p/>
     * Display the preview color and its human representation.
     *
     * @param previewColor the preview color to apply.
     */
    protected void applyPreviewColor(int previewColor) {
        setSaveCompleted(false);
        // mPickedColorPreview.getBackground().setColorFilter(previewColor, PorterDuff.Mode.SRC_ATOP);
        //mColorPreviewText.setText(ColorItem.makeHexString(previewColor));


    }

    /**
     * Animate the color being picked.
     *
     * @param pickedColor the color being picked.
     */
    protected void animatePickedColor(int pickedColor) {
        mLastPickedColor = pickedColor;
        if (mPickedColorProgressAnimator.isRunning()) {
            mPickedColorProgressAnimator.cancel();
        }
        mPickedColorProgressAnimator.start();
    }

    /**
     * Set the "save completed" state.
     * <p/>
     * True means that the save is completed. The preview color should not be saved again.
     *
     * @param isSaveCompleted the "save completed" state.
     */
    protected void setSaveCompleted(boolean isSaveCompleted) {
        //mSaveButton.setEnabled(!isSaveCompleted);
        mSaveCompletedProgressAnimator.cancel();
        mSaveCompletedProgressAnimator.setFloatValues(mSaveCompletedProgress, isSaveCompleted ? 0f : 1f);
        mSaveCompletedProgressAnimator.start();
    }

    /**
     * Set the progress of the picked color animation.
     * <p/>
     * Used by {@link ColorPickerBaseActivity#mPickedColorProgressAnimator}.
     *
     * @param progress A value in closed range [0,1] representing the progress of the picked color animation.
     */
    protected void setPickedColorProgress(float progress) {
        final float fastOppositeProgress = (float) Math.pow(progress, 0.3f);
        final float translationX = mTranslationDeltaX * progress;
        final float translationY = (float) (mTranslationDeltaY * Math.pow(progress, 2f));

        mPickedColorPreviewAnimated.setTranslationX(translationX);
        mPickedColorPreviewAnimated.setTranslationY(translationY);
        mPickedColorPreviewAnimated.setScaleX(fastOppositeProgress);
        mPickedColorPreviewAnimated.setScaleY(fastOppositeProgress);
    }

    /**
     * Set the progress of the animation of the "save completed" state.
     * <p/>
     * Used by {@link ColorPickerBaseActivity#mSaveCompletedProgressAnimator}.
     *
     * @param progress A value in closed range [0,1] representing the progress of the animation of the "save completed" state.
     */


    /**
     * Async task used to configure and start the camera preview.
     */
    private class CameraAsyncTask extends AsyncTask<Void, Void, Camera> {

        /**
         * The {@link android.view.ViewGroup.LayoutParams} used for adding the preview to its container.
         */
        protected FrameLayout.LayoutParams mPreviewParams;

        @Override
        protected Camera doInBackground(Void... params) {
            Camera camera = getCameraInstance();
            if (camera == null) {
                ColorPickerBaseActivity.this.finish();
            } else {
                //configure Camera parameters
                Camera.Parameters cameraParameters = camera.getParameters();

                //get optimal camera preview size according to the layout used to display it
                Camera.Size bestSize = Cameras.getBestPreviewSize(
                        cameraParameters.getSupportedPreviewSizes()
                        , mPreviewContainer.getWidth()
                        , mPreviewContainer.getHeight()
                        , mIsPortrait);
                //set optimal camera preview
                cameraParameters.setPreviewSize(bestSize.width, bestSize.height);
                camera.setParameters(cameraParameters);

                //set camera orientation to match with current device orientation
                Cameras.setCameraDisplayOrientation(ColorPickerBaseActivity.this, camera);

                //get proportional dimension for the layout used to display preview according to the preview size used
                int[] adaptedDimension = Cameras.getProportionalDimension(
                        bestSize
                        , mPreviewContainer.getWidth()
                        , mPreviewContainer.getHeight()
                        , mIsPortrait);

                //set up params for the layout used to display the preview
                mPreviewParams = new FrameLayout.LayoutParams(adaptedDimension[0], adaptedDimension[1]);
                mPreviewParams.gravity = Gravity.CENTER;
            }
            return camera;
        }

        @Override
        protected void onPostExecute(Camera camera) {
            super.onPostExecute(camera);

            // Check if the task is cancelled before trying to use the camera.
            if (!isCancelled()) {
                mCamera = camera;
                if (mCamera == null) {
                    ColorPickerBaseActivity.this.finish();
                } else {
                    //set up camera preview
                    mCameraPreview = new CameraColorPickerPreview(ColorPickerBaseActivity.this, mCamera);
                    mCameraPreview.setOnColorSelectedListener(ColorPickerBaseActivity.this);
                    mCameraPreview.setOnClickListener(ColorPickerBaseActivity.this);

                    //add camera preview
                    mPreviewContainer.addView(mCameraPreview, 0, mPreviewParams);
                }
            }
        }

        @Override
        protected void onCancelled(Camera camera) {
            super.onCancelled(camera);
            if (camera != null) {
                camera.release();
            }
        }
    }

    private void getShades() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        ScarCamoServices service = retrofit.create(ScarCamoServices.class);

        Utils.showDialog();
        Call<ShadePojo> shadesRequest = service.getShades();
        shadesRequest.enqueue(new Callback<ShadePojo>() {
            @Override
            public void onResponse(Call<ShadePojo> call, Response<ShadePojo> response) {
                if (response.body() != null) {
                    if (response.body().getCode().equals("1")) {
                        shadesArrayList = new ArrayList<>();
                        shadesArrayList.addAll(Arrays.asList(response.body().getResult()));


                        adapter = new DeckAdapter(ColorPickerBaseActivity.this, shadesArrayList,R.layout.item_gallery);
                        scrollView.setAdapter(adapter);
                    } else {
                        if (response.body().getMessage() != null) {
                            Utils.showAlertMessage(ColorPickerBaseActivity.this, response.body().getMessage());
                        } else {
                            Utils.showAlertMessage(ColorPickerBaseActivity.this, "Please try after sometime.");

                        }
                    }

                }
                Utils.hideDialog();
            }

            @Override
            public void onFailure(Call<ShadePojo> call, Throwable t) {
                Utils.hideDialog();
            }
        });
    }


    private void getSimilarCodeFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        ScarCamoServices service = retrofit.create(ScarCamoServices.class);

        Utils.showDialog();
        String selectedRGB = Color.red(this.mSelectedColor) + ".0," + Color.green(this.mSelectedColor) + ".0," + Color.blue(this.mSelectedColor)+".0";
        Call<ShadePojo> shadesRequest = service.findShade(selectedRGB);
        shadesRequest.enqueue(new Callback<ShadePojo>() {
            @Override
            public void onResponse(Call<ShadePojo> call, Response<ShadePojo> response) {
                if (response.body() != null) {
                    if (response.body().getCode().equals("1")) {
                        if (shadesArrayList != null) {
                            //findYourTone(mSelectedColor);
                            int index = response.body().getIndex();
                            if(index>0 && index <shadesArrayList.size()) {
                                isAutoScrolled = true;
                                scrollView.smoothScrollToPosition(index);
                                Constants.SelectedColor = mSelectedColor;

                                scrollView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!isActivityFinished) {
                                            Intent intentColorPickerActivity = new Intent(ColorPickerBaseActivity.this, SelectedSkinToneFragment.class);
                                            startActivity(intentColorPickerActivity);
                                            Constants.SelecteResult = shadesArrayList.get(scrollView.getCurrentItem());
                                            ColorPickerBaseActivity.this.finish();
                                        }
                                        //ColorPickerBaseActivity.this.finish();
                                    }
                                }, 3000);
                            }
                        } else {
                            Toast.makeText(ColorPickerBaseActivity.this, "Please try again.", Toast.LENGTH_LONG).show();

                            isToneSelected = false;
                        }
                    } else {
                        if (response.body().getMessage() != null) {
                            Utils.showAlertMessage(ColorPickerBaseActivity.this, response.body().getMessage());
                        } else {
                            Utils.showAlertMessage(ColorPickerBaseActivity.this, "Please choose again.");

                        }
                        Toast.makeText(ColorPickerBaseActivity.this, "Please try again.", Toast.LENGTH_LONG).show();

                        isToneSelected = false;
                    }

                }
                Utils.hideDialog();
            }

            @Override
            public void onFailure(Call<ShadePojo> call, Throwable t) {
                Utils.hideDialog();
                Toast.makeText(ColorPickerBaseActivity.this, "Please try again.", Toast.LENGTH_LONG).show();

                isToneSelected = true;
            }
        });
    }

    private void findYourTone(int selectedColor) {
        int redComponent = Color.red(selectedColor);
        int greenComponent = Color.green(selectedColor);
        int blueComponent = Color.blue(selectedColor);
        for (int i = 0; i < shadesArrayList.size(); i++) {
            String colorString = shadesArrayList.get(i).getColor_code();
            String[] rgbArray = colorString.split(",");
            int red = 0, green = 0, blue = 0;
            if (rgbArray.length == 3) {
                red = Integer.parseInt(rgbArray[0]);
                green = Integer.parseInt(rgbArray[1]);
                blue = Integer.parseInt(rgbArray[2]);
                if (red >= (redComponent - 20) && red <= (redComponent + 20)) {
                    if (green >= (greenComponent - 20) && green <= (greenComponent + 20)) {
                        if (blue >= (blueComponent - 20) && blue <= (blueComponent + 20)) {
                            if (!isColorFound) {
                                isColorFound = true;
                                //isToneSelected = true;
                                scrollView.scrollToPosition(i);
                                scrollView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Constants.SelectedColor = mSelectedColor;
                                        Intent intentColorPickerActivity = new Intent(ColorPickerBaseActivity.this, SelectedSkinToneFragment.class);
                                        startActivity(intentColorPickerActivity);
                                        Constants.SelecteResult = shadesArrayList.get(scrollView.getCurrentItem());
                                        ColorPickerBaseActivity.this.finish();
                                        //ColorPickerBaseActivity.this.finish();
                                    }
                                }, 2000);
                                break;
                            }

                        }
                    }
                }


            }
        }

        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                isToneSelected = false;
                if (!isColorFound) {
                    Toast.makeText(ColorPickerBaseActivity.this, "Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }, 2000);

    }



}
