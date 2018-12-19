package com.huichenghe.xinlvshuju.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

public class  NetStatus {

    public static int NET_CNNT_BAIDU_OK = 1; // 正常访问因特网状态
    public static int NET_CNNT_BAIDU_TIMEOUT = 2; // 无法访问因特网状态
    public static int NET_NOT_PREPARE = 3; // 网络未准备好
    public static int NET_ERROR = 4;
    public static int NET_TYPE_2G = 5;
    public static int NET_TYPE_3G = 6;
    public static int NET_TYPE_WIFI = 7;
    private static int TIMEOUT = 3000;

    /**
     * 返回当前网络状态
     *
     * @param context
     * @return
     */
    public static int getNetState(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
                if (networkinfo != null) {
                    if (networkinfo.isAvailable() && networkinfo.isConnected()) {
                        if (!connectionNetwork())
                            return NET_CNNT_BAIDU_TIMEOUT;
                        else
                            return NET_CNNT_BAIDU_OK;
                    } else {
                        return NET_NOT_PREPARE;
                    }
                }
            }
        } catch (Exception e) {
        }
        return NET_ERROR;
    }

    /**
     * 拼百度地址
     *
     * @return
     */
    static private boolean connectionNetwork() {
        boolean result = false;
        HttpURLConnection httpUrl = null;
        try {
            httpUrl = (HttpURLConnection) new URL("http://www.baidu.com")
                    .openConnection();
            httpUrl.setConnectTimeout(TIMEOUT);
            httpUrl.connect();
            result = true;
        } catch (IOException e) {
        } finally {
            if (null != httpUrl) {
                httpUrl.disconnect();
            }
            httpUrl = null;
        }
        return result;
    }

    /**
     * 判断当前网络是否是3G网络
     *
     * @param context
     * @return boolean
     */
    public static boolean is3G(Context context) {
    	try {
			
    		ConnectivityManager connectivityManager = (ConnectivityManager) context
    				.getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
    		if (activeNetInfo != null
    				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
    			return true;
    		}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
        return false;
    }

    /**
     * 判断当前网络是否是wifi网络
     *
     * @param context
     * @return boolean
     */
    public static boolean isWifi(Context context) {
    	try {
    		ConnectivityManager connectivityManager = (ConnectivityManager) context
    				.getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
    		if (activeNetInfo != null
    				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
    			return true;
    		}
			
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
        return false;
    }


    /**
     * 判断网络状态
     */
    public static boolean isNetWorkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if(info==null){
            return false;
        }else {
            WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifimanager.isWifiEnabled();
            wifimanager.getWifiState();
            boolean q = info.isConnected();
            boolean bh = info.isAvailable();
            return (info != null && info.isConnected() && info.isAvailable());
        }
    }


    /**
     * 判断当前网络是否是2G网络
     *
     * @param context
     * @return boolean
     */
    public static boolean is2G(Context context) {
    	try {
			
    		ConnectivityManager connectivityManager = (ConnectivityManager) context
    				.getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
    		if (activeNetInfo != null
    				&& (activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE
    				|| activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS || activeNetInfo
    				.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA)) {
    			return true;
    		}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
    	return false;
    }

    /**
     * wifi是否打开
     */
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 获得本机ip地址
     *
     * @return
     */
    public static String GetHostIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
        } catch (Exception e) {
        }
        return null;
    }


    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * 获取网络连接类型
     *
     * @param context
     * @return
     */
    public static int getNetType(Context context) {
        if (is2G(context))
            return NET_TYPE_2G;
        if (is3G(context))
            return NET_TYPE_3G;
        if (isWifi(context))
            return NET_TYPE_WIFI;
        else
            return NET_ERROR;
    }


    static OnKeyListener keylistener = new OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    };
}