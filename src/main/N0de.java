package main;

public class N0de {
	public String cuisine, attribute;
	public N0de trueChild, falseChild;
	public N0de (String cuisine){
		this.cuisine=cuisine;
	}
	
	public N0de(String attr, N0de trueNode, N0de falseNode){
		this.attribute = attr;
		this.trueChild = trueNode;
		this.falseChild = falseNode;
	}
	
	public String toString(){ 
		String s = (cuisine!=null) ? cuisine : attribute;
		return s +"\n"+ trueChild+"\n"+falseChild;
	}
}

