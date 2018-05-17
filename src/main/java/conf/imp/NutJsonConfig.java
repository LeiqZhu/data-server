package conf.imp;


import conf.BaseConfig;
import conf.NutConfFast;

import java.io.File;
import java.util.List;
import java.util.Map;

public class NutJsonConfig extends BaseConfig {

    public static NutJsonConfig newConfig(Map<String, Object> map) {
        NutJsonConfig conf = new NutJsonConfig();
        conf.setStore(map);
        return conf;
    }

    public NutJsonConfig() {

    }

    public NutJsonConfig(String fileName, String nodeName) {
        this();

        String pathOuter = System.getProperty("user.dir") + "/../" + fileName;
        String path = System.getProperty("user.dir") + "/" + fileName;

        File f = new File(pathOuter);
        if (f.exists()) {
            NutConfFast.load(pathOuter);
        }

        f = new File(path);
        if (f.exists()) {
            NutConfFast.load(path);
        }
        Object obj = nodeName == null ? NutConfFast.getAllConf() : NutConfFast.get(nodeName);
        if (obj != null) {
            this.setStore((Map<String, Object>) obj);
            return;
        }
        throw new RuntimeException("init " + fileName + " file and " + nodeName + " node failed");
    }

    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getJsonObject(String key, Map<String, T> def) {
        Map<String, T> val = (Map<String, T>) getStore().get(key);
        if (val != null) {
            return val;
        }
        return def;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getJsonArrays(String key, List<T> def) {
        List<T> val = (List<T>) getStore().get(key);
        if (val != null) {
            return val;
        }
        return def;
    }
}