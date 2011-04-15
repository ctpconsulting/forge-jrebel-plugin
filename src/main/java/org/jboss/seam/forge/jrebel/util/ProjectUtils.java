package org.jboss.seam.forge.jrebel.util;

import java.io.ByteArrayInputStream;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.facets.MavenCoreFacet;

public abstract class ProjectUtils {

    public static Model resolvePom(Project project) {
        MavenCoreFacet facet = project.getFacet(MavenCoreFacet.class);
        return facet.getPOM();
    }
    
    public static Plugin resolvePlugin(Model pom, String groupId, String artifactId) {
        return resolvePlugin(pom, groupId, artifactId, false);
    }
    
    public static Plugin resolvePlugin(Model pom, String groupId, String artifactId, boolean create) {
        String pluginIdent = groupId + ":" + artifactId;
        for (Plugin plugin : pom.getBuild().getPlugins()) {
            String ident = plugin.getGroupId() + ":" + plugin.getArtifactId();
            if (pluginIdent.equals(ident))
                return plugin;
        }
        Plugin result = null;
        if (create) {
            result = new Plugin();
            result.setGroupId(groupId);
            result.setArtifactId(artifactId);
            pom.getBuild().addPlugin(result);
        }
        return result;
    }
    
    public static Xpp3Dom createDom(String text) {
        try {
            Xpp3Dom dom = Xpp3DomBuilder.build(
                new ByteArrayInputStream(text.getBytes()), "UTF-8");
            return dom;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create Dom node", ex);
        }
    }
    
    public static String createPathVar(Model pom) {
        return pom.getArtifactId() + ".root";
    }
    
    public static String projectRootPath(Project project) {
        String path = project.getProjectRoot().getUnderlyingResourceObject().getAbsolutePath();
        return path.replace("\\", "\\\\");
    }

}
