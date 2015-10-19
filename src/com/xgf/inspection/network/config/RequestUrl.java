package com.xgf.inspection.network.config;

/**
 * remote request url
 */
public class RequestUrl {

	public static final String NAMESPACE = "http://tempuri.org";

	public static final String HOST_URL = "http://120.24.239.215/web/WebService.asmx";

	public interface record {
		/**
		 * 上传信息
		 */
		public String SendWirePoleCheckRecord = "SendWirePoleCheckRecord";
		
		/**
		 * 查询电线杆检测记录
		 */
		public String SerarchWirePoleCheckRecord = "SerarchWirePoleCheckRecord";

	}

}
