package com.ajie.custom.maven.plugin.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 *
 * @author niezhenjie
 *
 */
public class Main {

	public static void main(String[] args) {
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("properties/server1.properties/");
		Properties pro = new Properties();
		try {
			pro.load(is);
			System.out.println(pro.getProperty("host"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
