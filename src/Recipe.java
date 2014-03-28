import java.util.ArrayList;

public class Recipe {

	String name;
	String description;
	String type;
	ArrayList<Ingrediens> ingrediensList;

	// Default Constructor
	public Recipe() {

	}

	public String getName() {
		return name;
	}

	public ArrayList<Ingrediens> getIngredients(String recipeName) {
		return ingrediensList;
	}

	public String getDescription(String recipeName) {
		return description;
	}

	public String getType(String recipeName) {
		return type;
	}
}
