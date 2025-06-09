package com.main.ClassLoader;

import com.main.Subscriber.SubscriberInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for dynamically loading and instantiating subscriber classes at runtime using reflection.
 * <p>
 * Subscriber classes must implement the {@link SubscriberInterface} and reside in the
 * {@code com.main.Subscriber.Subscribers} package.
 */
public class SubscriberClassLoader {

    /**
     * The base package path where subscriber classes are expected to be located.
     */
    private static final String subscriberClassPath = "com.main.Subscriber.Subscribers.";

    /**
     * Logger instance for logging class loading actions and errors.
     */
    private static final Logger logger = LogManager.getLogger("SubscriberClassLoaderLogger");

    /**
     * Dynamically loads a subscriber class by its name and instantiates it with the given constructor parameters.
     *
     * @param subscriberName the name of the subscriber class (without package prefix)
     * @param paramTypes     the types of the constructor parameters
     * @param params         the actual constructor arguments
     * @return an instance of the subscriber class cast to {@link SubscriberInterface}
     * @throws RuntimeException if class loading or instantiation fails
     */
    public static SubscriberInterface loadSubscriber(String subscriberName,
                                                     Class<?>[] paramTypes,
                                                     Object[] params) {
        logger.info("Loading {} subscriber class", subscriberName);
        try {
            Class<?> loadedClass = Class.forName(subscriberClassPath + subscriberName);
            logger.info("Subscriber class loading for {} completed.", subscriberName);
            return (SubscriberInterface) loadedClass.getConstructor(paramTypes).newInstance(params);
        } catch (ClassNotFoundException | InvocationTargetException |
                 InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            logger.error("Subscriber class loading failed: {} {}", e.getMessage(), e);
            throw new RuntimeException("Subscriber class loading failed: " + e.getMessage(), e);
        }
    }
}
