package com.ajie.custom.maven.plugin.build;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.ajie.custom.maven.plugin.vo.Server;

/**
 * 抽象的mojo
 *
 * @author niezhenjie
 *
 */
public abstract class AbstractCustomMojo extends AbstractMojo {

	public static final String MAVEN_HOME = "MAVEN_HOME";
	/** 从配置文件中配置maven home目录 */
	public static final String MAVEN_HOME_KEY = "maven.home";
	/** 文件分隔符 */
	public static final String SEPARATOR = File.separator;
	/** maven命令 */
	public static String MAVEN_CMD = "bin" + SEPARATOR + "mvn";
	static {
		String os = System.getProperty("os.name");
		if (os.startsWith("win") || os.startsWith("Win")) {
			MAVEN_CMD += ".bat";// window系统 bin/mvn.bat
		}
	}

	public static final String TARGET_FOLDER = "target";

	@Parameter(property = "project")
	protected MavenProject project;
	/** 服务器信息，和serverFile二选一，如果两个都配置，最终读取serverFile里的信息 */
	@Parameter
	protected Server server;
	/**
	 * 服务器信息配置文件路径，该配置文件只能读取custom-maven-plugin项目下面的，不能读取待打包项目的路径，
	 * 使用相对classpath路径，和server二选一，如果两个都配置，最终读取server里的信息
	 */
	@Parameter
	protected String serverFile;
	/** user.dir */
	private String userDir;
	/** 打包后的文件目录 */
	private String targetDir;
	/** 打包后的项目路径 */
	private String targetFilePath;
	/** 项目名 */
	private String projectName;
	/** 类型名 jar、war.. */
	private String projectType;

	public String getMavenHome() {
		/*// 启动时通过参数传入
		String home = System.getProperty(MAVEN_HOME_KEY);
		if (!StringUtils.isEmpty(home)) {
			return home;
		}*/
		// pom文件中配置
		String home = project.getProperties().getProperty(MAVEN_HOME_KEY);
		if (null != home) {
			return home;
		}
		// 环境变量
		return System.getenv(MAVEN_HOME);
	}

	public String getMvn() throws MojoFailureException {
		String mavenHome = getMavenHome();
		if (null == mavenHome) {
			getLog().error("缺少maven主目录");
			throw new MojoFailureException("缺少maven主目录");
		}
		if (!mavenHome.endsWith(SEPARATOR)) {
			mavenHome += SEPARATOR;
		}
		return mavenHome + MAVEN_CMD;
	}

	public String getProjectName() {
		if (null != projectName) {
			return projectName;
		}
		projectName = project.getArtifactId();
		return projectName;
	}

	public String getPom() {
		StringBuilder sb = new StringBuilder();
		String userDir = getUserDir();
		sb.append(userDir);
		sb.append("pom.xml");
		return sb.toString();
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

	public MavenProject getProject() {
		return project;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * 项目目录，结束符为"/" ,因为在eclipse运行，所以user.dir始终指向项目路径
	 * 
	 * @return
	 */
	public String getUserDir() {
		if (null != userDir) {
			return userDir;
		}
		String userDir = System.getProperty("user.dir");
		if (!userDir.endsWith(SEPARATOR)) {
			userDir += SEPARATOR;
		}
		this.userDir = userDir;
		return userDir;
	}

	public String getTargetDir() {
		if (null != targetDir) {
			return targetDir;
		}
		String userDir = getUserDir();
		targetDir = userDir + TARGET_FOLDER;
		return targetDir;
	}

	public String getTargetFilePath() {
		if (null != targetFilePath)
			return targetFilePath;
		targetFilePath = getTargetDir() + SEPARATOR;
		return targetFilePath;
	}

	public String getProjectType() {
		if (null != projectType) {
			return projectType;
		}
		projectType = project.getPackaging();
		return projectType;
	}

	public Server getServer() throws MojoFailureException {
		// 单例，不存在并发，不需要锁
		if (null != server)
			return server;
		if (null == serverFile)
			return server;
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(serverFile);
		if (null == is)
			throw new MojoFailureException("找不到配置文件，serverFile=" + serverFile);
		Properties prop = new Properties();
		try {
			prop.load(is);
		} catch (IOException e) {
			throw new MojoFailureException("无法加载配置文件，serverFile=" + serverFile, e);
		}
		String host = prop.getProperty("host");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");
		String sPort = prop.getProperty("port");
		String sIsupload = prop.getProperty("isupload");
		if (null == host)
			throw new MojoFailureException("主机为空，host=" + host);
		if (null == username)
			throw new MojoFailureException("用户名为空，username=" + username);
		int port = 0;
		try {
			port = Integer.valueOf(sPort);
		} catch (Exception e) {
			getLog().warn("无法解析端口：" + sPort + "，使用默认端口代替port:" + port);
			port = Server.DEFAULT_PORT;
		}
		boolean isUpload = false;
		try {
			isUpload = Boolean.valueOf(sIsupload);
		} catch (Exception e) {
			if ("true".equalsIgnoreCase(sIsupload))
				isUpload = true;
			else if ("false".equalsIgnoreCase(sIsupload))
				isUpload = false;
			else {
				getLog().warn("无法判断是否上传，sIsupload：" + sIsupload + "，默认不上传");
				isUpload = false;
			}

		}
		Server server = new Server();
		server.setHost(host);
		server.setUserName(username);
		server.setPassword(password);
		server.setPort(port);
		server.setIsUpload(isUpload);
		return server;
	}
}
