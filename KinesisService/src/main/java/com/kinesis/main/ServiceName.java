package com.kinesis.main;

public enum ServiceName {
	SERVICE1, SERVICE2, SERVICE3, SERVICE4, SERVICE5, SERVICE6, SERVICE7, SERVICE8, SERVICE9, SERVICE10, SERVICE11, SERVICE12, SERVICE13, SERVICE14, SERVICE15;

	public static ServiceName getRandom() {
		return values()[(int) (Math.random() * values().length)];
	}
}
