package test;

import java.util.ArrayList;
import java.util.List;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author shivang
 */
public class JerseyResourceScanner {
    public static void main(String[] args) {
        JerseyResourceScanner runClass = new JerseyResourceScanner();
        runClass.scan(myHelloWorld.class);
    }

    public <T> void scan(Class<T> baseClass) {
        Resource resource = Resource.builder(baseClass).build();
        String uriPrefix = "";
        process(uriPrefix, resource);
    }

    private void process(String uriPrefix, Resource resource) {
        String pathPrefix = uriPrefix;
        List<Resource> resources = new ArrayList<>();
        resources.addAll(resource.getChildResources());
        if (resource.getPath() != null) {
            pathPrefix = pathPrefix + resource.getPath();
        }
        for (ResourceMethod method : resource.getAllMethods()) {
            if (method.getType().equals(ResourceMethod.JaxrsType.SUB_RESOURCE_LOCATOR)) {
                resources.add(
                        Resource.from(resource.getResourceLocator()
                                .getInvocable().getDefinitionMethod().getReturnType()));
            }
            else {
                System.out.println(method.getHttpMethod() + "\t" + pathPrefix);
            }
        }
        for (Resource childResource : resources) {
            process(pathPrefix, childResource);
        }
    }
}