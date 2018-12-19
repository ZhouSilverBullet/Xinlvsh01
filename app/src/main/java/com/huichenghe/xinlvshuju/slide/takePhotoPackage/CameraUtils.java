package com.huichenghe.xinlvshuju.slide.takePhotoPackage;

import android.hardware.Camera;
import android.util.DisplayMetrics;

import java.util.List;

/**
 * Created by why8222 on 2016/2/25.
 */
public class CameraUtils {
    public static final String TAG = CameraUtils.class.getSimpleName();

    public static Camera.Size getLargePictureSize(Camera camera){
        if(camera != null){
            List<Camera.Size> sizes = camera.getParameters().getSupportedPictureSizes();
            Camera.Size temp = sizes.get(0);
            for(int i = 1;i < sizes.size();i ++){
                float scale = (float)(sizes.get(i).height) / sizes.get(i).width;
                if(temp.width < sizes.get(i).width && scale < 0.6f && scale > 0.5f)
                    temp = sizes.get(i);
            }
            return temp;
        }
        return null;
    }

    public static Camera.Size getLargePreviewSize(Camera camera, DisplayMetrics metrics)
    {
        if(camera != null)
        {
            List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
            Camera.Size temp = sizes.get(0);
            for(int i = 1;i < sizes.size();i ++)
            {
                int supportWidth = sizes.get(i).width;
                int supportHeight = sizes.get(i).height;
//                if(temp.width < sizes.get(i).width)
//                    temp = sizes.get(i);
                if(metrics.widthPixels == supportHeight && metrics.heightPixels == supportWidth)
                {
                    temp = sizes.get(i);
                }
            }
            return temp;
        }
        return null;
    }

    public static  Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, DisplayMetrics metrics) {
        int w = metrics.widthPixels;
        int h = metrics.heightPixels;
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }



}
