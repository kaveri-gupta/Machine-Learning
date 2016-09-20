//package Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

//import Beans.Feature;
//import Beans.Node;
//import Startup.MainClass;

public class Utility {
	public static Map<String, Object> readFeatureFile(String fileName){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Feature> features = new ArrayList<Feature>();
		Feature f = null;
		try{
			Stream<String> file = Files.lines(Paths.get(fileName));
			List<List<String>> s = file.map(line -> line).filter(line -> Character.isDigit(line.charAt(0))).map(line -> line.split("\\s+")[1]).collect(Collectors.toList()).stream().map(line -> Arrays.asList(line.split(":"))).collect(Collectors.toList());
			features = s.stream().map(line -> {
				Feature f1 = new Feature(line.get(0));
				f1.setFeatureValues(Arrays.asList(line.get(1).split(",")).stream().map(l1 -> Arrays.asList(l1.split("="))).collect(Collectors.toMap(x -> x.get(1), x -> x.get(0))));
				//f1.setFeatureValues();
				return f1;
			}).collect(Collectors.toList());
//			System.out.println(features);
			map.put("features", features);
			file = Files.lines(Paths.get(fileName));
			List<String> s1 = file.map(line -> line).filter(line -> line.startsWith("classes")).map(l1 -> l1.split(":")[1]).collect(Collectors.toList());
			s1 = Arrays.asList(s1.get(0).split(","));
			map.put("categories", s1.stream().map(l1 -> Arrays.asList(l1. split("="))).collect(Collectors.toMap(x -> x.get(1), x -> x.get(0))));
		}catch(IOException e){
			System.out.println("Error in reading " + fileName);
			e.printStackTrace();
		}
		return map;
	}
	
	public static List<List<String>> readDataFile(String fileName, Set<Integer> missingData){
		List<List<String>> data = new ArrayList<List<String>>();
//		Set<Integer> missingData = new HashSet<>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			while((line = br.readLine()) != null){
				List<String> words = Arrays.asList(line.split(","));
				data.add(words);
				missingData.addAll(Arrays.stream(IntStream.range(0, words.size()).filter(i -> words.get(i).equalsIgnoreCase("?")).toArray()).boxed().collect(Collectors.toSet()));
				/*if(words.contains("?") && !missingData.contains(words.indexOf("?"))){
					missingData.add(words.indexOf("?"));
					//System.out.println(words);
				}*/
			}
		}catch(IOException e){
			System.out.println("Error in reading " + fileName);
			e.printStackTrace();
		}
		//System.out.println(missingData);
		/*if(0 != missingData.size()){
			Utility.processMissingData(data, missingData);
		}*/
		return data;
	}
	
	private static double getCurrentEntropy(Map<String, String> categories, List<List<String>> data){
		double entropy = 0f;
		double count;
		if(0 == data.size())
			return 0.0;
		int index = data.get(0).size() - 1;
		int dataSize = data.size();
		for(String cat : categories.keySet()){
			count = 0;
			for(List<String> row: data){
				if(row.get(index).equalsIgnoreCase(cat))
					count++;
			}
//			System.out.println(count);
			count = count / dataSize;
			if(0 != count)
				entropy = entropy - ((count) * Math.log10(count) / Math.log10(2));
//			System.out.println(entropy);
		}
//		System.out.println(entropy);
		return entropy;
	}
	
	public static Node createDecisionTree(List<Feature> features, Map<String, String> categories, List<List<String>> data, Node root, List<Integer> visitedFeatures, int depth){
		int index = 0;
		double maxIG = 0.0;
		Feature highGain = null;
		int rootIndex = 0;
		double currentEntropy = getCurrentEntropy(categories, data);
		if(0.0 == currentEntropy){
			if(0 == data.size()){
				return null;
			}
			List<String> temp = data.get(0);
			root.setLevel(visitedFeatures.size());
			root.setLabel(temp.get(temp.size() - 1));
			return root;
		}
		for(Feature f : features){
			index = features.indexOf(f);
			if(!visitedFeatures.contains(index)){
	//			System.out.print(f.getFeatureName());
				final int i = index;
				double entropy = 0.0;
				for(String attr : f.getFeatureValues().keySet()){
					List<List<String>> smallList = data.stream().filter(row -> row.get(i).equalsIgnoreCase(attr)).collect(Collectors.toList());
					entropy = entropy + ((double)smallList.size() / (double)data.size() * getCurrentEntropy(categories, smallList));
	//				System.out.println(smallList);
				}
	//			System.out.print(" E = " + entropy);
	//			System.out.println(" IG = " + (currentEntropy - entropy));
				entropy = currentEntropy - entropy;
				if(entropy > maxIG){
					maxIG = entropy;
					highGain = f;
					rootIndex = index;
				}
			}
		}
//		System.out.println("highGain = " + highGain);
		root.setAttributeName(highGain.getFeatureName());
		root.setSequenceNumber(rootIndex);
		root.setLevel(visitedFeatures.size());
		if(root.getLevel() < depth - 1 || depth == -1){
			if(features.size() != 1){
				for(String f : highGain.getFeatureValues().keySet()){
					List<Integer> temp = new ArrayList<Integer>(visitedFeatures);
					temp.add(rootIndex);
					int j = rootIndex;
					Node n = new Node();
					n = createDecisionTree(features, categories, data.stream().filter(row -> row.get(j).equalsIgnoreCase(f)).collect(Collectors.toList()), n, temp, depth);
					if(null == n){
						n = new Node();
						n.setLabel(getMostCommonLabel(categories, data));
						n.setLevel(visitedFeatures.size() + 1);
					}
					root.addChildNode(f, n);
				}
			}
		}else{
			if(features.size() != 1){
				for(String f : highGain.getFeatureValues().keySet()){
					List<Integer> temp = new ArrayList<Integer>(visitedFeatures);
					temp.add(rootIndex);
					int j = rootIndex;
					String label = Utility.getMostCommonLabel(categories, data.stream().filter(row -> row.get(j).equalsIgnoreCase(f)).collect(Collectors.toList()));
					if(null == label){
						label = Utility.getMostCommonLabel(categories, data);
					}
					Node n = new Node();
					n.setLevel(depth);
					n.setLabel(label);
					root.addChildNode(f, n);
				}
			}
		}
//		System.out.println("Root = " + root);
		return root;
	}
	public static String getMostCommonLabel(Map<String, String> categories, List<List<String>> data){
		String label = "";
		int maxCount = 0;
		int count;
		if(0 == data.size())
			return null;
		for(String c : categories.keySet()){
			count = data.stream().filter(line -> c.equalsIgnoreCase(line.get(line.size() - 1))).collect(Collectors.toList()).size();
			if(count > maxCount){
				maxCount = count;
				label = c;
			}
		}
		return label;
	}
	public static double testData(List<List<String>> data, Node root){
		double accuracy = 0.0;
		int spotOn = data.stream().filter(test -> test.get(test.size() - 1).equalsIgnoreCase(Utility.classifyData(test, root))).collect(Collectors.toList()).size();
		accuracy = (double)spotOn / (double)data.size() * 100;
		return accuracy;
	}
	public static String classifyData(List<String> test, Node root){
		if(-1 == root.getSequenceNumber()){
			return root.getLabel();
		}else{
			String attr = test.get(root.getSequenceNumber());
			Node n = root.getChildNode(attr);
			if(null == n)
				return null;
			else
				return classifyData(test, root.getChildNode(attr));
		}
	}
	public static int doCrossValidation(String directoryName, List<Integer> depthList, List<Feature> featureList, Map<String, String> categories){
		List<List<List<String>>> data = new ArrayList<List<List<String>>>();
		Set<Integer> missingData = new HashSet<>();
		double maxAccuracy = 0;
		List<File> CVFiles = Arrays.asList(new File(directoryName).listFiles());
		for(File f : CVFiles){
			data.add(Utility.readDataFile(f.getPath(), missingData));
		}
		if(1 == depthList.size() && -1 == depthList.get(0)){
			int strategy = 0;
			for(int j = 0; j <= 2; j++){
				int i = 0;
				double avgAccuracy = 0.0;
				double accuracy = 0.0;
				List<Double> accuracyList = new ArrayList<>();
				for(File f : CVFiles){
					int index = i;
	//				System.out.println("index = " + index);
					List<List<String>> dataset = new ArrayList<>();
					List<List<String>> trainingData = new ArrayList<>();
					for(File f1 : CVFiles){
						if(f1 == f)
							dataset = Utility.readDataFile(f1.getPath(), missingData);
						else
							trainingData.addAll( Utility.readDataFile(f1.getPath(), missingData));
					}
					//List<List<String>> trainingData = new ArrayList<>();
					//trainingData.addAll( data.stream().filter(list -> data.indexOf(list) != index).flatMap(List :: stream).collect(Collectors.toList()));
					Node root = new Node();
					Utility.processMissingData(trainingData, trainingData, missingData, i);
					Utility.processMissingData(dataset, trainingData, missingData, i == 1 ? 0 : i);
					root = Utility.createDecisionTree(featureList, categories, trainingData, root, new ArrayList<Integer>(), -1);
	//				System.out.println("Root = " + Utility.getTreeDepth(root, 0));
	//				System.out.println("Accuracy = " + Utility.testData(dataset, root));
					accuracyList.add(Utility.testData(dataset, root));
					accuracy += accuracyList.get(index);
	//				System.out.println(trainingData.size());
	//				System.out.println(dataset.size());
					i++;
				}
				avgAccuracy = accuracy / data.size();
				if(avgAccuracy > maxAccuracy){
					maxAccuracy = avgAccuracy;
					strategy = j;
				}
				double avg = avgAccuracy;
				System.out.println("Average Accuracy = " + avgAccuracy);
				System.out.println("Standard Deviation for = " + (strategy == 0 ? "Most Common Feature" : strategy == 1 ? "Most Common Feature for that label" : "Considering as new feature value") + " with accuracy = " + maxAccuracy +" is " + Math.sqrt(accuracyList.stream().mapToDouble(val -> {
					val = val - avg;
					val = val * val;
					return val;
				}).average().getAsDouble()) + "\n===========");
			}
			System.out.println("Best method is " + (strategy == 0 ? "Most Common Feature" : strategy == 1 ? "Most Common Feature for that label" : "Considering as new feature value") + " with accuracy = " + maxAccuracy);
			return strategy;
		}else{
			int bestDepth = 0;
			for(int depth : depthList){
				int i = 0;
				double avgAccuracy = 0;
				System.out.println("===========\nDepth = " + depth);
				List<Double> accuracyList = new ArrayList<Double>();
				for(List<List<String>> dataset : data){
					int index = i;
	//				System.out.println("index = " + index);
					dataset = new ArrayList<>(dataset);
					List<List<String>> trainingData = new ArrayList<>();
					trainingData.addAll( data.stream().filter(list -> data.indexOf(list) != index).flatMap(List :: stream).collect(Collectors.toList()));
					Node root = new Node();
					root = Utility.createDecisionTree(featureList, categories, trainingData, root, new ArrayList<Integer>(), depth);
	//				System.out.println("Root = " + Utility.getTreeDepth(root, 0));
	//				System.out.println("Accuracy = " + Utility.testData(dataset, root));
					accuracyList.add(Utility.testData(dataset, root));
					avgAccuracy += accuracyList.get(index);
	//				System.out.println(trainingData.size());
	//				System.out.println(dataset.size());
					i++;
				}
				//System.out.println("Accuracy List : " + accuracyList);
				avgAccuracy = avgAccuracy / data.size();
				System.out.println("Average Accuracy = " + avgAccuracy);
				double avg = avgAccuracy;
				if(avgAccuracy > maxAccuracy){
					maxAccuracy = avgAccuracy;
					bestDepth = depth;
				}
				System.out.println("Standard Deviation for depth = " + depth +" is " + Math.sqrt(accuracyList.stream().mapToDouble(val -> {
					val = val - avg;
					val = val * val;
					return val;
				}).average().getAsDouble()));
			}
			System.out.println("Best Accuracy = " + maxAccuracy + " at depth = " + bestDepth);
			return bestDepth;
		}
	}
	public static int getTreeDepth(Node root, int level){
		int depth = 0;
		if(root.hasChildren()){
			for(Node n : root.getChildren().values()){
				if(null != n){
					depth = getTreeDepth(n, root.getLevel());
					if(depth > level)
						level = depth;
				}
			}
			return level;
		}else if(root.getLevel() > level)
			return root.getLevel();
		else return level;
	}
	public static void processMissingData(List<List<String>> data, List<List<String>> trainingData, Set<Integer> missingData, int method){
		for(int index : missingData){
			switch(method){
			case 1 : Utility.setMajorityFeatureForLabel(data, trainingData, index);
					break;
			case 2 : Utility.addNewFeature(index);
					break;
			case 0 : 
			default: Utility.setMajorityFeatureValue(data, trainingData, index);
				break;
			}
			
			//System.out.println(data);
		}
		//System.out.println(missingValueMap);
	}
	public static void setMajorityFeatureValue(List<List<String>>data, List<List<String>> trainingData, int index){
		String feature = "";
		int maxCount = 0;
		int count;
		//System.out.println("Before :: " + data.size());
		List<Feature> featureList = MainClass.getFeatureList();
		for(String c : featureList.get(index).getFeatureValues().keySet()){
			count = trainingData.stream().filter(line -> c.equalsIgnoreCase(line.get(index))).collect(Collectors.toList()).size();
			if(count > maxCount){
				maxCount = count;
				feature = c;
			}
		}
		String f = feature;
		//System.out.println(data.stream().filter(line -> ("?").equalsIgnoreCase(line.get(index))).collect(Collectors.toList()).size());
		//data = data.stream().filter(line -> ("?").equalsIgnoreCase(line.get(index))).map(line -> {line.set(index, f); return line;}).collect(Collectors.toList());
		data = data.stream().map(line -> {if(("?").equalsIgnoreCase(line.get(index)))line.set(index, f); return line;}).collect(Collectors.toList());
		//System.out.println("After ::" + data.size());
	}
	public static void setMajorityFeatureForLabel(List<List<String>> data, List<List<String>> trainingData, int index){
		Set<String> categories = MainClass.getCategories();
		for(String c : categories){
			Utility.setMajorityFeatureValue(data.stream().filter(line -> c.equalsIgnoreCase(line.get(line.size() - 1))).collect(Collectors.toList()), trainingData, index);
			//System.out.println("Label = " + feature);
		}
		Utility.setMajorityFeatureValue(data, trainingData, index);
	}
	public static void addNewFeature(int index){
		List<Feature> featureList = MainClass.getFeatureList();
		featureList.get(index).getFeatureValues().put("?", "Unknown");
	}
}
