package main;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;


/*
 * Authors: Julian McClinton jm2af
 * 			Ryan McCampbell  rnm6u
 */

public class Main {
	
	public static HashMap<String, ArrayList<Recipe>> cuisines;
	public static ArrayList<String> ingredients;
	public static ArrayList<Recipe> recipesAll;
	
	public static final int CROSS_VAL_K = 6;
	
	
	public static void main(String[] args) {
		init();
		writeUniqueComp();
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
	
	@SuppressWarnings("unchecked")
	public static void writeUniqueComp(){
		HashMap<String, TreeSet<String>> cIng = new HashMap<>();
		HashMap<String, TreeSet<String>> ucIng; // uniqued
		try{
			PrintWriter w = new PrintWriter("res/composition_unique.txt", "UTF-8");

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
			for(String c : new TreeSet<>(ucIng.keySet())){
				ucIng.put(c, (TreeSet<String>) cIng.get(c).clone());
			}
			
			// produce unique sets
			for(String c1 : cIng.keySet()){
				w.print(c1+": ");
				for(String c2 : new TreeSet<>(cIng.keySet())){
					if(!c1.equals(c2))
						ucIng.get(c1).removeAll(cIng.get(c2));
				}		
				w.println(ucIng.get(c1).size());
				for(String in : ucIng.get(c1))
					w.println("\t[ "+in+" ]");
			}
						
			w.close();
		} catch(Exception e){
			e.printStackTrace();			
		}
	}
	
	public static String f(double i){
		return String.valueOf(((int)(i*1000))/1000f);
	}
	
	/**
	 * get the subset of training recipes
	 * @return
	 */
	public static ArrayList<Recipe> partitionTrainingSet(int k){
		ArrayList<Recipe> res = new ArrayList<>();
		
		// for each cuisine
			// subset of cuisine = #recipes in cuisine / k
		
		return res;
	}

}
