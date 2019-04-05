package com.ajie.custom.maven.plugin.build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.ajie.custom.maven.plugin.util.ExecuteUtil;

/**
 *
 *
 * @author niezhenjie
 *
 */
@Mojo(name = "install")
public class InstallMojo extends AbstractCustomMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("start install...");
		install();
	}

	public void install() throws MojoFailureException {
		String mvn = getMvn();
		StringBuilder sb = new StringBuilder();
		sb.append(mvn).append(" install:install-file").append(" -DpomFile=").append(getPom())
				.append(" -DgroupId=").append(project.getGroupId()).append(" -DartifactId=")
				.append(project.getArtifactId()).append(" -Dversion=").append(project.getVersion())
				.append(" -Dpackaging=").append(project.getPackaging());
		ExecuteUtil.execute(sb.toString(), getLog());
	}

	public static void main(String[] args) {
		try {
			Properties pro = new Properties();
			pro.setProperty("user.dir", "D:\\myworkspace\\resource\\");
			System.setProperties(pro);
			String userDir = System.getProperty("user.dir");
			System.out.println(userDir);
			// mvn install:install-file -Dfile=D:\myworkspace\api\pom.xml
			// -DgroupId=com.ajie -DartifactId=api -Dversion=1.0.10
			// -Dpackaging=jar
			System.out.println(System.getenv("MAVEN_HOME"));
			String mvn = System.getenv("MAVEN_HOME") + "/bin/mvn.bat";
			/*String cmd = mvn + " install:install-file -DpomFile=" + userDir + "pom.xml"
					+ " -DgroupId=com.ajie -DartifactId=resource -Dversion=1.0.10 -Dpackaging=war";*/
			//可以做，因为运行就运行在classpath，所以能自动找到pom文件，不需使用绝对路径指明（也不能使用绝对路径指明pom文件）
			String cmd = mvn + " package";
			Process process = Runtime.getRuntime().exec(cmd);
			/*process.waitFor();*/
			InputStream in = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
