package com.ajie.custom.maven.plugin.test;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 测试传参
 *
 * @author niezhenjie
 *
 */

@Mojo(name = "param")
public class Paramters extends AbstractMojo {

	@Parameter(property = "name", defaultValue = "ajie")
	private String name;

	@Parameter(property = "greet", defaultValue = "hello")
	private String greet;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info(name + " say " + greet);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGreet() {
		return greet;
	}

	public void setGreet(String greet) {
		this.greet = greet;
	}

}
