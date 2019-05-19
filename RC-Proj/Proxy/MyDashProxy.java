import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyDashProxy {

  static final int PROXY_PORT = 1234;

  static int SERVER_PORT = 8080;
  static int PLAYOUT_DELAY = 1000; //milliSeconds
  static String DEFAULT_URL = "localhost";

  Map<String, HTTPSession> sessions; //tem de ser thread safe
  Map<String, Descriptor> movies; //como é read-only pelos threads não precisa de ser thread safe


  public MyDashProxy(){
    sessions = new HashMap<>();
    movies = new HashMap<>();

  }

  /**
  * MAIN - accept and handle client connections
  */
  public static void main(String[] args) throws IOException {

  String [] fullUrl;
    try {
      switch (args.length) {
      case 2:
        fullUrl =  args[0].split(":");
        DEFAULT_URL = fullUrl[0];
        if( fullUrl[1] != null ) SERVER_PORT = Integer.parseInt(fullUrl[1]);
        PLAYOUT_DELAY = Integer.parseInt(args[1]);
        break;
      default:
        throw new Exception("bad parameters");
      }
    } catch (Exception x) {
      System.out.printf("Usage: java MyDashProxy <url-base> <playout-delay>\n");
      System.exit(0);
    }

    new MyDashProxy().init();
  }

  public void init()  {
    //buscar descritor dos filmes
    try{
    loadDescriptors();

		ServerSocket ss = new ServerSocket( PROXY_PORT );
    System.out.println("Proxy ready at " + PROXY_PORT + " with PLAYOUT_DELAY of " + PLAYOUT_DELAY + "ms");

		for (;;) {
			Socket clientS = ss.accept();

      //TODO: melhorar
      HTTPSession http = new HTTPSession(clientS, "1.1"); //TODO: neste caso a versão só se sabe depois do pedido chegar
      //String request = http.processHTTPrequest();
      HTTPSession.HttpRequest request = http.processHTTPrequest();
      if( request != null){
        newSession(request, clientS);

        System.out.println("Sessões: " + sessions.size());
      }
      /*
      criar sessão só com o socket
      receber conexão -> guardar a versão
      caso a versão seja 1.0 fazer só 1x
      caso seja 1.1 fazer várias vezes
      Para isto o socket tem de ir para dentro da class ???
      */

		}
  } catch (IOException e) {
    e.printStackTrace();
  }

	}

  public void loadDescriptors(){

    try{

    Socket server = new Socket( InetAddress.getByName(DEFAULT_URL), SERVER_PORT );
    HTTPSession http = new HTTPSession(DEFAULT_URL, SERVER_PORT, "1.0");
    String[] args = {"User-Agent: X-RC2017"}; 	
    HTTPSession.HttpReply reply = http.newHttpRequest("GET", "/movies.txt",args);
    byte[] buffer = reply.content;

    List<String> mov = new LinkedList<>();

    Scanner sc = new Scanner( new ByteArrayInputStream(buffer) );
    while( sc.hasNextLine() ){ //Ta malformed

      String fileName = sc.nextLine();
      System.out.println( fileName );

      mov.add(fileName);
    }

    //guardar primeiro tudo num array
    //para cada um fazer novo pedido
    for(String fileName : mov){
    	
      reply = http.newHttpRequest("GET", "/" + fileName + "/descriptor.txt",args);
      buffer = reply.content;

      //criar descritor
      movies.put(fileName, new Descriptor(buffer, fileName, http) );
    }

    server.close(); //devia ser assim? ou criar e guardar o socket dentro da class http e chamar um método finish??

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

private  int getTrack(String movie, int current_seg, int current_track, int queue_size, int bandwidth){

  //passar a queue pq entretanto pode ter sido enviado um segmento??
  int DISCOUNT =  500;

  Descriptor movieDescriptor = movies.get(movie);
  int track = current_track;
  int max_track = movieDescriptor.getNumberOfTracks();

  int t_buffered = queue_size*movieDescriptor.getSegmentDuration(); //tempo carregado na fila
  int t_available = t_buffered - DISCOUNT; if(t_available<0) t_available=0; //tempo disponivel = tempo de fila - desconto

  Function<Integer, Integer> download_time = arg_track -> (
  (movieDescriptor.getTrack(arg_track).getSegmentSize(current_seg)*8 / bandwidth)*1000
  );

  int t_download = 0;

  if( bandwidth <= 0){
    int dt = 2000 - 0 + 1000;
    double ratio = dt / max_track; // max_track = nº de qualidades
    int new_track = (int)Math.floor(PLAYOUT_DELAY / ratio) + 1;

    track = Math.min(new_track, max_track);
  }
  else {
    while( ( t_download = download_time.apply(Math.min(track+1, max_track)) ) < t_available && track < max_track ){
        track++;
    }

    while( ( t_download = download_time.apply(track) ) > t_available  && track!= 1 ){
        track--;
    }
  }

  System.out.println("\n\nQualidade: " + track);

  return track;
	//return (current_seg % movies.get(movie).getNumberOfTracks()) + 1;
}


  public void newSession(HTTPSession.HttpRequest request, Socket clientS) throws IOException {
    //System.out.println("Client: " + request.getUrl());

    //Obter id do user
    String[] split = request.getUrl().split("/");
    String id = split[1];
    String movie = split[2];
    String instruction = split[3];

    //ver se user já tem sessão? -> set vs hashmap
    if( sessions.get(id + "/" + movie) == null ){ //id tem de ser id/movie

      //caso não tenha sessão: criar 2 threads + fila
      System.out.println("\nNova Sessão!\n");

      BlockingQueue<Pair <Integer ,byte[]> > loadedSegments = new LinkedBlockingQueue<>(); 

      HTTPSession http = new HTTPSession(clientS, request.getVersion() ); // n temos de mudar isto?
      sessions.put(id+"/"+movie, http);
      http.updateSession(clientS, request);

      Thread t = loadSegments(movie, loadedSegments);
      sendSegments(http, movie, loadedSegments, id+"/"+movie);

    } else if( instruction == "next-segment" && request.getVersion() == "1.0" ){ //não funciona
        sessions.get(id + "/" + movie).updateSession(clientS, request);
    } else{
      //caso já tenha sessão: ignora pedido e fecha conexão tcp
      System.out.println("\nSessão já existe!\n");
      clientS.close();
    }

  }

  public Thread loadSegments(String movie, Queue<Pair<Integer, byte[]>> loadedSegments){ //BlockingQueue<Pair <Integer ,byte[]> > loadedSegments
    //Comunicate with Server
    Thread t = new Thread( () -> {

      try {
      //abrir conexão TCP para o servidor
      Socket server = new Socket( InetAddress.getByName(DEFAULT_URL), SERVER_PORT );
      HTTPSession http = new HTTPSession(server , "1.1");

      int current_seg = 1;
      int current_track = -1; //começar em qual? na max?
      int next_track = current_track;
      byte[] payload;
      byte[] init ;

      //pedir blocos do filme + variar qualidade consoanste o estado da conexão
      while( current_seg <= movies.get(movie).getSegmentCount() ) {

        next_track = getTrack(movie, current_seg, current_track, loadedSegments.size(), http.getBandwidth());

        if( next_track != current_track ){
            current_track = next_track;
            init = movies.get(movie).getInit(current_track);
        }else
            init = new byte[0];

        String seg = String.format("video/%d/seg-%d.m4s", current_track, current_seg++);
        String request = "/" + movie + "/" + seg;

        System.out.println("request: " + request);
	String[] args = {"User-Agent: X-RC2017"};
        HTTPSession.HttpReply reply = http.newHttpRequest("GET", request, args);

        //guardar seg na queue (o 0 também é guardado)
        payload = new byte[init.length + reply.content.length];
        System.arraycopy(init, 0, payload, 0, init.length);
        System.arraycopy(reply.content, 0, payload, init.length, reply.content.length);


        loadedSegments.add( new Pair<Integer, byte[]>(current_track, payload) );

        //System.out.println("Length: " + Integer.parseInt(reply.head.get("Content-Length")));
      }

      System.out.println("movie loaded!");

      //fechar conexão
      server.close();

    }catch(Exception e){
      e.printStackTrace();
    }
  });
  //}).start();

    t.start();

    return t;
  }

  public  Thread sendSegments(HTTPSession http, String movie, Queue<Pair<Integer, byte[]>> loadedSegments, String id){
    //Comunicate with Client
    Thread t = new Thread( () -> {
      try {
      //esperar que a fila encha com playout-delay
      int current_time = 0;
      do {
        current_time = loadedSegments.size()*movies.get(movie).getSegmentDuration() ;
      } while( current_time < PLAYOUT_DELAY);

      //começar a enviar consoante os pedidos do browser
      System.out.println("Play started!");

      //HTTPSession http = new HTTPSession(clientS, "1.1");

    //String[] args = null;
    HTTPSession.HttpRequest request = null;
    int current_seg = 1;
    //int current_track = 1;
    System.out.println("Number of segments: " + movies.get(movie).getSegmentCount());
    while( /*current_seg <= movies.get(movie).getSegmentCount()*/ true ) {

      //receber pedido de segmento
      //if(current_seg > 1){
        System.out.println("New segment!");
        request = http.processHTTPrequest();
      //}

      //Enquanto a fila estiver vazia esperar por um novo segmento
      while( loadedSegments.isEmpty() );

      //enviar segmento
      if( (request != null /*|| current_seg == 1*/) && current_seg <= movies.get(movie).getSegmentCount() ){
        Pair pair = loadedSegments.poll();
        System.out.printf("\n\nSending Segment: %d \t%s\n\n", current_seg, request.request_line);
        String[] args = {movies.get(movie).getTrack( ((Integer)pair.x) ).getcontent_type(), "Access-Control-Allow-Origin: *"};
        http.sendReply(((byte[])pair.y), args);

        current_seg++;
      }

      //enviar segmento com content-lenght = 0
      if( request != null && current_seg > movies.get(movie).getSegmentCount() ){

        System.out.printf("\n\nContent-Length: 0\n\n");
        String[] args = {movies.get(movie).getTrack(1).getcontent_type(), "Access-Control-Allow-Origin: *"};
        http.sendReply(new byte[0], args);

        break;
      }

    }

    //temp
    System.out.println("\n.....................................................................\n");
    //System.exit(0);

    //capturar excepção de IO -> cliente fechou sessão -> remover from sessions + desligar os threads(tem de se guardar o outro thread para poder acabar depois).
    System.out.println("End of transmission");

    //clientS.close();
    http.closeSocket();

    }catch(Exception e){
      e.printStackTrace();
    }

    //remover sessão
    sessions.remove(id);

    });
    t.start();
    return t;
  }

  class Pair<X,Y>{

    public X x;
    public Y y;

    public Pair(X x, Y y){
      this.x = x;
      this.y = y;
    }

  }

}
