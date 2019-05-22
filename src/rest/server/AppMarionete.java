package rest.server;

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

public class AppMarionete {

	private Object handler;
	private Map<String, Map<String, ResourceMethod>> marionete;

	public AppMarionete(Object handler) {
		this.handler = handler;
		this.marionete = createMarionete(handler);
	}

	public Object invoke(String http_method, String path, Object body) throws Exception { // Passar este objecto ou passar os args em separado?
		// TODO
		
		String filtered_path = path; // TODO: remover os query params
		
		Map<String, String> query_params = new HashMap<>(); // TODO: fazer parse dos query params
		
		Object result = invoke_method(http_method.toUpperCase(), filtered_path, query_params, body);
		
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

	private Object invoke_method(String http_method, String path, Map<String, String> query_params, Object body) throws Exception {

		Map<String, ResourceMethod> method_set = marionete.get(http_method);
		if(method_set != null) {
			for(Entry<String, ResourceMethod> method : method_set.entrySet()) {

				Map<String, String> path_params = match(path , method.getKey());

				if(path_params != null) {
					Method m = method.getValue().getInvocable().getDefinitionMethod();
					
					Object[] args = parseArguments(m, path_params, query_params, body);
					
					return m.invoke(this.handler, args);
					
				}
			}
		}

		throw new RuntimeException("Method not found!");
	}

	private static Object[] parseArguments(Method m, Map<String, String> path_params, Map<String, String> query_params, Object body) {
		List<Object> args = new ArrayList<>(m.getParameters().length);

		for( Parameter p : m.getParameters() ) {
			System.out.println(p.getName() + " " + p.getType().getName());
			System.out.println(p.getAnnotatedType().toString());

			boolean isBody = p.getAnnotations().length == 0;
			if(isBody) {
				System.out.println("isBody " + isBody);
				args.add(body); 
			} else {

				// Iterate method annotations
				for(Annotation a : p.getAnnotations()) {
					System.out.println( "annotaion: " + a.toString());

					if(a instanceof javax.ws.rs.PathParam) {
						javax.ws.rs.PathParam x = (javax.ws.rs.PathParam) a;

						String key = x.value();
						String value = path_params.get(key);

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