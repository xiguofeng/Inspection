package com.xgf.inspection.network.logic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
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

public class AppLogic {

	public static final int NET_ERROR = 0;

	public static final int SEND_RECORD_SUC = NET_ERROR + 1;

	public static final int SEND_RECORD_FAIL = SEND_RECORD_SUC + 1;

	public static final int SEND_RECORD_EXCEPTION = SEND_RECORD_FAIL + 1;

	public static void SendWirePoleCheckRecord(final Context context,
			final Handler handler, final String UserPhoneCode,
			final String QRcode, final String SerialNumber,
			final String FileSN, final String FileContent) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Log.e("xxx_123", "1");
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

					Log.e("xxx_sop", rpc.toString());
					if (null != envelope.getResponse()) {
						SoapObject responseSO = (SoapObject) envelope
								.getResponse();
						responseSO.toString();
						Log.e("xxx_responseSO", rpc.toString());
					}

					SoapObject so = (SoapObject) envelope.bodyIn;

					String resultStr = (String) so.getProperty(0);
					Log.e("xxx_SendWirePoleCheckRecord_resultStr", resultStr);

					if (!TextUtils.isEmpty(resultStr)) {
						JSONObject obj = new JSONObject(resultStr);
						parseGetVersionData(obj, handler);
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
			}
		}).start();

	}

	private static void parseGetVersionData(JSONObject response, Handler handler) {
		try {
			String sucResult = response.getString(MsgResult.RESULT_TAG).trim();
			if (sucResult.equals(MsgResult.RESULT_SUCCESS)) {
				handler.sendEmptyMessage(SEND_RECORD_SUC);
			} else {
				handler.sendEmptyMessage(SEND_RECORD_FAIL);
			}
		} catch (JSONException e) {
			handler.sendEmptyMessage(SEND_RECORD_EXCEPTION);
		}
	}

}
