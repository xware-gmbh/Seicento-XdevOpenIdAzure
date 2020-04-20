package com.xdev.server.aa.openid.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinSession;

public class StoreHelper {
	
	private static final Logger log = LoggerFactory.getLogger(StoreHelper.class);
	
	public static final String SETTINGS = "oaparamstore";

	private StoreHelper() {
	}

	public static void saveInSessionStore(HttpSession session, String param, Object value) {
		Objects.requireNonNull(session, "HttpSession cannot be null!");
		
		Map<String, Object> sessionParamStore = getParamMapFromSession(session);
		sessionParamStore.put(param, value);

		session.setAttribute(SETTINGS, sessionParamStore);
	}
	
	public static void saveInSessionStore(VaadinSession session, String param, Object value) {
		Objects.requireNonNull(session, "HttpSession cannot be null!");
		
		Map<String, Object> sessionParamStore = getParamMapFromSession(session);
		sessionParamStore.put(param, value);

		session.setAttribute(SETTINGS, sessionParamStore);
	}	
	
	
	
	public static void saveInContextStore(ServletContext context, String param, Object value) {
		Objects.requireNonNull(context, "ServletContext cannot be null!");
		
		Map<String, Object> contextParamStore = getParamMapFromContext(context);
		contextParamStore.put(param, value);
		
		context.setAttribute(SETTINGS, contextParamStore);
	}
	
	public static void saveInContextStore(ServletRequest request, String param, Object value) {
		Objects.requireNonNull(request, "ServletRequest cannot be null!");
		saveInContextStore(request.getServletContext(), param, value);
	}
	
	
	public static Object getParamFromSessionStore(VaadinSession session, String param)
	{
		Objects.requireNonNull(session, "VaadinSession cannot be null!");
		Map<String, Object> sessionParamStore = getParamMapFromSession(session);
		
		return sessionParamStore.get(param);
	}
	
	public static Object getParamFromSessionStore(HttpSession session, String param)
	{
		Objects.requireNonNull(session, "HttpSession cannot be null!");
		Map<String, Object> sessionParamStore = getParamMapFromSession(session);
		
		return sessionParamStore.get(param);
	}
	
	public static Object getParamFromContextStore(ServletContext context, String param)
	{
		Objects.requireNonNull(context, "ServletContext cannot be null!");
		Map<String, Object> contextParamStore = getParamMapFromContext(context);
		
		return contextParamStore.get(param);
	}	
	
	
	
	// Additional
	
	@SuppressWarnings("unchecked")
	protected static Map<String, Object> getParamMapFromSession(HttpSession session)
	{
		Object sessionParamStore = session.getAttribute(SETTINGS);

		if (sessionParamStore == null) {
			sessionParamStore = new ConcurrentHashMap<String, Object>();
		}
		
		return ((Map<String, Object>) sessionParamStore);
	}
	
	@SuppressWarnings("unchecked")
	protected static Map<String, Object> getParamMapFromSession(VaadinSession session)
	{
		Object sessionParamStore = session.getAttribute(SETTINGS);

		if (sessionParamStore == null) {
			sessionParamStore = new ConcurrentHashMap<String, Object>();
		}
		
		return ((Map<String, Object>) sessionParamStore);
	}	
	
	
	
	@SuppressWarnings("unchecked")
	protected static Map<String, Object> getParamMapFromContext(ServletContext context)
	{
		Object contextParamStore = context.getAttribute(SETTINGS);

		if (contextParamStore == null) {
			contextParamStore = new ConcurrentHashMap<String, Object>();
		}
		
		return ((Map<String, Object>) contextParamStore);
	}

	public static Properties readConfigFile(ServletContext context, String fileName) {
		Properties settings = new Properties();
		try {
			if (fileName != null)
			{
				InputStream fileStream = context.getResourceAsStream("/WEB-INF/"+fileName);
				settings.load(fileStream);
				fileStream.close();
			}

		} catch (IOException e) {

			log.error("Fehler beim Einlesen der Datei \"" + fileName + "\"", e);

		}
		
		return settings;
	}
	
	public static Properties readConfigFile(File fileName) {
		return readConfigFile(fileName.toPath());
	}
	
	public static Properties readConfigFile(Path filePath)
	{
		Properties settings = new Properties();
		try {
			if (filePath != null)
			{
				InputStream fileStream = Files.newInputStream(filePath);
				settings.load(fileStream);
				fileStream.close();
			}

		} catch (IOException e) {

			log.error("Fehler beim Einlesen der Datei \"" + filePath.toString() + "\"", e);

		}
		
		return settings;
	}

	public static Properties readConfigFile(Object propertyFileLocation) {
		Properties settings = new Properties();
		if (propertyFileLocation == null)
		{
			return settings;
		}
		if (propertyFileLocation instanceof Path)
		{
			return readConfigFile((Path) propertyFileLocation);
		}
		if (propertyFileLocation instanceof File)
		{
			return readConfigFile((File) propertyFileLocation);
		}
		if (propertyFileLocation instanceof String)
		{
			return readConfigFile((String) propertyFileLocation);
		}
		return settings;
	}
	

}
