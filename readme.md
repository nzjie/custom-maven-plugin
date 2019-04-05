PackageMojo提供了一键部署的功能，在pom中配置好服务器相关信息即可，使用：
将本插件安装到本地仓库，在需要使用的项目的pom文件引入以下插件
&gt;build>
	<plugins>
		<plugin>
			<groupId>com.ajie</groupId>
			<artifactId>custom-maven-plugin</artifactId>
			<version>1.0.10</version>
			<executions>
				<execution>
					<goals>
						绑定的生命周期
						<goal>install</goal>
					</goals>
				</execution>
			</executions>
			<configuration>
				<server>
					<host>服务器主机</host>
					<username>登录的用户名</username>
					<password>登录密码</password>
					<port>端口</port>
					<isupload>是否执行自动上传并部署 true|false defalut false</isupload>
				</server>
			</configuration>
		</plugin>
	</plugins>
</build>
其中，server信息的配置除上述以外，还可以使用
<configuration>
	<serverFile>相对于classpath的路径</serverFile>	
</configuration>
但是，需要注意的是，serverFile指向的配置文件不是放在需要运行的项目，而是放在本插件的项目里
运行插件：custom:package（debug模式：custom:package -X）

