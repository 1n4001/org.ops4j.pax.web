/**
 * 
 */
package org.ops4j.pax.web.extender.war.internal.parser;

import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.ops4j.pax.web.extender.war.internal.model.WebApp;
import org.ops4j.pax.web.extender.war.internal.model.WebAppInitParam;
import org.ops4j.pax.web.extender.war.internal.model.WebAppServlet;
import org.ops4j.pax.web.extender.war.internal.model.WebAppServletMapping;
import org.osgi.framework.Bundle;

/**
 * @author achim
 * 
 */
public class WebServletAnnotationScanner extends
		AnnotationScanner<WebServletAnnotationScanner> {

	public WebServletAnnotationScanner(Bundle bundle, String clazz) {
		super(bundle, clazz);
	}

	public void scan(WebApp webApp) {
		Class<?> clazz = loadClass();

		if (clazz == null) {
			log.warn("Class {} wasn't loaded", this.className);
			return;
		}

		if (!HttpServlet.class.isAssignableFrom(clazz)) {
			log.warn(clazz.getName()
					+ " is not assignable from javax.servlet.http.HttpServlet");
			return;
		}

		WebServlet annotation = (WebServlet) clazz
				.getAnnotation(WebServlet.class);

		if (annotation.urlPatterns().length > 0
				&& annotation.value().length > 0) {
			log.warn(clazz.getName()
					+ " defines both @WebServlet.value and @WebServlet.urlPatterns");
			return;
		}

		String[] urlPatterns = annotation.value();
		if (urlPatterns.length == 0) {
			urlPatterns = annotation.urlPatterns();
		}

		if (urlPatterns.length == 0) {
			log.warn(clazz.getName()
					+ " defines neither @WebServlet.value nor @WebServlet.urlPatterns");
			return;
		}

		String servletName = (annotation.name().equals("") ? clazz.getName()
				: annotation.name());

		WebAppServlet webAppServlet = webApp.findServlet(servletName);
		log.debug("Registering Servlet {} with url(s) {}", servletName,
				urlPatterns);

		if (webAppServlet == null) {
			// Add a new Servlet
			log.debug("Create a new Servlet");
			webAppServlet = new WebAppServlet();
			webAppServlet.setServletName(servletName);
			webAppServlet.setServletClassName(className);
			webApp.addServlet(webAppServlet);
			webAppServlet.setLoadOnStartup(annotation.loadOnStartup());
			webAppServlet.setAsyncSupported(annotation.asyncSupported());
			// TODO: what about the display Name
		}

		WebAppInitParam[] initParams = webAppServlet.getInitParams();
		// check if the existing servlet has each init-param from the
		// annotation
		// if not, add it
		for (WebInitParam ip : annotation.initParams()) {
			// if (holder.getInitParameter(ip.name()) == null)
			if (!initParamsContain(initParams, ip.name())) {
				WebAppInitParam initParam = new WebAppInitParam();
				initParam.setParamName(ip.name());
				initParam.setParamValue(ip.value());
				webAppServlet.addInitParam(initParam);
			}
		}
		// check the url-patterns, if there annotation has a new one, add it
		List<WebAppServletMapping> mappings = webApp
				.getServletMappings(servletName);

		log.debug("Found the following mappings {} for servlet: {}", mappings,
				servletName);

		// ServletSpec 3.0 p81 If a servlet already has url mappings from a
		// descriptor the annotation is ignored
		if (mappings == null || mappings.isEmpty()) {
			log.debug("alter/create mappings");
			for (String urlPattern : urlPatterns) {
				log.debug("adding mapping for URL {}", urlPattern);
				WebAppServletMapping mapping = new WebAppServletMapping();
				mapping.setServletName(servletName);
				mapping.setUrlPattern(urlPattern);
				webApp.addServletMapping(mapping);
			}
		}

		MultipartConfig multiPartConfigAnnotation = (MultipartConfig) clazz.getAnnotation(MultipartConfig.class);

		if (null != multiPartConfigAnnotation) {
			MultipartConfigElement multipartConfig = new MultipartConfigElement(multiPartConfigAnnotation);
			webAppServlet.setMultipartConfig(multipartConfig);
		}
		
		
	}

}
