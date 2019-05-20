package test;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.PathParam;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.uri.UriTemplate;

import com.fasterxml.jackson.databind.introspect.Annotated;

/**
 * 
 * inspired on the code form https://stackoverflow.com/questions/29654883/introspecting-jersey-resource-model-jersey-2-x
 * 
 * */


public class Mapper {

	public static void main(String[] args) {
		HelloWorld h = new myHelloWorld();
		Map<String, Map<String, ResourceMethod>> ya = scan(h);

		System.out.println("Start");
		
		Object body = "larai";
		
		// tem de se converter o body depois de retirar do http request, de acordo com o ConsumesType

		Object result = invoke("POST", "/dispatcher/login/bina", body, ya, h);
		
		// tem de se converter o result de acordo com o ProducesType para serializar e enviar para o cliente

		System.out.println(result instanceof Boolean);

	}

	public static Object invoke(String http_method, String path, Object body, Map<String, Map<String, ResourceMethod>> model, Object h) {

		Object result = null;

		for(Entry<String, Map<String, ResourceMethod>> e : model.entrySet()) {
			if(http_method.equals(e.getKey())) {
				for(Entry<String, ResourceMethod> e2 : e.getValue().entrySet()) {
					//System.out.println("\t" + e2.getKey());

					Map<String, String> map = match(path , e2.getKey());
					if(map != null) {
						Method m = e2.getValue().getInvocable().getDefinitionMethod();

						List<Object> args = new ArrayList<>(m.getParameters().length);

						for(Parameter p : m.getParameters() ) {
							System.out.println(p.getName() + " " + p.getType().getName());
							System.out.println(p.getAnnotatedType().toString());

							for(Annotation a : p.getAnnotations()) {
								System.out.println( "yo" + a.toString());
								boolean isPathParam = a instanceof javax.ws.rs.PathParam;
								boolean isQueryParam = a instanceof javax.ws.rs.QueryParam;
								System.out.println("isPathParam " + isPathParam);
								System.out.println("isQueryParam " + isQueryParam);
								if(isPathParam) {
									javax.ws.rs.PathParam x = (PathParam) a;
									System.out.println(x.value());
									String ze = map.get(x.value());
									System.out.println(ze);

									args.add(ze);
								}
								if(isQueryParam) {
									javax.ws.rs.QueryParam x = (javax.ws.rs.QueryParam) a;
									System.out.println(x.value());
									String ze = map.get(x.value());
									System.out.println(ze);
									args.add(ze);
								}
							}

							boolean isBody = p.getAnnotations().length == 0;
							System.out.println("isBody " + isBody);
							if(isBody) {
								args.add(body); 
							}
						}

						/*for( AnnotatedType t : m.getAnnotatedParameterTypes() ) {
							System.out.println(t.getType().toString());
							for(Annotation a : t.getAnnotations()) {
								System.out.println( "yo" + a.toString());
							}
						}*/

						try {
							//m.invoke(h, "", "");
							result = m.invoke(h, args.toArray(new Object[args.size()]));

						} catch (IllegalAccessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IllegalArgumentException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		}
		return result;
	}


	public static Map<String, String> match(String path, String pattern) {
		Map<String, String> map = new HashMap<String, String>();
		UriTemplate template = new UriTemplate(pattern);
		if( template.match(path, map) ) {
			System.out.println("Matched, " + map);
			return map;
		} else {
			System.out.println("Not matched, " + map);
			return null;
		}   
	}

	public static Map<String, Map<String, ResourceMethod>> scan(Object obj) {
		Resource resource = Resource.builder(obj.getClass()).build();
		String uriPrefix = "";
		Map<String, Map<String, ResourceMethod>> ya = new HashMap<>();


		process(uriPrefix, resource, ya);

		return ya;
	}

	private static void process(String uriPrefix, Resource resource, Map<String, Map<String, ResourceMethod>> ya) {
		String pathPrefix = uriPrefix;

		List<Resource> resources = new ArrayList<>();
		resources.addAll(resource.getChildResources());
		if (resource.getPath() != null) {
			pathPrefix = pathPrefix + "" + resource.getPath();
		}
		for (ResourceMethod method : resource.getAllMethods()) {
			if (method.getType().equals(ResourceMethod.JaxrsType.SUB_RESOURCE_LOCATOR)) {
				resources.add(
						Resource.from(resource.getResourceLocator()
								.getInvocable().getDefinitionMethod().getReturnType()));
			}
			else {
				// System.out.println(method.getHttpMethod() + "\t" + pathPrefix);

				Map<String, ResourceMethod> yo = ya.get(method.getHttpMethod());
				if(yo == null) {
					yo = new HashMap<String, ResourceMethod>();
					ya.put(method.getHttpMethod(), yo);
				}

				yo.put(pathPrefix, method);

			}
		}
		for (Resource childResource : resources) {
			process(pathPrefix, childResource, ya);
		}
	}

}
