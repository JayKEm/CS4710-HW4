package main;

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
	public static final double LEAF_THRESHOLD = .75;
	
	
	public static void main(String[] args) {
		recipesAll = Parser.parseRecipeCSV("res/training.csv");
		ingredients = new HashSet<>(Parser.loadIngredients("res/ingredients.txt"));
		Collections.shuffle(recipesAll);

		long buildTime, totTime = System.nanoTime();
		double totAvg = 0;
		for (int i = 0; i < CROSS_VAL_K; i++) {
			System.out.println("Analyzing subset: "+(i+1));
			
			List<Recipe> testing = partitionTrainingSet(i);
			ArrayList<Recipe> training = new ArrayList<>(recipesAll);
			training.removeAll(testing);
			
			buildTime = System.nanoTime();
			N0de tree = makeDecisionTree(training, ingredients);
			buildTime = System.nanoTime() - buildTime;
//			System.out.println("Built tree.");
//			System.out.println(tree);
//			tree.print();
			
			int correct = 0;
			for (Recipe r : testing) {
				String c = classify(tree, r);
				if(c.equals(r.cuisine)) correct++;
			}
			
			double avg = (double) correct / testing.size();
			System.out.printf("Correct: %d / %d (%.2f%%)\n", correct, testing.size(), 100*avg);
			totAvg += avg / CROSS_VAL_K;
			System.out.println("Tree Build Time: " + formatNanoTime(buildTime));
			System.out.println();
		}
		totTime = System.nanoTime() - totTime;
		System.out.println("Accuracy: " + f(100*totAvg, 2) + "%");
		System.out.println("Total Time: " + formatNanoTime(totTime));
	}
	
	public static String f(double i, int d){
		double doi = Math.pow(10, d);
		return String.valueOf(((int)(i*doi))/doi);
	}
	
	public static String formatNanoTime(long t){
		return f(t / 1000000000.0, 2) + " s";
	}

	/**
	 * Get the ith partition of the set and return the testing set
	 */
	public static List<Recipe> partitionTrainingSet(int k) {
		int num = recipesAll.size() / CROSS_VAL_K;
		return recipesAll.subList(k * num, (k + 1) * num);
	}

	/**
	 * Guess the cuisine given a recipe
	 */
	public static String classify(N0de node, Recipe r) {
		while (node.cuisine == null)
			node = (r.ingredients.contains(node.ingredient)) ? node.trueChild : node.falseChild;
		return node.cuisine;
	}

	// Not used
	public static String checkLeafAllSame(ArrayList<Recipe> set) {
		String cuisine = set.get(0).cuisine;
		for (Recipe r : set) {
			if (!r.cuisine.equals(cuisine)) {
				return null;
			}
		}
		return cuisine;
	}

	/**
	 * Check if the current set should be marked as a leaf and return its cuisine
	 */
	public static String checkLeafThreshold(ArrayList<Recipe> set) {
		HashMap<String, Integer> counts = new HashMap<>();
		for (Recipe recipe : set) {
			int count = counts.getOrDefault(recipe.cuisine, 0) + 1;
			if (count >= LEAF_THRESHOLD * set.size())
				return recipe.cuisine;
			counts.put(recipe.cuisine, count);
		}
		return null;
	}
	
	/**
	 * Create decisiion tree recursively
	 */
	public static N0de makeDecisionTree(ArrayList<Recipe> set, HashSet<String> ingreds) {
		String cuisine = checkLeafThreshold(set);
		if(cuisine != null)
			return new N0de(cuisine);
		
		String ingred = selectIngred(set, ingreds);
		ArrayList<Recipe> trueSet = new ArrayList<>();
		ArrayList<Recipe> falseSet = new ArrayList<>();
		
		for (Recipe r : set) {
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
	 */
	public static String selectIngred(ArrayList<Recipe> set, HashSet<String> ingreds) {
		double maxGain = Double.NEGATIVE_INFINITY;
		String maxIngred = null;
		for (String ingred : ingreds) {
			double g = gain(set, ingred);
			if (g > maxGain) {
				maxGain = g;
				maxIngred = ingred;
			}
		}		
		return maxIngred;
	}
	
	/**
	 * Compute the information gain, minus the set's entropy
	 */
	public static double gain(Collection<Recipe> set, String ingredient) {
		// Since the entropy of the initial set doesn't change between calls I
		// don't bother computing it
		ArrayList<Recipe> trueSet = new ArrayList<>();
		ArrayList<Recipe> falseSet = new ArrayList<>();
		for (Recipe recipe : set) {
			(recipe.ingredients.contains(ingredient) ? trueSet : falseSet).add(recipe);
		}
		return -(trueSet.size() * entropy(trueSet) +
				falseSet.size() * entropy(falseSet)) / set.size();
	}

	/**
	 * Calculate entropy of a set
	 */
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
