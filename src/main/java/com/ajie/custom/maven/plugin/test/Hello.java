package com.ajie.custom.maven.plugin.test;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * 最简单的Mojo
 * 
 * Mojo就是 Maven plain Old Java Object。每一个 Mojo 就是 Maven 中的一个执行目标（executable
 * goal），而插件则是对单个或多个相关的 Mojo 做统一分发。一个 Mojo 包含一个简单的 Java 类。插件中多个类似 Mojo
 * 的通用之处可以使用抽象父类来封装
 *
 * @author niezhenjie
 *
 */

@Mojo(name = "hello1")
public class Hello extends AbstractMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("hello world");

	}

}
