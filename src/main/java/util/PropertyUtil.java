package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {
    private static Properties properties;

    public static Properties getInstance(String fileName){
        if (properties == null){
            properties = new Properties();
        }
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        //InputStream in = PropertyUtil.class.getClassLoader().getResourceAsStream("pay.properties");
        // 使用properties对象加载输入流

        try {
            // 使用InPutStream流读取properties文件
            String proPath = System.getProperty("user.dir") + File.separator + fileName;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(proPath));
            properties.load(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
