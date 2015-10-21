package com.xgf.inspection.service;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.xgf.inspection.entity.ImageValue;
import com.xgf.inspection.network.logic.AppLogic;
import com.xgf.inspection.utils.FileUtil;
import com.xgf.inspection.utils.ImageUtils;

public class UploadService extends Service {

	private Context mContext;

	private String[] photeIndex = { "photeIndexFirst", "photeIndexSecond",
			"photeIndexThird" };

	private ArrayList<ImageValue> mImageList = new ArrayList<ImageValue>();

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
				failNum = 0;

				AppLogic.SendWirePoleCheckRecord(
						mContext,
						mHandler,
						mDeviceUuid,
						mQrCode,
						mSerialNumber,
						photeIndex[progressIndex],
						ImageUtils.Bitmap2StrByBase64(mImageList.get(
								progressIndex).getBitmap()));

			}
			case AppLogic.SEND_RECORD_FAIL: {
				if (failNum < 2) {
					failNum++;

					AppLogic.SendWirePoleCheckRecord(
							mContext,
							mHandler,
							mDeviceUuid,
							mQrCode,
							mSerialNumber,
							photeIndex[progressIndex],
							ImageUtils.Bitmap2StrByBase64(mImageList.get(
									progressIndex).getBitmap()));
				}

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
			String jsonArrayStr = FileUtil.read(mContext, "no_upload.json");
			JSONArray jsonArray;
			if (null != jsonArrayStr) {
				jsonArray = new JSONArray(jsonArrayStr);
			} else {
				jsonArray = new JSONArray();
			}
			Log.e("xxx_no_upload", jsonArray.toString());
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
