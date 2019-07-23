package com.autotest.model;

/**
 * KVOBJ
 * 
 * @author veaZhao
 *
 * @param <K>
 * @param <V>
 */
public class KVObject<K, V> {
	K key;
	V value;

	public KVObject() {

	}

	public KVObject(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

}
