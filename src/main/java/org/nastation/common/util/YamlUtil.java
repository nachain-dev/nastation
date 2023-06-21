package org.nastation.common.util;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class YamlUtil {

    private static Map<String, Map<String, Object>> properties = new HashMap<>();

    static {
        Yaml yaml = new Yaml();

        try{
            //InputStream in = YamlUtil.class.getClassLoader().getResource("/application.yaml").openStream();
            //properties = yaml.loadAs(in, HashMap.class);
        } catch (Exception e) {
            log.error("Load resource 'application.yml' error", e);
        }
    }

    public static Object getByPath(String path) {
        String separator = ".";
        String[] separatorKeys = null;
        if (path.contains(separator)) {
            separatorKeys = path.split("\\.");
        } else {
            return properties.get(path);
        }
        Map<String, Map<String, Object>> finalValue = new HashMap<>();
        for (int i = 0; i < separatorKeys.length - 1; i++) {
            if (i == 0) {
                finalValue = (Map) properties.get(separatorKeys[i]);
                continue;
            }
            if (finalValue == null) {
                break;
            }
            finalValue = (Map) finalValue.get(separatorKeys[i]);
        }
        return finalValue == null ? null : finalValue.get(separatorKeys[separatorKeys.length - 1]);
    }

    public static void main(String[] args) {

        Object byPath = YamlUtil.getByPath("spring.profiles.active");

        System.out.println(
                byPath
        );
    }


}