package uk.ac.qub.eeecs.gage.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Object pool class, providing a reusable collection of objects of a specified
 * type
 *
 * @version 1.0
 */
public class Pool<T> {

    /**
     * Factor that can provide instances of the objects managed within this
     * pool.
     */
    public interface ObjectFactory<T> {
        T createObject();
    }

    /**
     * Object factory used to populate this pool
     */
    private final ObjectFactory<T> mFactory;

    /**
     * Object pool and maximum pool size constant
     */
    private final List<T> mPool;
    private final int mMaxPoolSize;

    /**
     * Create an object pool using the specified factory and maximum pool size.
     *
     * @param factory     Object factory used to populate the pool
     * @param maxPoolSize Maximum number of objects that can be stored in the pool
     */
    public Pool(ObjectFactory<T> factory, int maxPoolSize) {
        mFactory = factory;
        mMaxPoolSize = maxPoolSize;
        mPool = new ArrayList<>(mMaxPoolSize);
    }

    /**
     * Get an object instance.
     * <p>
     * Note: The object instance may be either a new instance or a 'reused'
     * instance taken from the pool. The callee should ensure that the object is
     * adequately initialised taking into account the object may contain 'dirty'
     * data.
     *
     * @return Object instance (potentially dirty)
     */
    public T get() {
        T object;

        if (mPool.isEmpty())
            object = mFactory.createObject();
        else
            object = mPool.remove(mPool.size() - 1);

        return object;
    }

    /**
     * Add the object to the pool.
     * <p>
     * Note: When added to the pool the callee should ensure that the object is
     * not further accessed through whatever reference was being used.
     *
     * @param object instance to be added to the pool
     */
    public void add(T object) {
        if (mPool.size() < mMaxPoolSize)
            mPool.add(object);
    }
}