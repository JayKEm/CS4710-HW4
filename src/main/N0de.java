package main;

public class N0de {
	public String cuisine, ingredient;
	public N0de trueChild, falseChild;

	public N0de (String cuisine){
		this.cuisine=cuisine;
	}
	
	public N0de(String ingredient, N0de trueNode, N0de falseNode){
		this.ingredient = ingredient;
		this.trueChild = trueNode;
		this.falseChild = falseNode;
	}
	
	public String toString(){ 
		String s = (cuisine!=null) ? cuisine : ingredient;
		return s +"\n"+ trueChild+"\n"+falseChild;
	}
	
	public void print() {
		print(0, -1);
	}
	
	private void print(int indent, int truth) {
		for (int i=0; i < indent; i++)
			System.out.print("  ");
		if (truth >= 0)
			System.out.print(truth == 1 ? "Yes: " : "No: ");
		if (cuisine != null) {
			System.out.println("Cuisine: " + cuisine);
		} else {
			System.out.println(ingredient);
			trueChild.print(indent + 1, 1);
			falseChild.print(indent + 1, 0);
		}
	}
}
