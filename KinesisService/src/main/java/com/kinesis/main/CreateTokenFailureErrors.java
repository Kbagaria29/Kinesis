package com.kinesis.main;

public enum CreateTokenFailureErrors {
	USERID_INVALID,
	CLIENT_INVALID,
	SERVICE_INVALID;
	
	public static CreateTokenFailureErrors getRandom() {
		return values()[(int) (Math.random() * values().length)];
	}
}
