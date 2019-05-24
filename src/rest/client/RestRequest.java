package rest.client;


public class RestRequest {

  private String path;
  private Socket socket;
  private Map<String,String> queryParams;
  
  public RestRequest(Socket socket, String base_path) {
    this.socket = socket;
    this.path = base_path;
    this.queryParams = new HashMap<>();
  }
  
  // TODO: Fazer para booleans, ints, longs, doubles e arrays e listas
  public RestRequest addPathParam(String param) {
    this.path += "/" + URI_Utils.encode(param);
    return this;
  }

  // TODO: Fazer para booleans, ints, longs, doubles e arrays e listas
  public RestRequest addQueryParam(String key, String value) {
    this.queryParams.put(URI_Utils.encode(key), URI_Utils.encode(value));
    return this;
  }

  // private?
  private RestResponse http_request(String method, Object entity) {
    // Colocar os queryParams no path
    String query = "";
    for(Entry<String,String> e : this.queryParams.entrySet()) {
      String separator = query.equals("") ? "?" : "&";
      query += separator + e.getKey() + "=" + e.getValue();
    }
    
    // serializar a entity com base no seu tipo
    byte[] http_body = null;
    
    // Criar HTTPRequest
    HTTPRequest http_request = new HTTPRequest(method, this.path + query, http_body);
    
    // utilizar o socket para enviar request
    this.socket.
    
    // Esperar por uma reply 
    
    // Fechar o socket
    socket.close();
    
    // Criar uma resposta
    return new RestResponse();
  }

  public RestResponse post(Object entity) {
    return http_request("POST", entity);
  }
  
  public RestResponse get(Object entity) {
    return http_request("GET", entity);
  }
  
  public RestResponse put(Object entity) {
    return http_request("PUT", entity);
  }
  
  public RestResponse delete(Object entity) {
    return http_request("DELETE", entity);
  }

}
