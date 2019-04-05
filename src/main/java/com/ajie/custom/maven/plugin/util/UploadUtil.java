package com.ajie.custom.maven.plugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import com.ajie.custom.maven.plugin.vo.Server;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * 文件上传工具
 *
 * @author niezhenjie
 *
 */
final public class UploadUtil {

	private UploadUtil() {

	}

	/**
	 * 文件上传,上传文件不能使文件夹，可以是war或jar（java io无法读取文件夹）
	 * 
	 * @param src
	 *            需要上传的文件所在的目录
	 * @param procectName
	 *            项目名称，要带后缀，如blog.war
	 * @param server
	 *            服务器信息
	 * @param log
	 * @throws MojoFailureException
	 */
	public static void upload(String src, String fileName, Server server, Log log)
			throws MojoFailureException {
		if (null == src) {
			throw new MojoFailureException("无上传目录,src=" + src);
		}
		if (fileName.indexOf(".") == -1) {
			throw new MojoFailureException("无法上传文件夹,fileName=" + fileName);
		}
		src += fileName;
		FileInputStream in = null;
		OutputStream out = null;
		try {
			File file = new File(src);
			in = new FileInputStream(file);
			JSch jsch = new JSch();
			String host = server.getHost();
			String username = server.getUserName();
			String password = server.getPassword();
			int port = server.getPort();
			String path = server.getUploadBasePath();
			// 我上传的路径结构时${basePath}/${projectName}/${fileName}
			String name = fileName.substring(0, fileName.lastIndexOf("."));
			path += name + "/";
			Session session = jsch.getSession(username, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect(30000);
			Channel channel = session.openChannel("sftp");
			ChannelSftp sftp = (ChannelSftp) channel;
			sftp.connect(30000);
			String folder = createFolders(path, sftp);
			out = sftp.put(folder + fileName);
			byte[] buf = new byte[1024];
			int n = 0;
			while ((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("打包成功，上传失败", e);
		} finally {
			try {
				if (null != in)
					in.close();
				if (null != out)
					out.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 切割配置里的目录路径 basePath形式 如：/var/www/或var/www 不管哪种形式，都是绝对路径
	 * 
	 * @return
	 * @throws RemoteException
	 */
	static private String createFolders(String path, ChannelSftp sftp) throws IOException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		String[] folders = path.split("/");
		if (null == folders) {
			folders = new String[0];
		}
		String cd = "";
		// 进入目录，如果目录不存在，则创建目录
		for (int i = 0; i < folders.length; i++) {
			cd += "/" + folders[i];
			boolean currErr = false;// 创建目录过程中出现了错误
			Throwable e = null;
			try {
				sftp.cd(cd);
			} catch (SftpException exce) {
				// 没有则创建
				try {
					sftp.mkdir(cd);
				} catch (SftpException e1) {
					currErr = true;
					e = e1;
					break;
				}
			}
			if (currErr) {
				throw new IOException("无法创建目录 ", e);
			}
		}
		// 结尾加上/如/var/www/
		if (null != path) {
			cd += "/";
		}
		return cd;
	}
}
