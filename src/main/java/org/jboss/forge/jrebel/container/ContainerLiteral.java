package org.jboss.forge.jrebel.container;

import javax.enterprise.util.AnnotationLiteral;

/**
 *
 * @author thomashug
 */
@SuppressWarnings("all")
public class ContainerLiteral extends AnnotationLiteral<Container>
        implements Container {

    private static final long serialVersionUID = 1L;
    
    private final ContainerType type;
    
    public ContainerLiteral(ContainerType type) {
        this.type = type;
    }

    @Override
    public ContainerType value() {
        return type;
    }
    
}
