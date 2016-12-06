package com.kinesis.main;

public enum ValidateTokenFailureErrors {
	USERID_INVALID,
	INVALID_TOKEN,
	TOKEN_EXPIRED;

	public static ValidateTokenFailureErrors getRandom() {
		return values()[(int) (Math.random() * values().length)];
	}
}
