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
import org.jboss.seam.forge.jrebel.util.ProjectUtils;
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
        int choice = shell.promptChoice("Please select the version of " + getName() +
                " plugin you want to install:", dependencies);
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
        Plugin plugin = ProjectUtils.resolvePlugin(pom, groupId, artifactId);
        if (plugin != null)
            return plugin;
        plugin = new Plugin();
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
    
    protected abstract String getGroupId();
    
    protected abstract String getArtifactId();
    
    protected abstract String getName();
    

}
