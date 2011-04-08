package org.jboss.seam.forge.jrebel;

import java.util.Iterator;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.jboss.seam.forge.jrebel.container.Container;
import org.jboss.seam.forge.jrebel.container.ContainerMavenPlugin;
import org.jboss.seam.forge.jrebel.util.ProjectUtils;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.facets.builtin.MavenResourceFacet;
import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.shell.plugins.Command;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.PipeOut;
import org.jboss.seam.forge.shell.plugins.Plugin;
import org.jboss.seam.forge.shell.util.BeanManagerUtils;

@Alias("jrebel")
public class JRebelPlugin implements Plugin {
    
    public static final String REBEL_ENV_HOME = "REBEL_HOME";

    @Inject
    private Project project;
    
    @Inject
    private BeanManager beanManager;
    
    @Inject
    private JRebelXml rebelXml;
    
    @Inject
    private JRebelConfig rebelConfig;
    
    @Inject
    private Instance<ContainerMavenPlugin> containers;
    
    @Command(value = "setup")
    public void setup(
            @Option(name="rebelHome") String home, 
            final PipeOut out) {
        Model pom = ProjectUtils.resolvePom(project);
        createRebelXml(pom, out);
        createRebelConfig(home, createPathVar(pom), projectRootPath(), out);
        updateContainerPlugins(pom, out);
    }

    @Command(value = "rebel-xml")
    public void rebelXml(final PipeOut out) {
        Model pom = ProjectUtils.resolvePom(project);
        createRebelXml(pom, out);
    }
    
    @Command(value = "container")
    public void addContainer(
            @Option(name="name", required=true) Container container,
            final PipeOut out) {
        ContainerMavenPlugin plugin = BeanManagerUtils.getContextualInstance(beanManager, container.getContainer());
        plugin.addPlugin(ProjectUtils.resolvePom(project), out);
    }

    private void createRebelXml(Model pom, PipeOut out) {
        String pathVar = createPathVar(pom);
        MavenResourceFacet resource = project.getFacet(MavenResourceFacet.class);
        resource.createResource(rebelXml.createXml(pathVar).toCharArray(), "rebel.xml");
    }
    
    private void createRebelConfig(String rebelHome, String property, String value, PipeOut out) {
        rebelConfig.writeTo(rebelHome, property, value, out);
    }
    
    private void updateContainerPlugins(Model pom, PipeOut out) {
        Iterator<ContainerMavenPlugin> it = containers.iterator();
        while (it.hasNext()) {
            ContainerMavenPlugin plugin = it.next();
            plugin.updateConfig(pom, out);
        }
    }
    
    private String createPathVar(Model pom) {
        return pom.getArtifactId() + ".root";
    }
    
    private String projectRootPath() {
        String path = project.getProjectRoot().getUnderlyingResourceObject().getAbsolutePath();
        return path.replace("\\", "\\\\");
    }

}
