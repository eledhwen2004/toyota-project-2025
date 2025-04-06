package com.main.ClassLoader;

import com.main.Subscriber.SubscriberInterface;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class SubscriberClassLoader {
    private static final String classpathRoot = System.getProperty("user.dir") + "toyota-main-rate/target/classes/";
    private static final String subscriberClassPath = "com.main.Subscriber.Subscribers.";

    public static SubscriberInterface loadSubscriber(String subscriberName){
        // Point to root of class files
        URL[] urls = null;
        try {
            urls = new URL[]{new URL("file://" + classpathRoot)};
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try (URLClassLoader loader = new URLClassLoader(urls)) {
            Class<?> loadedClass = loader.loadClass(subscriberClassPath + subscriberName);
            // Create instance and cast to SubscriberInterface
            return (SubscriberInterface) loadedClass.getDeclaredConstructor().newInstance();
        } catch (IOException | ClassNotFoundException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}

