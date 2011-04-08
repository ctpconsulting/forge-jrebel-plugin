package org.jboss.seam.forge.jrebel.container;

import org.jboss.seam.forge.jrebel.JRebelPlugin;

public class JBoss6MavenPlugin extends CargoMavenPlugin {

    @Override
    protected String configString(boolean withConfig) {
        StringBuilder builder = new StringBuilder();
        if (withConfig)
            builder.append("<configuration>\n");
        builder.append("<container>")
               .append("    <containerId>jboss6x</containerId>")
               .append("    <home>${env.JBOSS_HOME}</home>")
               .append("</container>");
        builder.append("<configuration>")
               .append("    <properties>")
               .append("        <cargo.jvmargs>")
               .append("-noverify -javaagent:${env." + JRebelPlugin.REBEL_ENV_HOME + "}" +
                       "/jrebel.jar -Xms256m -Xmx512m -XX:MaxPermSize=256m")
               .append("        </cargo.jvmargs>")
               .append("    </properties>")
               .append("</configuration>");
        if (withConfig)
            builder.append("\n</configuration>");
        return builder.toString();
    }

}
