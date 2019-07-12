package com.autotest.listener;

public abstract interface TestExcutorListener extends TestListener {
	public void onStart(Object object);
	public void onFinish(Object object);
}
