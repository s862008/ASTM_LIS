package lis;

import java.util.HashMap;
import java.util.Map;

public class Result {

    private String labId;
    private Map<String, String> results;
    private Map<String, String> refernces;
    private Map<String, String> flags;

    public Result(String labId) {
        results = new HashMap();
        refernces = new HashMap();
        flags = new HashMap();
        this.labId = labid;
    }

    public String getLabId() {
        return labId;
    }

    public void putValue(String key, String value) {
        getResults().put(key, value);
    }

    public String getValue(String key) {
        return getResults().get(key);
    }

    public void putNormal(String key, String value) {
        getNormals().put(key, value);
    }

    public String getNormal(String key) {
        return getNormals().get(key);
    }

    public void putFlag(String key, String value) {
        getFlags().put(key, value);
    }

    public String getFlag(String key) {
        return getFlags().get(key);
    }

    public Map<String, String> getResults() {
        return results;
    }
 
    public Map<String, String> getNormals() {
        return refernces;
    }

    public Map<String, String> getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        return "Result{" + "labId=" + labId + ", results=" + results + ", refernces=" + refernces + ", flags=" + flags + '}';
    }
        
}
