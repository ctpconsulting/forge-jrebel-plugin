package org.jboss.seam.forge.jrebel.container;

import org.apache.maven.model.Model;
import org.jboss.seam.forge.shell.plugins.PipeOut;

public interface ContainerMavenPlugin {

    void updateConfig(Model pom, PipeOut out);
    
    void addPlugin(Model pom, PipeOut out);

}
