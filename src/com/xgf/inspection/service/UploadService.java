package com.xgf.inspection.service;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.xgf.inspection.entity.UploadValue;
import com.xgf.inspection.network.logic.AppLogic;
import com.xgf.inspection.photo.utils.BitmapUtils;
import com.xgf.inspection.utils.FileHelper;
import com.xgf.inspection.utils.ImageUtils;
import com.xgf.inspection.utils.JsonUtils;

public class UploadService extends Service {

	public static final int TIME_UPDATE = 1;

	private Context mContext;

	private String[] photeIndex = { "1", "2", "3" };

	private ArrayList<UploadValue> mUploadValueList = new ArrayList<UploadValue>();

	private String mDeviceUuid;
	private String mSerialNumber;
	private String mQrCode;

	private int failNum = 0;
	private boolean isComplete = false;
	private int progressIndex = 0;

	private int uploadNum = 0;

	private ArrayList<String> mUploadFailList = new ArrayList<String>();

	private ArrayList<UploadValue> mUploadValueFailList = new ArrayList<UploadValue>();

	private ArrayList<Bitmap> mBitmapList = new ArrayList<Bitmap>();

	private Handler mTimeHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TIME_UPDATE: {
				if (mUploadFailList.size() > 0) {
					saveUploadSuc();
				} else {
					clearCache();
				}
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
				uploadNum++;
				if (uploadNum == mUploadValueList.size()) {
					clearCache();
				}
				break;
			}
			case AppLogic.SEND_RECORD_FAIL: {
				if (null != msg.obj) {
					addUploadFail((String) msg.obj);
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
		Log.e("xxx_upload", "onStartCommand");

		flags = START_STICKY;
		mUploadFailList.clear();
		mUploadValueFailList.clear();
		uploadNum = 0;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					FileHelper.createSDFile("insnoupload.txt");
					String jsonArrayStr = FileHelper
							.readSDFile("insnoupload.txt");
					JSONArray jsonArray = new JSONArray();
					Log.e("xxx_jsonArrayStr", "jsonArrayStr:" + jsonArrayStr);
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

							BitmapUtils.setSize(300, 500);
							Bitmap bitmap = BitmapUtils.getBitmap(upload
									.getFileLocalUrl());
							if (null != bitmap) {
								mBitmapList.add(bitmap);
								upload.setFileContent(ImageUtils
										.Bitmap2StrByBase64(bitmap));
							} else {
								FileHelper.deleteSDFile("insnoupload.txt");
								return;
							}
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
								1000 * 60);
					}
					FileHelper.deleteSDFile("insnoupload.txt");

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
						jsonObject.put("FileLocalUrl", mUploadValueFailList
								.get(i).getFileLocalUrl());
						jsonArray.put(jsonObject);
					}
					FileHelper.deleteSDFile("insnoupload.txt");
					FileHelper.createSDFile("insnoupload.txt");
					FileHelper.writeSDFileNew(jsonArray.toString(),
							"insnoupload.txt");
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

	private void clearCache() {
		for (UploadValue uploadValue : mUploadValueList) {
			File file = new File(uploadValue.getFileLocalUrl());
			if (file.exists()) {
				com.xgf.inspection.photo.utils.FileUtils.deleteAllFiles(file);
			}
		}
		for (Bitmap bitmap : mBitmapList) {
			bitmap.recycle();
		}
	}
}
