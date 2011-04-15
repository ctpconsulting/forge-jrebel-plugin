package org.jboss.seam.forge.jrebel.container;

import javax.enterprise.util.AnnotationLiteral;

/**
 *
 * @author thomashug
 */
public class ContainerLiteral extends AnnotationLiteral<Container>
        implements Container {
    
    private final ContainerType type;
    
    public ContainerLiteral(ContainerType type) {
        this.type = type;
    }

    @Override
    public ContainerType value() {
        return type;
    }
    
}
