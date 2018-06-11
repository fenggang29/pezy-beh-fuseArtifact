package com.pezy.datafuse.license.entity;

public class ResultEnity {

	private Boolean result;
	private String errMsg;
	private Object extend;

	public void fromZw(ResultEnityZW resultEnityZW){
		this.setResult(resultEnityZW.getSuccess());
		this.setErrMsg(resultEnityZW.getReason());
		this.setExtend(resultEnityZW.getResult());
	}

	public ResultEnity(){
		this.result = true;
	}
	public ResultEnity(Boolean result){
		this.result = result;
	}
	public ResultEnity(Boolean result, String errMsg){
		this.result = result;
		this.errMsg = errMsg;
	}
	public ResultEnity(Boolean result, String errMsg, Object extend){
		this.result = result;
		this.errMsg = errMsg;
		this.extend = extend;
	}

	public boolean right(){
		return this.result == true;
	}
	public boolean wrong(){
		return this.result == false;
	}
	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Object getExtend() {
		return extend;
	}

	public void setExtend(Object extend) {
		this.extend = extend;
	}
}
