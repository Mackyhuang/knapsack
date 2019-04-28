package vip.ifmm.knapsack.loader;

import vip.ifmm.knapsack.exception.ResourceLoadException;

import java.io.*;
import java.util.Properties;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/28 </p>
 */
public class PropertiesLoader {

    public static Properties loader(String path){
        Properties properties = null;
        try {
            InputStream resourceStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(path);
            properties = new Properties();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceStream));
            properties.load(bufferedReader);
            resourceStream.close();
        } catch (IOException e) {
            throw new ResourceLoadException(String.format("%s 资源载入失败", path), e);
        }
        return properties;
    }
}
