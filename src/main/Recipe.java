package main;

import java.util.ArrayList;

public class Recipe {
	
	public int id;
	public String cuisine;
	public ArrayList<String> ingredients;

	public Recipe(int id, String cuisine, ArrayList<String> ingredients) {
		this.id = id;
		this.cuisine = cuisine;
		this.ingredients = ingredients;
	}
}
