package com.malcom.library.android.module.stats;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class for sub-beacon data.
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class Subbeacon extends JSONObject {

	public static enum SubbeaconType { CUSTOM, SPECIAL, ERROR };

	
	private String name_;
	private SubbeaconType type_;
	private Hashtable<String, Object> params_;
	private double startedOn_;
	private double stoppedOn_ = 0;

	public Subbeacon(String name) throws JSONException {
		setName(name);
		setType(SubbeaconType.CUSTOM); //by default will be CUSTOM if it's created by user
		setParams(new Hashtable<String, Object>());
	}
	
	public Subbeacon(String name, SubbeaconType type, Hashtable<String, Object> params) throws JSONException {
		setName(name);
		setType(type);
		setParams(params);
	}

	public JSONObject getJsonObject() {
		return this;
	}

	public double getStartedOn() {
		return startedOn_;
	}

	public void setStartedOn(double startedOn) {
		this.startedOn_ = startedOn/ 1000;

		try {
			put("started_on", startedOn/ 1000);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public double getStoppedOn() {
		return stoppedOn_;
	}

	public void setStoppedOn(double stoppedOn) {
		this.stoppedOn_ = stoppedOn/ 1000;

		try {
			put("stopped_on", stoppedOn/ 1000);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name_;
	}

	public void setName(String name) {
		this.name_ = name;
		try {
			put("name", name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Hashtable<String, Object> getParams() {
		return this.params_;
	}

	public void setParams(Hashtable<String, Object> params) {
		this.params_ = params;
		try {
			put("parameters", new JSONObject(params));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public SubbeaconType getType() {
		return type_;
	}

	public void setType(SubbeaconType type) {
		this.type_ = type;
		try {
			put("type", type.name());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
