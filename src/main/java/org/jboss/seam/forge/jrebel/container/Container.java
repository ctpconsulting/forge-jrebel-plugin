package org.jboss.seam.forge.jrebel.container;

public enum Container {
    
    JBOSS6(JBoss6MavenPlugin.class);

    private Class<? extends ContainerMavenPlugin> container;
    
    public Class<? extends ContainerMavenPlugin> getContainer() {
        return container;
    }

    private Container(Class<? extends ContainerMavenPlugin> container) {
        this.container = container;
    }
}
