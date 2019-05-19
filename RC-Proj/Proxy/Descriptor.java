import java.util.*;
import java.net.*;
import java.io.*;
import java.io.ByteArrayInputStream;

public class Descriptor {

  private Map<Integer,Track> tracks; //array de listas?? array de arrays? array de árvores??
  private int segment_duration;
  private int segment_count;
  //private List< byte[] > init; //funciona?

  public Descriptor(byte[] file, String movie, HTTPSession http) throws IOException {
    tracks = new HashMap<>(6);
    loadFromFile(file, movie, http);
  }

  public void loadFromFile(byte[] file, String movie, HTTPSession http) throws IOException {

    Scanner sc = new Scanner( new ByteArrayInputStream(file) );

    sc.next();
    int movie_tracks = sc.nextInt();
    sc.nextLine();

    sc.next();
    segment_duration = sc.nextInt();
    sc.nextLine();

    System.out.println("movie-tracks: " + movie_tracks );
    System.out.println("segment-duration: " + segment_duration );
    int counter = 0;
    for(int i = 1; i <= movie_tracks; i++){
        sc.nextLine();

        String content_type = sc.nextLine();

        sc.next();
        int bandWidth = sc.nextInt();
        sc.nextLine();
	//TODO: Alterar args
        //Pedido HTTP para obter o ficheiro init
        String init = sc.next();
        int size = sc.nextInt();
        sc.nextLine();

	      //HTTPSession http = new HTTPSession(server , "1.1");
	      String seg = String.format("video/%d/init.mp4", i);
        String request = "/" + movie + "/" + seg;

	String[] args = {"User-Agent: X-RC2017"};
        HTTPSession.HttpReply reply = http.newHttpRequest("GET", request,args);

        Track current_track = new Track(bandWidth, content_type, reply.content);
        tracks.put(i, current_track);
        counter = 0;
        String line;
        while(!(line = sc.nextLine()).equals("")){
           //sc.next();
           current_track.addSegment(++counter, Integer.parseInt(line.split(" ")[1]) );
        }

        System.out.printf("%d %s %d\n", bandWidth, content_type, counter);
    }
    segment_count = counter;
  }

  public int getSegmentDuration(){
    return this.segment_duration;
  }

  public int getSegmentCount(){
    return this.segment_count;
  }

  public byte[] getInit(int track){
    return this.tracks.get(track).getInit();
  }

  public Track getTrack(int track){
    return tracks.get( track);
  }

  public int getNumberOfTracks(){
    return tracks.size();
  }

  public class Track {

    Map<Integer,Integer> segment_sizes;
    int avgBandWidth;
    String content_type;
    byte[] init;

    public Track( int avgBandWidth, String content_type, byte[] init ){
      segment_sizes = new HashMap<Integer,Integer>(  );
      this.avgBandWidth = avgBandWidth;
      this.content_type = content_type;
      this.init = init;
    }

    public int getAvgBandWidth(){
      return avgBandWidth;
    }
    public String getcontent_type(){
      return content_type;
    }

    public byte[] getInit(){
      return init;
    }

    public int getN_segments(){
      return segment_sizes.size();
    }

    public int getSegmentSize(int segment){
      return segment_sizes.get(segment);
    }

    public void addSegment(int seg_number,int seg_size){
      segment_sizes.put(seg_number, seg_size);
    }

  }

}
