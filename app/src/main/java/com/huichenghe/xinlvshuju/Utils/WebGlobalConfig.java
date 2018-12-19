package com.huichenghe.xinlvshuju.Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WebGlobalConfig {

	private static String TAG = "WebGlobalConfig";

	public static String SHOUHUAN_BUY_WEB = "http://bracelet.cositea.com:8089/bracelet/";

	public static String WebRoot = "http://bracelet.cositea.com:8089/bracelet/";
//	public static String WebRoot = "http://192.168.6.107:8089/";

	public static String WebRoot_Register = WebRoot + "user_register";
	public static String WebRoot_Login = WebRoot + "user_login";
	public static String WebRoot_FindPwd = WebRoot + "user_findPwd";
	public static String WebRoot_UpdateInfor = WebRoot + "user_updateInfor";
	public static String WebRoot_UpdatePwd = WebRoot + "user_updatePwd";
	public static String WebRoot_LoginOut = WebRoot + "user_loginOut";
	
	public static String WebRoot_getHeader = WebRoot + "download_userHeader";
		
	public static String WebRoot_TiredData = WebRoot + "uploadData_index";
	
	public static String WebRoot_myAttention = WebRoot + "attention_myAttention";
	public static String WebRoot_addAttention = WebRoot + "attention_addAttention";
	public static String WebRoot_getHealth = WebRoot + "attention_friendHealth";
	
	
	public static String WebRoot_androidVersion = WebRoot + "androidVersion";
	
	public static String WebRoot_uploadDataTrend = WebRoot + "uploadData_trend";
	

	// 9001：表示用户没有登录或者登录超时
	// 9002：表示参数传递错误
	// 9003：表示调用成功
	// 9004：表示调用出错

	public static int WEB_ERROR_LOGIN = 9001;
	public static int WEB_ERROR_PARAMETER = 9002;
	public static int WEB_OPT_SUCESS = 9003;
	public static int WEB_OPT_FAILED = 9004;

	public static String get32MD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString().toUpperCase();
	}
}