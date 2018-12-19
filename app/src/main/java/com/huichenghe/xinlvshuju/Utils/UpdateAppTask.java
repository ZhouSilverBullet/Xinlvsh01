package com.huichenghe.xinlvshuju.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.huichenghe.xinlvshuju.DataEntites.ParamEntity;
import com.huichenghe.xinlvshuju.DownloadService;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.slide.AboutActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 要在程序启动检查版本更新
 * Created by DIY on 2015/12/23.
 */
public class UpdateAppTask extends AsyncTask<Void, Void, Boolean> {
    private Context mContext;
    private Sub_update_app sub;
    public static final String TAG = UpdateAppTask.class.getSimpleName();

    public UpdateAppTask(Context mContext, Sub_update_app sub)
    {
        this.mContext = mContext;
        this.sub = sub;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        sub = new Sub_update_app(mContext);
        boolean isSuccess = false;
        try {

            isSuccess = sub.requestCheckUpdate();

        } catch (IOException e) {
            e.printStackTrace();

        }

        return isSuccess;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(ProgressUtils.getInstance().isShowing())
        ProgressUtils.getInstance().closeProgressDialog();
        if(onDataBack != null)
        onDataBack.onBack();
        if (aBoolean) {
            //请求判断当前版本和最新版本是否相同
            final ParamEntity mParamEntity = sub.getParamEntity();
            if (!isLatestVersion(mParamEntity.getVersion())) {
                //不是最新版本
                // showDialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle(mContext.getString(R.string.new_version_release));
                dialog.setMessage(mContext.getString(R.string.new_version) + mParamEntity.getVersion() + "\r\n"
                        + getDescription(mParamEntity.getDescription()));

                dialog.setPositiveButton(mContext.getString(R.string.update_now),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                /*String path=Environment.getExternalStorageDirectory().getAbsolutePath() + "/xinlvshouhuan.apk";
                                File file=new File(path);
                                if(file.length()==mParamEntity.getFileSize()){
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setAction(android.content.Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(file),
                                            "application/vnd.android.package-archive");
                                    mContext.startActivity(intent);
                                    return;
                                }*/
                                Log.i("tag", "立刻升级");
                                //进行下载操作并且关闭dialog
                                Intent mIntent = new Intent(mContext, DownloadService.class);
                                mIntent.putExtra("url", mParamEntity.getUrl());
                                mIntent.putExtra("size", mParamEntity.getFileSize());
                                mContext.startService(mIntent);
                            }
                        });

                dialog.setNegativeButton(mContext.getString(R.string.update_later),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //判定当前版本是否兼容，是则直接退出dialog，否则提示用户必须升级
                                if (!isLatestVersion(mParamEntity.getMinVersion())) {

                                    MyToastUitls.showToast(mContext, R.string.is_not_compatible_version, 1);

                                    if (mContext instanceof AboutActivity) {
                                        ((AboutActivity) mContext).finish();
//                                        MainActivity.instance.finish();
                                    } else {
//                                        MainActivity.instance.finish();
                                    }


                                } else {
                                    //是兼容版本，直接退出对话框
                                    dialog.cancel();
                                }

                            }
                        });
                AlertDialog alertDialog = dialog.create();
                alertDialog.setCancelable(false);
                alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        //默认返回 false
                        return keyCode == KeyEvent.KEYCODE_SEARCH;

                    }
                });
                alertDialog.show();
            } else {
                //已经是最新版本
                if (mContext instanceof AboutActivity) {
                    MyToastUitls.showToast(mContext, R.string.is_latest_version, 1);
                }

            }

        } else {
            //请求失败
            if(mContext instanceof AboutActivity) {
                if(ProgressUtils.getInstance().isShowing())
                ProgressUtils.getInstance().closeProgressDialog();
                Toast.makeText(mContext, R.string.net_wrong, Toast.LENGTH_LONG).show();
                Log.i("tag", "请求失败");
            }else{
                //不提醒网络连接问题
            }
        }
    }

    //规范字符方法
    private String getDescription(String onlineString) {
        if(onlineString.contains("~")){
            onlineString.replaceAll("~","");
        }

        Log.i("tag", onlineString);
        char[] c = onlineString.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }

        String a = String.valueOf(c);
        Pattern pattern = Pattern.compile("\\d[、|,]");

        Matcher matcher = pattern.matcher(a);

        String description = "";
        ArrayList<Integer> list=new ArrayList<Integer>();
        if(matcher.find()) {
            do {
                int countD = matcher.start();
//                Log.i("TAG", "规范后的格式:" + matcher.start());
                list.add(countD);
            }while ((matcher.find()));
            for (int i = 0; i < list.size() - 1; i++) {
                description += a.substring(list.get(i), list.get(i + 1)) + "\n";
            }
            description += a.substring(list.get(list.size() - 1));

            return description;
        }else {
            description=a;
            return description;
        }

    }

    /**
     * 是否是最新版本
     *
     * @param latestVersion 最新版本号
     * @return boolean
     */
    private boolean isLatestVersion(String latestVersion) {
        String currentVersion = getAppVersion();

        assert currentVersion != null;
        if (currentVersion.equals(latestVersion)) {
            return true;
        }
        String[] localArray = currentVersion.split("\\.");
        String[] onlineArray = latestVersion.split("\\.");

        int length = localArray.length < onlineArray.length ? localArray.length : onlineArray.length;

        for (int i = 0; i < length; i++) {
            if (Integer.parseInt(onlineArray[i]) > Integer.parseInt(localArray[i])) {
                return false;
            } else if (Integer.parseInt(onlineArray[i]) < Integer.parseInt(localArray[i])) {
                return true;
            }
            // 相等 比较下一组值
        }
        return false;
    }

    /**
     * 获取app软件版本
     *
     * @return
     */
    private String getAppVersion() {
        PackageInfo mPackageInfo;
        try {
            mPackageInfo = mContext
                    .getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0);
            return mPackageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public interface OnDataBack
    {
        void onBack();
    }
    private OnDataBack onDataBack;

    public void setOnDataBack(OnDataBack back)
    {
        this.onDataBack = back;
    }




}
