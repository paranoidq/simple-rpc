package me.srpc.serialize.protostuff;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class SchemaCache {
    private static final Logger logger = LoggerFactory.getLogger(SchemaCache.class);

    private static class SchemaCacheHolder {
        private static SchemaCache cache = new SchemaCache();
    }


    public static SchemaCache getInstance() {
        return SchemaCacheHolder.cache;
    }

    private Cache<Class<?>, Schema<?>> cache = CacheBuilder.newBuilder()
        .maximumSize(1024).expireAfterWrite(1, TimeUnit.HOURS)
        .build();

    private Schema<?> get(final Class<?> cls, Cache<Class<?>, Schema<?>> cache) {
        try {
            return cache.get(cls, new Callable<Schema<?>>() {
                public Schema<?> call() throws Exception {
                    return RuntimeSchema.createFrom(cls);
                }
            });
        } catch (ExecutionException e) {
            logger.error("Cannot get ProtoStuff Schema", e);
            return null;
        }
    }

    public Schema<?> get(final Class<?> cls) {
        return get(cls, cache);
    }
}
