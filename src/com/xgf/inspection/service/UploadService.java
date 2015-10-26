package com.xgf.inspection.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
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
import com.xgf.inspection.utils.JsonUtils;

public class UploadService extends Service {

	public static final int TIME_UPDATE = 1;
	
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

	private ArrayList<String> mUploadFailList = new ArrayList<String>();

	private ArrayList<UploadValue> mUploadValueFailList = new ArrayList<UploadValue>();

	private Handler mTimeHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TIME_UPDATE: {
				saveUploadSuc();
				break;
			}
			default:
				break;
			}
		};

	};
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case AppLogic.SEND_RECORD_SUC: {
				if (null != msg.obj) {
					break;
				}
			}
			case AppLogic.SEND_RECORD_FAIL: {
				if (null != msg.obj) {
					addUploadFail((String) msg.obj);
					break;
				}
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
		Log.e("xxx_upload", "onStartCommand");

		flags = START_STICKY;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					FileHelper.createSDFile("noupload.txt");
					String jsonArrayStr = FileHelper.readSDFile("noupload.txt");
					JSONArray jsonArray = new JSONArray();
					if (!TextUtils.isEmpty(jsonArrayStr)) {
						jsonArray = new JSONArray(jsonArrayStr);
						int size = jsonArray.length();
						mUploadValueList.clear();
						for (int i = 0; i < size; i++) {
							JSONObject uploadJsonObject = jsonArray
									.getJSONObject(i);
							UploadValue upload = (UploadValue) JsonUtils
									.fromJsonToJava(uploadJsonObject,
											UploadValue.class);
							mUploadValueList.add(upload);
							Log.e("xxx_upload",
									"upload" + upload.getSerialNumber());
							AppLogic.SendWirePoleCheckRecordByHttp(mContext,
									mHandler, upload.getUserPhoneCode(),
									upload.getQRcode(),
									upload.getSerialNumber(),
									upload.getFileSN(), upload.getFileContent());
						}
						mTimeHandler.sendEmptyMessageDelayed(TIME_UPDATE,
								1000 * 60 * 2);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();

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

	private void addUploadFail(String SerialNumber) {
		if (!mUploadFailList.contains(SerialNumber)) {
			mUploadFailList.add(SerialNumber);
		}
	}

	private void saveUploadSuc() {
		for (int i = 0; i < mUploadValueList.size(); i++) {
			if (mUploadFailList.contains(mUploadValueList.get(i)
					.getSerialNumber())) {
				mUploadValueFailList.add(mUploadValueList.get(i));
			}
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					JSONArray jsonArray = new JSONArray();
					for (int i = 0; i < mUploadValueFailList.size(); i++) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("UserPhoneCode", mUploadValueFailList
								.get(i).getUserPhoneCode());
						jsonObject.put("QRcode", mUploadValueFailList.get(i)
								.getQRcode());
						jsonObject.put("SerialNumber", mUploadValueFailList
								.get(i).getSerialNumber());
						jsonObject.put("FileSN", mUploadValueFailList.get(i)
								.getFileSN());
						jsonObject.put("FileContent",
								mUploadValueFailList.get(i).getFileContent());
						jsonArray.put(jsonObject);
					}
					FileHelper.deleteSDFile("noupload.txt");
					FileHelper.createSDFile("noupload.txt");
					FileHelper.writeSDFileNew(jsonArray.toString(),
							"noupload.txt");
					mUploadValueList.clear();
					mUploadFailList.clear();
					mUploadValueFailList.clear();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
