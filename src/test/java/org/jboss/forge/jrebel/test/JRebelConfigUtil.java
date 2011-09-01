package org.jboss.forge.jrebel.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public final class JRebelConfigUtil {
    
    private static final String JREBEL_PROPERTIES_PATH = "./target/conf/jrebel.properties";
    
    private JRebelConfigUtil() {}

    public static void createEmptyConfigFile() throws Exception {
        FileUtils.forceMkdir(new File("./target/conf"));
        FileUtils.deleteQuietly(configFile());
        configFile().createNewFile();
    }
    
    public static String readConfigFile() throws IOException {
        FileInputStream fis = null;
        try {
            File config = configFile();
            fis = new FileInputStream(config);
            return IOUtils.toString(fis);
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    public static File rebelXml(File baseDir) {
        return new File(baseDir + dir() + "src" + dir() + "main" + dir() + "resources" + dir() + "rebel.xml");
    }
    
    public static File configFile() {
        return new File(JREBEL_PROPERTIES_PATH);
    }
    
    private static String dir() {
        return File.separator;
    }
}
