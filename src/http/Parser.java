/**
 * 
 */
package http;

import java.io.InputStream;

/**
 * @author ruben
 *
 */
public interface Parser {
	
	/**
	 * Serialize an HttpMessage to byte array to send data through a socket. 
	 * @param httpMessage Message to send.
	 * @return The message in a byte array form factor.
	 */
	public byte[] serialize( HTTPMessage httpMessage );
	
	/**
	 * Returns a HttpMessage from raw data that arrived on the socket.
	 * @param in Inputstream of the socket
	 * @return The message.
	 */
	public HTTPMessage desseralizeReply(InputStream in );

}
