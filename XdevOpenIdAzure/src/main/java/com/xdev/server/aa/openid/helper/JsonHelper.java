package com.xdev.server.aa.openid.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonHelper {

	private static Logger log = LoggerFactory.getLogger(JsonHelper.class);
	
	protected JsonHelper() {
	}
	
	public static JsonObject parseJSON(String rawjson)
	{
		JsonParser jsonParser = new JsonParser();
		JsonElement jse = jsonParser.parse(rawjson);
		
        if (jse.isJsonObject()) {
            JsonObject jObject = jse.getAsJsonObject();
            
            log.debug(jObject.toString());
            
            return jObject;
        }
		
		return null;
	}
	
	


}
