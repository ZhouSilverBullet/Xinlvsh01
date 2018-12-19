package com.huichenghe.xinlvshuju.CustomView;




import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.xinlvshuju.R;


@SuppressWarnings("ResourceType")
public class MyProgressDialog {
	
	public Dialog mDialog;
	private AnimationDrawable animationDrawable = null;
	private Context context;
	boolean isshowing;			// 代表msg是否是显示状态
	TextView text;
	ImageView loadingImage;
	
	public MyProgressDialog(Context context) {

		this.context = context;
	}

	public void build(String message)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.progress_view, null);
		text = (TextView) view.findViewById(R.id.progress_message);
		if(!TextUtils.isEmpty(message)){
			Log.i("","显示的字符串是2：" + message);
			text.setText(message);
			text.setVisibility(View.VISIBLE);
			isshowing = true;
		}else{
			isshowing = false;
			text.setVisibility(View.GONE);
		}
		loadingImage = (ImageView) view.findViewById(R.id.progress_view);
		loadingImage.setImageResource(R.drawable.loading_animation_white);
//		loadingImage.setImageDrawable(contextR.anim.loading_animation);
		animationDrawable = (AnimationDrawable)loadingImage.getDrawable();
		animationDrawable.setOneShot(false);
		animationDrawable.start();
		if(mDialog == null)
		{
			mDialog = new Dialog(context, R.style.PromptDialogStyle);
		}
		mDialog.setContentView(view);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(true);
		mDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				if(BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice())
					BluetoothLeService.getInstance().close(false);
			}
		});
	}

	public void changeTheImageView(int res)
	{
		if(res == 0)
			return;
		loadingImage.setImageResource(res);
	}

	public void changeTheText(int id)
	{
		String newText = context.getString(id);
		text.setText(newText);
	}
	public void setMessage(int id)
	{
		String msg = context.getString(id);
		if(!isshowing)
		{ 											// 如果没有显示，则设置为visible
				text.setVisibility(View.VISIBLE);
				isshowing = true;
		}
													// 设置提醒字符串
		text.setText(msg);
	}
	
	
	public void show() {
		mDialog.show();
	}
	
	public void setCanceledOnTouchOutside(boolean cancel) {
		mDialog.setCanceledOnTouchOutside(cancel);
	}
	
	public void dismiss() {
		if(mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			animationDrawable.stop();
			isshowing = false;
		}
	}

	public boolean isShowing() {
		// TODO Auto-generated method stub
		return mDialog.isShowing();
	}

}
