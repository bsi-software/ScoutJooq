package com.acme.application.shared.common;

import java.time.ZonedDateTime;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.application.shared.common.DateTimeUtility;

public class DateTimeUtilityTest {

	private static final Logger LOG = LoggerFactory.getLogger(DateTimeUtilityTest.class);
	
	public static String SOME_TIME_STRING = "2017-09-15T14:22:18.622Z";
	public static ZonedDateTime SOME_TIME_OBJECT = DateTimeUtility.convert(SOME_TIME_STRING);
	
	@Test
	public void toStringTest() {
		ZonedDateTime time = DateTimeUtility.nowInUtc();
		LOG.info("Default toString() '" + time.toString() + "'");
		LOG.info("Custom toStringInUtc() '" + DateTimeUtility.convert(time) + "'");
	}
	
	@Test
	public void toTimeTest() {
		String time = "2017-09-15T14:22:18.622Z";
		LOG.info("Default parsed '" + ZonedDateTime.parse(time) + "'");
		LOG.info("Custom parsedInUtc() '" + DateTimeUtility.convert(time) + "'");
	}
	
	@Test
	public void simpleConversionTest() {
		
		// use static test data
		assertEquals("Time strings don't match", SOME_TIME_STRING, DateTimeUtility.convert(SOME_TIME_OBJECT));
		assertEquals("Time objects not equal", DateTimeUtility.convert(SOME_TIME_STRING), SOME_TIME_OBJECT);
		assertTrue("Time objects not working with isEqual()", DateTimeUtility.convert(SOME_TIME_STRING).isEqual(SOME_TIME_OBJECT));
		
		// generate some current test data
		ZonedDateTime now = DateTimeUtility.nowInUtc();
		String nowString = DateTimeUtility.convert(now);
		ZonedDateTime nowTime = DateTimeUtility.convert(nowString);
		
		assertEquals("Now strings don't match", nowString, DateTimeUtility.convert(nowTime));
		assertEquals("Now objects not equal", DateTimeUtility.convert(nowString), nowTime);
		assertTrue("Now objects not working with isEqual()", DateTimeUtility.convert(nowString).isEqual(nowTime));
	}
	
	@Test
	public void someTestWithDifferentTimeZones() {
		// TODO implement some tests
	}
}
