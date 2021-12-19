package com.wn.plugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.spi.SourcePlugin;
import com.wn.study.plugin.source.ClickhouseSource;
import com.wn.study.plugin.source.ElasticsearchSource;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.ServiceLoader;

public class PluginTest {

    /**
     * 两种方式加载
     * @throws Throwable
     */
    @Test
    public void testPluginLoader() throws Throwable {
        List<String> pluginPaths = Lists.newArrayList();
        pluginPaths.add(System.getProperty("user.dir") + File.separator + "plugins/plugin-source-test-1");
        pluginPaths.add(System.getProperty("user.dir") + File.separator + "plugins/plugin-source-test-2");
        for (String pluginPath : pluginPaths) {
            ClassLoader classLoader = getMyClassLoader(pluginPath);
            System.out.println("ServiceLoader load class" + pluginPath);
            ServiceLoader<SourcePlugin> serviceLoader = ServiceLoader.load(SourcePlugin.class, classLoader);
            List<SourcePlugin> demos = ImmutableList.copyOf(serviceLoader);
            SourcePlugin demo = demos.get(0);
            System.out.println(demo.generateData());
        }
        List<String> classNames = Lists.newArrayList();
        classNames.add("com.wn.study.plugin.source.ClickhouseSource");
        classNames.add("com.wn.study.plugin.source.ElasticsearchSource");

        for (int i = 0; i < classNames.size(); i++) {
            System.out.println("class load " + classNames.get(0));
            ClassLoader classLoader = getMyClassLoader(pluginPaths.get(i));
            Class clazz = classLoader.loadClass(classNames.get(i));
            SourcePlugin sourcePlugin = (SourcePlugin) clazz.newInstance();
            System.out.println(sourcePlugin.generateData());
        }
    }

    /**
     * there is NoSuchMethodError
     */
    @Test
    public void testNativeClassLoader() {
        SourcePlugin sourcePlugin = new ClickhouseSource();
        sourcePlugin.generateData();
        sourcePlugin = new ElasticsearchSource();
        sourcePlugin.generateData();
    }

    private ClassLoader getMyClassLoader(String pluginDir) throws MalformedURLException {
        ImmutableList<String> spiPackages = ImmutableList.<String>builder()
                .add("com.spi.SourcePlugin")
                .build();
        ClassLoader parent = getClass().getClassLoader();
        return new PluginClassLoader(pluginDir, parent, spiPackages);
    }

}
