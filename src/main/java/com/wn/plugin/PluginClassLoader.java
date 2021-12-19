package com.wn.plugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class PluginClassLoader extends URLClassLoader {

    private final ClassLoader spiClassLoader;
    /**
     * spi加载类加载的包
     */
    private final List<String> spiPackages;

    public PluginClassLoader(
            String baseDir,
            ClassLoader spiClassLoader,
            Iterable<String> spiPackages) {
        super(getURLs(baseDir), null);

        this.spiClassLoader = spiClassLoader;
        this.spiPackages = ImmutableList.copyOf(spiPackages);
    }

    private static URL[] getURLs(String baseDir) {
        List<URL> list = Lists.newArrayList();
        for (File f : new File(baseDir).listFiles()) {
            try {
                list.add(new URL("file", null, f.getCanonicalPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        URL[] res = new URL[list.size()];
        return list.toArray(res);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            /**
             * Check if class is in the loaded classes cache
             */
            Class<?> cachedClass = findLoadedClass(name);
            if (cachedClass != null) {
                return resolveClass(cachedClass, resolve);
            }

            /**
             * Check if class is in the loaded classes cache
             */
            if (isSpiClass(name)) {
                return resolveClass(spiClassLoader.loadClass(name), resolve);
            }

            /**
             * Look for class locally
             */
            return super.loadClass(name, resolve);
        }
    }

    private boolean isSpiClass(String name) {
        // todo maybe make this more precise and only match base package
        return spiPackages.stream().anyMatch(name::startsWith);
    }

    private Class<?> resolveClass(Class<?> clazz, boolean resolve) {
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }
}
