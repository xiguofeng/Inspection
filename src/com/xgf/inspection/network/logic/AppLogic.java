package com.xgf.inspection.network.logic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

}
