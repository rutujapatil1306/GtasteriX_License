package com.spring.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.spring.jwt")
public class JwtWithSpringSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtWithSpringSecurityApplication.class, args);

		System.out.println("\n\n");

		System.err.println("  *****    *******  *******       *****   *******    *****    ******   *******" );
		System.err.println(" *     *   *      *    *         *           *      *     *   *     *     *   " );
		System.err.println("*       *  *      *    *         *           *     *       *  *     *     *   " );
		System.err.println("*       *  *******     *          *****      *     *       *  ******      *   " );
		System.err.println("*********  *           *               *     *     *********  *   *       *   " );
		System.err.println("*       *  *           *               *     *     *       *  *    *      *   " );
		System.err.println("*       *  *        *******       *****      *     *       *  *     *     *   " );
	}

}
