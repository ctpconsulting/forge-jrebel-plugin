package org.jboss.forge.jrebel;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.api.Deployment;
import org.jboss.forge.project.Project;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

public class JRebelPluginTest extends AbstractShellTest {

    @Deployment
    public static JavaArchive getDeployment() {
        return AbstractShellTest.getDeployment().addPackages(true, JRebelPlugin.class.getPackage());
    }

    @SuppressWarnings("unused")
    @Test
    public void shouldInstallJRebelWithoutMaven() throws Exception {
        // given
        Project p = initializeJavaProject();
        FileUtils.forceMkdir(new File("./target/conf"));
        new File("./target/conf/jrebel.properties").createNewFile();
        
        // when
        getShell().execute("jrebel setup --withoutMaven --rebelHome ./target");
        
        // then
        
    }
}
