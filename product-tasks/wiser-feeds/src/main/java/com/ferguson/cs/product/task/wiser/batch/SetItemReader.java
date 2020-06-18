package com.ferguson.cs.product.task.wiser.batch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.aop.support.AopUtils;
import org.springframework.batch.item.ItemReader;

public class SetItemReader<T> implements ItemReader<T> {

	private Set<T> set;

	public SetItemReader(Set<T> set) {
		// If it is a proxy we assume it knows how to deal with its own state.
		// (It's probably transaction aware.)
		if (AopUtils.isAopProxy(set)) {
			this.set = set;
		}
		else {
			this.set = new HashSet<>(set);
		}
	}

	@Override
	public T read() {
		Iterator<T> iterator = set.iterator();
		if (iterator.hasNext()) {
			T object = iterator.next();
			iterator.remove();
			return object;
		}
		return null;
	}

}
