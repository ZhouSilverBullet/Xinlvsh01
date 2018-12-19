package com.huichenghe.xinlvshuju.CommonUtilss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.huichenghe.xinlvshuju.SDPathUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class CommonUtils {
	
	private static final String TAG = "CommonUtils";
	
	public static Bitmap getDrawingThubImage(View draw_whole_view, String filepath, float zoom) {
		draw_whole_view.setDrawingCacheEnabled(true);
//		TLog.i(TAG, "touch_main, setCurrentIcon() draw_view.getDrawingCache() = "+draw_view.getDrawingCache());
		
		Bitmap bm = draw_whole_view.getDrawingCache(true);
		Log.e(TAG, "0504, getDrawingThubImage()---clock bm = "+bm);
		if (bm == null) {
			draw_whole_view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			draw_whole_view.layout(0, 0, draw_whole_view.getMeasuredWidth(), draw_whole_view.getMeasuredHeight());
			draw_whole_view.buildDrawingCache();
			bm = draw_whole_view.getDrawingCache();
			Log.e(TAG, "0504, getDrawingThubImage()---phone bm = "+bm+"; draw_whole_view.getMeasuredWidth()="+draw_whole_view.getMeasuredWidth());
			if (bm == null) {
				return bm;
			}
		}
		Matrix matrix = new Matrix(); 
		matrix.postScale(1/zoom,1/zoom); //长和宽放大缩小的比例
		Bitmap obmp;
		try {
			obmp = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		} catch (Exception e) {
			e.printStackTrace();
			obmp = Bitmap.createBitmap(bm);
		}
		draw_whole_view.setDrawingCacheEnabled(false);
		bm.recycle();
		
		
//        Bitmap obmp = Bitmap.createBitmap(bm);
		
		FileOutputStream  m_fileOutPutStream = null;
        
		Log.i(TAG, "save, setCurrentIcon() filepath="+filepath);
        File file = new File(filepath);
        if (!file.exists()) {
        	try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        try {
           m_fileOutPutStream= new FileOutputStream(file);
        } catch (FileNotFoundException e) {
           e.printStackTrace();
        }

        obmp.compress(CompressFormat.PNG, 100, m_fileOutPutStream);
        try {
        	m_fileOutPutStream.flush();
        	m_fileOutPutStream.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return obmp;
	}
	
	
	public static void saveImageFromBm(Bitmap photo, String spath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public static Bitmap getBitmapFromUri(Context c, Uri uri) {
		 try {
			 // 读取uri所在的图片
			 return MediaStore.Images.Media.getBitmap(c.getContentResolver(), uri);
		  } catch (Exception e) {
			  Log.e(TAG, e.getMessage());
			  Log.e(TAG, "getBitmapFromUri() failed, uri：" + uri);
			  e.printStackTrace();
			  return null;
		  }
	 }
	/**
	 * 将byte数组转换成bitmap图像数据
	 * @param b
	 * @param customWidth
	 * @param customHeight
	 * @return
	 */
	public static Bitmap Bytes2Bimap(byte[] b, int customWidth, int customHeight) {
        if (b != null && b.length > 0) {
            Bitmap bm = BitmapFactory.decodeByteArray(b, 0, b.length);
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scale = 1;
            if (customWidth > 0) {
            	scale = ((float) customWidth) / width;
            } else if (customHeight > 0) {
            	scale = ((float) customHeight) / height;
            }
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            
            return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        } else {
            return null;
        }
    }
	/**
	 * bmp文件缩放
	 * @param bm
	 * @param customWidth
	 * @param customHeight
	 * @return
	 */
	public static Bitmap BimapScrool(Bitmap bm, int customWidth, int customHeight) {
		if(bm == null ) return null;
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scale = 1;
        if (customWidth > 0) {
        	scale = ((float) customWidth) / width;
        } else if (customHeight > 0) {
        	scale = ((float) customHeight) / height;
        }
        if (scale == 1) {
        	return bm;
        }
       //TLog.i(TAG, "BimapScrool line 61 customWidth ="+customWidth+";customHeight = "+customHeight +"; scale ="+scale);
        Bitmap ret = null;
        try
        {
	        Matrix matrix = new Matrix();
	        matrix.postScale(scale, scale);       
	        ret = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        } catch(Exception ex) {
			ret = null;
		}
        return ret;
    }
	/**
	 * bmp文件缩放
	 * @param bm
	 * @param customWidth
	 * @param customHeight
	 * @return
	 */
	public static Bitmap BimapScrool2(Bitmap bm, int customWidth, int customHeight) {
		if(bm == null ) return null;
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scale = 1;
        if (customWidth > 0) {
        	scale = ((float) customWidth) / width;
        } else if (customHeight > 0) {
        	scale = ((float) customHeight) / height;
        }
        if (scale == 1) {
        	return bm;
        }
       //TLog.i(TAG, "BimapScrool line 61 customWidth ="+customWidth+";customHeight = "+customHeight +"; scale ="+scale);
        Bitmap ret = null;
        try
        {
	        Matrix matrix = new Matrix();
	        matrix.postScale(scale, scale);       
	        ret = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        } catch(Exception ex) {
			ret = null;
		}
        bm.recycle();
        bm = null;
        return ret;
    }
	
	/**
	 *  计算出该TextView中文字的长度(像素)
	 * @param c
	 * @param text
	 * @param textSize_dp
	 * @return
	 */
	public static float getTextViewWidth(Context c, String text, float textSize_dp){
		if (text == null || text.equals("")) {
			return 0;
		}
		TextView textView = new TextView(c);
		TextPaint paint = textView.getPaint();
		paint.setTextSize(Dp2Px(c, textSize_dp));
		// 得到使用该paint写上text的时候,像素为多少
		float textLength = paint.measureText(text);
		//return Px2Dp(c, textLength);
		return textLength;
	}
	
	/**
	 * 将dp转换为px
	 * @param context
	 * @param dp
	 * @return
	 */
	public static float Dp2Px(Context context, float dp) { 
		// px = dp * (dpi / 160)
	    final float scale = context.getResources().getDisplayMetrics().density; 
	    return (dp * scale + 0.5f); 
	} 
	/**
	 *  将px转换为dp
	 * @param context
	 * @param px
	 * @return
	 */
	public static int Px2Dp(Context context, float px) { 
	    final float scale = context.getResources().getDisplayMetrics().density; 
	    return (int) (px / scale + 0.5f); 
	} 
	
//	public static void saveImageFromView(View canvasView, String save_path) {
//		canvasView.setDrawingCacheEnabled(true);
//		Bitmap bm = canvasView.getDrawingCache();
//		TLog.i(TAG, "saveImageFromView()  canvasView="+canvasView+"; bm="+bm);
//		if (bm != null) {
//		    Bitmap obmp = Bitmap.createBitmap(bm);
//			
//			FileOutputStream  m_fileOutPutStream = null;
//			if (save_path == null)
//				save_path = "/mnt/sdcard/shen_test_thub_img.png";
//		    File file = new File(save_path);
//		    if (!file.exists()) {
//		    	try {
//					file.createNewFile();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//		    }
//		    
//		    try {
//		       m_fileOutPutStream= new FileOutputStream(file);
//		    } catch (FileNotFoundException e) {
//		       e.printStackTrace();
//		    }
//		
//		    obmp.compress(CompressFormat.PNG, 100, m_fileOutPutStream);
//		    try {
//		    	m_fileOutPutStream.flush();
//		    	m_fileOutPutStream.close();
//		    } catch (IOException e) {
//		    	e.printStackTrace();
//		    }
//		}	  
//		canvasView.setDrawingCacheEnabled(false);
//	}
	
	/**
	 * 
	 */
	public static void getImageFromCamera(Activity act, String capturePath, int safrId) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));
            getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, safrId);
            act.startActivityForResult(getImageByCamera, 0);
        } else {
            Toast.makeText(act, "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }
	
	public static void getImageFromAlbum(Activity act, int safrId) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        act.startActivityForResult(intent, safrId);
    }


	public static String exce(String cmd, String workdirectory) {
		StringBuffer result = new StringBuffer(); 
	    try { 
	        // 创建操作系统进程（也可以由Runtime.exec()启动） 
	        // Runtime runtime = Runtime.getRuntime(); 
	        // Process proc = runtime.exec(cmd); 
	        // InputStream inputstream = proc.getInputStream(); 
	        ProcessBuilder builder = new ProcessBuilder(cmd); 
	        if (workdirectory == null) {
	        	workdirectory = SDPathUtils.getSdcardPath();
	        }
	 
	        InputStream in = null; 
	        // 设置一个路径（绝对路径了就不一定需要） 
	        if (workdirectory != null) {
	            // 设置工作目录（同上） 
	            builder.directory(new File(workdirectory)); 
	            // 合并标准错误和标准输出 
	            builder.redirectErrorStream(true); 
	            // 启动一个新进程 
	            Process process = builder.start(); 
	 
	            // 读取进程标准输出流 
	            in = process.getInputStream(); 
	            byte[] re = new byte[1024]; 
	            while (in.read(re) != -1) {
	                result = result.append(new String(re)); 
	            } 
	        }
	        // 关闭输入流 
	        if (in != null) { 
	            in.close(); 
	        } 
	    } catch (Exception ex) { 
	        ex.printStackTrace(); 
	    } 
	    return result.toString(); 
	}
	
	public static void execCommand(String command) throws IOException {
		 
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        try {
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
	
	public static void copyAssestFileToSdcard(Context myContext,
			String ASSETS_NAME, String filename) {
		// 如果目录不中存在，创建这个目录
		try {
			if (!(new File(filename)).exists()) {
				InputStream is = myContext.getResources().getAssets()
						.open(ASSETS_NAME);
				FileOutputStream fos = new FileOutputStream(filename);
				byte[] buffer = new byte[7168];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void copyAssestFile(String src_file, String dest_file) {
		try {
			InputStream is = new FileInputStream(src_file);
			FileOutputStream fos = new FileOutputStream(dest_file);
			byte[] buffer = new byte[7168];
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
