package com.autotest.listener;

public abstract interface TestModuleListener extends TestListener {
	public void onStart(Object object);
	public void onFinish(Object object);
}
