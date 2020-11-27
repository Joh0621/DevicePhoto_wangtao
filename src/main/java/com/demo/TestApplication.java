//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

@SpringBootApplication
public class TestApplication {



    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);

           // 读取文件参数
        String path = System.getProperty("user.dir");
        System.out.println(path);
        List<String> lines = null;
        try {
            lines = Files.readAllLines((new File("src/main/resources/properties.txt")).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap();
        lines.stream().filter((x) -> {
            return !x.equals("") && !x.contains("#");
        }).forEach((x) -> {
            String var10000 = (String)map.put(x.split("=")[0].trim(), x.split("=")[1].trim());
        });

        int timeInterval = Integer.valueOf((String)map.get("timeInterval"));

        // 定时任务
        Timer timer = new Timer();
        timer.schedule(new Task(),0, timeInterval);
    }


}
