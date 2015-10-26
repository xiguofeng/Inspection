package com.xgf.inspection.network.logic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.util.Util;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.xgf.inspection.network.config.MsgResult;
import com.xgf.inspection.network.config.RequestUrl;
import com.xgf.inspection.utils.NetUtils;

public class AppLogic {

	public static final int NET_ERROR = 0;

	public static final int SEND_RECORD_SUC = NET_ERROR + 1;

	public static final int SEND_RECORD_FAIL = SEND_RECORD_SUC + 1;

	public static final int SEND_RECORD_EXCEPTION = SEND_RECORD_FAIL + 1;

	public static final int SEARCH_RECORD_SUC = SEND_RECORD_EXCEPTION + 1;

	public static final int SEARCH_RECORD_FAIL = SEARCH_RECORD_SUC + 1;

	public static final int SEARCH_RECORD_EXCEPTION = SEARCH_RECORD_FAIL + 1;

	public static void SendWirePoleCheckRecord(final Context context,
			final Handler handler, final String UserPhoneCode,
			final String QRcode, final String SerialNumber,
			final String FileSN, final String FileContent) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				if (NetUtils.checkNetworkConnection(context)) {

					try {
						SoapObject rpc = new SoapObject(RequestUrl.NAMESPACE,
								RequestUrl.record.SendWirePoleCheckRecord);

						rpc.addProperty("UserPhoneCode", "1");
						rpc.addProperty("QRcode", "2");
						rpc.addProperty("SerialNumber", "3");
						rpc.addProperty("FileSN", "4");
						rpc.addProperty("FileContent", "5");

						Log.e("xxx_SendWirePoleCheckRecord_requestStr",
								"UserPhoneCode:" + UserPhoneCode + "---QRcode:"
										+ URLEncoder.encode(QRcode, "UTF-8")
										+ "---SerialNumber" + SerialNumber
										+ "---FileSN:" + FileSN
										+ "--FileContent:" + FileContent);

						AndroidHttpTransport ht = new AndroidHttpTransport(
								RequestUrl.HOST_URL);

						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
								SoapEnvelope.VER11);

						envelope.bodyOut = rpc;
						envelope.dotNet = true;
						envelope.setOutputSoapObject(rpc);

						ht.call(RequestUrl.NAMESPACE + "/"
								+ RequestUrl.record.SendWirePoleCheckRecord,
								envelope);

						SoapObject so = (SoapObject) envelope.bodyIn;

						String resultStr = so.getProperty(0).toString();
						Log.e("xxx_SendWirePoleCheckRecord_resultStr",
								resultStr);

						if (!TextUtils.isEmpty(resultStr)) {
							parseSendWirePoleCheckRecordData(resultStr, handler);
						}

					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					}
				} else {
					handler.sendEmptyMessage(NET_ERROR);
				}
			}
		}).start();

	}

	private static void parseSendWirePoleCheckRecordData(String response,
			Handler handler) {
		if (response.equals("2")) {
			handler.sendEmptyMessage(SEND_RECORD_SUC);
		} else {
			handler.sendEmptyMessage(SEND_RECORD_FAIL);
		}
	}

	public static void SendWirePoleCheckRecordByHttp(final Context context,
			final Handler handler, final String UserPhoneCode,
			final String QRcode, final String SerialNumber,
			final String FileSN, final String FileContent) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					URL httpurl = new URL(RequestUrl.HOST_URL + "/"
							+ RequestUrl.record.SendWirePoleCheckRecord);
					HttpURLConnection connection = (HttpURLConnection) httpurl
							.openConnection();

					connection.setDoOutput(true);// 允许连接提交信息
					connection.setRequestMethod("POST");// 提交方式“GET”、“POST”
					// connection.setRequestProperty("Cookie",
					// "ibismkhnd9hto8m8j5sj1vg5s5");// 将当前的sessionid一并上传
					// 进行编码
					connection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					// connection.setRequestProperty("Content-Length",
					// "length");

					connection.connect();
					// POST请求
					DataOutputStream out = new DataOutputStream(connection
							.getOutputStream());

					StringBuffer requestSb = new StringBuffer();
					requestSb
							.append("UserPhoneCode=")
							.append(UserPhoneCode)
							.append("&QRcode=")
							.append(QRcode)
							.append("&SerialNumber=")
							.append(SerialNumber)
							.append("&FileSN=")
							.append(FileSN)
							.append("&FileContent=")
							.append(URLEncoder
									.encode("iVBORw0KGgoAAAANSUhEUgAAACAAAAAeCAYAAABNChwpAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAq1JREFUeNq8ls9LVFEUx1+m0WStKishcJMUlPZDqslR/ANq5aYfm8oJZyVtpBYVQUXpMltUC1tpBbluGYyCTlgQSOCqhIgyM5WCIZHpe+Dz4PF6k/Pu5Bz4MGfOu+fc77vv3vPeunQ67TlajXiLf1gsuxSp8tytU+yHTtci5QjoKeJXRMARkRS/IUmsYgL8O34GzqvgIqBOnMa/Dx6xukoI6BYbxLh4A+PEutdagB29TODuvZCfYcyaCbDjVi8+i5FAfIRYfdwjWaqAWtEgLvP/YajxLBPzGNNAzqpWLQ6IY2Ib2Ebajr8Df2Mgx47do4haFrtGrQ/E8uKb+Crm8GfxjZwJeB2aoJj9El/EY4qEzWI3xCWxkxWwurshyvImYFhcJPBd9PE8w4rzJYjsA4/J/VX0V9f2yBWxlTHDJiDNkt3iQrs4I3565ZkJ/gRmm8XTwOTXxR3bhAVxm0ZiSSfF6D+WzcWs1hi188xlcxaCp+C56GDDHGRvtPyHyVuo1UztDuaKPIY5dvEUGylbzquW3Cy1pqidW60PzIhW8VIkxAs2Tly7Sm6CWq3ULqkRLYlT4gH/74m9MSa3sXfxB6i1FLcTroib+IUiZ7+YzZLjUWPFtRWn+H0v5mMImCfHrK2cd4GfnHXYA6NrLcCa2HlxAT9s2dAqxhZQG/jOG4t4NPYh8kQM4qeKrID1gU0uAuxDc734GGinu8QQxZvED2giNsQYj5wZaiRdBKQCS2lfOb1iWpxlh9vrt1HswS9wbZqxNYHH0OYioD3wrN+JfrFFTIijfH7N8QbNEJtgTD85Va4CTP1xfLurfZztLnFCTEbkTHKti7GWcy7wOKvjCGimhfoNaYDlHgw0mCgrMKaRHL8BJXjBlSxgUSyIVyT2ECvVFsk5RI0F+Mv+CDAAdsalGjhb1y0AAAAASUVORK5CYII=",
											"UTF-8"));
					Log.e("xxx_FileContent", FileContent.trim());
					System.out.print(FileContent.trim());

					String s = "UserPhoneCode=" + UserPhoneCode + "&QRcode="
							+ "20" + "&SerialNumber=" + "30" + "&FileSN="
							+ "40" + "&FileContent=" + "50";
					Log.e("xxx_s", requestSb.toString());
					out.writeBytes(requestSb.toString());
					out.flush();
					out.close();

					// 读取响应
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(connection.getInputStream()));
					String lines;
					StringBuffer sb = new StringBuffer("");
					while ((lines = reader.readLine()) != null) {
						lines = new String(lines.getBytes(), "utf-8");
						sb.append(lines);
					}
					parseSendWirePoleCheckRecordByHttpData(sb.toString(),
							handler);
					reader.close();
					// 断开连接
					connection.disconnect();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();

	}

	private static void parseSendWirePoleCheckRecordByHttpData(String response,
			Handler handler) {
		Log.e("xxx_SendWirePoleCheckRecordByHttp_result", response.toString());
		if (response.contains("2")) {
			handler.sendEmptyMessage(SEND_RECORD_SUC);
		} else {
			handler.sendEmptyMessage(SEND_RECORD_FAIL);
		}
	}
	
	public static void SendWirePoleCheckRecordByVolley(final Context context,
			final Handler handler, final String UserPhoneCode,
			final String QRcode, final String SerialNumber,
			final String FileSN, final String FileContent) {
		
	}

	public static void SerarchWirePoleCheckRecord(final Context context,
			final Handler handler, final String SerialNumber) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				if (NetUtils.checkNetworkConnection(context)) {
					try {
						SoapObject rpc = new SoapObject(RequestUrl.NAMESPACE,
								RequestUrl.record.SerarchWirePoleCheckRecord);

						rpc.addProperty("SerialNumber",
								URLEncoder.encode(SerialNumber, "UTF-8"));

						AndroidHttpTransport ht = new AndroidHttpTransport(
								RequestUrl.HOST_URL);

						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
								SoapEnvelope.VER11);

						envelope.bodyOut = rpc;
						envelope.dotNet = true;
						envelope.setOutputSoapObject(rpc);

						ht.call(RequestUrl.NAMESPACE + "/"
								+ RequestUrl.record.SerarchWirePoleCheckRecord,
								envelope);

						SoapObject so = (SoapObject) envelope.bodyIn;

						String resultStr = so.getProperty(0).toString();
						Log.e("xxx_SerarchWirePoleCheckRecord_resultStr",
								resultStr);

						if (!TextUtils.isEmpty(resultStr)) {
							JSONObject obj = new JSONObject(resultStr);
							parseSerarchWirePoleCheckRecordData(obj, handler);
						}

					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					handler.sendEmptyMessage(NET_ERROR);
				}
			}
		}).start();
	}

	private static void parseSerarchWirePoleCheckRecordData(
			JSONObject response, Handler handler) {
		try {
			String sucResult = response.getString("result").trim();
			if (sucResult.equals("2")) {
				handler.sendEmptyMessage(SEND_RECORD_SUC);
			} else {
				handler.sendEmptyMessage(SEND_RECORD_FAIL);
			}
		} catch (JSONException e) {
			handler.sendEmptyMessage(SEND_RECORD_EXCEPTION);
		}
	}

	public static void SendWirePoleCheckRecordByService(final Context context,
			final Handler handler, final String UserPhoneCode,
			final String QRcode, final String SerialNumber,
			final String FileSN, final String FileContent) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				if (NetUtils.checkNetworkConnection(context)) {

					try {
						SoapObject rpc = new SoapObject(RequestUrl.NAMESPACE,
								RequestUrl.record.SendWirePoleCheckRecord);

						rpc.addProperty("UserPhoneCode",
								URLEncoder.encode(UserPhoneCode, "UTF-8"));
						rpc.addProperty("QRcode",
								URLEncoder.encode(QRcode, "UTF-8"));
						rpc.addProperty("SerialNumber",
								URLEncoder.encode(SerialNumber, "UTF-8"));
						rpc.addProperty("FileSN",
								URLEncoder.encode(FileSN, "UTF-8"));
						rpc.addProperty("FileContent",
								URLEncoder.encode(FileContent, "UTF-8"));

						AndroidHttpTransport ht = new AndroidHttpTransport(
								RequestUrl.HOST_URL);

						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
								SoapEnvelope.VER11);

						envelope.bodyOut = rpc;
						envelope.dotNet = true;
						envelope.setOutputSoapObject(rpc);

						ht.call(RequestUrl.NAMESPACE + "/"
								+ RequestUrl.record.SendWirePoleCheckRecord,
								envelope);

						SoapObject so = (SoapObject) envelope.bodyIn;

						String resultStr = so.getProperty(0).toString();
						Log.e("xxx_SendWirePoleCheckRecord_ByService_resultStr",
								resultStr);

						if (!TextUtils.isEmpty(resultStr)) {
							parseSendWirePoleCheckRecordByServiceData(
									resultStr, handler, SerialNumber);
						}

					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					}
				} else {
					handler.sendEmptyMessage(NET_ERROR);
				}
			}
		}).start();
	}

	private static void parseSendWirePoleCheckRecordByServiceData(
			String response, Handler handler, String SerialNumber) {
		if (response.equals("2")) {
			Message message = new Message();
			message.obj = SerialNumber;
			message.what = SEND_RECORD_SUC;
			handler.sendMessage(message);
		} else {
			Message message = new Message();
			message.obj = SerialNumber;
			message.what = SEND_RECORD_FAIL;
			handler.sendMessage(message);
		}
	}

}
