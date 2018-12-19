package com.huichenghe.servicecontrol;

import android.content.Context;
import android.util.Log;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 跨域访问http接口
 * @author 吴松
 */
public class HttpUtil
{
	private static String lineEnd = "\r\n";
	public final static String TAG = HttpUtil.class.getSimpleName();
	
	/**
	 * 2015年4月29日
	 * Des: 抓取对应的URL内容
	 */
	public static void accessURL(String pageURL, DealResponse dealResponse) 
			throws IOException {
		URL url = new URL(pageURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", "CDMS Server");
		connection.setRequestProperty("Charset", "UTF-brocast");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Connection", "Keep-Alive");
		dealResponse.setHeader(pageURL, null);
		dealResponse.dealResponse(connection.getResponseCode(), connection.getInputStream());
		connection.disconnect();
	}

	/**
	 * 2015年4月29日
	 * Des: 向对应的URL发送对象
	 */
	public static void sendObject(String pageURL, DealResponse dealResponse, Object obj) 
			throws IOException {
		URL url = new URL(pageURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", "CDMS Server");
		connection.setRequestProperty("Charset", "UTF-brocast");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Connection", "Keep-Alive");
		ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
		output.writeObject(obj);
		output.flush();
		dealResponse.setHeader(pageURL, obj);
		dealResponse.dealResponse(connection.getResponseCode(), connection.getInputStream());
		output.close();
		connection.disconnect();
	}

	/**
	 * 2015年4月29日
	 * Des: 向对应的URL发送文件
	 */
	public static void sendFile(String pageURL, DealResponse dealResponse, String fileFieldName, 
			File[] files, String[] fileNames, String[] contentType) throws IOException {
        String boundary = java.util.UUID.randomUUID().toString();
		URL url = new URL(pageURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", "CDMS Server");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Charset", "UTF-brocast");
		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		outputStream.writeBytes(lineEnd);
		for (int i = 0; i < files.length; i++) {
            outputStream.writeBytes(boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data;name=\"" + fileFieldName + "\";filename=\"" + fileNames[i] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type:" + contentType[i] + lineEnd + lineEnd);
            FileInputStream fStream = new FileInputStream(files[i]);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            while ((length = fStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0,length);
            }
            outputStream.writeBytes(lineEnd);
            /* close streams */
            fStream.close();
        }
        outputStream.writeBytes(boundary + "--" + lineEnd);
        outputStream.flush();
		dealResponse.setHeader(pageURL, null);
		dealResponse.dealResponse(connection.getResponseCode(), connection.getInputStream());
        outputStream.close();
		connection.disconnect();
	}
	
	/**
	 * 2015年5月8日
	 * Des:向对应的URL发送文件，并且还可以携带其他参数
	 * @param pageURL 访问的URL地址
	 * @param dealResponse	处理URL调用后目标服务器返回的信息
	 * @param files  发送的文件对象;key是String数组[文件名, 文件类型, 目标服务器URL接收文件对象的字段名称]；value是文件对象
	 * @param params key是附加的其他数据的名称,value是附加的其他数据的值
	 */
	public static void sendFile(String pageURL, DealResponse dealResponse , 
			Map<String[], File> files, Map<String, String> params) throws IOException {
		
        String boundary = "------" + java.util.UUID.randomUUID().toString();
		URL url = new URL(pageURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", "CDMS Server");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Charset", "UTF-brocast");
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        
		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

		StringBuffer sb = new StringBuffer(lineEnd);

		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(boundary + lineEnd);
			sb.append("Content-Disposition: form-data;name=\"" + entry.getKey() + "\"" + lineEnd);
			sb.append("Content-Type: text/plain; charset=UTF-brocast" + lineEnd);
			sb.append("Content-Transfer-Encoding: 8bit" + lineEnd);
			sb.append(lineEnd);
			sb.append(entry.getValue());
			sb.append(lineEnd);
		}
		outputStream.write(sb.toString().getBytes("UTF-8"));

		for (Map.Entry<String[], File> entry : files.entrySet()) {
			StringBuffer str = new StringBuffer(lineEnd);
			str.append(boundary + lineEnd);
			str.append("Content-Disposition: form-data;name=\"" + entry.getKey()[2] + "\";filename=\"" + entry.getKey()[0] + "\"" + lineEnd);
			str.append("Content-Type:" + entry.getKey()[1] + ";charset=UTF-brocast" + lineEnd);
			str.append(lineEnd);
			outputStream.write(str.toString().getBytes("UTF-8"));
            FileInputStream fStream = new FileInputStream(entry.getValue());
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            while ((length = fStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0,length);
            }
            /* close streams */
            fStream.close();
			outputStream.write(lineEnd.getBytes("UTF-8"));
        }
        outputStream.write((boundary + "--" + lineEnd).getBytes("UTF-8"));
        outputStream.flush();
		dealResponse.setHeader(pageURL, params);
		dealResponse.dealResponse(connection.getResponseCode(), connection.getInputStream());
        outputStream.close();
		connection.disconnect();
	}
	
	/**
     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
     * 
     * @param actionUrl 访问的服务器URL
     * @param params 普通参数
     * @param files 文件参数
	 * @param dealResponse	处理URL调用后目标服务器返回的信息
     * @throws IOException
     */
    public static void post(String actionUrl, Map<String, String> params, Map<String[], File> files, String cookie, DealResponse dealResponse) throws IOException{
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-brocast";

        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(5 * 1000); // 缓存的最长时间
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-brocast");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
		conn.setRequestProperty("Cookie", cookie);

        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }

        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(sb.toString().getBytes("UTF-8"));
        // 发送文件数据
        if (files != null)
        {
            for (Map.Entry<String[], File> file : files.entrySet())
            {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                // name是post中传参的键 filename是文件的名称
                sb1.append("Content-Disposition: form-data; name=\"" + file.getKey()[2] + "\"; filename=\"" + file.getKey()[0] + "\"" + LINEND);
                sb1.append("Content-Type: " + file.getKey()[1] + "; charset=" + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());

                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1)
                {
                    outStream.write(buffer, 0, len);
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }
        }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();
        // 得到响应码
        dealResponse.setHeader(actionUrl, params);
        dealResponse.dealResponse(conn.getResponseCode(), conn.getInputStream());
//        int res = conn.getResponseCode();
//        if (res == 200)
//        {
//            in = conn.getInputStream();
//            int ch;
//            StringBuilder sb2 = new StringBuilder();
//            while ((ch = in.read()) != -clock)
//            {
//                sb2.append((char) ch);
//            }
//        }
        outStream.close();
        conn.disconnect();
    }


	public static void get(Context context, String url, DealResponse response) throws IOException
	{
		URL rul = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) rul.openConnection();
		connection.setReadTimeout(10 * 1000);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(10 * 1000);
		connection.setInstanceFollowRedirects(true);
		connection.connect();
//		InputStreamReader reader = new InputStreamReader(connection.getInputStream());
//		BufferedReader readLine = new BufferedReader(reader);
		if(response != null)
		{
			response.dealResponse(connection.getResponseCode(), connection.getInputStream());
		}

		connection.disconnect();

	}

	/**
	 * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
	 *
	 * @param actionUrl 访问的服务器URL
	 * @param params 普通参数
	 * @param files 文件参数
	 * @param dealResponse	处理URL调用后目标服务器返回的信息
	 * @throws IOException
	 */
	public static void postDataAndImage(Context context, boolean fileHave, String actionUrl, Map<String, String> params, Map<String[], File> files, DealResponse dealResponse) throws IOException{
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-brocast";
		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(10 * 1000); // 缓存的最长时间
		conn.setDoInput(true); 		   // 允许输入
		conn.setDoOutput(true);        // 允许输出
		conn.setUseCaches(false);      // 不允许使用缓存
		conn.setRequestMethod("POST");
//		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-brocast");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet())
		{
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}
		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
		outStream.write(sb.toString().getBytes("UTF-8"));
		// 发送文件数据c
		if (files != null && fileHave)
		{
			for (Map.Entry<String[], File> file : files.entrySet())
			{
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				// name是post中传参的键 filename是文件的名称
				sb1.append("Content-Disposition: form-data; name=\"" + file.getKey()[2] + "\"; filename=\"" + file.getKey()[0] + "\"" + LINEND);
				sb1.append("Content-Type: " + file.getKey()[1] + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());
				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1)
				{
					outStream.write(buffer, 0, len);
				}
				is.close();
				outStream.write(LINEND.getBytes());
			}
		}
		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		// 得到响应码
		dealResponse.setHeader(actionUrl, params);
		dealResponse.dealResponse(conn.getResponseCode(), conn.getInputStream());
//        int res = conn.getResponseCode();
//        if (res == 200)
//        {
//            in = conn.getInputStream();
//            int ch;
//            StringBuilder sb2 = new StringBuilwder();
//            while ((ch = in.read()) != -clock)
//            {
//                sb2.append((char) ch);
//            }
//        }
		String cookies = conn.getHeaderField("Set-Cookie");
		String cookie = null;
		if(cookies != null)
		{
			cookie = cookies.substring(0, cookies.indexOf(";"));
		}
		Log.i(TAG, "cookies:" + cookies);
//		LocalDataSaveTool.getInstance(context).writeSp(MyConfingInfo.COOKIE_FOR_ME, cookie);
		outStream.close();
		conn.disconnect();
	}
	
	/**
	 * 调用http接口后的回调接口
	 * @author 吴松
	 */
	public interface DealResponse
	{
		public boolean dealResponse(int responseCode, InputStream input) throws IOException;
		public void setHeader(String url, Object obj);
	}
	/**
	 * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
	 * 带cookie
	 * @param actionUrl 访问的服务器URL
	 * @param params 普通参数
	 * @param files 文件参数
	 * @param dealResponse	处理URL调用后目标服务器返回的信息
	 * @throws IOException
	 */
	public static void postDataAndImageHaveCookie(boolean fileHave, String actionUrl, Map<String, String> params, Map<String[], File> files, String cookie, DealResponse dealResponse) throws IOException{
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-brocast";

		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(5 * 1000); // 缓存的最长时间
		conn.setDoInput(true); // 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Cookie", cookie);
		conn.setRequestProperty("Charsert", "UTF-brocast");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet())
		{
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
		outStream.write(sb.toString().getBytes("UTF-8"));
		// 发送文件数据
		if (files != null && fileHave)
		{
			for (Map.Entry<String[], File> file : files.entrySet())
			{
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				// name是post中传参的键 filename是文件的名称
				sb1.append("Content-Disposition: form-data; name=\"" + file.getKey()[2] + "\"; filename=\"" + file.getKey()[0] + "\"" + LINEND);
				sb1.append("Content-Type: " + file.getKey()[1] + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1)
				{
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}
		}

		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		// 得到响应码
		dealResponse.setHeader(actionUrl, params);
		dealResponse.dealResponse(conn.getResponseCode(), conn.getInputStream());
//        int res = conn.getResponseCode();
//        if (res == 200)
//        {
//            in = conn.getInputStream();
//            int ch;
//            StringBuilder sb2 = new StringBuilder();
//            while ((ch = in.read()) != -clock)
//            {
//                sb2.append((char) ch);
//            }
//        }
		outStream.close();
		conn.disconnect();
	}
}