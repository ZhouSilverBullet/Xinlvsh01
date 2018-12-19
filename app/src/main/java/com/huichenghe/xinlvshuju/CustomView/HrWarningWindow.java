package com.huichenghe.xinlvshuju.CustomView;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.huichenghe.bleControl.Ble.BleDataForHRWarning;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lixiaoning on 2016/5/11.
 */
public class HrWarningWindow
{
    private PopupWindow popupWindow;
    private Context mContext;
    private EditText maxEdit, minEdit;
    private TextView setButton;
    private ImageView mBack;

    public HrWarningWindow(Context context)
    {
        this.mContext = context;
        showHRWarningWindow();
    }

    private void showHRWarningWindow()
    {
        View view = getThePopwindowView();
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.mypopupwindow_anim_style_left_right);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow = null;
            }
        });
    }

    private View getThePopwindowView()
    {
        View v = LayoutInflater.from(mContext).inflate(R.layout.popwindow_setting_hr_remind, null);
//        NestedScrollView scrollView = (NestedScrollView)v.findViewById(R.id.nestedScrollView);
//        scrollView.setPadding(0, getStatusBarHeight(), 0, 0);
        mBack = (ImageView) v.findViewById(R.id.back_button_setting);
        mBack.setOnClickListener(listener);
        maxEdit = (EditText)v.findViewById(R.id.max_hr_edit);
        minEdit = (EditText)v.findViewById(R.id.min_hr_edit);
        String result = LocalDataSaveTool.getInstance(mContext).readSp(MyConfingInfo.HRWARNING_SETTING_VALUE);
        try
        {
            JSONObject json = new JSONObject(result);
            int max = json.getInt(MyConfingInfo.MAX_HR);
            int min = json.getInt(MyConfingInfo.MIN_HR);
            if(max != 0 && min != 0)
            {
                maxEdit.setText(String.valueOf(max));
                minEdit.setText(String.valueOf(min));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        setButton = (TextView)v.findViewById(R.id.setting_the_warning_hr);
        setButton.setOnClickListener(listener);
        return v;
    }

    NoDoubleClickListener listener = new NoDoubleClickListener()
    {
        @Override
        public void onNoDoubleClick(View v)
        {
            if(v == mBack)
            {
                if(popupWindow != null && popupWindow.isShowing())
                {
                    popupWindow.dismiss();
                }
            }
            else if (v == setButton)
            {
                settingWarning();
            }
        }
    };

    private Handler handler = new Handler();
    private void settingWarning()
    {
        String maxHR = maxEdit.getText().toString().trim();
        String minHR = minEdit.getText().toString().trim();
        boolean isOk = checkTheMaxHRAndMinHR(maxHR, minHR);
        if(isOk)
        {
            BleDataForHRWarning.getInstance().closeOrOpenWarning(Integer.valueOf(maxHR), Integer.valueOf(minHR), (byte)0x00);
            handler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    popupWindow.dismiss();
                }
            }, 600);

        }
    }

    private boolean checkTheMaxHRAndMinHR(String maxHR, String minHR)
    {
        if(maxHR == null || maxHR.equals(""))
        {
            MyToastUitls.showToast(mContext, R.string.setting_failed_not_null, 1);
            return false;
        }
        if(minHR == null || minHR.equals(""))
        {
            MyToastUitls.showToast(mContext, R.string.setting_failed_not_null, 1);
            return false;
        }
        int maxHr = Integer.parseInt(maxHR);
        int minHr = Integer.parseInt(minHR);
        if(maxHr > 180)
        {
            MyToastUitls.showToast(mContext, R.string.setting_hr_max_falied, 1);
            return false;
        }

        if(minHr < 40)
        {
            MyToastUitls.showToast(mContext, R.string.setting_hr_min_falied, 1);
            return false;
        }
        if(maxHr == minHr)
        {
            MyToastUitls.showToast(mContext, R.string.setting_hr_min_falied_not, 1);
            return false;
        }
        if(maxHr < minHr)
        {
            MyToastUitls.showToast(mContext, R.string.setting_hr_min_falied_not_reverse, 1);
            return false;
        }
        return true;
    }
}
