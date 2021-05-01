package cn.tee3.n2m.ui.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class StringUtils {
    public static boolean isEmpty(String str) {
        if (str != null && !"".equalsIgnoreCase(str.trim())
                && !"null".equalsIgnoreCase(str.trim())) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isNotEmpty(String string) {
        return null != string && string.length() > 0 && !"".equals(string);
    }

    /**
     * 获取网络的时时网速，使用方法是：
     * 每隔一段时间读取一次总流量，然后用本次和前一次的差除以间隔时间来获取平均速度，再换算为 K/s M/s
     * 等单位，显示即可。
     *
     * @return 实时的网速（单位byte）
     */
    public static int getNetSpeedBytes() {
        String line;
        String[] segs;
        int received = 0;
        int i;
        int tmp = 0;
        boolean isNum;
        try {
            FileReader fr = new FileReader("/proc/net/dev");
            BufferedReader in = new BufferedReader(fr, 500);
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("rmnet") || line.startsWith("eth") || line.startsWith("wlan")) {
                    segs = line.split(":")[1].split(" ");
                    for (i = 0; i < segs.length; i++) {
                        isNum = true;
                        try {
                            tmp = Integer.parseInt(segs[i]);
                        } catch (Exception e) {
                            isNum = false;
                        }
                        if (isNum == true) {
                            received = received + tmp;
                            break;
                        }
                    }
                }
            }
            in.close();
        } catch (IOException e) {
            return -1;
        }
        return received;
    }
}
