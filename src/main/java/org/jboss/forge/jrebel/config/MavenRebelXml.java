package org.jboss.forge.jrebel.config;

import java.util.List;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.jboss.forge.jrebel.util.ProjectUtils;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;

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
    
}
