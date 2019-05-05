package vip.ifmm.knapsack.packageScan;

import java.net.URL;

/**
 * author: mackyhuang
 * <p>email: mackyhuang@163.co </p>
 * <p>date: 2019/4/29 </p>
 */
public class ScannerHelper {
    private ScannerHelper() {

    }

    /**
     * 获取当前url的全名称 去除前缀
     * "file:/vip/ifmm/knapsack" - "/vip/ifmm/knapsack"
     * "jar:file:/vip/ifmm/knapsack.jar!/vip/ifmm" - "/vip/ifmm/knapsack.jar"
     * @param url url资源
     * @return 去除前缀后的结果
   */
    public static String getRootPath(URL url) {
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');

        if (-1 == pos) {
            return fileUrl;
        }

        return fileUrl.substring(5, pos);
    }

    /**
     * 将包名的点好分隔符 替换成 /分割
     * @param name 包名转路径
     * @return 路径信息
     */
    public static String transformPackageName(String name) {
        //第一个参数 正则表达式
        return name.replaceAll("\\.", "/");
    }

    /**
     * 截取类名 去除扩展名
     * "ScannerHelper.class" - "ScannerHelper"
     * @param name 文件名
     * @return 无后缀的文件名
     */
    public static String trimExtension(String name) {
        int pos = name.indexOf('.');
        if (-1 != pos) {
            return name.substring(0, pos);
        }

        return name;
    }

}

