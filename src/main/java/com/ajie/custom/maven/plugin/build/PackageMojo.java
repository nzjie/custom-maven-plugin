package com.ajie.custom.maven.plugin.build;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.ajie.custom.maven.plugin.util.ExecuteUtil;
import com.ajie.custom.maven.plugin.util.UploadUtil;
import com.ajie.custom.maven.plugin.vo.Server;

/*
 * 自定义打包插件，打包完成可以自动上传服务器<br>
 * pom配置：<br>
 * <build>
		<plugins>
			<plugin>
				<groupId>com.ajie</groupId>
				<artifactId>custom-maven-plugin</artifactId>
				<version>1.0.10</version>
				<executions>
					<execution>
						<goals>
							<goal>install</goal>
						</goals>
					</execution>
				</executions>
				<configuration>
					<server>
						<host>www.ajie.top</host>
						<username>ajie</username>
						<password>123</password>
						<port>22</port>
						<isupload>true</isupload>
					</server>
				</configuration>
			</plugin>
		</plugins>
	</build>
 *
 * @author niezhenjie
 *
 */
@Mojo(name = "package")
public class PackageMojo extends AbstractCustomMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("start package ...");
		packageProject();
	}

	public void packageProject() throws MojoFailureException {
		String mvn = getMvn();
		String cmd = mvn + " package";
		ExecuteUtil.execute(cmd, getLog());
		getLog().info("package success");
		Server server = getServer();
		if (null == server)
			return;
		if (!server.isUpload())
			return;
		getLog().info("start upload file to server");
		if (getLog().isDebugEnabled()) {
			getLog().debug(server.toString());
		}
		long start = System.currentTimeMillis();
		UploadUtil.upload(getTargetFilePath(), getProjectName() + "." + getProjectType(), server,
				getLog());
		long end = System.currentTimeMillis();
		getLog().info("upload success, time consuming: " + (end - start) / 1000 + "s");
		getLog().info("exec remote deploy script");
		start = System.currentTimeMillis();
		ExecuteUtil.execute(getServer(), getProjectName(), getLog());
		end = System.currentTimeMillis();
		getLog().info(
				"exec remote deploy script success,time consuming: " + (end - start) / 1000 + "s");
		getLog().info("done");
	}

}
