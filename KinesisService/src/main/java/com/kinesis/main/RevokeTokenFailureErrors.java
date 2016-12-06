package com.kinesis.main;

public enum RevokeTokenFailureErrors {
	USERID_INVALID,
	INVALID_TOKEN;

	public static RevokeTokenFailureErrors getRandom() {
		return values()[(int) (Math.random() * values().length)];
	}
}
