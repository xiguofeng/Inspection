package com.xgf.inspection.photo.cropimage;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.xgf.inspection.R;
import com.xgf.inspection.photo.utils.OSUtils;

public class ChooseDialog implements OnClickListener {
	// SD卡不存在
	public static final String SDCARD_NOT_EXISTS = "SD卡不存在，无法设置头像";
	private Dialog mDialog = null;
	private Activity mActivity = null;
	private CropHelper mCropHelper = null;
	private String mPicUrl;

	public ChooseDialog(Activity act, CropHelper helper,String picUrl) {
		mActivity = act;
		mCropHelper = helper;
		mPicUrl=picUrl;
	}

	public void popSelectDialog() {
		if (OSUtils.ExistSDCard()) {
			setDialog();
			mDialog.show();
		} else {
			showToast(SDCARD_NOT_EXISTS);
			return;
		}
	}

	private void setDialog() {
		// 此处直接new一个Dialog对象出来，在实例化的时候传入主题
		if (mDialog == null) {
			mDialog = new Dialog(mActivity, R.style.MyDialog);
			mDialog.setContentView(R.layout.head_set_choice_dialog);
			mDialog.setCanceledOnTouchOutside(true);
			TextView takePic = (TextView) mDialog.findViewById(R.id.take_pictures);
			TextView cancelTxt = (TextView) mDialog.findViewById(R.id.select_cancel);
			
			TextView selectAlbum = (TextView) mDialog
					.findViewById(R.id.select_photo);
			takePic.setOnClickListener(this);
			selectAlbum.setOnClickListener(this);
			cancelTxt.setOnClickListener(this);
		}
	}

	public void showToast(String msg) {
		Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (viewId == R.id.take_pictures) {
			clickInCamera();
		} else if (viewId == R.id.select_photo) {
			clickInAlbum(mPicUrl);
		}else if (viewId == R.id.select_cancel) {
			mDialog.dismiss();
		}
	}

	// 拍照
	public void clickInCamera() {
		if (mDialog != null)
			mDialog.dismiss();
		mCropHelper.startCamera();
	}

	// 从本地相册
	public void clickInAlbum(String picFileUrl) {
		if (mDialog != null)
			mDialog.dismiss();
		mCropHelper.startAlbum(picFileUrl);
	}
}
