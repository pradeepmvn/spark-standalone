package org.optum.spark.stream.core;

import java.io.Serializable;

import com.google.gson.Gson;


/**
 * Model that writes dat to a persistent store
 * @author pmamill
 *
 */
public class OpsResult implements Serializable{
	private static final long serialVersionUID = 13421541625752L;
	public OpsResult(double result, String originalRow) {
		super();
		this.result = result;
		this.originalRow = originalRow;
	}
	
	private double result;
	private String originalRow;
	public double getResult() {
		return result;
	}
	public void setResult(double result) {
		this.result = result;
	}
	public String getOriginalRow() {
		return originalRow;
	}
	public void setOriginalRow(String originalRow) {
		this.originalRow = originalRow;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new Gson().toJson(this);
	}
}
