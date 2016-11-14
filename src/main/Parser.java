package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
	
	/*
	 * Authors: Julian McClinton jm2af
	 * 			Ryan McCampbell  rnm6u
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
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			List<String> tokens = Arrays.asList(line.split(","));
			int id = Integer.valueOf(tokens.get(0));
			String cuisine = tokens.get(1).replace("\"", "");
			ArrayList<String> ingredients = new ArrayList<>(tokens.subList(2, tokens.size()));
			for (int i=0; i < ingredients.size(); i++) {
				ingredients.set(i, ingredients.get(i).replace("\"", ""));
			}
			recipes.add(new Recipe(id, cuisine, ingredients));
		}
		return recipes;
	}

	public static ArrayList<String> loadIngredients(String file){
		ArrayList<String> source = null;
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			try {
				source = new ArrayList<>();
				String line = br.readLine();

				while (line != null ) {
					source.add(line.replace("\"", ""));
					System.out.println(source.get(source.size()-1));
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
