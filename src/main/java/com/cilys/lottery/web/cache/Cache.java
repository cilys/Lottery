package com.cilys.lottery.web.cache;

/**
 * Created by admin on 2020/6/27.
 */
public interface Cache<K, V> {
    void put(K k, V v);
    V get(K k);
    V remove(K k);
    void clear();
}
