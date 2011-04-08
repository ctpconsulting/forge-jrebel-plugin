package org.jboss.seam.forge.jrebel.container;

import java.util.List;

import javax.inject.Inject;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.StringUtils;
import org.jboss.seam.forge.jrebel.JRebelPlugin;
import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.facets.DependencyFacet;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.ShellColor;
import org.jboss.seam.forge.shell.plugins.PipeOut;

public abstract class BaseContainerMavenPlugin implements ContainerMavenPlugin {
    
    @Inject 
    protected Shell shell;

    protected Plugin createContainerPlugin(DependencyFacet dependencyFacet) {
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

        return plugin;
    }
    
    protected Plugin createContainerPlugin(String groupId, String artifactId, String version) {
        Plugin plugin = new Plugin();
        plugin.setGroupId(groupId);
        plugin.setArtifactId(artifactId);
        plugin.setVersion(version);

        return plugin;
    }
    
    protected boolean checkRebelEnv(PipeOut out) {
        String rebelHome = System.getenv(JRebelPlugin.REBEL_ENV_HOME);
        if (StringUtils.isEmpty(rebelHome)) {
            out.println(ShellColor.YELLOW, "***WARN*** JRebel home variable " + JRebelPlugin.REBEL_ENV_HOME +
                    " is not set. Please set it manually before starting the container.");
            return false;
        }
        return true;
    }
    
    protected abstract String getGroupId();
    
    protected abstract String getArtifactId();
    
    protected abstract String getName();

}
