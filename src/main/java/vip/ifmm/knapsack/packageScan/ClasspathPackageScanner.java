package vip.ifmm.knapsack.packageScan;

import vip.ifmm.knapsack.core.Knapsack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/29 </p>
 */
public class ClasspathPackageScanner implements PackageScanner {
    public static List<String> fullyQualifiedClassNameList = null;
    private String basePackage;
    private ClassLoader classLoader;
    /**
     * 初始化
     * @param basePackage
     */
    public ClasspathPackageScanner(String basePackage) {
        this.basePackage = basePackage;
        this.classLoader = getClass().getClassLoader();
    }
    public ClasspathPackageScanner(String basePackage, ClassLoader classLoader) {
        this.basePackage = basePackage;
        this.classLoader = classLoader;
    }
    /**
     *获取指定包下的所有字节码文件的全类名
     */
    public List<String> getFullyQualifiedClassNameList() throws IOException {
        //logger.info("开始扫描包{}下的所有类", basePackage);
        return doScan(basePackage, new ArrayList<String>());
    }

    /**
     * 遍历的主函数 根据jar和class后缀把遍历对象区分开来，使用递归的方式遍历包下的所有包和类
     */
    private List<String> doScan(String basePackage, List<String> nameList) throws IOException {
        String currenthPath = ScannerHelper.transformPackageName(basePackage);
        URL url = classLoader.getResource(currenthPath);
        String filePath = ScannerHelper.getRootPath(url);
        List<String> scanResultList = null;
        //若jar，则使用jar流递归遍历 否则使用文件方式遍历
       if (isJarFile(filePath)) {
            scanResultList = readFromJarFile(filePath, currenthPath);
        } else {
            scanResultList = readFromDirectory(filePath);
        }
       //遍历当前level的元素 如果是类就直接添加，否则 就代表它还是一个package,把它加入basePackage，递归
        for (String currentElem : scanResultList) {
            if (isClassFile(currentElem)) {
                nameList.add(toFullyQualifiedName(currentElem, basePackage));
            } else {
                doScan(basePackage + "." + currentElem, nameList);
            }
        }
        return nameList;
    }

    /**
     * 把当前类名 去除.class 加上包
     */
    private String toFullyQualifiedName(String className, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(ScannerHelper.trimExtension(className));
        return sb.toString();
    }

    /**
     * 使用jar流读取当前level的类
     */
    private List<String> readFromJarFile(String jarPath, String currentPackageName) throws IOException {
        JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jarIn.getNextJarEntry();
        List<String> nameList = new ArrayList<String>();
        //遍历获取当前level的类
        while (null != entry) {
            String name = entry.getName();
            if (name.startsWith(currentPackageName) && isClassFile(name)) {
                nameList.add(name);
            }
            entry = jarIn.getNextJarEntry();
        }
        return nameList;
    }

    /**
     * 用文件读取当前level的类
     */
    private List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] scanResultList = file.list();
        if (null == scanResultList) {
            return null;
        }
        return Arrays.asList(scanResultList);
    }

    /**
     * 通过是否以.class结尾来判断是否是class文件
     */
    private boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

    /**
     * 通过是否以.jar结尾来判断是否是class文件
     */
    private boolean isJarFile(String name) {
        return name.endsWith(".jar");
    }

    /**
     * 全限定类列表中是否 包含 某个类的简单类名
     */
    public static String isContain(String className){
        if (Knapsack.fullyQualifiedClassNameList != null){
            Iterator<String> iterator = Knapsack.fullyQualifiedClassNameList.iterator();
            while (iterator.hasNext()){
                String next = iterator.next();
                if (next.substring(next.lastIndexOf(".") + 1).equals(className)){
                    return next;
                }
            }
        }
        return null;
    }

}
