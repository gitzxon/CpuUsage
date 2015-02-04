package com.example.leon.cpuusage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by leon on 15/1/7.
 */
public class CpuUtil {

    // top命令
    public static final String[] TOP = { "/system/bin/top", "-n", "1", "-s", "cpu", "-m", "10" };
    // screen record
    public static final String[] SCREEN_RECORD = {"/system/bin/screenrecord", "/sdcard/MyScreen.mp4"};


    /**
     * 执行一个shell命令，并返回字符串值
     *
     * @param cmd
     * 命令名称&参数组成的数组（例如：{"/system/bin/cat", "/proc/version"}）
     * @param workdirectory
     * 命令执行路径（例如："system/bin/"）
     * @return 执行结果组成的字符串
     * @throws IOException
     */
    public static synchronized String run(String[] cmd, String workdirectory)
            throws IOException {
        StringBuffer result = new StringBuffer();
        try {
            // 创建操作系统进程（也可以由Runtime.exec()启动）
            // Runtime runtime = Runtime.getRuntime();
            // Process proc = runtime.exec(cmd);
            // InputStream inputstream = proc.getInputStream();
            ProcessBuilder builder = new ProcessBuilder(cmd);

            InputStream in = null;
            // 设置一个路径（绝对路径了就不一定需要）
            if (workdirectory != null) {
                // 设置工作目录（同上）
                builder.directory(new File(workdirectory));
                // 合并标准错误和标准输出
                builder.redirectErrorStream(true);
                // 启动一个新进程
                Process process = builder.start();

                // 读取进程标准输出流
                in = process.getInputStream();
                byte[] re = new byte[1024];
                while (in.read(re) != -1) {
                    result = result.append(new String(re));
                }
            }
            // 关闭输入流
            if (in != null) {
                in.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result.toString();
    }



    // 现在执行top -n 1，我们只需要第二行（用第二行求得CPU占用率，精确数据）
    // 第一行：User 35%, System 13%, IOW 0%, IRQ 0% // CPU占用率
    // 第二行：User 109 + Nice 0 + Sys 40 + Idle 156 + IOW 0 + IRQ 0 + SIRQ 1 = 306
    // CPU使用情况
    public static synchronized String runCmd(String[] cmd) {
        String line = "";
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(cmd);
            is = proc.getInputStream();

            // 换成BufferedReader
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));

            while ((line = buf.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            if (is != null) {
                buf.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // 获取指定应用的top命令获取的信息
    // PID CPU% S #THR VSS RSS PCY UID Name // 进程属性
    // 如果当前应用不在运行则返回null
    public static synchronized String runCmd(String[] cmd, String pkgName) {
        String line = null;
        InputStream is = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(cmd);
            is = proc.getInputStream();

            // 换成BufferedReader
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            do {
                line = buf.readLine();
                // 读取到相应pkgName跳出循环（或者未找到）
                if (null == line || line.endsWith(pkgName)) {
                    break;
                }
            } while (true);

            if (is != null) {
                buf.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
}
