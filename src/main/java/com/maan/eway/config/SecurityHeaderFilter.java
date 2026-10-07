package com.maan.eway.config;

import jakarta.servlet.Filter;

import jakarta.servlet.FilterChain;

import jakarta.servlet.FilterConfig;

import jakarta.servlet.ServletException;

import jakarta.servlet.ServletRequest;

import jakarta.servlet.ServletResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SecurityHeaderFilter implements Filter {

	@Override

	public void init(FilterConfig filterConfig) throws ServletException {

		// Initialization code if needed

	}

	@Override

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)

			throws IOException, ServletException {

		HttpServletResponse httpResponse = (HttpServletResponse) response;

		// Force the X-XSS-Protection header to the value required by VAPT

		httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

		// Continue with the next filter in the chain

		chain.doFilter(request, response);

	}

	@Override

	public void destroy() {

		// Cleanup code if needed

	}

}
