package com.pezy.datafuse.license.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class ResultEnityZW {

	private Boolean success;
	private String reason;
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private List<Object> result= new ArrayList<Object>();
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Map<String, Object> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	public List<Object> getResult() {
		return result;
	}
	public void setResult(List<Object> result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "ResultEnity [success=" + success + ", reason=" + reason
				+ ", parameters=" + parameters + ", result=" + result + "]";
	}
	
}
