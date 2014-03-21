package smartshopper.library;

public class Product {
	private int ID;
	private String item_name;
	private int Aisle;

	//Setters and getters for Product Object
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public int getAisle() {
		return Aisle;
	}

	public void setAisle(int aisle) {
		Aisle = aisle;
	}

}
