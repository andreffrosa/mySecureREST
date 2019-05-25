package test;

public class Human{
	
	private String name;
	private String surname;
	private Human son;
	public byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
	
	public Human(String name, String surname) {
		this.name = name;
		this.setSurname(surname);
	}
	
	public void setSon(Human son) {
		this.son = son;
	}

	public String greeting() {
		return "Olá eu sou o " + name;
	}

	public byte[] getBytes() {
		return data;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
	
}
