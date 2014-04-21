package org.tendiwa.client;

public interface EntityFilter<T> {
public boolean check(T entity);
}
