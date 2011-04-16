package org.jboss.seam.forge.jrebel.config;

import java.util.List;
import javax.inject.Inject;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jboss.seam.forge.jrebel.util.ProjectUtils;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.facets.MavenCoreFacet;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.ShellMessages;

/**
 * Add the configuration for the JRebel Maven plugin to generate the rebel.xml.
 * 
 * @author thomashug
 */
public class MavenRebelXml implements RebelXml {
    
    @Inject
    private Project project;
    
    @Inject
    private Shell shell;

    @Override
    public void createRebelXml(Model pom) {
        MavenCoreFacet facet = project.getFacet(MavenCoreFacet.class);
        Plugin plugin = ProjectUtils.resolvePlugin(pom, "org.zeroturnaround", "jrebel-maven-plugin", true);
        addExecution(plugin);
        addConfiguration(pom, plugin);
        facet.setPOM(pom);
        ShellMessages.success(shell, "Added the Maven JRebel Plugin. Run mvn compile to create your rebel.xml");
    }

    private void addExecution(Plugin plugin) {
        List<PluginExecution> executions = plugin.getExecutions();
        if (executions.isEmpty()) {
            PluginExecution exec = new PluginExecution();
            exec.setId("generate-rebel-xml");
            exec.setPhase("process-resources");
            exec.addGoal("generate");
            plugin.addExecution(exec);
        }
    }

    private void addConfiguration(Model pom, Plugin plugin) {
        Xpp3Dom config = (Xpp3Dom) plugin.getConfiguration();
        if (config == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("<configuration>")
                       .append("<rootPath>$${")
                           .append(ProjectUtils.createPathVar(pom))
                       .append("}</rootPath>")
                   .append("</configuration>");
            config = ProjectUtils.createDom(builder.toString());
            plugin.setConfiguration(config);
        }
    }
    
}
