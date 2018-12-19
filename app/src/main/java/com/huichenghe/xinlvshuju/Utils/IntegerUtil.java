package com.huichenghe.xinlvshuju.Utils;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

/**
 * byte[]数组和byte[]数组合并
 * 
 * @author 
 * 
 */

@SuppressLint("DefaultLocale")
public class IntegerUtil {


	/**
	 * 方法二：
	 */
	/**
	 * int to byte
	 * 
	 * @param i
	 * @return
	 */
	private static byte[] intToByte(int i) {
		byte[] bt = new byte[4];
		bt[0] = (byte) (0xff & i);
		bt[1] = (byte) ((0xff00 & i) >> 8);
		bt[2] = (byte) ((0xff0000 & i) >> 16);
		bt[3] = (byte) ((0xff000000 & i) >> 24);
		return bt;
	}

	/**
	 * byte to int
	 * 
	 * @param bytes
	 * @return
	 */
	public static int bytesToInt(byte[] bytes) {
		int num = bytes[0] & 0xFF;
		num |= ((bytes[1] << 8) & 0xFF00);
		num |= ((bytes[2] << 16) & 0xFF0000);
		num |= ((bytes[3] << 24) & 0xFF000000);
		return num;
	}
	/**
	 * byte to int
	 *
	 * @param bytes
	 * @return
	 */
	public static short bytesToShort(byte[] bytes) {
        short num = (short) (bytes[1] & 0xFF);
        num |= ((bytes[0] << 8) & 0xFF00);
        return num;
	}
	/**
	 *小端格式 
	 * @param bytes
	 * @return
	 */
	public static int bytesToLittInt(byte[] bytes) {
		int num = bytes[0] & 0xFF;
		num |= ((bytes[1] << 8) & 0xFF00);
		return num;
	}
	
	/**
	 * 小端格式，两个字节
	 * 
	 * @param i
	 * @return phone byte
	 */
	public static byte[] intTo2LittBytes(int i) {
		byte[] bt = new byte[2];
		bt[0] = (byte) (0xff & i);
        bt[1] = (byte) ((0xff00 & i) >> 8);
        return bt;
	}

	
	/**
	 * 大端格式 bytes phone int
	 * 
	 * @param bytes
	 * @return
	 */
	public static int bytesToBIGInt(byte[] bytes) {
		int num = bytes[1] & 0xFF;
		num |= ((bytes[0] << 8) & 0xFF00);
		return num;
	}

	
	/**
	 * 大端格式，两个字节
	 * 
	 * @param i
	 * @return phone byte
	 */
	public static byte[] intTo2Bytes_BIG(int i) {
		byte[] bt = new byte[2];
		bt[1] = (byte) (0xff & i);
		bt[0] = (byte) ((0xff00 & i) >> 8);
		return bt;
	}
	
	/**
	 * sitting 字节 大端格式 bytes phone int
	 * 
	 * @param bytes
	 * @return
	 */
	public static int bytesTo4BIGInt(byte[] bytes) {
		int num = bytes[3] & 0xFF;
		num |= ((bytes[2] << 8) & 0xFF00);
		num |= ((bytes[1] << 16) & 0xFF0000);
		num |= ((bytes[0] << 32) & 0xFF000000);
		
		return num;
	}
	
	
	/**
	 * 大端格式，四个字节
	 * 
	 * @param i
	 * @return phone byte
	 */
	public static byte[] intTo4Bytes_BIG(int i) {
		byte[] bt = new byte[4];
		bt[3] = (byte) (0xff & i);
		bt[2] = (byte) ((0xff00 & i) >> 8);
		bt[1] = (byte) ((0xff0000 & i) >> 16);
		bt[0] = (byte) ((0xff000000 & i) >> 32);
		return bt;
	}
	
	/**
	 * 大端格式，两个字节
	 * 
	 * @param i
	 * @return phone byte
	 */
	public static byte[] intTo2Bytes(int i) {
		byte[] bt = new byte[2];
		bt[0] = (byte) (0xff & i);
		bt[1] = (byte) ((0xff00 & i) >> 8);
		return bt;
	}

	/**
	 * 十进制 转 二进制 并 存入byte数组里
	 * 
	 * @param num
	 * @return
	 */
	public static byte[] int2bytes(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}

	/**
	 * 二进制 转 十进制
	 * 
	 * @param b
	 * @return
	 */
	public static int bytes2int(byte[] b) {
		// byte[] b=new byte[]{clock,phone,info,sitting};
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 4; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}

    /**
     * 把16进制字符串数组转换成字节数组
     *
     * @param hexStr
     *            例如：String[] hexStr = {"A0","8B","D1","AA","00","03"};
     * @return 转化后的字节数组
     */
    public synchronized static byte[] hexStrArrayToByte(String[] hexStr) {


        int length = hexStr.length;
        byte[] b = new byte[length];

        for (int i = 0; i < length; i++) {
            if(length==0)
                return null;
            b[i] = hexStr2ByteArrayBig(hexStr[i])[0];
        }

        return b;
    }

	public static int byteToInt(byte[] b) {

		int s = 0;

		int s0 = b[0] & 0xff;// 最低位

		int s1 = b[1] & 0xff;

		int s2 = b[2] & 0xff;

		int s3 = b[3] & 0xff;

		s3 <<= 24;

		s2 <<= 16;

		s1 <<= 8;

		s = s0 | s1 | s2 | s3;

		return s;

	}



	/**
	 * 把字节数组转换成16进制字符串
	 * 
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp);
		}
		return sb.toString();
	}



	/**
	 * 整形
	 * @param num
	 * @return
	 */
	public static String int2HexStr(int num){
		StringBuilder intHex=new StringBuilder();
		if(num<0x10||(num>0xFF&&num<0x1000))
			intHex=intHex.append("0").append(Integer.toHexString(num));
		else if(num>0x0F)
			intHex=intHex.append(Integer.toHexString(num));
		else
			return "00";
		
		return intHex.toString();
		
	}

    /**
     * 16进制的字符串表示转成字节数组 高位在前，低位在后
     *
     * @param hexString
     *			16进制格式的字符串
     * @return 转换后的字节数组
     **/
    private static byte[] hexStr2ByteArrayBig(String hexString) {
        if (hexString==null)
            throw new IllegalArgumentException("this hexString must not be empty");

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            //因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            //将hex 转换成byte   "&" 操作为了防止负数的自动扩展
            // hex转换成byte 其实只占用了4位，然后把高位进行右移四位
            // 然后“|”操作  低四位 就能得到 两个 16进制数转换成一个byte.
            //
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    /**
     * 16进制的字符串表示转成字节数组 低位在前，高位在后
     *
     * @param hexString
     *			16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] hexStr2ByteArrayLow(String hexString) {
        if (hexString==null)
            throw new IllegalArgumentException("this hexString must not be empty");

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = (byteArray.length-1); i>-1; i--) {
            //因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            //将hex 转换成byte   "&" 操作为了防止负数的自动扩展
            // hex转换成byte 其实只占用了4位，然后把高位进行右移四位
            // 然后“|”操作  低四位 就能得到 两个 16进制数转换成一个byte.
            //
            byte high = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    /**
     * 16进制字符串转换成byte数组
     * @return 转换后的byte数组
     */
    public static byte[] hex2Byte(String hex) {
        String digital = "0123456789ABCDEF";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            // 其实和上面的函数是一样的 multiple 16 就是右移4位 这样就成了高4位了
            // 然后和低四位相加， 相当于 位操作"|"
            //相加后的数字 进行 位 "&" 操作 防止负数的自动扩展. {0xff byte最大表示数}
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }
        return bytes;
    }


   public static byte getCS(byte[] data){
       int checkSun=0;
       for(int i=0;i<data.length-2;i++){
           checkSun+=(data[i]&0xFF);
       }
       return (byte) (checkSun&0xff);
   }

    public static byte getCheckSun(byte[] data){
        int checkSun=0;// 32位

        for(byte d:data){// 8位
            checkSun=  ((checkSun^d)&0xFF);
        }

        return intToByte(checkSun)[0];
    }

    static public void main(String[] args){
//        final byte[] oes = hexStr2ByteArray("680e06000e0605173a00e616");
//        System.out.println(oes.length);
//        System.out.println(oes[0]+","+oes[clock]+","+oes[phone]+","+oes[info]+","+oes[sitting]);
//        final byte[] o = hexStr2ByteArray("0e");
//        System.out.println(o.length);
//        System.out.println(o[0]);
//        System.out.println(intTo2Bytes(126)[0]);
//        System.out.println(intTo2Bytes(255)[0]);
//        System.out.println(intTo2Bytes(256)[0]);

        int[] test=new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};

        List<Integer> foo=new ArrayList<Integer>();

        for (int i = 0; i < 16; i++) {
            foo.add(i);
        }

        for (int i = 0; i < 16; i++) {
            System.out.println(foo.remove(i));
        }


    }
}
