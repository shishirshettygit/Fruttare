package com.fruttare.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Commons {

	public final static String OAUTH_CONSUMER = "kifYJh4RMpKJch9avmW7Uw"; //oauth key consumer maushi
	public final static String OAUTH_SECRET = "SDMq2C1zkzdUEee3FaWavx6kshuyxiUOx23HqtXsY";
	
	public final static String ERROR_MESSAGE="Error occured while uploading photo";
	public final static String CHARACTER_EXCEED_ERROR="You have exceeded 140 characters.Remove a Twitter handle and try again.";
	public final static String TWITTER_STRING=" get real fruit where they least expect it #FruttareSA";
	public final static String SUCCESS_POST="Image successfully posted on Twitter";
	
	
	public static String getTodaysDate() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter= new SimpleDateFormat("dd-MMM-yyyy-HH-mm-ss");
		String dateNow = formatter.format(currentDate.getTime());
		return dateNow;

	}
}
