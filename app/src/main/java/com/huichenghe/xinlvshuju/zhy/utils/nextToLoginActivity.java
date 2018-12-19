package com.huichenghe.xinlvshuju.zhy.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;
import com.huichenghe.xinlvshuju.slide.EditPersionInfoActivity;
import com.huichenghe.xinlvshuju.slide.LoginActivity;

public class nextToLoginActivity extends BaseActivity
{
    private static final String TAG = nextToLoginActivity.class.getSimpleName();
    private Button mRegist;
//    private ImageView hotStart;
    private MYRecever recer ;
    private TextView typeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_to_login);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        init();

        String startModle = getIntent().getStringExtra(MyConfingInfo.HOT_START);
        if(startModle != null && startModle.equals(MyConfingInfo.HOT_START_FROM_MAIN))
        {
//            if(hotStart.getVisibility() == View.GONE || hotStart.getVisibility() == View.INVISIBLE)
//            {
//                hotStart.setVisibility(View.VISIBLE);
//            }
        }

        recer = new MYRecever();
        IntentFilter filter = new IntentFilter(MyConfingInfo.CLOSE_OTHER_PAGER);
        registerReceiver(recer, filter);

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(recer);


    }

    /**
     * 初始化控件和设置监听
     */
    private void init()
    {
        typeFace = (TextView)findViewById(R.id.typeface_setting);
//        AssetManager manager = getAssets();
//        Typeface ty = Typeface.createFromAsset(manager, "fonts/GABRIOLA.TTF");
//        typeFace.setTypeface(ty);


//        mImageView = (ImageView)findViewById(R.id.regist_layout_back);
//        regist_layout = (LinearLayout)findViewById(R.id.regist_layout);
        Button mLogin = (Button) findViewById(R.id.login_button);
        mRegist = (Button)findViewById(R.id.regist_button);
        RelativeLayout mDirect = (RelativeLayout) findViewById(R.id.direct_enter);

//        mImageView.setOnClickListener(listener);
        mLogin.setOnClickListener(listener);
        mRegist.setOnClickListener(listener);
        mDirect.setOnClickListener(listener);


//        hotStart = (ImageView)findViewById(R.id.hotStart_to_show);
//        hotStart.setOnClickListener(listener);





    }


    /**
     * 设置监听
     */
    private NoDoubleClickListener listener = new NoDoubleClickListener()
    {
        @Override
        public void onNoDoubleClick(View v)
        {
            switch (v.getId())
            {
                case R.id.login_button:

                    Intent mIntent = new Intent();
                    mIntent.setClass(nextToLoginActivity.this, LoginActivity.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mIntent);
                    break;

                case R.id.regist_button:
                    Intent mIntents = new Intent();
                    mIntents.setClass(nextToLoginActivity.this, registActivity.class);
                    mIntents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mIntents);



                    // 显示注册布局
//                    if(Build.VERSION.SDK_INT < 21 && Build.VERSION.SDK_INT >= 18)
//                    {
//                        regist_layout.setVisibility(View.VISIBLE);
//                    }
//                    else
//                    {
//                        showRegistLayout(mRegist, regist_layout, true);
//                    }
//                    isRegist = true;
                    break;
                case R.id.direct_enter:
                    // 判断是否有缓存的个人信息
//                    String info = LocalDataSaveTool.getInstance(nextToLoginActivity.this)
//                            .readSp(MyConfingInfo.USER_NICK);
//
//                    if(!info.isEmpty())  // 有缓存
//                    {
//                        Intent mmIntent = new Intent();
//                        mmIntent.setClass(nextToLoginActivity.this, MainActivity.class);
//                        mmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(mmIntent);
//                    }
//                    else                // 无缓存
//                    {
                        // 进入EditPersionInfoActivity
                        Intent intent = new Intent();
                        intent.setClass(nextToLoginActivity.this, EditPersionInfoActivity.class);
                        intent.putExtra(MyConfingInfo.WHERE_ARE_YOU_FORM, MyConfingInfo.FORM_DIRECTLY_USE);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
//                    }
//                    nextToLoginActivity.this.finish();      // 结束当前页面
                    break;

//                case R.id.regist_layout_back:
//                    // 隐藏注册布局
//                    showRegistLayout(mRegist, regist_layout, false);



//                    break;


//                case R.id.hotStart_to_show:
//                    if(isRegist) {
//                        if (Build.VERSION.SDK_INT < 21 && Build.VERSION.SDK_INT >= 18) {
//                            regist_layout.setVisibility(View.GONE);
//                        } else {
//                            showRegistLayout(mRegist, regist_layout, false);
//                        }
//                        isRegist = false;
//                    }
//                    else
//                    {
//                        nextToLoginActivity.this.onBackPressed();
//                    }
//                    break;
            }
        }
    };







    /**
     * 带动画的显示或隐藏注册布局
     * @param
     * @param mRegist
     * @param regist_layout
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showRegistLayout(Button mRegist, final LinearLayout regist_layout, boolean show)
    {
        if(show)
        {
            // 执行圆形动画
            int[] location = new int[2];            // 两个元素的数组
            mRegist.getLocationOnScreen(location);
            int x = location[0] + (mRegist.getWidth()/2);                    // 圆心x轴
            int y = location[1];                    // 圆心y轴

            // get the final radius for the clipping circle
            int finalRadius = Math.max(regist_layout.getWidth(), regist_layout.getHeight());  // 最大半径
            int minRadius = mRegist.getWidth()/6;        // 最小半径

            // create the animator for this view (the start radius is zero)
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(regist_layout, x, y, minRadius, finalRadius);

            // make the view visible and start the animation
            regist_layout.setVisibility(View.VISIBLE);
            anim.start();
        }
        else
        {


            int[] location = new int[2];
            mRegist.getLocationOnScreen(location);
            int cxx = location[0] + (mRegist.getWidth()/2);
            int cyy = location[1]/2;
//        int cyy = vg.getMeasuredHeightAndState();

//        int cxx = (int)vg.getX();
//        int cyy = (int)vg.getY();

            // get the initial radius for the clipping circle
            // 获取园的半径
            int initialRadiuss = regist_layout.getHeight();    // 最大半径
            int minRadiuss = regist_layout.getWidth()/6;             // 最小半径

            // create the animation (the final radius is zero)
            Animator anims =
                    ViewAnimationUtils.createCircularReveal(regist_layout, cxx, cyy, initialRadiuss, minRadiuss);

            // make the view invisible when the animation is done
            anims.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    regist_layout.setVisibility(View.GONE);
                }
            });
            // start the animation
            anims.start();


        }




    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
//        if(keyCode == KeyEvent.KEYCODE_BACK)
//        {
//            Log.i(TAG, "状态是---:" + isRegist);
//            if(isRegist)
//            {
//                if(Build.VERSION.SDK_INT < 21 && Build.VERSION.SDK_INT >= 18)
//                {
//                    regist_layout.setVisibility(View.GONE);
//                }
//                else
//                {
//                    showRegistLayout(mRegist, regist_layout, false);
//                }
//                isRegist = false;
//                return true;
//            }
//        }

        return super.onKeyDown(keyCode, event);
    }



    class MYRecever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equals(MyConfingInfo.CLOSE_OTHER_PAGER))
            {
                nextToLoginActivity.this.finish();
            }

        }
    }
}
