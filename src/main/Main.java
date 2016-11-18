package main;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

/*
 * Authors: Julian McClinton jm2af
 * 			Ryan McCampbell  rnm6u
 */

public class Main {
	
	public static HashMap<String, ArrayList<Recipe>> cuisines;
	public static HashMap<String, TreeSet<String>> ingredUnique;
	public static ArrayList<String> ingredients;
	public static ArrayList<Recipe> recipesAll;
	
	public static final int CROSS_VAL_K = 6;
	
	
	public static void main(String[] args) {
		init();
		partitionTrainingSet(CROSS_VAL_K);
	}
	
	public static void init(){
		recipesAll = Parser.parseRecipeCSV("res/training.csv");
		ingredients = Parser.loadIngredients("res/ingredients.txt");
		cuisines = getCuisineList(recipesAll);
		ingredUnique = uniqueIngredients();
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

	/**
	 * outputs the ingredient composition for each cuisine
	 */
	public static void writeComposition(){
		int i; HashMap<String, Integer> ing;
		try{
			PrintWriter w = new PrintWriter("res/composition.txt", "UTF-8");

			for (String c : new TreeSet<>(cuisines.keySet())){
				ing = new HashMap<>();
				for(Recipe r : cuisines.get(c)){
					for(String ingredient : r.ingredients){
						i = (ing.containsKey(ingredient)) ? ing.get(ingredient): 0;
						ing.put(ingredient, i+1);
					}
				}

				double tot = ing.size();
				w.println(tot);

				w.println(c+": ");
				for(String in : new TreeSet<>(ing.keySet()))
					w.println("\t[ "+in+": "+f(ing.get(in)/tot)+"% ]");
			}
			w.close();
		} catch(Exception e){
			e.printStackTrace();			
		}
	}
	
	/**
	 * creates a set of ingredients unique to each cuisine
	 * @return map of cuisine to list of unique ingredients
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, TreeSet<String>> uniqueIngredients(){
		HashMap<String, TreeSet<String>> cIng = new HashMap<>();
		HashMap<String, TreeSet<String>> ucIng; // uniqued

		// load all ingredients in cuisines
		for (String c : new TreeSet<>(cuisines.keySet())){
			TreeSet<String> ing = new TreeSet<>(); 
			for(Recipe r : cuisines.get(c)){
				for(String ingredient : r.ingredients){
					ing.add(ingredient);
				}
			}
			cIng.put(c, ing);
		}

		// clone
		ucIng = (HashMap<String, TreeSet<String>>) cIng.clone();
		for(String c : new TreeSet<>(ucIng.keySet()))
			ucIng.put(c, (TreeSet<String>) cIng.get(c).clone());

		// produce unique sets
		for(String c1 : cIng.keySet())
			for(String c2 : new TreeSet<>(cIng.keySet()))
				if(!c1.equals(c2)) ucIng.get(c1).removeAll(cIng.get(c2));
		return ucIng;
	}
	
	public static String f(double i){
		return String.valueOf(((int)(i*1000))/1000f);
	}
	
	/**
	 * get the subset of training recipes
	 * @return map of cuisines whose recipe list have been broken down into k arrays
	 */
	public static HashMap<String, ArrayList<ArrayList<Recipe>>> partitionTrainingSet(int k){
		HashMap<String, ArrayList<ArrayList<Recipe>>> res = new HashMap<>();
		
		// for each cuisine
			// subset of cuisine = #recipes in cuisine / k

		for (String c : new TreeSet<>(cuisines.keySet())){
			ArrayList<ArrayList<Recipe>> set = new ArrayList<>();
			ArrayList<Recipe> recipes = cuisines.get(c);
			int j = 0;
			
			for(int i = 0; i<k; i++) set.add(new ArrayList<Recipe>());
			for(Recipe r : recipes) set.get(j++ % k).add(r);
			res.put(c, set);
		}
		
		return res;
	}

	public static double gain(Collection<Recipe> set, String ingredient) {
		
		return 0.;
	}

	public static double entropy(Collection<Recipe> set) {
		HashMap<String, Integer> counts = new HashMap<>();
		for (Recipe recipe : set) {
			counts.put(recipe.cuisine, counts.getOrDefault(recipe.cuisine, 0) + 1);
		}
		double entropy = 0.0;
		for (int count : counts.values()) {
			double prob = (double)count / set.size();
			entropy -= prob * Math.log(prob);
		}
		return entropy;
	}
}
