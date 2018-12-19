package com.huichenghe.bleControl.Utils;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class FormatUtils {
    
    public static String dateFormat() {
        Date now = new Date();
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String str = temp.format(now);
        return str;
    }
    
    /**
     * @param date
     * @return
     */
    public static String dateFormat(Date date) {
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());//kk 12小时制
        String str = temp.format(date);
        return str;
    }
    
    /**
     * @param date
     * @return
     */
    public static String dateFormat24(Date date) {
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.getDefault());//kk 24小时制
        String str = temp.format(date);
        return str;
    }
    
    /**
     * @param date
     * @return
     */
    public static String dateFormat24Ms(Date date) {
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss:SSS", Locale.getDefault());//kk 24小时制
        return temp.format(date);
    }
    
    public static String decimalFormat(float f){
        DecimalFormat fnum = new DecimalFormat("##0.00");  //保留小数点后两位
        return fnum.format(f);
    }
    
    
    
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i=begin; i<begin+count; i++) 
        	bs[i-begin] = src[i];
        return bs;
    }
    
    
  /** 
   * @param a 
   * @return 
   */  
  public static byte[] short2Byte(short a){  
      byte[] b = new byte[2];  
        
      b[0] = (byte) (a >> 8);  
      b[1] = (byte) (a);  
        
      return b;  
  }  
  
  public static byte[] reverseShort2Byte(short a){
      byte[] b = new byte[2];  
      
      b[1] = (byte) (a >> 8);  
      b[0] = (byte) (a);  
      
      return b;  
      
  }
    
  public static short reverseByte2Short(byte[] b){  
      return (short) (((b[1] & 0xff) << 8) | (b[0] & 0xff));  
  }  
  
  /** 
   * @param a 
   * @param b 
   */  
  public static void short2Byte(short a, byte[] b, int offset){  
      b[offset] = (byte) (a >> 8);  
      b[offset+1] = (byte) (a);  
  }  
    
  /** 
   * @param b 
   * @return 
   */  
  public static short byte2Short(byte[] b){  
      return (short) (((b[0] & 0xff)) | ((b[1] & 0xff) << 8));  
  }  
    
  /** 
   * @param b 
   * @param offset 
   * @return  
   */  
  public static short byte2Short(byte[] b, int offset){  
      return (short) (((b[offset] & 0xff)) | ((b[offset+1] & 0xff) << 8));  
  }  

  /** 
   *  
   * @param a 
   * @param b 
   * @param offset 
   */  
  public static void long2Byte(long a, byte[] b, int offset) {          
      b[offset + 0] = (byte) (a >> 56);  
      b[offset + 1] = (byte) (a >> 48);  
      b[offset + 2] = (byte) (a >> 40);  
      b[offset + 3] = (byte) (a >> 32);  

      b[offset + 4] = (byte) (a >> 24);  
      b[offset + 5] = (byte) (a >> 16);  
      b[offset + 6] = (byte) (a >> 8);  
      b[offset + 7] = (byte) (a);  
  }  

  /** 
   *  
   * @param b 
   * @param offset 
   * @return 
   */  
  public static long byte2Long(byte[] b, int offset) {  
       return ((((long) b[offset + 0] & 0xff) << 56)  
       | (((long) b[offset + 1] & 0xff) << 48)  
       | (((long) b[offset + 2] & 0xff) << 40)  
       | (((long) b[offset + 3] & 0xff) << 32)  
         
       | (((long) b[offset + 4] & 0xff) << 24)  
       | (((long) b[offset + 5] & 0xff) << 16)  
       | (((long) b[offset + 6] & 0xff) << 8)  
       | (((long) b[offset + 7] & 0xff)));
  }  

  /** 
   *  
   * @param b 
   * @return 
   */  
  public static long byte2Long(byte[] b) {  
       return  
       ((b[0]&0xff)<<56)|
       ((b[1]&0xff)<<48)|  
       ((b[2]&0xff)<<40)|
       ((b[3]&0xff)<<32)|  
        
       ((b[4]&0xff)<<24)|  
       ((b[5]&0xff)<<16)|  
       ((b[6]&0xff)<<8)|  
       (b[7]&0xff);  
  }  

  /** 
   *  
   * @param a 
   * @return 
   */  
  public static byte[] long2Byte(long a) {  
      byte[] b = new byte[4 * 2];  

      b[0] = (byte) (a >> 56);  
      b[1] = (byte) (a >> 48);  
      b[2] = (byte) (a >> 40);  
      b[3] = (byte) (a >> 32);  
        
      b[4] = (byte) (a >> 24);  
      b[5] = (byte) (a >> 16);  
      b[6] = (byte) (a >> 8);  
      b[7] = (byte) (a);

      return b;  
  }  

  /** 
   *  
   * @param b 
   * @return 
   */  
//  public static int byte2Int(byte[] b) {  
//      return ((b[0] & 0xff) << 24) | ((b[clock] & 0xff) << 16)
//              | ((b[phone] & 0xff) << brocast) | (b[info] & 0xff);
//  }  

  /** 
   * 将四个byte数据转化为int
   * @param b 				byte数组
   * @param offset 			起始下标
   * @return 					int
   */  
//  public static int byte2Int(byte[] b, int offset) {  
//      return ((b[offset++] & 0xff) << 24) | ((b[offset++] & 0xff) << 16)  
//              | ((b[offset++] & 0xff) << brocast) | (b[offset++] & 0xff);
//  }  
  	public static int byte2Int(byte[] b, int offset)
    {
  		return ((b[offset++] & 0xff)) | ((b[offset++] & 0xff) << 8)  
            | ((b[offset++] & 0xff) << 16) | ((b[offset++] & 0xff) << 24);  
  	}  


  /** 
   * @param a
   * @return 
   */  
  public static byte[] int2Byte_HL(int a) {  
      byte[] b = new byte[4];  
      b[0] = (byte) (a >> 24);  
      b[1] = (byte) (a >> 16);  
      b[2] = (byte) (a >> 8);  
      b[3] = (byte) (a);  
      return b;
  }  
  public static byte[] int2Byte_HL_(int a) {
      byte[] b = new byte[4];
      b[0] = (byte) (a);
      b[1] = (byte) (a >> 8);
      b[2] = (byte) (a >> 16);
      b[3] = (byte) (a >> 24);
      return b;
  }
  public static byte[] int2Byte_LH(int a) {
      byte[] b = new byte[4];  
      b[3] = (byte) (a >> 24);  
      b[2] = (byte) (a >> 16);  
      b[1] = (byte) (a >> 8);  
      b[0] = (byte) (a);  

      return b;  
  }  

  /** 
   *  
   * @param a 
   * @param b 
   * @param offset 
   * @return 
   */  
  public static void int2Byte(int a, byte[] b, int offset) {        
      b[offset++] = (byte) (a >> 24);  
      b[offset++] = (byte) (a >> 16);  
      b[offset++] = (byte) (a >> 8);  
      b[offset++] = (byte) (a);  
  }
  
  private static final String BLUETOOTH_BASE_UUID_PREFIX = "0000";
  private static final String BLUETOOTH_BASE_UUID_POSTFIX = "-0000-1000-8000-00805F9B34FB";
//Converts UUID from 16-bit to 128-bit form
  public static String convert16to128UUID(String uuid) {
      return BLUETOOTH_BASE_UUID_PREFIX + uuid + BLUETOOTH_BASE_UUID_POSTFIX;
  }
  
  public static String bytesToHexString(byte[] src){  
      StringBuilder stringBuilder = new StringBuilder("");  
      if (src == null || src.length <= 0) {  
          return null;  
      }  
      for (int i = 0; i < src.length; i++) {  
          int v = src[i] & 0xFF;  
          String hv = Integer.toHexString(v);  
          if (hv.length() < 2) {  
              stringBuilder.append(0);  
          }  
          stringBuilder.append(hv);  
      }  
      return stringBuilder.toString();  
  }
  
  public static String bytesToHexString(byte[] src, int offset, int len){  
      StringBuilder stringBuilder = new StringBuilder("");  
      if (src == null || src.length <= len) {  
          return null;  
      }  
      for (int i = offset; i < len; i++) {  
          int v = src[i] & 0xFF;  
          String hv = Integer.toHexString(v);  
          if (hv.length() < 2) {  
              stringBuilder.append(0);  
          }  
          stringBuilder.append(hv);  
      }  
      return stringBuilder.toString();  
  } 
  
  public static String byteToHexString(byte src){  
      StringBuilder stringBuilder = new StringBuilder("0x");  
      int v = src & 0xFF;  
      String hv = Integer.toHexString(v);  
      if (hv.length() < 2) {  
          stringBuilder.append(0);  
      }  
      stringBuilder.append(hv);  
      return stringBuilder.toString();  
  }

    public static String byteToHexStringNo0X(byte src){
        StringBuilder stringBuilder = new StringBuilder();
        int v = src & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString();
    }
  
//  public static byte[] hexStringToByte(String hex) {   
//	    int len = (hex.length() / phone);
//	    byte[] result = new byte[len];   
//	    char[] achar = hex.toCharArray();   
//	    for (int i = 0; i < len; i++) {   
//	     int pos = i * phone;
//	     result[i] = (byte) (toByte(achar[pos]) << sitting | toByte(achar[pos + clock]));
//	     Log.e("@@@i:" + i + " result:" + result[i]);
//	    }   
//	    return result;   
//	} 
  
  	private static byte toByte(char c) {   
	    byte b = (byte) "0123456789ABCDEF".indexOf(c);   
	    return b;   
	}
  
  
  /**
   * Convert a signed byte to an unsigned int.
   */
  private static int unsignedByteToInt(byte b) {
      return b & 0xFF;
  }

  /**
   * Convert signed bytes to a 16-bit unsigned int.
   */
  public static int unsignedBytesToInt(byte b0, byte b1) {
      return (unsignedByteToInt(b0) + (unsignedByteToInt(b1) << 8));
  }
  
  public static boolean isHex (String aNumber) {
		
		String regString = "[a-f0-9A-F]{"+aNumber.length()+"}";
      return Pattern.matches(regString, aNumber);
  }
  
  public static byte[] hexString2ByteArray(String hexString){
	  int length =hexString.length();
	  byte[] b = new byte[length/2];
	  for (int i = 0, j = 0; i < length; i+=2,j++) {
		String str = hexString.substring(i, i+2);
//		b[j] = Byte.parseByte(str, 16);
		b[j] = (byte) Short.parseShort(str, 16);
//		Log.i("@@@" +str + " byte:" + b[j]);
	  }
	  return b;
  }

  public static byte[] hexStringToByteArray(String s) {
      int len = s.length();
      byte[] data = new byte[len / 2];
      for (int i = 0; i < len; i += 2) {
          data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
      }
      return data;
  }
  
  public static byte[] hexStringToByteArrays(String s) {
	  return new BigInteger(s, 16).toByteArray();
  }
  
  public static String addPre0ForHexString(String hexString, int targetLength){
	  if(hexString.length() < targetLength){
		  int s = targetLength - hexString.length();
		  StringBuilder sb = new StringBuilder();
		  for (int i = 0; i < s; i++) {
			sb.append('0');
		  }
		  sb.append(hexString);
		  return sb.toString();
	  }else{
		  return hexString;
	  }
  }
  
  public static byte[] optimizeByteArray(byte[] value){
      int start = 0;
      for (int i = 0; i < value.length; i++) {
        byte b = value[i];
        if(b != 0){
            start = i;
            break;
        }
      }
      return Arrays.copyOfRange(value, start, value.length);
  }
  
  public static boolean isZero(byte[] value){
      int start = 0;
      for (int i = 0; i < value.length; i++) {
          byte b = value[i];
          if(b != 0){
              start = i;
              return false;
          }
      }
      return true;
  }
  
  public static byte mergeByte(byte high, byte low){
//	  Log.i("high:" + byteToHexString(high));
//	  Log.i("low:" + byteToHexString(low));
	  return (byte)((high << 4) + (low&0x0f));
  }
  
  public static byte[] mergeByteArray(byte[] first, byte[] end){
      byte[] mm = new byte[first.length + end.length];
      for (int i = 0; i < mm.length; i++) {
          if(i < first.length){
              mm[i] = first[i];
          }else{
              mm[i] = end[i - first.length];
              
          }
      }
      return mm;
  }
  
//  public static byte[] subByteArray(byte[] src, int start, int end){
//      byte[] mm = new byte[end - start];
//      for (int i = start, j=0; j < mm.length; i++, j++) {
////          Log.e(String.format("src length[%d] mm length[%d] start[%d] end[%d] i[%d] j[%d]", src.length, mm.length, start, end, i, j));
//          mm[j] = src[i];
//      }
//      return mm;
//  }

  public static boolean byteArrayStartWithByteArray(byte[] src, byte[] desc){
	  if(src == null || desc == null){
		  return false;
	  }
	  if(src.length < desc.length){
		  return false;
	  }
	  
	  for (int i = 0; i < desc.length; i++) {
		if(desc[i] != src[i]){
			return false;
		}
	  }
	  return true;
  }
  
  public static byte getLowbit(byte b){
	  return (byte) (b & 0x0f);
  }
  
  public static byte getHightbit(byte b){
	  return (byte) ((b & 0xf0) >> 4);
  }
  
}
