import java.util.*;
import java.net.*;
import java.io.*;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Iterator;

public class HTTPSession {

    //private static final int DEFAULT_BANDWIDTH = 60000;
    private static final int DEFAULT_SIZE = 1;

    private Socket destination;
    private String version;
    private String server;
    private int port;
  //  private String[] args;
    //private int bandwidth; //bytes/s ??
    //private long rtt//long ou int?
    private HttpRequest last_request;

    private Queue<Integer> n_bytes;
    private Queue<Integer> n_rtts;

    public HTTPSession(Socket destination, String version/*, String[] args*/){
      this.destination = destination;
      this.version = version;
      //this.args = args;
      last_request = null;
      n_bytes = new LinkedList<Integer>();
      n_rtts = new LinkedList<Integer>();
    }

    public HTTPSession(String server, int port, String version) throws IOException {
      this.destination = new Socket( InetAddress.getByName(server), port );
      this.server = server;
      this.port = port;
      this.version = version;
      //this.args = args;

      last_request = null;

      n_bytes = new LinkedList<Integer>();
      n_rtts = new LinkedList<Integer>();
    }

    public void updateSession(Socket socket, HttpRequest request){
        destination = socket;
        last_request = request;
    }

    public void closeSocket() throws IOException {
      destination.close();
    }

    public int getBandwidth(){

      long totalBytes = 0;
      long totalRTT = 0;
      Iterator<Integer> it = n_bytes.iterator();
      Iterator<Integer> it2 = n_rtts.iterator();

      while(it.hasNext()){ //basta ver de um dos iteratores porque estes vao acabar sempre no mesmo ponto
        totalBytes+= it.next();
        totalRTT+= it2.next();
      }

      //totalBytes *= 8; //bytes to bits
      //totalRTT /= 1000; // ms to s
      int bandwidth = (totalRTT != 0) ? (int)Math.floor( totalBytes*8*1000 / ((double) totalRTT) ) : -1;
      System.out.println("Calculating bandwidth: " + bandwidth);
      return  bandwidth;// bits/s
    }

    /**
     * Reads one message from the HTTP header
     */
    public String readLine() throws IOException {
      InputStream is = destination.getInputStream();

      StringBuffer sb = new StringBuffer() ;

      int c ;
      while( (c = is.read() ) >= 0 ) {
        if( c == '\r' ) continue ;
        if( c == '\n' ) break ;
        sb.append( new Character( (char)c) ) ;
      }
      return sb.toString() ;
    }

    /**
     * Parses the first line of the HTTP request and returns an array
     * of three strings: reply[0] = method, reply[1] = object and reply[2] = version
     * Example: input "GET /index.html HTTP/1.0"
     * output reply[0] = "GET", reply[1] = "/index.html" and reply[2] = "HTTP/1.0"
     *
     * If the input is malformed, it returns something unpredictable
     */
    public static String[] parseHttpRequest( String request) {
      String[] error = { "ERROR", "", "" };
      String[] result = { "", "", "" };
      int pos0 = request.indexOf( ' ');
      if( pos0 == -1) return error;
      result[0] = request.substring( 0, pos0).trim();
      pos0++;
      int pos1 = request.indexOf( ' ', pos0);
      if( pos1 == -1) return error;
      result[1] = request.substring( pos0, pos1).trim();
      result[2] = request.substring( pos1 + 1).trim();
      if(! result[1].startsWith("/")) return error;
      if(! result[2].startsWith("HTTP")) return error;
      return result;
    }


    public HttpRequest processHTTPrequest() throws IOException {

        if ( last_request != null /*&& version.equals("1.0")*/ ){
          HttpRequest temp = last_request;
          last_request = null;
          return temp;
        }
        else {
          Map<String,String> head = new HashMap<String,String>();

          String request_line = readLine();

          String answerLine = null;
          while( !(answerLine = readLine()).equals("") ){
            String[] ans = answerLine.split(": ");
            head.put(ans[0] , ans[1]);
          }

          return new HttpRequest(head, request_line);
        }

    }

    /**
     * Parses the first line of the HTTP reply and returns an array
     * of three strings: reply[0] = version, reply[1] = number and reply[2] = result message
     * Example: input "HTTP/1.0 501 Not Implemented"
     * output reply[0] = "HTTP/1.0", reply[1] = "501" and reply[2] = "Not Implemented"
     *
     * If the input is malformed, it returns something unpredictable
     */
    public static String[] parseHttpReply (String reply) {
      String[] result = { "", "", "" };
      int pos0 = reply.indexOf(' ');
      if( pos0 == -1) return result;
      result[0] = reply.substring( 0, pos0).trim();
      pos0++;
      int pos1 = reply.indexOf(' ', pos0);
      if( pos1 == -1) return result;
      result[1] = reply.substring( pos0, pos1).trim();
      result[2] = reply.substring( pos1 + 1).trim();
      return result;
    }

    public void sendHttpRequest(String type, String url, String[] args) throws IOException {
       
		StringBuilder request = new StringBuilder( String.format("%s %s HTTP/%s\r\n", type, url, this.version) );
  		request.append("Date: " + new Date().toString()+"\r\n");

      		for(int i = 0; i < args.length; i++){
        		request.append(args[i] +"\r\n");
      		}

		request.append("\r\n");
  		destination.getOutputStream().write( request.toString().getBytes() );
	
    }

    public void sendReply (byte[] content, String[] args)	throws IOException {

  		StringBuilder reply = new StringBuilder( String.format("HTTP/%s 200 OK\r\n",this.version) );
  		reply.append("Date: " + new Date().toString()+"\r\n");
  		//reply.append("Server: " + ""+"\r\n");
  		//reply.append("Content-Type: "+ args[0] +"\r\n");

      for(int i = 0; i < args.length; i++){
        reply.append(args[i] +"\r\n");
      }

  		reply.append("Content-Length: " + String.valueOf(content.length) + "\r\n\r\n");

      //temp
      //System.out.println( reply.toString() );

  		destination.getOutputStream().write( reply.toString().getBytes() );
      //if( content.length > 0 )
        destination.getOutputStream().write( content );
  	}

    public HttpReply getHttpReply() throws IOException {
      Map<String, String> head = new HashMap<>();
      byte[] content;

      String answerLine = readLine();
  		System.out.println("Got answer: " + answerLine);

  		//String[] result = parseHttpReply(answerLine); //ver se está ok ou não

      //Process header
      while( !(answerLine = readLine()).equals("") ){
        String[] ans = answerLine.split(": ");
        head.put(ans[0] , ans[1]);
      }
      //Empty Line
      int size = Integer.parseInt( head.get("Content-Length") );
      byte[] buffer = new byte[size];

      int n = 0;
      while( n < size){
        n += destination.getInputStream().read(buffer, n, size-n);
      }

      return new HttpReply(head, buffer);
    }

    public HttpReply newHttpRequest(String type, String url, String[] args) throws IOException {

      if(version.equals("1.0"))
         destination = new Socket( InetAddress.getByName(server), port );

      long start = System.currentTimeMillis();
      sendHttpRequest(type, url, args);
      HttpReply reply = getHttpReply();
      long finish = System.currentTimeMillis();

      int rtt = (int)(finish - start);
      int size = reply.content.length;

      n_bytes.add(size);
      n_rtts.add(rtt);

      if( n_bytes.size() > DEFAULT_SIZE ){
        n_bytes.poll();
        n_rtts.poll();
      }

      if(version.equals("1.0"))
         destination.close();

      /*System.out.println("Rtt: " + rtt);
      System.out.println("Size: " + size);
      System.out.println("bandwidth: " + this.getBandwidth() );*/

      return reply;
    }

    //arranjar forma de saber se a conexão terminou ou não para poder remover do mapa das sessões, não sei se será nesta class

    public class HttpReply {
      public Map<String, String> head;
      public byte[] content;

      public HttpReply(Map<String, String> head, byte[] content){
        this.head = head;
        this.content = content;
      }
    }

    public class HttpRequest {
      public String request_line;
      public Map<String, String> head;

      public HttpRequest(Map<String, String> head, String request_line){
        this.head = head;
        this.request_line = request_line;
      }

      public String getMethod(){
        return request_line.split(" ")[0];
      }

      public String getUrl(){
        return request_line.split(" ")[1];
      }

      public String getVersion(){
        //percebam agora o fdps
        return request_line.split(" ")[2].split("/")[1];

      }

      /*public Map<String, String> getHead(){
        return head;
      }*/
    }



}
