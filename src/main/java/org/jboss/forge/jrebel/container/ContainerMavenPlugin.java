package org.jboss.forge.jrebel.container;

import org.apache.maven.model.Model;

public interface ContainerMavenPlugin {

    void updateConfig(Model pom);
    
    void addPlugin(Model pom);

}
