package com.scarcamo;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.scarcamo.adapter.DeckAdapter;
import com.scarcamo.custom.CButtonBold;
import com.scarcamo.pojo.Result;
import com.scarcamo.pojo.ShadePojo;
import com.scarcamo.retrofit.ScarCamoServices;
import com.scarcamo.sugarDB.UserData;
import com.scarcamo.utilities.Constants;
import com.scarcamo.utilities.Utils;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FindYourToneActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, SurfaceHolder.Callback,
        Camera.ShutterCallback, Camera.PictureCallback {

    private SurfaceView selectedImage;

    private CButtonBold nextBtn;
    private int[] colorArray = {R.color.colorGreen, R.color.colorGrey, R.color.colorPrimaryDark, R.color.colorAccent};
    private Bitmap bitmap;
    private ArrayList<Result> shadesArrayList;
    private DiscreteScrollView scrollView;
    private DeckAdapter adapter;
    private FrameLayout parent;
    private int selectedPosition = 0;
    Camera mCamera;
    private int mCameraId;
    private SurfaceHolder mHolder;
    private boolean previewIsRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_your_tone);

        //bitmap = Constants.SELECTED_IMAGE;
        setUpView();

        getShades();
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
                if (response.body() != null && response.body().getCode().equals("1")) {
                    shadesArrayList = new ArrayList<>();
                    shadesArrayList.addAll(Arrays.asList(response.body().getResult()));

                    int centreX = (int) (selectedImage.getPivotX() + selectedImage.getWidth() / 2);
                    int centreY = (int) (selectedImage.getPivotY() + selectedImage.getHeight() / 2);

                    //final int pxl = bitmap.getPixel(bitmap.getWidth()/2, bitmap.getHeight()/2);

                    adapter = new DeckAdapter(FindYourToneActivity.this, shadesArrayList, R.layout.item_gallery);
                    scrollView.setAdapter(adapter);


                }
                Utils.hideDialog();
            }

            @Override
            public void onFailure(Call<ShadePojo> call, Throwable t) {
                Utils.hideDialog();
            }
        });
    }

    private void findYourTone(int pxl) {
        int redComponent = Color.red(pxl);
        int greenComponent = Color.green(pxl);
        int blueComponent = Color.blue(pxl);
        for (int i = 0; i < shadesArrayList.size(); i++) {
            String color = shadesArrayList.get(i).getColor_code();
            String[] rgbArray = color.split(",");
            int red = 0, green = 0, blue = 0;
            if (rgbArray.length == 3) {
                red = Integer.parseInt(rgbArray[0]);
                green = Integer.parseInt(rgbArray[1]);
                blue = Integer.parseInt(rgbArray[2]);

                if (red >= (redComponent - 15) && red <= (redComponent + 15)) {
                    if (green >= (greenComponent - 15) && green <= (greenComponent + 15)) {
                        if (blue >= (blueComponent - 15) && blue <= (blueComponent + 15)) {
                            //System.out.println("System.Out + iii  "+i);
                            scrollView.scrollToPosition(i);
                            selectedPosition = i;
                        }
                    }
                }


            }
        }
        Utils.showSnackBar(FindYourToneActivity.this, parent, "Touch image to pick skin tone again.");

    }

    private void setUpView() {
        // Camera surface view created

        //selectedImage.setImageBitmap(bitmap);
        parent = (FrameLayout) findViewById(R.id.parent);
        scrollView = findViewById(R.id.picker);

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        if (Camera.getNumberOfCameras() > 1) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        selectedImage = (SurfaceView) findViewById(R.id.selectedImage);
        try {
            mCamera = Camera.open(mCameraId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mHolder = selectedImage.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.setSizeFromLayout();
        mHolder.addCallback(this);

        scrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.07f)
                .setMinScale(0.7f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                .build());
        scrollView.addScrollListener(new DiscreteScrollView.ScrollListener<RecyclerView.ViewHolder>() {
            @Override
            public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {
                ((CardView) newCurrent.itemView).getChildAt(0).setPadding(4, 12, 4, 4);
                ((CardView) currentHolder.itemView).getChildAt(0).setPadding(0, 0, 0, 0);
            }
        });
        nextBtn = (CButtonBold) findViewById(R.id.nextBtn);
        initializeListener();
    }

    private void initializeListener() {

        nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == nextBtn) {
            if (shadesArrayList != null && shadesArrayList.size() > 0) {
                Constants.SelecteResult = shadesArrayList.get(scrollView.getCurrentItem());
                String colorString = shadesArrayList.get(scrollView.getCurrentItem()).getColor_code();
                String[] rgbArray = colorString.split(",");
                int red = 0, green = 0, blue = 0;
                if (rgbArray.length == 3) {
                    red = Integer.parseInt(rgbArray[0]);
                    green = Integer.parseInt(rgbArray[1]);
                    blue = Integer.parseInt(rgbArray[2]);
                    int color = Color.rgb(red, green, blue);
                    Constants.SelectedColor = color;
                }
                saveCardOnServer(Constants.SelecteResult);
            }

        }
    }

    private void saveCardOnServer(Result selecteResult) {

        List<UserData> userDataList = UserData.listAll(UserData.class);
        if(userDataList.size()>10){
            UserData userData = UserData.findById(UserData.class, userDataList.get(userDataList.size()-1).getId());
            userData.delete();
        }
        UserData mUserData = new UserData(selecteResult.getId(),selecteResult.getColor_code(),Utils.parseDate(selecteResult.getModify_date()));
        mUserData.save();
        FindYourToneActivity.this.finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int action = event.getAction();

        final int evX = (int) event.getX();
        final int evY = (int) event.getY();
        System.out.println("onTouch");
        switch (action) {
            case MotionEvent.ACTION_DOWN:


            case MotionEvent.ACTION_UP:

                int pxl = bitmap.getPixel(evX, evY);

                findYourTone(pxl);


        }
        return true;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

    }

    @Override
    public void onShutter() {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
        int rotation = this.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; // Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; // Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;// Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;// Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotate);
        mCamera.setParameters(params);
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onResume() {
        myStartPreview();  // restart preview after awake from phone sleeping
        super.onResume();

    }

    @Override
    protected void onPause() {
        myStopPreview();
        super.onPause();
    }

    // safe call to start the preview
    // if this is called in onResume, the surface might not have been created yet
    // so check that the camera has been set up too.
    public void myStartPreview() {
        if (!previewIsRunning && (mCamera != null)) {
            mCamera.startPreview();
            previewIsRunning = true;
        }
    }

    // same for stopping the preview
    public void myStopPreview() {
        if (previewIsRunning && (mCamera != null)) {
            mCamera.stopPreview();
            previewIsRunning = false;
        }
    }

    @Override
    protected void onDestroy() {
        mCamera.release();
        mHolder.removeCallback(this);
        super.onDestroy();
    }
}
