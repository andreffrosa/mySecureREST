package rest.server;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import javax.ws.rs.PathParam;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.uri.UriTemplate;

import http.HTTPRequest;
import util.URI_Utils;

public class AppMarionete {

	private Object handler;
	private Map<String, Map<String, ResourceMethod>> marionete;

	public AppMarionete(Object handler) {
		this.handler = handler;
		this.marionete = createMarionete(handler);
	}

	public Object invoke(String http_method, String path, byte[] body, String content_type) throws Exception {
		
		String[] splitted_path = URI_Utils.splitPath(path);
		
		Map<String, String> query_params = URI_Utils.parseParams(splitted_path[1], "UTF-8"); // TODO: Receber charset do cleinte
		
		Object result = invoke_method(http_method.toUpperCase(), splitted_path[0], query_params, body, content_type);
		
		return result;
	}

	private static Map<String, Map<String, ResourceMethod>> createMarionete(Object handler) {

		Map<String, Map<String, ResourceMethod>> marionete = new HashMap<>();

		Resource main_resource = Resource.builder(handler.getClass()).build();

		Queue<Entry<String,Resource>> queue = new LinkedList<>();

		queue.add(new AbstractMap.SimpleEntry<String, Resource>("", main_resource));

		while(!queue.isEmpty()) {
			Entry<String,Resource> current = queue.remove();

			Resource resource = current.getValue();

			String pathPrefix = current.getKey() + (resource.getPath() != null ? resource.getPath() : "" );

			for (ResourceMethod method : resource.getAllMethods()) {

				// If has sub resources
				if (method.getType().equals(ResourceMethod.JaxrsType.SUB_RESOURCE_LOCATOR)) {
					Resource aux = Resource.from(resource.getResourceLocator().getInvocable().getDefinitionMethod().getReturnType());
					queue.add(new AbstractMap.SimpleEntry<String, Resource>(pathPrefix, aux));
				}
				else {
					// Add method to marionete
					String http_method = method.getHttpMethod();
					Map<String, ResourceMethod> http_method_map = marionete.get(http_method);
					if(http_method_map == null) {
						http_method_map = new HashMap<String, ResourceMethod>();
						marionete.put(http_method, http_method_map);
					}

					http_method_map.put(pathPrefix, method);
				}
			}

			// Add all child resources
			for (Resource child : resource.getChildResources()) {
				queue.add(new AbstractMap.SimpleEntry<String, Resource>(pathPrefix, child));
			}
		}

		return marionete;
	}

	private Object invoke_method(String http_method, String path, Map<String, String> query_params, byte[] body, String content_type) throws Exception {

		Map<String, ResourceMethod> method_set = marionete.get(http_method);
		if(method_set != null) {
			for(Entry<String, ResourceMethod> method : method_set.entrySet()) {

				Map<String, String> path_params = match(path , method.getKey());

				if(path_params != null) {
					
					Method m = method.getValue().getInvocable().getDefinitionMethod();
					
					Object[] args = parseArguments(m, path_params, query_params, body, content_type);
					
					return m.invoke(this.handler, args);
					
				}
			}
		}

		throw new RuntimeException("Method not found!");
	}

	private static Object[] parseArguments(Method m, Map<String, String> path_params, Map<String, String> query_params, byte[] body, String content_type) throws UnsupportedEncodingException {
		List<Object> args = new ArrayList<>(m.getParameters().length);

		for( Parameter p : m.getParameters() ) {
			System.out.println(p.getName() + " " + p.getType().getName());
			System.out.println(p.getAnnotatedType().toString());

			boolean isBody = p.getAnnotations().length == 0;
			if(isBody) {
				System.out.println("isBody " + isBody);
				Object entity = Entity.deserialize(content_type, body, p.getAnnotatedType());
				args.add(entity); 
			} else {

				// Iterate method annotations
				for(Annotation a : p.getAnnotations()) {
					System.out.println( "annotaion: " + a.toString());

					if(a instanceof javax.ws.rs.PathParam) {
						javax.ws.rs.PathParam x = (javax.ws.rs.PathParam) a;

						String key = x.value();
						String value = URI_Utils.decode(path_params.get(key), "UTF-8");

						args.add(value);

						System.out.println("PathParam: " + key + "=" + value);

						break;

					} else if(a instanceof javax.ws.rs.QueryParam) {
						javax.ws.rs.QueryParam x = (javax.ws.rs.QueryParam) a;
						String key = x.value();
						String value = query_params.get(key);

						args.add(value);

						System.out.println("QueryParam: " + key + "=" + value);
						break;
					}
				}
			}
		}

		return args.toArray(new Object[args.size()]);
	}

	private static Map<String, String> match(String path, String pattern) {
		Map<String, String> map = new HashMap<String, String>();
		UriTemplate template = new UriTemplate(pattern);

		if( template.match(path, map) ) {
			System.out.println("Matched, " + map); // Debug
			return map;
		} else {
			System.out.println("Not matched, " + map); // Debug
			return null;
		}   
	}
}
