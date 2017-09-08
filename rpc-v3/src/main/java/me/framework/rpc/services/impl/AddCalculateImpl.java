package me.framework.rpc.services.impl;

import me.framework.rpc.services.AddCalculate;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class AddCalculateImpl implements AddCalculate {
    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
