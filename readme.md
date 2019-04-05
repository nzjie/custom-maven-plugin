PackageMojo提供了一键部署的功能，在pom中配置好服务器相关信息即可，使用：
将本插件安装到本地仓库，在需要使用的项目的pom文件引入以下插件(使用时将&gt;全部替换成>,&lt;全部替换成<，这里为了是github显示做了处理)
&lt;build&gt;
	&lt;plugins&gt;
		&lt;plugin&gt;
			&lt;groupId&gt;com.ajie&lt;/groupId&gt;
			&lt;artifactId&gt;custom-maven-plugin&lt;/artifactId&gt;
			&lt;version&gt;1.0.10&lt;/version&gt;
			&lt;executions&gt;
				&lt;execution&gt;
					&lt;goals&gt;
						绑定的生命周期
						&lt;goal&gt;install&lt;/goal&gt;
					&lt;/goals&gt;
				&lt;/execution&gt;
			&lt;/executions&gt;
			&lt;configuration&gt;
				&lt;server&gt;
					&lt;host&gt;服务器主机&lt;/host&gt;
					&lt;username&gt;登录的用户名&lt;/username&gt;
					&lt;password&gt;登录密码&lt;/password&gt;
					&lt;port&gt;端口&lt;/port&gt;
					&lt;isupload&gt;是否执行自动上传并部署 true|false defalut false&lt;/isupload&gt;
				&lt;/server&gt;
			&lt;/configuration&gt;
		&lt;/plugin&gt;
	&lt;/plugins&gt;
&lt;/build&gt;
其中，server信息的配置除上述以外，还可以使用
&lt;configuration&gt;
	&lt;serverFile&gt;相对于classpath的路径&lt;/serverFile&gt;	
&lt;/configuration&gt;
但是，需要注意的是，serverFile指向的配置文件不是放在需要运行的项目，而是放在本插件的项目里
运行插件：custom:package（debug模式：custom:package -X）

