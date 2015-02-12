package com.connectsdk.sampler.util;

public class TestResponseObject {

	public boolean isSuccess;	
	public int httpResponseCode;	
	public String responseMessage;
	
	public static final String display_image = "ImageDisplayed";
	
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
