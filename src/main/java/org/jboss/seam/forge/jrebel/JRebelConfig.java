package org.jboss.seam.forge.jrebel;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.jboss.seam.forge.shell.ShellColor;
import org.jboss.seam.forge.shell.plugins.PipeOut;

public class JRebelConfig {

    public void writeTo(String rebelHome, String property, String value, PipeOut out) {
        try {
            File properties = resolveRebelProperties(rebelHome);
            if (!containsProperty(properties, property)) {
                FileUtils.fileAppend(properties.getAbsolutePath(), "\n" + property + "=" + value);
                out.println(ShellColor.GREEN, "***SUCCESS*** Updated JRebel config: " + property + " = " + value);
            } else {
                out.println(ShellColor.YELLOW, "***INFO*** JRebel config already contains " + property + ", skipping.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to write to jrebel.properties", e);
        }
    }

    private File resolveRebelProperties(String rebelHome) throws IOException {
        String path = null;
        if (!StringUtils.isEmpty(rebelHome)) {
            path = rebelHome;
        } else {
            path = System.getenv(JRebelPlugin.REBEL_ENV_HOME);
        }
        if (StringUtils.isEmpty(path) || !isValidRebelHome(path))
            throw new RuntimeException("Please provide either a rebel home or set it in your environment");
        File jrebel = new File(addConfigPath(path));
        if (!jrebel.exists()) {
            jrebel.createNewFile();
        }
        return jrebel;
    }
    
    private boolean isValidRebelHome(String path) {
        File dir = new File(path);
        return dir.exists() && dir.isDirectory();
    }

    private String addConfigPath(String rebelHome) {
        return rebelHome + File.separator + "conf" + File.separator + "jrebel.properties";
    }
    
    private boolean containsProperty(File properties, String name) throws IOException {
        String content = FileUtils.fileRead(properties);
        return content.contains(name + "=");
    }
    
}
