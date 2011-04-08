package org.jboss.seam.forge.jrebel.container;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.facets.MavenCoreFacet;
import org.jboss.seam.forge.project.facets.builtin.MavenDependencyFacet;
import org.jboss.seam.forge.shell.plugins.PipeOut;

public abstract class CargoMavenPlugin extends BaseContainerMavenPlugin {
    
    public static final String GROUP_ID = "org.codehaus.cargo";
    public static final String ARTIFACT_ID = "cargo-maven2-plugin";
    public static final String DEPENDENCY = GROUP_ID + ":" + ARTIFACT_ID;
    
    @Inject
    private Project project;

    @Override
    public void updateConfig(Model pom, PipeOut out) {
        MavenCoreFacet facet = project.getFacet(MavenCoreFacet.class);
        try {
            for (Plugin plugin : pom.getBuild().getPlugins()) {
                if (isCargoPlugin(plugin)) {
                    addJRebelConfig(plugin, out);
                    facet.setPOM(pom);
                    return;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to update config", e);
        }
    }

    @Override
    public void addPlugin(Model pom, PipeOut out) {
        try {
            MavenCoreFacet facet = project.getFacet(MavenCoreFacet.class);
            MavenDependencyFacet dependencyFacet = project.getFacet(MavenDependencyFacet.class);
            Plugin plugin = addJRebelConfig(createContainerPlugin(dependencyFacet), out);
            pom.getBuild().getPlugins().add(plugin);
            facet.setPOM(pom);
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

    @Override
    protected String getName() {
        return "Cargo";
    }

    protected boolean isCargoPlugin(Plugin plugin) {
        String group = plugin.getGroupId();
        String artifact = plugin.getArtifactId();
        return DEPENDENCY.equals(group + ":" + artifact);
    }
    
    protected Plugin addJRebelConfig(Plugin plugin, PipeOut out) throws XmlPullParserException, IOException {
        checkRebelEnv(out);
        Xpp3Dom config = (Xpp3Dom) plugin.getConfiguration();
        Xpp3Dom startup = Xpp3DomBuilder.build(
                new ByteArrayInputStream(configString(config == null).getBytes()), "UTF-8");
        if (config != null) {
            config.addChild(startup);
        } else {
            plugin.setConfiguration(startup);
        }
        return plugin;
    }
    
    protected abstract String configString(boolean withConfig);

}
