package com.ajie.custom.maven.plugin.vo;

/**
 * 服务器信息，如果使用文件加载，在首次获取使用server时，需要调用init
 *
 * @author niezhenjie
 *
 */
public class Server {
	/** 默认端口 */
	public static final int DEFAULT_PORT = 22;
	/** 默认上传的根目录 */
	public static final String DEFAULT_UPLOAD_PATH = "/var/www/";
	/** 主机地址 */
	private String host;
	/** 用户名 */
	private String username;
	/** 密码 */
	private String password;
	/** 端口 */
	private int port;
	/** 是否上传 */
	private boolean isupload;
	/** 上传至服务器路径，最终文件会上传至uploadpath+projectName路径 */
	private String uploadBasePath;

	public Server() {

	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String userName) {
		this.username = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static int getDefaultPort() {
		return DEFAULT_PORT;
	}

	public void setIsUpload(boolean b) {
		isupload = b;
	}

	public boolean isUpload() {
		return isupload;
	}

	public void setUploadBasePath(String uploadpath) {
		this.uploadBasePath = uploadpath;
	}

	/**
	 * 获取上传文件至服务器的路径，路径结束符为/
	 * 
	 * @return
	 */
	public String getUploadBasePath() {
		if (null == uploadBasePath)
			uploadBasePath = DEFAULT_UPLOAD_PATH;
		if (!uploadBasePath.endsWith("/")) {
			uploadBasePath += "/";
		}
		return uploadBasePath;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{host:").append(host).append(",");
		sb.append("username:").append(username).append(",");
		sb.append("password:").append(password).append(",");
		sb.append("isupload:").append(isupload).append(",");
		sb.append("uploadBasePath:").append(uploadBasePath).append("}");
		return sb.toString();
	}

}
