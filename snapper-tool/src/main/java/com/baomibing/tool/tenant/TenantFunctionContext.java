package com.baomibing.tool.tenant;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Optional;

/**
 * TenantFunctionContext
 *
 * @author zening 2024/10/18 11:11
 * @version 1.0.0
 **/
public abstract class TenantFunctionContext {

    private static final TransmittableThreadLocal<TenantFunctionConsume> locale = new TransmittableThreadLocal<>();

    public static void putFunction(TenantFunctionConsume function) {
        if (!exist()) {
            locale.set(function);
        }
    }

    public static Optional<TenantFunctionConsume> currentFunction() {
        if (locale.get() == null) {
            return Optional.empty();
        }
        return Optional.of(locale.get());
    }

    public static boolean exist() {
        return currentFunction().isPresent();
    }

    public static void remove() {
        locale.remove();
    }
}
