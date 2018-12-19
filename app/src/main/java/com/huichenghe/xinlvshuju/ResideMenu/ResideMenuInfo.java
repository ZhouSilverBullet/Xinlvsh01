package com.huichenghe.xinlvshuju.ResideMenu;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.CustomView.CircleImageView;
import com.huichenghe.xinlvshuju.R;


/**
 * 侧拉头部的用户信息对象，布局
 */
public class ResideMenuInfo extends LinearLayout {

	/** menu item icon 菜单条目的图标*/
	private CircleImageView iv_icon;
	/** menu item title 菜单条目的标题*/
	private TextView battery, connectState;
	private TextView nickName;

	public ResideMenuInfo(Context context) {
		super(context);
		initViews(context);
	}

	public ResideMenuInfo(Context context, int icon, String title, String dengji) {
		super(context);
		initViews(context);
		iv_icon.setImageResource(icon);
	}

	private void initViews(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.residemenu_info, this);
		nickName = (TextView)findViewById(R.id.user_nick);
		iv_icon = (CircleImageView) findViewById(R.id.image_icon);
		battery = (TextView)findViewById(R.id.battery_value);
		connectState = (TextView)findViewById(R.id.tv_the_connecte_state);
	}

	public void setNickName(String nick)
	{
		nickName.setText(nick);
	}


	public void setBattery(String batterys)
	{
		battery.setText(batterys);
	}

	public void setConnectState(String state)
	{
		connectState.setText(state);
	}

	/**
	 * set the icon color;
	 * 
	 * @param icon
	 */
	public void setIcon(int icon) {
		iv_icon.setImageResource(icon);
	}
	public void seteleicon(int icon){
		Drawable drawable_n = getResources().getDrawable(icon);
		drawable_n.setBounds(0, 0, drawable_n.getMinimumWidth(),drawable_n.getMinimumHeight());  //此为必须写的
		battery.setCompoundDrawables(drawable_n, null, null, null);
	}

	/**
	 * set the title with string;
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
//		tv_username.setText(title);
	}

	/**
	 * set the title with string;
	 * 
	 * @param dengji
	 */
	public void setDengJi(String dengji) {
//		tv_dengji.setText(dengji);
	}



	public CircleImageView getHeadPottrin()
	{
		return iv_icon;
	}

	public void setIcon(Bitmap mBitmap) {
		iv_icon.setImageBitmap(mBitmap);
	}
}
