package com.main.ClassLoader;

import com.main.Subscriber.SubscriberInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

public class SubscriberClassLoader {
    private static final String subscriberClassPath = "com.main.Subscriber.Subscribers.";
    private static final Logger logger = LogManager.getLogger("SubscriberClassLoaderLogger");

    public static SubscriberInterface loadSubscriber(String subscriberName,
                                                     Class<?>[] paramTypes,
                                                     Object[] params) {
        logger.info("Loading {} subscriber class ",subscriberName);
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
