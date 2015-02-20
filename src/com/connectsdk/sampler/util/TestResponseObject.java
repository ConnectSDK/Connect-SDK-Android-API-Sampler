package com.connectsdk.sampler.util;

public class TestResponseObject {

	public boolean isSuccess;	
	public int httpResponseCode;	
	public String responseMessage;
	
	
	public static final String Default = "default";
	public static final int SuccessCode = 200;
	
	public static final String Display_image = "ImageLaunched";
	public static final String Play_Video = "VideoLaunched";
	public static final String Play_Audio = "AudioLaunched";
	
	public static final String Closed_Media = "MediaClosed";
	public static final String Stopped_image = "ImageStopped";
	public static final String Paused_Media = "MediaPaused";
	public static final String Played_Media = "MediaPlayed";
	public static final String Rewind_Media = "MediaRewind";
	public static final String FastForward_Media = "MediaFastForward";
	
	
	
	
	
	
	
	public TestResponseObject() {
		super();
		this.isSuccess = false;
		this.httpResponseCode = 0;
		this.responseMessage = "default";
		
	}
	
	public TestResponseObject(boolean isSuccess, int httpResponseCode, String responseMessage) {
		super();
		this.isSuccess = isSuccess;
		this.httpResponseCode = httpResponseCode;
		this.responseMessage = responseMessage;
	}

	

	
	
	
}
