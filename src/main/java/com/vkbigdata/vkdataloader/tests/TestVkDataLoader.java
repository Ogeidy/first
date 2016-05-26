package main.java.com.vkbigdata.vkdataloader.tests;

import static org.junit.Assert.*;
import org.json.simple.JSONObject;
import org.junit.Test;
import main.java.com.vkbigdata.vkdataloader.VkDataLoader;

public class TestVkDataLoader {
	
	@Test
	public void testParseStringOk() {
		
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
