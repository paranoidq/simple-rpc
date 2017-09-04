package me.rpc.services.impl;

import me.rpc.services.Calculate;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class CalculateImpl implements Calculate {
    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
