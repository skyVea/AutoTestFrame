package com.autotest.listener;

public abstract interface TestRegionListener extends TestListener {
	public void onStart(Object object);

	public void onFinish(Object object);
}
