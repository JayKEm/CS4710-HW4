package main;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
		
		double totAvg = 0;
		for (int i =0 ; i< CROSS_VAL_K; i++){
			int correct=0;
			System.out.println("Analyzing subset: "+(i+1));
			
			List<Recipe> testing = partitionTrainingSet(i);
			ArrayList<Recipe> training = new ArrayList<>(recipesAll);
			training.removeAll(testing);
			
			N0de tree = selectFeature(training);
			System.out.println("Built tree.");
			for(Recipe r : testing){
				String c = classify(tree, r);
				if(c.equals(r.cuisine)) correct++;
			}
			
			totAvg += correct/(CROSS_VAL_K*(double)testing.size());
		}
		
		System.out.println("Accuracy = " + f(100*totAvg, 2)+"%");
	}
	
	public static String classify(N0de node, Recipe r){
		while(node.cuisine==null)
			node = (r.ingredients.contains(node.attribute)) ? node.trueChild : node.falseChild;
		return node.cuisine;
	}
	
	public static void init(){
		recipesAll = Parser.parseRecipeCSV("res/training.csv");
		ingredients = Parser.loadIngredients("res/ingredients.txt");
		cuisines = getCuisineList(recipesAll);
		ingredUnique = uniqueIngredients();
	}
	
	/**
	 * Create decisiion tree recursively
	 * @param set
	 * @return
	 */
	public static N0de selectFeature(ArrayList<Recipe> set){
		String c = set.get(0).cuisine;
		boolean sameCuisine = true;
		for(Recipe r : set){
			if(!r.cuisine.equals(c)){
				sameCuisine = false;
				break;
			}
		}
		
		if(sameCuisine) return new N0de(c);
		
		ArrayList<Recipe> trueSet = new ArrayList<>();
		ArrayList<Recipe> falseSet = new ArrayList<>();
		String ingred = selectIngred(set);
		
		for(Recipe r : set){
			if(r.ingredients.contains(ingred)) trueSet.add(r);
			else falseSet.add(r);
		}
		
		N0de trueNode = selectFeature(trueSet);
		N0de falseNode = selectFeature(falseSet);
		
		return new N0de(ingred, trueNode, falseNode);
	}
	
	
	/**
	 * find the ingredient with highest gain
	 * @param set
	 * @return
	 */
	public static String selectIngred(ArrayList<Recipe> set){
		double maxGain = Double.NEGATIVE_INFINITY;
		String maxIngred = null;
		for(String ingred : ingredients){
			double g = gain(set, ingred);
			if (g > maxGain) {
				maxGain = g;
				maxIngred = ingred;
			}
		}
		
		return maxIngred;
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
					w.println("\t[ "+in+": "+f(ing.get(in)/tot,3)+"% ]");
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
	
	public static String f(double i, int d){
		double doi = Math.pow(10, d);
		return String.valueOf(((int)(i*doi))/doi);
	}
	
	public static List<Recipe> partitionTrainingSet(int k){
		int num = recipesAll.size()/CROSS_VAL_K;
		return recipesAll.subList(k*num, (k+1)*num);
	}

	public static double gain(Collection<Recipe> set, String ingredient) {
		ArrayList<Recipe> trueSet = new ArrayList<>();
		ArrayList<Recipe> falseSet = new ArrayList<>();
		for (Recipe recipe : set) {
			(recipe.ingredients.contains(ingredient) ? trueSet : falseSet).add(recipe);
		}
		return entropy(set) - (
				trueSet.size() * entropy(trueSet) +
				falseSet.size() * entropy(falseSet)) / set.size();
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
