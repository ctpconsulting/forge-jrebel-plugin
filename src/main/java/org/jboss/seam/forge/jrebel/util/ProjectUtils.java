package org.jboss.seam.forge.jrebel.util;

import org.apache.maven.model.Model;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.facets.MavenCoreFacet;

public abstract class ProjectUtils {

    public static Model resolvePom(Project project) {
        MavenCoreFacet facet = project.getFacet(MavenCoreFacet.class);
        return facet.getPOM();
    }

}