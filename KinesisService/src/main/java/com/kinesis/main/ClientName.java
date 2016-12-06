package com.kinesis.main;

public enum ClientName {
	CLIENT1, CLIENT2, CLIENT3, CLIENT4, CLIENT5, CLIENT6, CLIENT7, CLIENT8, CLIENT9, CLIENT10, CLIENT11, CLIENT12, CLIENT13, CLIENT14, CLIENT15;

	public static ClientName getRandom() {
		return values()[(int) (Math.random() * values().length)];
	}
}
