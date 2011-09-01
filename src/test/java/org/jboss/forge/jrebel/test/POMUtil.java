package org.jboss.forge.jrebel.test;

import org.apache.maven.model.Plugin;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;

public class POMUtil {

    private POMUtil() {}
    
    public static Plugin findPlugin(Project project, String groupId, String artifactId) {
        MavenCoreFacet facet = project.getFacet(MavenCoreFacet.class);
        for (Plugin plugin : facet.getPOM().getBuild().getPlugins()) {
            if (plugin.getGroupId().equals(groupId) && plugin.getArtifactId().equals(artifactId))
                return plugin;
        }
        return null;
    }
}
