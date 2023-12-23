package org.ustc.common.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Random;

/**
 * ClassName: ClientUtil
 * Package: org.ustc.common.utils
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */
public class ClientUtil {

    public static String getRandomName() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        char[] str = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        int count = 0;
        while (count < 10) {
            int i = Math.abs(random.nextInt(str.length));
            stringBuilder.append(str[i]);
            count++;
        }
        return stringBuilder.toString();
    }

    public static int getCpuNumber() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        return operatingSystemMXBean.getAvailableProcessors();
    }

    public static String getOS() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        return operatingSystemMXBean.getName();
    }

    public static double getLoad() {
        Random random = new Random();
        return random.nextInt(100);
    }
}
