package org.jboss.forge.jrebel.container;

import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.StringUtils;
import org.jboss.forge.jrebel.JRebelPlugin;
import org.jboss.forge.jrebel.util.ProjectUtils;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;

public abstract class BaseContainerMavenPlugin implements ContainerMavenPlugin {

    protected Plugin addContainerPlugin(Model pom, DependencyFacet dependencyFacet) {
        List<Dependency> dependencies = dependencyFacet.resolveAvailableVersions(
                DependencyBuilder.create()
                        .setGroupId(getGroupId())
                        .setArtifactId(getArtifactId()));
        int choice = getShell().promptChoice("Please select the version of " + getName() +
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
    
    protected boolean checkRebelEnv() {
        String rebelHome = System.getenv(JRebelPlugin.REBEL_ENV_HOME);
        if (StringUtils.isEmpty(rebelHome)) {
            ShellMessages.info(getShell(), "JRebel home variable " + JRebelPlugin.REBEL_ENV_HOME +
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

    protected abstract Shell getShell();

    protected abstract Project getProject();

}
