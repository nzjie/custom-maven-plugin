package com.ajie.custom.maven.plugin.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import com.ajie.custom.maven.plugin.vo.Server;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * 执行命令
 *
 * @author niezhenjie
 *
 */
public final class ExecuteUtil {

	private ExecuteUtil() {

	}

	/**
	 * 执行mvn命令
	 * 
	 * @param cmd
	 * @param log
	 * @throws MojoFailureException
	 */
	public static void execute(String cmd, Log log) throws MojoFailureException {
		InputStream is = null;
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			is = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.replace("[INFO]", ""); // 去除info标记，否则会有两个[INFO]
				log.info(line);
			}
			int exitVal = process.waitFor();
			if (0 != exitVal) {
				// 有错误
				BufferedReader error = new BufferedReader(new InputStreamReader(is));
				line = null;
				while ((line = error.readLine()) != null) {
					log.error(line);
				}
			}
		} catch (IOException e) {
			throw new MojoFailureException("发布失败", e);
		} catch (InterruptedException e) {
			throw new MojoFailureException("发布失败", e);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					// 忽略
				}
			}
		}
	}

	public static void execute(Server server, String projectName, Log log)
			throws MojoFailureException {
		StringBuilder cmd = new StringBuilder();
		cmd.append(server.getUploadBasePath());
		cmd.append(projectName);
		cmd.append("/");
		cmd.append("deploy.sh");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = null;
		JSch jsch = new JSch();
		String host = server.getHost();
		String username = server.getUserName();
		String password = server.getPassword();
		int port = server.getPort();
		// 我上传的路径结构时${basePath}/${projectName}/${fileName}
		Session session = null;
		ChannelExec channel = null;
		try {
			session = jsch.getSession(username, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect(30000);
			channel = (ChannelExec) session.openChannel("exec");
			channel.setInputStream(null);
			channel.setErrStream(out);
			channel.setCommand(cmd.toString());
			in = channel.getInputStream();
			channel.connect();
			byte[] buf = new byte[1024];
			while (true) { // 因为是异步的，数据不一定能及时获取到，所以需要轮询
				while (in.available() > 0) {
					in.read(buf);
					out.write(buf);
				}
				if (channel.isClosed()) { // channel关闭了，但是还有数据在流中，继续读
					if (in.available() > 0)
						continue;
					break;
				}
				Thread.sleep(10);
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(out.toByteArray())));
			if (log.isDebugEnabled()) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					log.info(new String(line.getBytes("utf-8"), "utf-8"));
				}
			}
		} catch (Exception e) {
			log.error("文件上传成功，执行脚本失败", e);
		} finally {
			try {
				if (null != in)
					in.close();
				if (null != channel && channel.isConnected())
					channel.disconnect();
				if (null != session && session.isConnected())
					session.disconnect();
				out = null;
			} catch (Exception e) {
			}
		}

	}

	public static void main(String[] args) {
		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession("", "", 22);
			session.setPassword("");
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand("/var/www/resource/test2.sh");
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					if (in.available() > 0)
						continue;
					System.out.println("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
