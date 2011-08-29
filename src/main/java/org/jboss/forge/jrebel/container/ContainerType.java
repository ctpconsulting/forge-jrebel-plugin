package org.jboss.forge.jrebel.container;

public enum ContainerType {
    
    JBOSS6(JBoss6MavenPlugin.class);
    
    private Class<? extends ContainerMavenPlugin> clazz;
    
    private ContainerType(Class<? extends ContainerMavenPlugin> clazz) {
        this.clazz = clazz;
    }
    
    public Class<? extends ContainerMavenPlugin> getContainer() {
        return clazz;
    }

}
