package test;

import java.util.List;

public class myHelloWorld implements HelloWorld {

	@Override
	public boolean login(String username, String password) {
		System.out.println(username + " " + password);
		return false;
	}

	@Override
	public List<String> listFiles(String username, String path) {
		System.out.println(username + " " + path);
		return null;
	}

	@Override
	public boolean mkdir(String username, String path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean upload(String username, String path, byte[] data) {
		System.out.println(username + " " + path + " " + new String(data));
		return false;
	}

	@Override
	public byte[] download(String username, String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean copy(String username, String origin, String dest) {
		System.out.println(username + " " + origin + " " + dest);
		return false;
	}

	@Override
	public boolean remove(String username, String path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeDirectory(String username, String path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getFileMetadata(String username, String path) {
		// TODO Auto-generated method stub
		return false;
	}

}
