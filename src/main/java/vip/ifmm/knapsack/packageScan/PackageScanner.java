package vip.ifmm.knapsack.packageScan;

import java.io.IOException;
import java.util.List;

/**
 * author: mackyhuang
 * <p>email: mackyhuang@163.co </p>
 * <p>date: 2019/4/29 </p>
 */
public interface PackageScanner {
    public List<String> getFullyQualifiedClassNameList() throws IOException;
}
