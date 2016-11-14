package main;

import java.util.Collection;
import java.util.HashSet;

/*
 * Authors: Julian McClinton jm2af
 * 			Ryan McCampbell  rnm6u
 */

public class Recipe {
	
	public int id;
	public String cuisine;
	public HashSet<String> ingredients;

	public Recipe(int id, String cuisine, Collection<String> ingredients) {
		this.id = id;
		this.cuisine = cuisine;
		this.ingredients = new HashSet<>(ingredients);
	}

	@Override
	public String toString() {
		return "Recipe [id=" + id + ", cuisine=" + cuisine + ", ingredients=" + ingredients + "]";
	}
}
