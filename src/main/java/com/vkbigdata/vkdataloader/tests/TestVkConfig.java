package main.java.com.vkbigdata.vkdataloader.tests;

import static org.junit.Assert.*;
import org.junit.Test;
import main.java.com.vkbigdata.vkdataloader.VkConfig;

public class TestVkConfig {

	private VkConfig conf;
	private static String CONFIG_FILE = "TestConfig.txt";
	
	public TestVkConfig() {

		conf = new VkConfig(CONFIG_FILE);
	}
	
	@Test
	public void testReadConfig() {
		
		String testStringU = "{\"id\":56,\"title\":\"СПбПУ Петра Великого (Политех)\"}";
		String testStringF = "{\"response\":{\"count\":2,\"items\":[{\"id\":2157469,"
				+ "\"title\":\"Институт компьютерных наук и технологий (бывш. Технической кибернетики)\"},"
				+ "{\"id\":2157473,\"title\":\"Инженерно-экономический институт\"}]}}";
				
		conf.readConfig();
		if (!conf.APP_ID.equals("5075749")) {
			fail("Fail in APP_ID, expected:5075749, actual:" + conf.APP_ID);
		} else if (conf.universityNum != 1) {
			fail("Fail in universityNum, expected:1, actual:" + conf.universityNum);
		} else if (conf.yearFrom != 2010) {
			fail("Fail in yearFrom, expected:2010, actual:" + conf.yearFrom);
		} else if (conf.yearTo != 2015) {
			fail("Fail in yearTo, expected:2015, actual:" + conf.yearTo);
		} else if (!conf.universities[0].toString().equals(testStringU)) {
			fail("Fail in universities, expected:"
						+ testStringU
						+ ", actual:" + conf.universities[0].toString());
		} else if (!conf.faculties[0].toString().equals(testStringF)) {
			fail("Fail in faculties, expected:"
					+ testStringF
					+", actual:" + conf.faculties[0].toString());
		}
	}
	
	@Test
	public void testSetAccessToken() {
		
		conf.readConfig();
		
		String accessToken = "623a4bb18184f292277ade99c19186ab29551de747305b08fd5edb04e60a02dd0216ba93fb3b8fae296e4";
		conf.setAccessToken(accessToken);
		
		conf.readConfig();
		assertEquals(accessToken, conf.ACCESS_TOKEN);
	}
}
