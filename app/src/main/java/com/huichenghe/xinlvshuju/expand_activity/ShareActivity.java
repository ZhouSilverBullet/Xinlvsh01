package com.huichenghe.xinlvshuju.expand_activity;


import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.ShotScreenForShare;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;
import com.huichenghe.xinlvshuju.mainpack.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class ShareActivity extends BaseActivity implements PlatformActionListener
{
    private static final String TAG = ShareActivity.class.getSimpleName();
    private ArrayList<Integer> data;
    private AppCompatEditText mEditText;
    private ImageView back;
    static private Bitmap b;
    private File file;
    private String imagePath;
    static private AppCompatImageView mImageView;
    private LinearLayout shareOther;
    private static ContentLoadingProgressBar contentLoadingProgressBar;

//    private Handler mHandler= new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case 0:
//                    Toast.makeText(ShareActivity.this, R.string.share_success,Toast.LENGTH_LONG).show();
//                    break;
//                case clock:
//                    Toast.makeText(ShareActivity.this, R.string.share_cancle,Toast.LENGTH_LONG).show();
//                    break;
//                case -clock:
//                    Toast.makeText(ShareActivity.this, R.string.share_failed,Toast.LENGTH_LONG).show();
//                    break;
//                case heartWarning:
//                    mImageView.setImageBitmap(b);
//                    if(Build.VERSION.SDK_INT <= 21)
//                    {
//                        mImageView.setVisibility(View.VISIBLE);
//                    }
//                    else
//                    {
//                        showTheFirstLayout(mImageView);
//                    }
//                    break;
//            }
//        }
//    };



    static class MyHandler extends Handler
    {
        WeakReference<ShareActivity> weakReference;

        public MyHandler(ShareActivity activity)
        {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            ShareActivity shareActivity = weakReference.get();
            if(shareActivity != null)
            {
                switch (msg.what){
                case 0:
                    MyToastUitls.showToast(shareActivity,  R.string.share_success, 1);
                    break;
                case 1:
                    MyToastUitls.showToast(shareActivity, R.string.share_cancle, 1);
                    break;
                case -1:
                    MyToastUitls.showToast(shareActivity, R.string.share_failed, 1);
                    break;
                case 5:
                    contentLoadingProgressBar.hide();
                    mImageView.setImageBitmap(b);
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                    {
                        mImageView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        showTheFirstLayout(mImageView, shareActivity);
                    }
                    break;
            }
            }




        }
    }

    private MyHandler mHandler = new MyHandler(this);

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void showTheFirstLayout(final View image, ShareActivity activity)
    {

//        final int[] cxx = new int[clock];
//        final int[] cyy = new int[clock];
////        cxx = (int) searchButton.getX() + (searchButton.getWidth()/phone);
//        ViewTreeObserver observer = image.getViewTreeObserver();
//        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
//        {
//            @Override
//            public boolean onPreDraw()
//            {
//                cxx[0] = image.getMeasuredWidth();
//                cyy[0] = image.getMeasuredHeight();
//                return true;
//            }
//        });


        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        final float cxx = metrics.widthPixels/2;
        final float cyy = image.getHeight();
//                int cyyy = searchButton.getHeight();

        Log.i(TAG, "图片的宽和高" + cxx + "--" + cyy);
        // get the final radius for the clipping circle 最大的作为半径
        final int finalRadius = Math.max(image.getWidth(), image.getHeight());

        // create the animator for this view (the start radius is zero)
//        image.postOnAnimation(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });
        image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                image.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Animator anims =
                        ViewAnimationUtils.createCircularReveal(image, (int) cxx, (int) cyy, 0, finalRadius);

                // make the view visible and start the animation
                image.setVisibility(View.VISIBLE);
                anims.start();
            }
        });



//                searchButton.setVisibility(View.GONE);
//        searchButton.hide();
//        showState = SHOW_SECOND_PAGER;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ShotScreenForShare.getInstance().setOnShotListener(new ShotScreenForShare.ShortScreenListener()
        {
            @Override
            public void onShortOverListener()
            {
                MyApplication.threadService.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getAndShowTheShotScreen();
                    }
                });
            }
        });
        settingTheStatebarAndNavigationbar();
        initToolbar();
        initOther();
          //初始化shareSDK


//        Log.i(TAG, "语言环境:" + Locale.getDefault().getLanguage().equals("zh"));


    }


    private void getAndShowTheShotScreen()
    {
        if(file.exists())
        {
            try {

                FileInputStream inputStream = new FileInputStream(file);
                b = BitmapFactory.decodeStream(inputStream);
//                mImageView.setImageBitmap(b);
                mHandler.sendEmptyMessage(5);

                inputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    protected void onResume()
    {
        super.onResume();

    }


    /**
     * 其他分享
     */
    private void shareToOther(String path, String subject, String content)
    {
        Intent other = new Intent(Intent.ACTION_SEND);
        if(path != null && path.isEmpty())
        {
            other.setType("text/plain");
        }
        else
        {
            File file = new File(path);
            if(file != null && file.exists() && file.isFile())
            {
                other.setType("image/png");
                Uri uri = Uri.fromFile(file);
                other.putExtra(Intent.EXTRA_STREAM, uri);
            }
        }
        other.putExtra(Intent.EXTRA_SUBJECT, subject);
        other.putExtra(Intent.EXTRA_TEXT, content);
        other.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(other, getString(R.string.app_name)));
    }

    private void initOther()
    {
        imagePath = SDPathUtils.getSdcardPath() + File.separator + "mistepshareIcon.png";
        file = new File(imagePath);
        NoDoubleClickListener listern = new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                if (view == shareOther)
                {
                    shareToOther(imagePath, getString(R.string.app_name), null);
                }
            }
        };
        shareOther = (LinearLayout)findViewById(R.id.share_to_other);
        shareOther.setOnClickListener(listern);
        contentLoadingProgressBar = (ContentLoadingProgressBar)findViewById(R.id.content_loading);
//        contentLoadingProgressBar.show();
        mEditText=(AppCompatEditText)findViewById(R.id.edit_the_share_content);
        mImageView = (AppCompatImageView) findViewById(R.id.image_shot_screen);


        MyShareRecyclerAdapter adapter = new MyShareRecyclerAdapter();
        data = new ArrayList<Integer>();
//        data.add(R.mipmap.qq_true);
//        data.add(R.mipmap.room_true);
        data.add(R.mipmap.wechart_true);
        data.add(R.mipmap.frendly_true);
        data.add(R.mipmap.weibo_true);
        data.add(R.mipmap.turter_true);
        data.add(R.mipmap.facebook_true);
        RecyclerView shareIcon = (RecyclerView) findViewById(R.id.share_icon);
        shareIcon.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager manager = new LinearLayoutManager(ShareActivity.this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        shareIcon.setLayoutManager(manager);
        shareIcon.setHasFixedSize(true);
        shareIcon.setAdapter(adapter);

    }


    private void initToolbar()
    {
//        Toolbar toolbar = (Toolbar)findViewById(R.id.share_toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setOnMenuItemClickListener(myMenuListener);
//        toolbar.setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationIcon(R.mipmap.back);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        back = (ImageView)findViewById(R.id.back_from_share);
        back.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                ShareActivity.this.onBackPressed();
            }
        });
    }

    private void deleteTheScreenShort()
    {
        new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected Boolean doInBackground(Void... params)
            {
                String path = SDPathUtils.getSdcardPath() + File.separator + "mistepshareIcon.png";
                File file = new File(path);
                if(file.exists())
                {
                    file.delete();
                }
                return null;
            }
        };



    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
//        if(keyCode == KeyEvent.KEYCODE_BACK)
//        {
//            deleteTheScreenShort();
//        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置沉浸式状态栏和底部虚拟键全屏
     */
    private void settingTheStatebarAndNavigationbar() {
//        // 透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //底部虚拟键透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


    Toolbar.OnMenuItemClickListener myMenuListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.share_menu_send:
                    //Toast.makeText(ShareActivity.this, "share to send", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    };

    private void sendToTarget()
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_text));
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));
    }

    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }



    class MyShareRecyclerAdapter extends RecyclerView.Adapter<MyShareRecyclerAdapter.MyHolder>
    {
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View v = LayoutInflater.from(ShareActivity.this).inflate(R.layout.item_for_share_screen, parent, false);
            return new MyHolder(v);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, final int position)
        {
            holder.checkBox.setBackgroundResource(data.get(position));
            holder.checkBox.setOnClickListener(new NoDoubleClickListener()
            {
                @Override
                public void onNoDoubleClick(View view)
                {
                    toSendShare(position);
                }
            });
//            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//            {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//                {
////                    Toast.makeText(ShareActivity.this, "分享到。。。", Toast.LENGTH_SHORT).show();
////                    sendToTarget();
//                }
//            });
        }

        /**
         * 点击分享到指定平台
         * @param position
         */
        private void toSendShare(int position)
        {
            switch (position)
            {
                case 0:     // 微信
//                                       if(isPkgInstalled("com.tencent.mobileqq")) {
//                        shareToQQ();
//                    }else{
//                        Toast.makeText(ShareActivity.this, R.string.install_qq, Toast.LENGTH_LONG).show();
//                    }
                    if(isPkgInstalled("com.tencent.mm")) {
                        shareToWechat();
                    }else{
                        MyToastUitls.showToast(ShareActivity.this, R.string.install_weichat, 1);
                    }
                break;

                case 1:     // 微信朋友圈
//                    if(isPkgInstalled("com.tencent.mobileqq")) {
//                        shareToQzone();
//                    }else{
//                        Toast.makeText(ShareActivity.this, R.string.install_qq,Toast.LENGTH_LONG).show();
//                    }
                    if(isPkgInstalled("com.tencent.mm")) {
                        shareToWechatMoment();
                    }else{
                        MyToastUitls.showToast(ShareActivity.this,R.string.install_weichat, 1);
                    }

                    break;
                case 2:     // 微博
//                    if(isPkgInstalled("com.tencent.mm")) {
//                        shareToWechat();
//                    }else{
//                        Toast.makeText(ShareActivity.this, R.string.install_weichat,Toast.LENGTH_LONG).show();
//                    }

                    if(isPkgInstalled("com.sina.weibo"))
                        shareToSina();
                    else
                        MyToastUitls.showToast(ShareActivity.this,R.string.install_weibo, 1);
                    break;
                case 3:     // 推特
                    if(isPkgInstalled("com.twitter.android")) {
//                        shareToWechatMoment();
                        shareToTurterOrFacebook(imagePath, getString(R.string.app_name), null, "com.twitter.android");
                    }else{
                        MyToastUitls.showToast(ShareActivity.this,R.string.install_turter, 1);
                    }
                    break;
                case 4:     // facebook
                    if(isPkgInstalled("com.facebook.katana"))
                    {
//                        shareToSina();
                        shareToTurterOrFacebook(imagePath, getString(R.string.app_name), null, "com.facebook.katana");
                    }
                    else
                    {
                        MyToastUitls.showToast(ShareActivity.this,R.string.install_facebook, 1);
                    }
            }

        }




        @Override
        public int getItemCount()
        {
            return data.size();
        }



        class MyHolder extends RecyclerView.ViewHolder
        {
            CheckBox checkBox;
            public MyHolder(View itemView)
            {
                super(itemView);
                checkBox = (CheckBox)itemView.findViewById(R.id.share_target);
            }
        }


    }


    /**
     * 安卓自带分享方法
     */
    private void shareToTurterOrFacebook(String imageFilePath, String subject, String content, String packag)
    {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        if(imageFilePath != null && imageFilePath.isEmpty())
        {
            sendIntent.setType("text/plain");
        }
        else
        {
            File imageFile = new File(imagePath);
            if(imageFile != null && imageFile.exists() && imageFile.isFile())
            {
                sendIntent.setType("image/png");
                Uri uri = Uri.fromFile(imageFile);
                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
            }
        }
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.setPackage(packag);
        startActivity(sendIntent);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        String path = SDPathUtils.getSdcardPath() + File.separator + "mistepshareIcon.png";
        File file = new File(path);
        if(file.exists())
        {
            file.delete();
        }


    }

    /**
     * 判定是否安装相应程序
     * @param pkgName 包名
     * @return
     */
    private boolean isPkgInstalled(String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getApplicationContext().getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    private void shareToSina(){
        MyToastUitls.showToast(ShareActivity.this, R.string.share_now, 1);
        SinaWeibo.ShareParams shp = new SinaWeibo.ShareParams();
        if(Locale.getDefault().getLanguage() != null && Locale.getDefault().getLanguage().equals("zh"))
        {
            shp.setTitle("mistep");
            shp.setTitleUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.huichenghe.xinlvsh01");
            shp.setExecuteUrl();
        }
        else
        {
            shp.setTitle("mistep");
            shp.setTitleUrl("https://play.google.com/store/apps/details?id=com.huichenghe.xinlvsh01");
            shp.setExecuteUrl();
        }


        shp.setText(mEditText.getText().toString());
        shp.setImagePath(file.getAbsolutePath());

        Platform sinaWeibo=ShareSDK.getPlatform(SinaWeibo.NAME);
        sinaWeibo.setPlatformActionListener(this);

        sinaWeibo.share(shp);
    }

    /**
     * 分享到朋友圈
     */
    private void shareToWechatMoment(){
        MyToastUitls.showToast(ShareActivity.this, R.string.share_now, 1);
        Wechat.ShareParams shp = new Wechat.ShareParams();
        if(Locale.getDefault().getLanguage() != null && Locale.getDefault().getLanguage().equals("zh"))
        {
            shp.setTitle("mistep");
            shp.setTitleUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.huichenghe.xinlvsh01");
            shp.setExecuteUrl();
        }
        else
        {
            shp.setTitle("mistep");
            shp.setTitleUrl("https://play.google.com/store/apps/details?id=com.huichenghe.xinlvsh01");
            shp.setExecuteUrl();
        }

        shp.setText(mEditText.getText().toString());
        // wechat.setImageData(b);
        shp.setImagePath(file.getAbsolutePath());
        shp.setShareType(Platform.SHARE_IMAGE);

        Platform weixin = ShareSDK.getPlatform(ShareActivity.this, WechatMoments.NAME);
        weixin.setPlatformActionListener(this);
        weixin.share(shp);
    }
    /**
     * 分享到微信
     */
    private void shareToWechat(){
        MyToastUitls.showToast(ShareActivity.this, R.string.share_now, 1);
        Wechat.ShareParams shp = new Wechat.ShareParams();
        if(Locale.getDefault().getLanguage() != null && Locale.getDefault().getLanguage().equals("zh"))
        {
            shp.setTitle("mistep");
            shp.setTitleUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.huichenghe.xinlvsh01");
            shp.setExecuteUrl();
        }
        else
        {
            shp.setTitleUrl("https://play.google.com/store/apps/details?id=com.huichenghe.xinlvsh01");
            shp.setTitle("mistep");
            shp.setExecuteUrl();
        }

        shp.setText(mEditText.getText().toString());

        // wechat.setImageData(b);
        shp.setImagePath(file.getAbsolutePath());
        shp.setShareType(Platform.SHARE_IMAGE);

        Platform weixin = ShareSDK.getPlatform(ShareActivity.this, Wechat.NAME);
        weixin.setPlatformActionListener(this);
        weixin.share(shp);
    }
    /**
     * 分享到qq
     */
    private void shareToQQ()
    {
        MyToastUitls.showToast(ShareActivity.this, R.string.share_now, 1);
        QQ.ShareParams shp = new QQ.ShareParams();
        if(Locale.getDefault().getLanguage() != null && Locale.getDefault().getLanguage().equals("zh"))
        {
            shp.setTitle("mistep");
            shp.setTitleUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.huichenghe.xinlvsh01");
            shp.setExecuteUrl();
        }
        else
        {
            shp.setTitle("mistep");
            shp.setTitleUrl("https://play.google.com/store/apps/details?id=com.huichenghe.xinlvsh01");
            shp.setExecuteUrl();
        }
        /*sp.setTitle("我的运动");
        sp.setTitleUrl("http://");*/ // 标题的超链接

        shp.setText("");
        shp.setImagePath(file.getAbsolutePath());
//        shp.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.huichenghe.xinlvsh01");
        Platform qq = ShareSDK.getPlatform (QQ.NAME);

        qq. setPlatformActionListener(this); // 设置分享事件回调
        // 执行图文分享
        qq.share(shp);
    }

    /**
     * 分享到qzone
     */
    private void shareToQzone()
    {
        MyToastUitls.showToast(ShareActivity.this, R.string.share_now, 1);
        QZone.ShareParams shp = new QZone.ShareParams();

        if(Locale.getDefault().getLanguage() != null && Locale.getDefault().getLanguage().equals("zh"))
        {
            shp.setTitle("mistep");
            shp.setTitleUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.huichenghe.xinlvsh01");
//            shp.setText("http://a.app.qq.com/o/simple.jsp?pkgname=com.huichenghe.xinlvsh01");
            shp.setExecuteUrl();
        }
        else {
            shp.setTitle("mistep");
            shp.setTitleUrl("https://play.google.com/store/apps/details?id=com.huichenghe.xinlvsh01");
            shp.setExecuteUrl();
        }
//        sp.setTitle("【心率手环】");
//        sp.setTitleUrl(null); // 标题的超链接
//        sp.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.huichenghe.xinlvsh01");
        String text=mEditText.getText().toString();
        Log.i("text", text);
        text=text.equals("")? getString(R.string.app_name):text;
        Log.e("text",text);
        shp.setText(text);

        shp.setImagePath(file.getAbsolutePath());

        Platform qzone = ShareSDK.getPlatform (QZone.NAME);

        qzone. setPlatformActionListener(this); // 设置分享事件回调
        // 执行图文分享
        qzone.share(shp);
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onCancel(Platform platform, int i) {
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        mHandler.sendEmptyMessage(-1);

    }
}

