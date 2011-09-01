package org.jboss.forge.jrebel;

import java.util.Iterator;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.jboss.forge.jrebel.config.FileRebelXml;
import org.jboss.forge.jrebel.config.JRebelConfig;
import org.jboss.forge.jrebel.config.MavenRebelXml;
import org.jboss.forge.jrebel.config.RebelXml;
import org.jboss.forge.jrebel.container.ContainerLiteral;
import org.jboss.forge.jrebel.container.ContainerMavenPlugin;
import org.jboss.forge.jrebel.container.ContainerType;
import org.jboss.forge.jrebel.util.ProjectUtils;
import org.jboss.forge.project.Project;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;

@Alias("jrebel")
@Help("Integrate JRebel in your project")
public class JRebelPlugin implements Plugin {

    public static final String REBEL_ENV_HOME = "REBEL_HOME";

    @Inject
    private Project project;

    @Inject @Any
    private Instance<RebelXml> rebelXml;

    @Inject
    private JRebelConfig rebelConfig;

    @Inject @Any
    private Instance<ContainerMavenPlugin> containers;

    @Command(value = "setup", help = "Initialize JRebel for this project")
    public void setup(
            @Option(name="rebelHome", help="Specify a REBEL_HOME") String home,
            @Option(name="withoutMavenPlugin", help="Don't use the Zeroturnaround Maven Plugin",
                    defaultValue="false", flagOnly=true) Boolean skipMaven) {
        Model pom = ProjectUtils.resolvePom(project);
        createRebelXml(pom, skipMaven);
        createRebelConfig(home, skipMaven, pom);
        updateContainerPlugins(pom);
    }

    @Command(value = "rebel-xml", help = "Creates or overwrites rebel.xml")
    public void rebelXml() {
        Model pom = ProjectUtils.resolvePom(project);
        createRebelXml(pom, Boolean.TRUE);
    }

    @Command(value = "container", help = "Add a container with the JRebel start parameters")
    public void addContainer(
            @Option(name="named", required=true) ContainerType container) {
        ContainerMavenPlugin plugin = containers.select(new ContainerLiteral(container)).get();
        plugin.addPlugin(ProjectUtils.resolvePom(project));
    }

    private void createRebelXml(Model pom, Boolean skipMaven) {
        Class<? extends RebelXml> selector = skipMaven ? FileRebelXml.class : MavenRebelXml.class;
        rebelXml.select(selector).get().createRebelXml(pom);
    }

    private void createRebelConfig(String rebelHome, Boolean skipMaven, Model pom) {
        if (skipMaven)
            rebelConfig.writeTo(rebelHome, ProjectUtils.createPathVar(pom), 
                    ProjectUtils.projectRootPath(project));
    }

    private void updateContainerPlugins(Model pom) {
        Iterator<ContainerMavenPlugin> it = containers.iterator();
        while (it.hasNext()) {
            ContainerMavenPlugin plugin = it.next();
            plugin.updateConfig(pom);
        }
    }

}
