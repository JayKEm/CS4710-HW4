package main;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/*
 * Authors: Julian McClinton jm2af
 * 			Ryan McCampbell  rnm6u
 */

public class Main {
	
	public static HashMap<String, ArrayList<Recipe>> cuisines;
	public static HashMap<String, TreeSet<String>> ingredUnique;
	public static HashSet<String> ingredients;
	public static ArrayList<Recipe> recipesAll;
	
	public static final int CROSS_VAL_K = 6;
	
	
	public static void main(String[] args) {
		init();
		long build, tot = System.nanoTime();
		double totAvg = 0;
		for (int i =0 ; i< CROSS_VAL_K; i++){
			System.out.println("Analyzing subset: "+(i+1));
			
			List<Recipe> testing = partitionTrainingSet(i);
			ArrayList<Recipe> training = new ArrayList<>(recipesAll);
			training.removeAll(testing);
			
			build = System.nanoTime();
			N0de tree = makeDecisionTree(training, ingredients);
			build = System.nanoTime() - build;
//			System.out.println("Built tree.");
//			tree.print();
			
			int correct=0;
			for(Recipe r : testing){
				String c = classify(tree, r);
				if(c.equals(r.cuisine)) correct++;
			}
			
			double avg = (double) correct / testing.size();
			System.out.println("Correct: " + correct + " / " + testing.size() + 
					" (" + f(100*avg, 2) + "%)");
			totAvg += avg / CROSS_VAL_K;
			System.out.println("Tree Build Time: "+formatNanoTime(build));
			System.out.println();
		}
		tot = System.nanoTime() - tot;
		System.out.println("Accuracy = " + f(100*totAvg, 2) + "%");
		System.out.println("Total Time: " + formatNanoTime(tot));
	}
	
	public static void init(){
		recipesAll = Parser.parseRecipeCSV("res/training.csv");
		ingredients = new HashSet<>(Parser.loadIngredients("res/ingredients.txt"));
		Collections.shuffle(recipesAll);
	}
	
	public static String f(double i, int d){
		double doi = Math.pow(10, d);
		return String.valueOf(((int)(i*doi))/doi);
	}
	
	public static String formatNanoTime(long t){
		return f(t/1000000000d, 2) + " s";	
	}
	
	public static List<Recipe> partitionTrainingSet(int k){
		int num = recipesAll.size()/CROSS_VAL_K;
		return recipesAll.subList(k*num, (k+1)*num);
	}

	public static String classify(N0de node, Recipe r){
		while(node.cuisine==null)
			node = (r.ingredients.contains(node.ingredient)) ? node.trueChild : node.falseChild;
		return node.cuisine;
	}
	
	/**
	 * Create decisiion tree recursively
	 * @param set
	 * @return
	 */
	public static N0de makeDecisionTree(ArrayList<Recipe> set, HashSet<String> ingreds) {
		String c = set.get(0).cuisine;
		boolean sameCuisine = true;
		for(Recipe r : set){
			if(!r.cuisine.equals(c)){
				sameCuisine = false;
				break;
			}
		}
		
		if(sameCuisine) return new N0de(c);
		
		String ingred = selectIngred(set, ingreds);
		ArrayList<Recipe> trueSet = new ArrayList<>();
		ArrayList<Recipe> falseSet = new ArrayList<>();
		
		for(Recipe r : set){
			if(r.ingredients.contains(ingred)) trueSet.add(r);
			else falseSet.add(r);
		}

		ingreds.remove(ingred);
		N0de trueNode = makeDecisionTree(trueSet, ingreds);
		N0de falseNode = makeDecisionTree(falseSet, ingreds);
		ingreds.add(ingred);
		return new N0de(ingred, trueNode, falseNode);
	}
	
	/**
	 * find the ingredient with highest gain
	 * @param set
	 * @return
	 */
	public static String selectIngred(ArrayList<Recipe> set, HashSet<String> ingreds){
		double maxGain = Double.NEGATIVE_INFINITY;
		String maxIngred = null;
		for(String ingred : ingreds){
			double g = gain(set, ingred);
			if (g > maxGain) {
				maxGain = g;
				maxIngred = ingred;
			}
		}
		
		return maxIngred;
	}
	
	public static double gain(Collection<Recipe> set, String ingredient) {
		ArrayList<Recipe> trueSet = new ArrayList<>();
		ArrayList<Recipe> falseSet = new ArrayList<>();
		for (Recipe recipe : set) {
			(recipe.ingredients.contains(ingredient) ? trueSet : falseSet).add(recipe);
		}
		return -(trueSet.size() * entropy(trueSet) +
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


	/********** Unused **********/

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
	
}
