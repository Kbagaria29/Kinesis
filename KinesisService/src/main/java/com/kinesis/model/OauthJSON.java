package com.kinesis.model;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OauthJSON {

	private final static ObjectMapper JSON = new ObjectMapper();
	static {
		JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private String Api;
	private String clientName;
	private String serviceName;
	private int userId;
	private String TokenId;
	private String Error;
    private String tickerSymbol;

	public byte[] toJsonAsBytes() {
		try {
			return JSON.writeValueAsBytes(this);
		} catch (IOException e) {
			return null;
		}
	}

	public static OauthJSON fromJsonAsBytes(byte[] bytes) {
		try {
			return JSON.readValue(bytes, OauthJSON.class);
		} catch (IOException e) {
			return null;
		}
	}
}
