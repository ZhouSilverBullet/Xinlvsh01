package com.huichenghe.xinlvshuju.slide;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import com.huichenghe.SwipeLibrary.SwipeBackLayout;
import com.huichenghe.SwipeLibrary.app.SwipeBackActivity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.NetStatus;

public class HelpActivity extends SwipeBackActivity
{
    public static final String TAG = HelpActivity.class.getSimpleName();
    private WebView mWebViewGetHelpPager;
    private SwipeBackLayout swipeBackLayout;
    private int VABRATE_DURATION = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        intiSwipeLayout();


//        new PackageNameUtils().getPackageNameM(this);
//        if(BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice())
//        {
//            BluetoothLeService.getInstance().setBLENotify(null, true, false);
//        }
        settingTheStatebarAndNavigationbar();
        initToolbar();
//        mWebViewGetHelpPager.loadUrl("http://www.baidu.com/");
        if(!NetStatus.isNetWorkConnected(getApplicationContext()))
        {
            MyToastUitls.showToast(HelpActivity.this, R.string.network_disconnect, 1);
        }
        else
        {
            loadingURL(MyConfingInfo.newWebRoot);
        }
    }

    private void loadingURL(String url)
    {
        mWebViewGetHelpPager = (WebView)findViewById(R.id.web_view_help);
        mWebViewGetHelpPager.setWebChromeClient(new WebChromeClient());
        mWebViewGetHelpPager.setWebViewClient(new WebViewClientForHelpPager());
        mWebViewGetHelpPager.getSettings().setJavaScriptEnabled(true);
//        Log.i(TAG, "帮助连接：" + MyConfingInfo.newWebRoot);
        mWebViewGetHelpPager.clearCache(true);
        mWebViewGetHelpPager.loadUrl(url);
    }

    private void intiSwipeLayout()
    {
        swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeSize(600);
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        swipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {

            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
//                vibrate(VABRATE_DURATION);
            }

            @Override
            public void onScrollOverThreshold() {
//                vibrate(VABRATE_DURATION);
            }
        });
    }

    private void vibrate(int vabrate_duration)
    {
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattion = {0, vabrate_duration};
        vibrator.vibrate(pattion, -1);
    }


    private void initToolbar()
    {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_help_activity);
        toolbar.setTitleTextColor(getResources().getColor(R.color.White_forme));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.back_icon_new);
        toolbar.setNavigationOnClickListener(new NoDoubleClickListener()
        {
            @Override
            public void onNoDoubleClick(View v)
            {
//                if(mWebViewGetHelpPager.canGoBack())
//                {
//                    String urlCurrent = mWebViewGetHelpPager.getUrl();
//                    if(urlCurrent.equals(MyConfingInfo.webHelpCn) || urlCurrent.equals(MyConfingInfo.webHelpEn)
//                            || urlCurrent.equals(MyConfingInfo.webHelpCnNotConnect) || urlCurrent.equals(MyConfingInfo.webHelpEnNotConnect)
//                            || urlCurrent.startsWith(MyConfingInfo.webBackLinkCN) || urlCurrent.startsWith(MyConfingInfo.webBackLinkEN))
//                    {
//                        HelpActivity.this.onBackPressed();
//                    }
//                    mWebViewGetHelpPager.goBack();
//                }
                HelpActivity.this.onBackPressed();
            }
        });
    }

    private void settingTheStatebarAndNavigationbar()
    {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {   // 控制webView前进后退
//        if(keyCode == KeyEvent.KEYCODE_BACK)
//        {
//            if(mWebViewGetHelpPager.canGoBack())
//            {
//                String urlCurrent = mWebViewGetHelpPager.getUrl();
//                Log.i(TAG, "当前连接地址：" + urlCurrent);
//                if(urlCurrent.equals(MyConfingInfo.webHelpCn) || urlCurrent.equals(MyConfingInfo.webHelpEn)
//                        || urlCurrent.equals(MyConfingInfo.webHelpCnNotConnect) || urlCurrent.equals(MyConfingInfo.webHelpEnNotConnect)
//                        || urlCurrent.startsWith(MyConfingInfo.webBackLinkCN) || urlCurrent.startsWith(MyConfingInfo.webBackLinkEN))
//                {
//                    HelpActivity.this.onBackPressed();
//                }
//
//                mWebViewGetHelpPager.goBack();
//                return true;
//            }
//        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }
}
