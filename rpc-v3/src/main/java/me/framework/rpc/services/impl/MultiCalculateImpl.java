package me.framework.rpc.services.impl;

import me.framework.rpc.services.MultiCalculate;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MultiCalculateImpl implements MultiCalculate {
    @Override
    public int multi(int a, int b) {
        return a * b;
    }
}
