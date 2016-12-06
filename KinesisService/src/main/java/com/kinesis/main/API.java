package com.kinesis.main;

public enum API {
	    CreateTokenFromUserIdSuccess,
	    CreateTokenFromUserIdFailure,
	    ValidateTokenSuccess,
	    ValidateTokenFailure,
	    RevokeTokenSuccess,
	    RevokeTokenFailure;

	    public static API getRandom() {
	        return values()[(int) (Math.random() * values().length)];
	    }
}
