package org.jboss.seam.forge.jrebel.config;

import org.apache.maven.model.Model;
import org.jboss.seam.forge.shell.plugins.PipeOut;


public interface RebelXml {

    void createRebelXml(Model pom, PipeOut out);

}
