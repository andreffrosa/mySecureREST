package rest.server;

public class MethodNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MethodNotFoundException() {
		super();
	}
	
	public MethodNotFoundException(String msg) {
		super(msg);
	}

}
