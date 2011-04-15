package org.jboss.seam.forge.jrebel.container;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.inject.Inject;
import org.apache.maven.model.Model;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.jboss.seam.forge.jrebel.JRebelPlugin;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.facets.DependencyFacet;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.ShellMessages;
import org.jboss.seam.forge.shell.plugins.PipeOut;

public abstract class BaseContainerMavenPlugin implements ContainerMavenPlugin {
    
    @Inject 
    protected Shell shell;
    
    @Inject
    protected Project project;

    protected Plugin addContainerPlugin(Model pom, DependencyFacet dependencyFacet) {
        List<Dependency> dependencies = dependencyFacet.resolveAvailableVersions(
                DependencyBuilder.create()
                        .setGroupId(getGroupId())
                        .setArtifactId(getArtifactId()));
        int choice = shell.promptChoice("Please select the version of " + getName() + " plugin you want to install:", dependencies);
        Dependency dependency = dependencies.get(choice);

        Plugin plugin = new Plugin();
        plugin.setGroupId(dependency.getGroupId());
        plugin.setArtifactId(dependency.getArtifactId());
        plugin.setVersion(dependency.getVersion());
        pom.getBuild().addPlugin(plugin);
        return plugin;
    }
    
    protected Plugin addContainerPlugin(Model pom, String groupId, String artifactId, String version) {
        // if it's already in the build, we don't mind the version
        String pluginIdent = groupId + ":" + artifactId;
        for (Plugin plugin : pom.getBuild().getPlugins()) {
            String ident = plugin.getGroupId() + ":" + plugin.getArtifactId();
            if (pluginIdent.equals(ident))
                return plugin;
        }
        Plugin plugin = new Plugin();
        plugin.setGroupId(groupId);
        plugin.setArtifactId(artifactId);
        plugin.setVersion(version);
        pom.getBuild().addPlugin(plugin);
        return plugin;
    }
    
    protected boolean checkRebelEnv(PipeOut out) {
        String rebelHome = System.getenv(JRebelPlugin.REBEL_ENV_HOME);
        if (StringUtils.isEmpty(rebelHome)) {
            ShellMessages.info(out, "JRebel home variable " + JRebelPlugin.REBEL_ENV_HOME +
                    " is not set. Please set it manually before starting the container.");
            return false;
        }
        return true;
    }
    
    protected boolean hasConfig(Plugin plugin) {
        return plugin.getConfiguration() != null;
    }
    
    protected Xpp3Dom create(String text) {
        try {
            Xpp3Dom dom = Xpp3DomBuilder.build(
                new ByteArrayInputStream(text.getBytes()), "UTF-8");
            return dom;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create Dom node", ex);
        }
        
    }
    
    protected abstract String getGroupId();
    
    protected abstract String getArtifactId();
    
    protected abstract String getName();
    

}
