package org.jboss.seam.forge.jrebel;

import org.jboss.seam.forge.jrebel.config.JRebelConfig;
import java.util.Iterator;
import javax.enterprise.inject.Any;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.jboss.forge.project.Project;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.seam.forge.jrebel.config.FileRebelXml;
import org.jboss.seam.forge.jrebel.config.MavenRebelXml;
import org.jboss.seam.forge.jrebel.config.RebelXml;
import org.jboss.seam.forge.jrebel.container.ContainerLiteral;
import org.jboss.seam.forge.jrebel.container.ContainerType;
import org.jboss.seam.forge.jrebel.container.ContainerMavenPlugin;
import org.jboss.seam.forge.jrebel.util.ProjectUtils;

@Alias("jrebel")
@Help("Integrate JRebel in your project")
@Topic("Productivity")
public class JRebelPlugin implements Plugin {

    public static final String REBEL_ENV_HOME = "REBEL_HOME";

    @Inject
    private Project project;

    @Inject
    private Instance<RebelXml> rebelXml;

    @Inject
    private JRebelConfig rebelConfig;

    @Inject
    private Instance<ContainerMavenPlugin> containers;

    @Command(value = "setup", help = "Initialize JRebel for this project")
    public void setup(
            @Option(name="rebelHome", help="Specify a REBEL_HOME") String home,
            @Option(name="withoutMaven", help="Don't use the Zeroturnaround Maven Plugin",
                    defaultValue="false", flagOnly=true) Boolean skipMaven,
            final PipeOut out) {
        Model pom = ProjectUtils.resolvePom(project);
        createRebelXml(pom, skipMaven, out);
        createRebelConfig(home, pom);
        updateContainerPlugins(pom);
    }

    @Command(value = "rebel-xml", help = "Creates or overwrites rebel.xml")
    public void rebelXml(final PipeOut out) {
        Model pom = ProjectUtils.resolvePom(project);
        createRebelXml(pom, Boolean.TRUE, out);
    }

    @Command(value = "container", help = "Add a container with the JRebel start parameters")
    public void addContainer(
            @Option(name="named", required=true) ContainerType container,
            final PipeOut out) {
        ContainerMavenPlugin plugin = containers.select(new ContainerLiteral(container)).get();
        plugin.addPlugin(ProjectUtils.resolvePom(project));
    }

    private void createRebelXml(Model pom, Boolean skipMaven, PipeOut out) {
        Class<? extends RebelXml> selector = skipMaven ? FileRebelXml.class : MavenRebelXml.class;
        rebelXml.select(selector).get().createRebelXml(pom);
    }

    private void createRebelConfig(String rebelHome, Model pom) {
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
