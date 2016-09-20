//package Beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
	private int sequenceNumber;
	private String attributeName;
	private Map<String, Node> children;
	private String label;
	private int level;
	
	public Node(){
		this.sequenceNumber = -1;
		this.attributeName = null;
		children = new HashMap<String, Node>();
		this.label = null;
		this.level = -1;
	}
	public Node(String attributeName){
		this.sequenceNumber = -1;
		this.attributeName = attributeName;
		children = new HashMap<String, Node>();
		this.label = null;
		this.level = -1;
	}
	
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public Map<String, Node> getChildren() {
		return children;
	}
	public void setChildren(Map<String, Node> children) {
		this.children = children;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	public boolean hasChildren(){
		if(this.children.size() > 0)
			return true;
		else return false;
	}
	public void addChildNode(String parentAttribute, String newFeature){
		Node n = new Node(newFeature);
		this.children.put(parentAttribute, n);
	}
	public void addChildNode(String parentAttribute, Node child){
		this.children.put(parentAttribute, child);
	}
	public Node getChildNode(String attrVal){
		return this.children.get(attrVal);
	}
	
	@Override
	public String toString() {
		return "Node [sequenceNumber=" + sequenceNumber + ", attributeName=" + attributeName + ", children=" + children
				+ ", label=" + label + ", level=" + level + "]";
	}
	
}
