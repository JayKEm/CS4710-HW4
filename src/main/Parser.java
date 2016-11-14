package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Parser {
	
	/*
	 * Authors: Julian McClinton jm2af
	 * 			Ryan McCampbell  rnm6u
	 */

	public static ArrayList<String> loadIngredients(){
		ArrayList<String> source = null;
		try{
			BufferedReader br = new BufferedReader(new FileReader("res/ingredients.txt"));
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
		} catch(Exception e){
		}
		
		return source;
	}

}
