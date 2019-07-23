package com.autotest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表头属性
 * 
 * @author veaZhao
 */
@Target(value = { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	String columName();// 表的字段名

	/**
	 * @return 是否作为主键
	 */
	boolean isPrimary() default false; // 是否为主键

	String type() default "varchar";

	/**
	 * @return 长度，做校验用
	 */
	int length() default 100;

	/**
	 * @return 是否必填，做校验用
	 */
	boolean isRequired() default false;

	/**
	 * @return 是否自增长，常用做主键
	 */
	boolean isIncrement() default false;

	/**
	 * @return 是否只读
	 */
	boolean isReadOnly() default true;

	/**
	 * @return 是否忽略检查，其他所有配置
	 */
	boolean isIgnore() default true;
}