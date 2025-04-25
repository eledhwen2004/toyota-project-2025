package com.main.ClassLoader;

import com.main.Subscriber.SubscriberInterface;

import java.lang.reflect.InvocationTargetException;

/**
 * The SubscriberClassLoader is responsible for dynamically loading subscriber classes
 * based on the subscriber name at runtime. It loads the class files from the classpath
 * and uses reflection to instantiate and return a SubscriberInterface implementation.
 */
public class SubscriberClassLoader {

    // Root directory for class files
    private static final String classpathRoot = System.getProperty("user.dir") + "/target/classes/";
    // Path to subscriber classes
    private static final String subscriberClassPath = "com.main.Subscriber.Subscribers.";

    /**
     * Loads a subscriber class by its name, using the provided constructor parameter types
     * and parameters. This method loads the class from the classpath and instantiates it
     * using reflection.
     *
     * @param subscriberName The name of the subscriber class to load.
     * @param paramTypes The types of the constructor parameters.
     * @param params The parameters to pass to the constructor.
     * @return An instance of the SubscriberInterface implementation.
     * @throws RuntimeException If an error occurs during class loading or instantiation.
     */

    public static SubscriberInterface loadSubscriber(String subscriberName,
                                                     Class<?>[] paramTypes,
                                                     Object[] params) {
        Class<?> loadedClass = null;
        try {
            loadedClass = Class.forName(subscriberClassPath + subscriberName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            return (SubscriberInterface) loadedClass.getConstructor(paramTypes).newInstance(params);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

}
