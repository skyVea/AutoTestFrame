package com.autotest.utils;

import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * ID生成器，生成的ID不重复，可作为主键ID使用
 * 
 * @author veaZhao
 *
 */
public class IDGenerator {
	protected Logger logger = LoggerFactory.getLogger(super.getClass());

	protected static int casheSize = 100;

	protected static ArrayBlockingQueue<String> cache = new ArrayBlockingQueue(casheSize);

	protected long seqMaxVal = 999990L;

	protected int seqValLen = 6;

	protected int randomMaxVal = 90;
	protected int randomValLen = 2;

	protected static AtomicInteger instanceCount = new AtomicInteger(1);
	protected static AtomicLong globalCount = new AtomicLong(1);
	protected SimpleDateFormat dateTimeFormater = new SimpleDateFormat("yyMMddHHmmss");
	static String dateTime;

	public IDGenerator() {
	}

	public String nextId() {
		synchronized (IDGenerator.class) {
			String id = (String) cache.poll();
			if (id != null) {
				return id;
			}
			batchCache();
			return nextId();
		}
	}

	private void batchCache() {
		cache.clear();
		for (int i = 0; i < casheSize; ++i) {
			String id = generate(i);
			if (!(cache.offer(id)))
				return;
		}
	}

	protected String generate(int order) {
		long seq = globalCount.incrementAndGet();
		if (seq >= this.seqMaxVal) {
			checkIllegal();
			dateTime = this.dateTimeFormater.format(Long.valueOf(System.currentTimeMillis()));
			globalCount.set(1);
			seq = globalCount.incrementAndGet();
		}
		String date = null;

		date = this.dateTimeFormater.format(Long.valueOf(System.currentTimeMillis()));
		String randStr = "";
		if (this.randomMaxVal > 0) {
			randStr = format(this.randomValLen, 0, "0");
		}

		String seqStr = format(this.seqValLen, seq, "0");

		return date + randStr + seqStr;
	}

	private void checkIllegal() {
		String currentDateTime = this.dateTimeFormater.format(Long.valueOf(System.currentTimeMillis()));
		if ((!(StringUtils.isEmpty(dateTime))) && (dateTime.equals(currentDateTime)))
			this.logger.warn("NUIDGenerator生成器在当前时间【" + currentDateTime + "】已生成ID超过百万,会造成ID号重复");
	}

	private String format(int valLength, long value, String str) {
		String returnValue = "";
		long randLength = value + "".length();
		if (randLength < valLength) {
			String tmp = "";
			for (int i = 0; i < valLength - randLength; ++i) {
				tmp = tmp + str;
			}
			returnValue = tmp + value;
		} else {
			returnValue = value + "";
		}

		return returnValue;
	}
}
