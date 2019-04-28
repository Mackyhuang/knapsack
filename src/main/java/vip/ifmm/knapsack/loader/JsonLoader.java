package vip.ifmm.knapsack.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/28 </p>
 */
public class JsonLoader {

    public static void loader(String path, Charset charset){
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(path), charset);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
