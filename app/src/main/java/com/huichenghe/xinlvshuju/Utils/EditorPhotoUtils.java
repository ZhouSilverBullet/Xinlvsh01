package com.huichenghe.xinlvshuju.Utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.huichenghe.xinlvshuju.SDPathUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EditorPhotoUtils {

	private static final String TAG = "EditorPhotoUtils";
	private static final File PHOTO_DIR = new File(
			SDPathUtils.getSdcardPath() + "/SH_DCIM");
	private File mCurrentPhotoFile;
	private File mResultPhotoFile;
	private ImageView mImageView;
	private Activity mActivity;
	private int mStatus;
	private int mPhotoPickSize = 640;
	private takePhotoCallback takePhoteCall;
	
	//invork
	//public EditorPhotoUtils(Context c, ImageView faceView);

	private interface Status {
		/**
		 * The loader is fetching data
		 */
		public static final int LOADING = 0;		
		/**
		 * Not currently busy. We are waiting for the user to enter data
		 */
		public static final int EDITING = 1;

		/**
		 * The data is currently being saved. This is used to prevent more
		 * auto-saves (they shouldn't overlap)
		 */
		public static final int SAVING = 2;

		/**
		 * Prevents any more saves. This is used if in the following cases: -
		 * After Save/Close - After Revert - After the user has accepted an edit
		 * suggestion
		 */
		public static final int CLOSING = 3;

		/**
		 * Prevents saving while running a child activity.
		 */
		public static final int SUB_ACTIVITY = 4;
	}

	public EditorPhotoUtils(Activity act, ImageView faceView) {
		// TODO Auto-generated constructor stub
		mActivity = act;
		mImageView = faceView;
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat;
		dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss", Locale.getDefault());
		return dateFormat.format(date) + ".jpg";
	}


	private String getResultPhotoName()
	{
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("'IMG'_yyyyMMdd___HHmmss", Locale.getDefault());
		return format.format(date) + ".jpg";
	}

	private static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	private Intent getPhotoPickIntent() {
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//		intent.addCategory(Intent.CATEGORY_OPENABLE);  
//		intent.setType("image/*");
//		intent.putExtra("crop", "false");
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);
//		intent.putExtra("outputX", mPhotoPickSize);
//		intent.putExtra("outputY", mPhotoPickSize);
//		intent.putExtra("return-data", true);
		return intent;
	}
	
	private Intent getPhotoPickIntentNoCrop() {
 
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
        Intent wrapperIntent=Intent.createChooser(intent, null);
        
        return wrapperIntent;

	}

	private Intent getCropImageIntent(Uri photoUri) {
		mResultPhotoFile = new File(PHOTO_DIR, getResultPhotoName());
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");					// 图片可裁剪状态
		intent.putExtra("aspectX", 1);						// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("scale", true);					// 裁剪支持缩放
		intent.putExtra("outputX", mPhotoPickSize);
		intent.putExtra("outputY", mPhotoPickSize);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mResultPhotoFile));// 原图路径
		intent.putExtra("circleCrop", true);				 // 裁剪形状，默认矩形
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 图片输出格式
		intent.putExtra("return-data", false);
		intent.putExtra("noFaceDetection", true);			 // 是否需要人脸识别
		return intent;
	}

	private Uri getResultPhotoUri()
	{
		File file = new File(PHOTO_DIR, getResultPhotoName());
		return Uri.fromFile(file);
	}

	// private static final int REQUEST_CODE_JOIN = 0;
	private static final int REQUEST_CODE_CAMERA_WITH_DATA = 1;
	private static final int REQUEST_CODE_PHOTO_PICKED_WITH_DATA = 2;
	private static final int RESULT_LOAD_IMAGE = 3;
	private static final int RESULT_CHOOSE_PHOTO_TO_CAPE = 4;
	// private static final int REQUEST_CODE_ACCOUNTS_CHANGED = info;

	public void onUseAsPrimaryChosen() {

	}

	public void onRemovePictureChosen() {

	}

	public void onTakePhotoChosen() {

		// mRawContactIdRequestingPhoto = mEditor.getRawContactId();
		try {
			// Launch camera to take photo for selected contact
			PHOTO_DIR.mkdirs();
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			mStatus = Status.SUB_ACTIVITY;
			mActivity.startActivityForResult(intent,
					REQUEST_CODE_CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void onPickFromGalleryChosen() {

		// mRawContactIdRequestingPhoto = mEditor.getRawContactId();
		try {
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntent();
			mStatus = Status.SUB_ACTIVITY;
			mActivity.startActivityForResult(intent,
					RESULT_CHOOSE_PHOTO_TO_CAPE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void onPickFromGalleryChosenNoCrop()
	{

		// mRawContactIdRequestingPhoto = mEditor.getRawContactId();
		try {
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntentNoCrop();
			mStatus = Status.SUB_ACTIVITY;
			mActivity.startActivityForResult(intent,
					RESULT_LOAD_IMAGE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}


	public void setTakePhotoListener(takePhotoCallback callback)
	{
		this.takePhoteCall = callback;
	}




	/**
	 * 牌照后在Acitivy的onActivityResult调用此方法，执行拍照后的逻辑
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (mStatus == Status.SUB_ACTIVITY) {
			mStatus = Status.EDITING;
		}

		switch (requestCode) {
		case REQUEST_CODE_PHOTO_PICKED_WITH_DATA:
		{
			Log.i(TAG, "上传头像--REQUEST_CODE_PHOTO_PICKED_WITH_DATA");
			// Ignore failed requests
			if (resultCode != Activity.RESULT_OK)	// 代表Activity操作成功
				return;
			// As we are coming back to this view, the editor will be reloaded
			// automatically,
			// which will cause the photo that is set here to disappear. To
			// prevent this,
			// we remember to set a flag which is interpreted after loading.
			// This photo is set here already to reduce flickering.
//			Bitmap mPhoto = data.getParcelableExtra("data");
			MediaScannerConnection.scanFile(mActivity,
					new String[]{mResultPhotoFile.getAbsolutePath()},
					new String[]{null}, null);
			String path = mResultPhotoFile.getPath();
			Bitmap mPhoto = BitmapFactory.decodeFile(path, null);

			// setPhoto(mRawContactIdRequestingPhoto, mPhoto);
//			Log.e(TAG, "REQUEST_CODE_PHOTO_PICKED_WITH_DATA  mPhoto=" + path);
//			Log.e(TAG, "走了这里requesCode:" + requestCode + "resultCode:" + resultCode + data);
			mImageView.setImageBitmap(mPhoto);
			takePhoteCall.getBitmap(mPhoto, mResultPhotoFile.getAbsolutePath());
			break;
		}
		case REQUEST_CODE_CAMERA_WITH_DATA:
		{
			Log.i(TAG, "上传头像--REQUEST_CODE_CAMERA_WITH_DATA--" + resultCode);
			// Ignore failed requests
			if (resultCode != Activity.RESULT_OK)
				return;
//			 doCropPhoto(mCurrentPhotoFile);
			try {
				// Add the image to the media store
				MediaScannerConnection.scanFile(mActivity,
						new String[] { mCurrentPhotoFile.getAbsolutePath() },
						new String[] { null }, null);

				// Launch gallery to crop the photo
//				Uri uri = data.getData();
				Uri uri = Uri.fromFile(mCurrentPhotoFile);

				final Intent intent = getCropImageIntent(uri);
				mStatus = Status.SUB_ACTIVITY;
				mActivity.startActivityForResult(intent,
						REQUEST_CODE_PHOTO_PICKED_WITH_DATA);
			} catch (Exception e) {
				Log.e(TAG, "Cannot crop image", e);
				e.printStackTrace();
			}
			break;
		}
		case RESULT_LOAD_IMAGE:
		{
			Log.i(TAG, "上传头像--RESULT_LOAD_IMAGE");
			// Ignore failed requests
			if (resultCode != Activity.RESULT_OK)
				return;
			
			ContentResolver cr = mActivity.getContentResolver();  
            
	           try
			   {
	        	   	Bitmap bmp = BitmapFactory.decodeStream(cr.openInputStream(data.getData()));
	    			mImageView.setImageBitmap(bmp);
	           }
	           catch (Exception ex) {
	        	   
	           }
			break;
		}
		case RESULT_CHOOSE_PHOTO_TO_CAPE:
		{
			Log.i(TAG, "上传头像--RESULT_CHOOSE_PHOTO_TO_CAPE");
			if(data == null)return;
			Uri uri = data.getData();
			if(uri != null)
			{
				Intent intent = getCropImageIntent(uri);
				mStatus = Status.SUB_ACTIVITY;
				mActivity.startActivityForResult(intent,
						REQUEST_CODE_PHOTO_PICKED_WITH_DATA);
			}
			break;
		}
		}
	}


	/**
	 * 保存照片到sd卡
	 * @param mPhoto
	 * @param imageviewSavePath
	 */
	private void saveImageFromBm(Bitmap mPhoto, String imageviewSavePath)
	{
		Log.e(TAG, "头像的存储路径：" + imageviewSavePath);
		try {
			// 缓冲输出流，需传一个文件输出流
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imageviewSavePath, false));
			// 压缩图片
			// 参数一：存储照片的格式，参数二：照片是否压缩，参数三：输出流
			mPhoto.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public interface takePhotoCallback
	{
		void getBitmap(Bitmap bitmap, String headName);
	}

}
