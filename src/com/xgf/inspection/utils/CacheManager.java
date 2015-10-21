package com.xgf.inspection.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用户信息管理类
 */
public class CacheManager {

	public static final String USER_INFO_PREFERNCE_KEY = "userinfo";

	public static final String USER_FIRST_USE_KEY = "ISFIRSTUSE";

	public static final String USER_ID_KEY = "user_id";

	/**
	 * 保存用户登录返回信息
	 * 
	 * @param context
	 */
	public static void setUserInfo(Context context) {
		SharedPreferences userInfoPreferences = context.getSharedPreferences(
				USER_INFO_PREFERNCE_KEY, Context.MODE_PRIVATE);

	}

	/**
	 * 保存用户登录返回信息到配置文件
	 * 
	 * @param context
	 * @param userName
	 * @param userPass
	 * @param user
	 *            登录成功后，服务器返回的用户信息
	 */
	public static void saveUserInfo(Context context, String user) {
		if (null != user) {
			SharedPreferences.Editor userInfoSp = context.getSharedPreferences(
					USER_INFO_PREFERNCE_KEY, Context.MODE_PRIVATE).edit();

			userInfoSp.putString(USER_ID_KEY, null == user ? "" : user);

			userInfoSp.commit();
		}

	}

	public static void clearUserInfo(Context context) {
		SharedPreferences.Editor userInfoSp = context.getSharedPreferences(
				USER_INFO_PREFERNCE_KEY, Context.MODE_PRIVATE).edit();

		userInfoSp.putString(USER_ID_KEY, "");

	}

}
