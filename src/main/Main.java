package main;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
	
	private static HashMap<String, ArrayList<Recipe>> cuisines;
	private static ArrayList<String> ingredients;
	private static ArrayList<Recipe> recipesAll;
	
	/*
	 * Authors: Julian McClinton jm2af
	 * 			Ryan McCampbell  rnm6u
	 */

	public static void main(String[] args) {
		init();
	}
	
	public static void init(){
		recipesAll = Parser.parseRecipeCSV("res/training.csv");
		ingredients = Parser.loadIngredients("res/ingredients.txt");
		cuisines = getCuisineList(recipesAll);
	}
	
	
	/**
	 * separates all recipes into separate cuisine
	 * @return
	 */
	public static HashMap<String, ArrayList<Recipe>> getCuisineList(ArrayList<Recipe> recipes){
		HashMap<String, ArrayList<Recipe>> res = new HashMap<>();
		for(Recipe r : recipes){
			ArrayList<Recipe> c;
			if(!res.containsKey(r.cuisine)){
				c = new ArrayList<>();
				res.put(r.cuisine, c);
			} else c = res.get(r.cuisine);
			c.add(r);
		}
		return res;
	}
	
	public static void printComposition(){
		
	}
	
	/**
	 * get the subset of training recipes
	 * @return
	 */
	public static ArrayList<Recipe> getTrainingSubset(){
		ArrayList<Recipe> res = new ArrayList<>();
		int k = 6;
		
		// for each cuisine
			// subset of cuisine = #recipes in cuisine / k
		
		return res;
	}

}
