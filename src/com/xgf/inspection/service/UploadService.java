package com.xgf.inspection.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.xgf.inspection.entity.UploadValue;
import com.xgf.inspection.network.logic.AppLogic;
import com.xgf.inspection.utils.FileHelper;
import com.xgf.inspection.utils.FileUtil;
import com.xgf.inspection.utils.FileUtils;
import com.xgf.inspection.utils.ImageUtils;
import com.xgf.inspection.utils.JsonUtils;

public class UploadService extends Service {

	private Context mContext;

	private String[] photeIndex = { "photeIndexFirst", "photeIndexSecond",
			"photeIndexThird" };

	private ArrayList<UploadValue> mUploadValueList = new ArrayList<UploadValue>();

	private String mDeviceUuid;
	private String mSerialNumber;
	private String mQrCode;

	private int failNum = 0;
	private boolean isComplete = false;
	private int progressIndex = 0;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case AppLogic.SEND_RECORD_SUC: {
			}
			case AppLogic.SEND_RECORD_FAIL: {
				break;
			}
			case AppLogic.SEND_RECORD_EXCEPTION: {
				break;
			}

			case AppLogic.SEARCH_RECORD_SUC: {

			}
			case AppLogic.SEARCH_RECORD_FAIL: {
				break;
			}
			case AppLogic.SEARCH_RECORD_EXCEPTION: {
				break;
			}

			case AppLogic.NET_ERROR: {
				break;
			}

			default:
				break;
			}

		}

	};

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("uplooad", "onStartCommand");

		flags = START_STICKY;
		try {
			Log.e("xxx_start_upload", "1");
			FileHelper.createSDFile("noupload.txt");
			String jsonArrayStr = FileHelper.readSDFile("noupload.txt");
			JSONArray jsonArray = new JSONArray();
			if (!TextUtils.isEmpty(jsonArrayStr)) {
				jsonArray = new JSONArray(jsonArrayStr);
			}
			int size = jsonArray.length();
			mUploadValueList.clear();
			for (int i = 0; i < size; i++) {
				JSONObject uploadJsonObject = jsonArray.getJSONObject(i);
				UploadValue upload = (UploadValue) JsonUtils.fromJsonToJava(
						uploadJsonObject, UploadValue.class);

				AppLogic.SendWirePoleCheckRecord(mContext, mHandler,
						upload.getUserPhoneCode(), upload.getQRcode(),
						upload.getSerialNumber(), upload.getFileSN(),
						upload.getFileContent());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
