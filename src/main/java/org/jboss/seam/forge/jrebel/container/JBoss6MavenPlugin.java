package org.jboss.seam.forge.jrebel.container;

import static org.jboss.seam.forge.jrebel.container.ContainerType.JBOSS6;

@Container(JBOSS6)
public class JBoss6MavenPlugin extends CargoMavenPlugin {
    
    @Override
    protected String getName() {
        return "JBoss 6";
    }

    @Override
    protected String getContainerId() {
        return "jboss6x";
    }

    @Override
    protected String getContainerHome() {
        return "${env.JBOSS_HOME}";
    }

}
