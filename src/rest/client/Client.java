package rest.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import rest.RestRequest;

public interface Client {
	
	public mySecureRestClient setLocation(String location) throws MalformedURLException;
	
	public RestRequest newRequest(String resource_path) throws UnknownHostException, IOException;
	
	
}
