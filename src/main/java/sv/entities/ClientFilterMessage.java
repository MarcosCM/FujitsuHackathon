package sv.entities;

public class ClientFilterMessage {
	private String street;
	
	public ClientFilterMessage(String street){
		this.street = street;
	}
	
	public void setStreet(String street){
		this.street = street;
	}
	
	public String getStreet(){
		return this.street;
	}
}
