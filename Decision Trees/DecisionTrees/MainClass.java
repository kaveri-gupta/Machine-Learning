//package Startup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import Beans.Feature;
//import Beans.Node;
//import Utilities.Utility;

public class MainClass {
	private static List<Feature> featureList = new ArrayList<Feature>();
	private static List<List<String>> trainingData = new ArrayList<List<String>>();
	private static List<List<String>> testData = new ArrayList<>();
	private static Map<String, String> categories;
	private static Set<Integer> missingData = new HashSet<>();
	private static Node root = new Node();;
	
	public static void main(String[] args) {
		//System.out.println("Hello World!");
		int optDepth = -1;
		Map<String, Object> map = Utility.readFeatureFile(args[0]);
		featureList = (List<Feature>)map.get("features");
		categories = (Map<String, String>)map.get("categories");
		trainingData = Utility.readDataFile(args[1], missingData);
	//	root = Utility.createDecisionTree(featureList, categories, trainingData, root, new ArrayList<Integer>(), -1);
	//	System.out.println("Tree = " + root);
	//	System.out.println("Depth of the tree is = " + Utility.getTreeDepth(root, 0));
	//	missingData = new HashSet<>();
		testData = Utility.readDataFile(args[2], missingData);
		/*if(0 != missingData.size()){
			Utility.processMissingData(testData, trainingData, missingData);
		}*/
	//	System.out.println("Accuracy of test file = " + Utility.testData(testData, root));
		try{
			int i = 4;
			List<Integer> depthList = new ArrayList<Integer>();
			while(true){
				try{
					int h = Integer.parseInt(args[i++]);
					depthList.add(h);
				}
				catch(Exception e){
					break;
				}
			}
			if(1 == depthList.size() && -1 == depthList.get(0)){
				int method = Utility.doCrossValidation(args[3], depthList, featureList, categories);
				//System.out.println("Method = " + (method == 0 ? "Most Common Feature" : method == 1 ? "Most Common Feature for that label" : "Considering as new feature value"));
				Utility.processMissingData(trainingData, trainingData, missingData, method);
				Utility.processMissingData(testData, trainingData, missingData, method);
				root = Utility.createDecisionTree(featureList, categories, trainingData, new Node(), new ArrayList<Integer>(), optDepth);
			}else{
				optDepth = Utility.doCrossValidation(args[3], depthList, featureList, categories);
				System.out.println("Optimum depth = " + optDepth);
				root = Utility.createDecisionTree(featureList, categories, trainingData, new Node(), new ArrayList<Integer>(), optDepth);
			}
		}catch(Exception e){
			root = Utility.createDecisionTree(featureList, categories, trainingData, root, new ArrayList<Integer>(), optDepth);
		}finally{
			System.out.println("===========");
		//	System.out.println("Tree is " + root);
			double accuracy = Utility.testData(testData, root);
			System.out.println("Accuracy = " + accuracy);
			System.out.println("Error = " + (100.0 - accuracy));
			System.out.println("Depth of the tree is = " + Utility.getTreeDepth(root, 0));
		}
	}
	public static List<Feature> getFeatureList(){
		return featureList;
	}
	public static Set<String> getCategories(){
		return categories.keySet();
	}
}
