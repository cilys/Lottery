package com.cilys.lottery.web.cache1;

/**
 * Created by admin on 2020/6/27.
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {
    @Override
    public void put(K k, V v) {

    }

    @Override
    public V get(K k) {
        return null;
    }

    @Override
    public V remove(K k) {
        return null;
    }

    @Override
    public void clear() {

    }
}