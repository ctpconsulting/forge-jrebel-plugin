package org.jboss.seam.forge.jrebel.config;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.codehaus.plexus.util.IOUtil;
import org.jboss.seam.forge.jrebel.util.ProjectUtils;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.facets.builtin.MavenResourceFacet;
import org.jboss.seam.forge.shell.ShellMessages;
import org.jboss.seam.forge.shell.plugins.PipeOut;

public class FileRebelXml implements RebelXml {
    
    @Inject
    private Project project;
    
    @Override
    public void createRebelXml(Model pom, PipeOut out) {
        String pathVar = ProjectUtils.createPathVar(pom);
        MavenResourceFacet resource = project.getFacet(MavenResourceFacet.class);
        resource.createResource(createXml(pathVar, pom).toCharArray(), "rebel.xml");
        ShellMessages.success(out, "Created rebel.xml");
    }

    private String createXml(String pathVar, Model pom) throws RuntimeException {
        try {
            pathVar = "${" + pathVar + "}";
            String baseTemplate = readResource("rebel.xml");
            String baseClasspath = replace(readResource("rebel-classpath.xml"), pathVar);
            String baseWeb = isWebModule(pom) ? replace(readResource("rebel-web.xml"), pathVar) : "";
            return replace(baseTemplate, baseClasspath, baseWeb);
        } catch (IOException e) {
            throw new RuntimeException("Could not create rebel.xml", e);
        }
    }

    private boolean isWebModule(Model pom) {
        return pom.getPackaging().equals("war");
    }
    
    private String readResource(String path) throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream(path);
        return IOUtil.toString(input);
    }
    
    private String replace(String base, String... token) {
        return MessageFormat.format(base, (Object[]) token);
    }
}