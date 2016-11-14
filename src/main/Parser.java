package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
 * Authors: Julian McClinton jm2af
 * 			Ryan McCampbell  rnm6u
 */

public class Parser {

	/**
	 * loads list of all recipes from training file
	 * @param file
	 * @return all recipes
	 */
	public static ArrayList<Recipe> parseRecipeCSV(String file) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		ArrayList<Recipe> recipes = new ArrayList<>();
		Pattern pattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			Matcher match = pattern.matcher(line);
			ArrayList<String> tokens = new ArrayList<>();
			while (match.find()) {
				String group = match.group(1);
				if (group != null)
					tokens.add(group);
				else
					tokens.add(match.group(2));
			}
			int id = Integer.valueOf(tokens.get(0));
			String cuisine = tokens.get(1);
			List<String> ingredients = tokens.subList(2, tokens.size());
			recipes.add(new Recipe(id, cuisine, ingredients));
		}
		return recipes;
	}

	/**
	 * Loads list of possible ingredients user is allowed to use
	 * @param file
	 * @return things
	 */
	public static ArrayList<String> loadIngredients(String file){
		ArrayList<String> source = null;
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			try {
				source = new ArrayList<>();
				String line = br.readLine();

				while (line != null) {
					source.add(line.replace("\"", ""));
					line = br.readLine();
				}
			} finally {
				br.close();
			}
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		
		return source;
	}

}
