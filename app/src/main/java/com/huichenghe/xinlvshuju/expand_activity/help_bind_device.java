package com.huichenghe.xinlvshuju.expand_activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.mainpack.BaseActivity;

public class help_bind_device extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_bind_device);
        initView();
    }

    private void initView()
    {
        findViewById(R.id.back_button_re).setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.back_button_re:
                    help_bind_device.this.onBackPressed();
                    break;
            }
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        setIcon();
    }

    private void setIcon()
    {
        ImageView stepOne = (ImageView) findViewById(R.id.step_icon_one);
        ImageView stepTwo = (ImageView) findViewById(R.id.step_icon_two);
        ImageView stepThree = (ImageView) findViewById(R.id.step_icon_three);
        stepOne.setImageResource(R.mipmap.step_one);
        stepTwo.setImageResource(R.mipmap.step_two);
        stepThree.setImageResource(R.mipmap.step_three);





    }
}
