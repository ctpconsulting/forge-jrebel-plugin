package org.jboss.forge.jrebel.container;

import static org.jboss.forge.jrebel.util.ProjectUtils.createDom;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jboss.forge.jrebel.JRebelPlugin;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.shell.ShellMessages;

public abstract class CargoMavenPlugin extends BaseContainerMavenPlugin {
    
    public static final String GROUP_ID = "org.codehaus.cargo";
    public static final String ARTIFACT_ID = "cargo-maven2-plugin";
    public static final String PREFERRED_VERSION = "1.1.2";
    public static final String DEPENDENCY = GROUP_ID + ":" + ARTIFACT_ID;

    @Override
    public void updateConfig(Model pom) {
        try {
            MavenCoreFacet facet = getProject().getFacet(MavenCoreFacet.class);
            for (Plugin plugin : pom.getBuild().getPlugins()) {
                if (isCargoPlugin(plugin)) {
                    addJRebelConfig(plugin);
                    facet.setPOM(pom);
                    return;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to update config", e);
        }
    }

    @Override
    public void addPlugin(Model pom) {
        try {
            MavenCoreFacet facet = getProject().getFacet(MavenCoreFacet.class);
            Plugin plugin = addContainerPlugin(pom, GROUP_ID, ARTIFACT_ID, PREFERRED_VERSION);
            addJRebelConfig(plugin);
            facet.setPOM(pom);
            ShellMessages.success(getShell(), "Added " + getName() + " container");
        } catch (Exception e) {
            throw new RuntimeException("Failed to add plugin", e);
        }
    }

    @Override
    protected String getGroupId() {
        return GROUP_ID;
    }

    @Override
    protected String getArtifactId() {
        return ARTIFACT_ID;
    }

    protected boolean isCargoPlugin(Plugin plugin) {
        String group = plugin.getGroupId();
        String artifact = plugin.getArtifactId();
        return DEPENDENCY.equals(group + ":" + artifact);
    }
    
    protected Plugin addJRebelConfig(Plugin plugin) {
        checkRebelEnv();
        Xpp3Dom config = pluginConfig(plugin);
        addCargoConfig(config);
        addContainer(config);
        plugin.setConfiguration(config);
        return plugin;
    }
    
    protected Xpp3Dom pluginConfig(Plugin plugin) {
        if (hasConfig(plugin))
            return (Xpp3Dom) plugin.getConfiguration();
        return createDom("<configuration></configuration>");
    }
    
    protected Xpp3Dom addCargoConfig(Xpp3Dom parent) {
        Xpp3Dom[] containers = parent.getChildren("configuration");
        if (containers != null && containers.length > 0)
            return parent;
        StringBuilder builder = new StringBuilder();
        builder.append("<configuration>")
               .append("    <type>standalone</type>")
               .append("    <properties>")
               .append("        <cargo.jvmargs>")
               .append("-noverify -javaagent:${env." + JRebelPlugin.REBEL_ENV_HOME + "}" +
                       "/jrebel.jar -Xms256m -Xmx512m -XX:MaxPermSize=256m")
               .append("        </cargo.jvmargs>")
               .append("    </properties>")
               .append("</configuration>");
        parent.addChild(createDom(builder.toString()));
        return parent;
    }
    
    protected Xpp3Dom addContainer(Xpp3Dom parent) {
        Xpp3Dom[] containers = parent.getChildren("container");
        for (int i = 0; containers != null && i < containers.length; i++) {
            Xpp3Dom id = containers[i].getChild("containerId");
            if (getContainerId().equals(id.getValue()))
                return parent;
        }
        parent.addChild(createDom(buildContainerString()));
        return parent;
    }
    
    protected String buildContainerString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<container>")
               .append("    <containerId>").append(getContainerId()).append("</containerId>")
               .append("    <home>").append(getContainerHome()).append("</home>")
               .append("</container>");
        return builder.toString();
    }
    
    protected abstract String getContainerId();
    
    protected abstract String getContainerHome();

}
