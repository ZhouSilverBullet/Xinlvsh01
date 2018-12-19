package com.huichenghe.xinlvshuju.slide.takePhotoPackage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BleDataForTakePhoto;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.xinlvshuju.CommonUtilss.CommonUtils;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;
import com.huichenghe.xinlvshuju.mainpack.MyApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TakePhotoActivity extends BaseActivity
{
    public static final String TAG = TakePhotoActivity.class.getSimpleName();
    private ImageView take;
    private int CAMARCODE = 1;
    private float screenWidth, screenHeight;
    private Camera mCamera;
    private TextView canclePhoto;
    private ImageView priviewImage;
    private ImageView takePhoto;
    private ImageView kuaiMen, focusTouch, enterCamera;
    private ImageView changePreview;
    private boolean isPreview = false;
    private PopupWindow showCameraWindow;
    private boolean isBack = false;
    private ImageView takePhotoImage;
    private final int UPDTE_PREVIEW = 0;
    private final int CLOSE_FOCUS_ICON = 1;
    private final String UPDATEBITMAP = "update bitmap";
    private static TakePhotoActivity takePhotoActivity;
    private boolean isFocus = false;
    private boolean isTakingNow = false;
    private SurfaceTexture surfaces;
    private TextureView tureView;
    private int cameraOne = 0;
    private int cameraTwo = 1;
    private Bitmap photo;
    private int currenCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
    private String currentPath = null;
    private ScanFileUtils scanFileUtils;
    private int focusWidth;
    private Animation focusAnimation;
    private Handler takeHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case UPDTE_PREVIEW:
//                    Bundle bun = msg.getData();
//                    byte[] bitmapData = bun.getByteArray(UPDATEBITMAP);
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
//                    priviewImage.setImageBitmap(photo);
                    if(photo != null)
                    {
                        priviewImage.setImageBitmap(photo);
                    }
                    break;
                case CLOSE_FOCUS_ICON:
                    if(focusTouch.getVisibility() == View.VISIBLE)
                    {
                        focusTouch.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        setStatusBarTransparent();
        intiScreen();
        MyApplication.threadService.execute(new Runnable() {
            @Override
            public void run() {
                intiPhotoFile();
            }
        });
    }


    private void intiPhotoFile()
    {
        File file = new File(SDPathUtils.getSdcardPath() + MyConfingInfo.CAMERA_PATH);
        String[] photoList = file.list();
        if(photoList == null || photoList.length <= 0)
        {
            return;
        }
        currentPath = SDPathUtils.getSdcardPath() + MyConfingInfo.CAMERA_PATH + photoList[photoList.length - 1];
    }

    /**
     * 设置状态栏透明
     */
    private void setStatusBarTransparent()
    {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i(TAG, "takePhotoActivity -- onResume");

    }
    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            showCameraWindow();
        }
    };


    private void intiScreen()
    {
        focusWidth = (int)CommonUtils.Dp2Px(TakePhotoActivity.this, 48);
        DisplayMetrics dm = TakePhotoActivity.this.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        // 打开拍照
        BleDataForTakePhoto.getInstance().setOnDeviceTakePhotoOpen(new CameraCallback());
        BleDataForTakePhoto.getInstance().openTakePhoto((byte)0x00);
    }
    private long lastTime = 0;
    private int spaceTime = 1000;
    class CameraCallback implements DataSendCallback
    {
        @Override
        public void sendSuccess(byte[] receveData)
        {
            if(receveData == null)
            {
                takePhotoNow();
            }
            else
            {
                takeHandler.postDelayed(runnable, 0);
            }
        }
        @Override
        public void sendFailed()
        {

        }
        @Override
        public void sendFinished()
        {

        }
    }



//    BleDataForTakePhoto.OnDeviceTakePhotoOpen onDeviceTakePhotoOpen = new BleDataForTakePhoto.OnDeviceTakePhotoOpen()
//    {
//        @Override
//        public void onOpen()
//        {
//            Log.i(TAG, "点击打开相机onopen");
//            // 收到手环回复打开相机数据，打开系统相机
//            isBack = true;
//        }
//        @Override
//        public synchronized void onMessageRecever()
//        {
//            Long currentTime = Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis();
//            if(currentTime - lastTime > spaceTime)
//            {
//                lastTime = currentTime;
//                // 收到手环调用的信息
//                Log.i(TAG, "收到命令并拍照");
//                takePhotoNow();
//            }
//
//        }
//    };

    private int checkCamera(int one)
    {
        if(one == Camera.CameraInfo.CAMERA_FACING_BACK)
        {
            return Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else
        {
            return Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    public void dismissCameraPopWindow()
    {
        if(showCameraWindow.isShowing())
        {
            showCameraWindow.dismiss();
        }
    }
    /**
     * 显示window
     */
    private void showCameraWindow()
    {
        if(isDestroyed() || isFinishing())
        {
            return;
        }
        isBack = false;
        View serfaceView = getCameraLayout();
        if(showCameraWindow == null)
        {
            showCameraWindow = new PopupWindow(serfaceView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }
        showCameraWindow.setFocusable(true);
        showCameraWindow.setAnimationStyle(R.style.mypopupwindow_anim_style);
        showCameraWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        showCameraWindow.setBackgroundDrawable(new BitmapDrawable());
        showCameraWindow.showAtLocation(serfaceView, Gravity.CENTER, 0, 0);
        showCameraWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                showCameraWindow = null;
//                BleDataForTakePhoto.getInstance().openTakePhoto((byte)0x01);
                TakePhotoActivity.this.onBackPressed();
                if(scanFileUtils != null)
                {
                    scanFileUtils.closeConnection();
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        BleDataForTakePhoto.getInstance().openTakePhoto((byte)0x01);
    }

    Runnable run = new Runnable()
    {
        @Override
        public void run()
        {
            isTakingNow = false;
        }
    };

    private View getCameraLayout()
    {
        View view = LayoutInflater.from(TakePhotoActivity.this).inflate(R.layout.my_camera_layout, null);

        NoDoubleClickListener listener = new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v)
            {
                if(v == takePhoto)
                {
                    takePhotoNow();
                }
                else if(v == priviewImage)
                {
                    if(currentPath != null)
                    {
                        MyApplication.threadService.execute(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                scanFileUtils = new ScanFileUtils(TakePhotoActivity.this);
                                scanFileUtils.startScan(currentPath);
                            }
                        });

                    }
                    else
                    {
                        MyToastUitls.showToast(TakePhotoActivity.this, R.string.do_not_have_photo_to_see, 1);
                    }

//                    Intent intent;
//                    String saveName = LocalDataSaveTool.getInstance(TakePhotoActivity.this).readSp(MyConfingInfo.PICTURE_NAME);
//                        String fileCameras = MyConfingInfo.getSdcardPath()
//                                + File.separator + "Pictures"
//                                + File.separator + saveName;
//                        String filePathShow = MyConfingInfo.getSdcardPath() +
//                                File.separator + "DCIM" +
//                                File.separator + "Camera/";
                    /**
                     *

                    if(currentPath != null && !currentPath.equals(""))
                    {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "image/*");
                        Log.i(TAG, "照片路径：" + currentPath);
                        TakePhotoActivity.this.startActivity(intent);
                    }
                    else
                    {
//                        intent = new Intent(Intent.ACTION_PICK_ACTIVITY);
//                        intent.setType("image/*");
//                        Log.i(TAG, "照片路径：打开gallery");
//                        TakePhotoActivity.this.startActivity(intent);
//                        Intent mIntent = TakePhotoActivity.this
//                                .getPackageManager()
//                                .getLaunchIntentForPackage(packName);
//                        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                        startActivity(mIntent);
                    }*/
                }
                else if(v == changePreview)
                {
                    currenCamera = checkCamera(currenCamera);
                        // 切换前后置摄像头
                        mCamera.stopPreview();
                        mCamera.release();
                        mCamera = null;
                        isPreview = false;
                        startCamera(tureView, surfaces);
//                        mCamera = getCameraInstance(currenCamera);
                }
                else if(v == canclePhoto)
                {
                    showCameraWindow.dismiss();
                }
            }
        };
        changePreview = (ImageView)view.findViewById(R.id.preview_change);
        kuaiMen = (ImageView)view.findViewById(R.id.kuai_men_icon);
        focusTouch = (ImageView)view.findViewById(R.id.focus_icon);
        takePhoto = (ImageView)view.findViewById(R.id.take_photo_now);
        priviewImage = (ImageView)view.findViewById(R.id.preview_picture);
        canclePhoto = (TextView)view.findViewById(R.id.cancle_photo);
        if(currentPath != null)
        readBitmap();
        takePhoto.setOnClickListener(listener);
        priviewImage.setOnClickListener(listener);
        changePreview.setOnClickListener(listener);
        canclePhoto.setOnClickListener(listener);
        tureView = (TextureView)view.findViewById(R.id.camera_layout);
        tureView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(!isPreview)
                {
                    return false;
                }
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if(checkFocus(event))
                    return true;
                }
                return true;
            }
        });
        tureView.setKeepScreenOn(true);
        // 设置监听
        tureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
        {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
            {
                TakePhotoActivity.this.surfaces = surface;
                startCamera(tureView, surface);
            }
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
            {

            }
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
            {
                if(mCamera != null)
                {
                    if(isPreview)
                    {
                        mCamera.stopPreview();
                        mCamera.release();
                        mCamera = null;
                        isPreview = false;
                    }
                }
                return true;
            }
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface)
            {
            }
        });
        return view;
    }

    private boolean checkFocus(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();
        float major = event.getTouchMajor();    // 获取点击的横向的距离
        float minor = event.getToolMinor();
        int a = ((int)(x - major / 2));
        int b = ((int)(y - minor / 2));
        int c = ((int)(x + major / 2));
        int d = ((int)(y + minor / 2));
        Rect touchRect = new Rect(a, b, c, d);
        if(touchRect.left < 0)
            touchRect.left = 0;
        if(touchRect.top < 0)
            touchRect.top = 0;
        if(touchRect.right > screenWidth)
            touchRect.right = (int)screenWidth;
        if(touchRect.bottom > screenHeight)
            touchRect.bottom = (int)screenHeight;
//        if(touchRect.left >= touchRect.right || touchRect.top >= touchRect.bottom)
//            return false;
        doFocus();
        RelativeLayout.LayoutParams pamas = (RelativeLayout.LayoutParams)focusTouch.getLayoutParams();
        int left = touchRect.left - (focusWidth / 2);
        int top = touchRect.top - (focusWidth / 2);
        if(left < 0)
            left = 0;
        else if(left + focusWidth > screenWidth)
            left = (int)(screenWidth - focusWidth);
        if(top + focusWidth > screenHeight)
            top = (int)(screenHeight - focusWidth);
        pamas.leftMargin = left;
        pamas.topMargin = top;
        focusTouch.setLayoutParams(pamas);
        focusTouch.setVisibility(View.VISIBLE);
        if(focusAnimation == null)
        {
            focusAnimation = AnimationUtils.loadAnimation(TakePhotoActivity.this, R.anim.focus_anim);
        }
        focusTouch.startAnimation(focusAnimation);
        takeHandler.sendEmptyMessageDelayed(CLOSE_FOCUS_ICON, 2000);
        return true;
    }

    private void readBitmap()
    {
        MyApplication.threadService.execute(new Runnable() {
            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 12;                    // 照片大小
                options.inTempStorage = new byte[100 * 1024];// 缓存大小
                options.inPreferredConfig = Bitmap.Config.RGB_565;// 画质
                options.inPurgeable = true;                   // 内存不足允许清理内存
                options.inInputShareable = true;
                photo = BitmapFactory.decodeFile(currentPath, options);
                takeHandler.sendEmptyMessage(UPDTE_PREVIEW);
            }
        });
    }


    /**
     * 模拟点击快门，拍照
     */
    private void takePhotoNow()
    {
        Log.i(TAG, "手动拍照：" + isTakingNow);
        if(isTakingNow)return;
        takeHandler.removeCallbacks(run);
        takeHandler.postDelayed(run, 2000);
        isTakingNow = true;
        if(currenCamera == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            mCamera.takePicture(shutterCallback, picktureCallback, relultPicktureCallback);
        }
        else
        {
            if(isFocus)
            {
                mCamera.takePicture(shutterCallback, picktureCallback, relultPicktureCallback);
            }
            else
            {
                mCamera.cancelAutoFocus();
                if(mCamera != null)
                {
                    mCamera.autoFocus(new Camera.AutoFocusCallback()
                    {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera)
                        {
                            camera.cancelAutoFocus();
                            if(success)
                            {
                                camera.takePicture(shutterCallback, picktureCallback, relultPicktureCallback);
                            }
                        }
                    });
                }
            }
        }

    }

    /**
     * 点击快门调用
     */
    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback()
    {
        @Override
        public void onShutter()
        {
            kuaiMen.setVisibility(View.VISIBLE);
        }
    };

    /**
     * 获取到原始图片调用
     */
    Camera.PictureCallback picktureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };
    /**
     * 最终图片调用
     */

    Camera.PictureCallback relultPicktureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            kuaiMen.setVisibility(View.GONE);
            new saveTask().execute(data);
            if(mCamera != null)
            {
                takeHandler.removeCallbacks(run);
                mCamera.stopPreview();
                mCamera.startPreview();
                isPreview = true;
                isFocus = false;
                isTakingNow = false;
                doFocus();
            }
        }
    };

    class saveTask extends AsyncTask<byte[], Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(byte[]... params)
        {
            byte[] photoData = params[0];
            savePicture(photoData);
            return null;
        }
    }

    private void savePicture(byte[] photoData)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTempStorage = new byte[100 * 1024];
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inSampleSize = 12;
        options.inInputShareable = true;
        photo = BitmapFactory.decodeByteArray(photoData, 0, photoData.length, options);

//        Matrix matrix = new Matrix();
//        matrix.preRotate(90);
//        photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
//        Message msg = Message.obtain();
//        msg.what = UPDTEPREVIEW;
//        Bundle bundle = new Bundle();
//        bundle.putByteArray(UPDATEBITMAP, photoData);
//        msg.setData(bundle);
        takeHandler.sendEmptyMessage(UPDTE_PREVIEW);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HH-mm-ss-SSS", Locale.getDefault());
        String pictureName = format.format(calendar.getTime());
        // 存入系统相册
//        currentPath = MediaStore.Images.Media.insertImage(getContentResolver(), photo, pictureName, "来自迈步手环");
//        LocalDataSaveTool.getInstance(TakePhotoActivity.this).writeSp(MyConfingInfo.PICTURE_NAME, luj);
//        String filePath = MyConfingInfo.getSdcardPath()
//                + File.separator + "Pictures" + File.separator + pictureName + ".jpg";
        // 存入系统相册
        String filePathRoot = SDPathUtils.getSdcardPath() +
                File.separator + "DCIM" +
                File.separator + "Camera" +
                File.separator;
        File root = new File(filePathRoot);
        if(!root.exists())
        {
            root.mkdirs();
        }
        String filePath = filePathRoot + pictureName + ".jpg";
        File file = new File(filePath);
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            outputStream = new BufferedOutputStream(fileOutputStream);
            outputStream.write(photoData);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (fileOutputStream != null) {
                    outputStream.flush();
                    fileOutputStream.flush();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                    outputStream.close();
                }
            } catch (IOException es) {
                es.printStackTrace();
            }
        }finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    if (outputStream != null) {
                        outputStream.flush();
                    }
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentPath = filePath;
        }
        Intent scanIntent;
        Log.i(TAG, "照片扫描路径：" + filePath);
        File scanFile = new File(filePath);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(scanFile));
        }
        else
        {
            scanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(scanFile));
        }
        sendBroadcast(scanIntent);




//        String filePaths = MyConfingInfo.getSdcardPath()
//                + File.separator + "DCIM"
//                + File.separator + "Camera"
//                + File.separator + format.format(calendar.getTime()) + ".jpg";
//        File saveFile = new File(filePaths);
//        try {
//            FileOutputStream outPut = new FileOutputStream(saveFile);
////            photo.compress(Bitmap.CompressFormat.JPEG, 100, outPut);
//            outPut.write(photoData);
//            outPut.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    /**
     * 打开相机
     * @param tureView
     */
    private void startCamera(TextureView tureView, SurfaceTexture surfaceView)
    {
        if(!isPreview)
        {
            mCamera = getCameraInstance(currenCamera);
        }
        if(mCamera != null && !isPreview)
        {
            Camera.Parameters params = mCamera.getParameters();
//            if (params.getSupportedFocusModes().contains(
//                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
//                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//            }
//            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            DisplayMetrics metrics = TakePhotoActivity.this.getResources().getDisplayMetrics();
            Camera.Size previewSize = CameraUtils.getLargePreviewSize(mCamera, metrics);
            params.setPreviewSize(previewSize.width, previewSize.height);
            Camera.Size pictureSize = CameraUtils.getLargePictureSize(mCamera);
            params.setPictureSize(pictureSize.width, pictureSize.height);
            int angree = 90;
            if(currenCamera == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                angree = 90;
            }
            else
            {
                angree = 270;
            }
            params.setRotation(angree);
            mCamera.setParameters(params);
            mCamera.setDisplayOrientation(90);
//            params.set("rotation", 90);
//            params.setPreviewSize((int)screenWidth, (int)screenHeight);
//            params.setPreviewFpsRange(4, 10);
//            params.setPictureFormat(ImageFormat.JPEG);
//            params.set("jpeg-quality", 85);
//            List<Camera.Size> sizes = params.getSupportedPictureSizes();
//            int maxHeight = 0;
//            int maxWidth = 0;
//            for (int i = 0; i < sizes.size() - 3; i++)
//            {
//                int height = sizes.get(i).height;
//                int width = sizes.get(i).width;
//                if(height > maxHeight)
//                {
//                    maxHeight = height;
//                }
//                if(width > maxWidth)
//                {
//                    maxWidth = width;
//                }
//            }
//            Log.i(TAG, "手机支持的分辨率高：" + maxHeight + "宽：" + maxWidth);
//            params.setPictureSize(maxWidth, maxHeight);
////            setDisplay(params, mCamera);
//            mCamera.setDisplayOrientation(90);
            try {
                mCamera.setPreviewTexture(surfaceView);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
//                @Override
//                public void onPreviewFrame(byte[] data, Camera camera) {
//
//                }
//            });
            mCamera.startPreview();
//            tureView.setAlpha(1.0f);
//            tureView.setRotation(0.0f);
            isPreview = true;
            doFocus();
        }
    }

    private void doFocus()
    {
            if(mCamera != null)
            {
                mCamera.autoFocus(new Camera.AutoFocusCallback()
                {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if(success)
                        {
                            isFocus = true;
                            if(focusTouch.getVisibility() == View.VISIBLE)
                            {
                                focusTouch.setVisibility(View.GONE);
                            }
                            camera.cancelAutoFocus();
                        }
                    }
                });
        }
    }

    private Camera getCameraInstance(int currenCamera)
    {
        Camera c = null;
        try{
            c = Camera.open(currenCamera);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * 设置预览方向
     * @param params
     * @param mCamera
     */
    private void setDisplay(Camera.Parameters params, Camera mCamera)
    {
        if(Build.VERSION.SDK_INT >= 8)
        {
            Method showMethod;
            try {
                showMethod = mCamera.getClass().getDeclaredMethod("setDisplayOrientation", new Class[]{int.class});
                if(showMethod != null)
                {
                    try {
                        showMethod.invoke(mCamera, new Object[]{90});
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
