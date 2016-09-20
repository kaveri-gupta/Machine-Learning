//package Beans;

import java.util.HashMap;
import java.util.Map;

public class Feature {
	private String featureName;
	private Map<String, String> featureValues;
	
	public Feature(String featureName){
		this.featureName = featureName;
		featureValues = new HashMap<String, String>();
	}
	
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public Map<String, String> getFeatureValues() {
		return featureValues;
	}
	public void setFeatureValues(Map<String, String> featureValues) {
		this.featureValues = featureValues;
	}
	
	public void addFeatureValue(String key, String val){
		featureValues.put(key, val);
	}
	
	@Override
	public String toString() {
		return "Feature [featureName=" + featureName + ", featureValues="
				+ featureValues + "]";
	}
	
}
