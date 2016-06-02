package main.java.com.vkbigdata.vkdataloader.tests;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.junit.Test;
import main.java.com.vkbigdata.vkdataloader.VkDataLoader;

public class TestVkDataLoader {
	
	private static final Logger log = LogManager.getLogger(TestVkDataLoader.class.getSimpleName());
	
	@Test
	public void testParseStringOk() {
		
		log.trace("trace");
		log.debug("debug");
		log.info("info");
		log.warn("warn");
		log.error("error");
		log.fatal("fatal");
		
		String str = "{\"id\":1,\"title\":\"Россия\"}";
		
		JSONObject jsObj = VkDataLoader.parseString(str);
		assertTrue(str.equals(jsObj.toJSONString()));
	}
	
	@Test
	public void testParseStringFail() {
		
		String str = "{\"id\":1,\"title\":\"Россия}";

		assertNull(VkDataLoader.parseString(str));
	}

}
