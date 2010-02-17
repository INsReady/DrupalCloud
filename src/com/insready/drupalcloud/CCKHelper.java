package com.insready.drupalcloud;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CCKHelper {
	private JSONObject mJso;
	public CCKHelper(JSONObject _jso){
		mJso = _jso;
	}
	public String unWrap(String field_name){
		try {
			return new JSONObject(new JSONArray(mJso.getString(field_name)).getString(0)).getString("value");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	public String unWrap(String field_name,String key){
		try {
			return new JSONObject(new JSONArray(mJso.getString(field_name)).getString(0)).getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
