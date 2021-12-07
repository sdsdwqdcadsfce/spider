package com.peait.spider.result;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class Result<T> {
	
	private int code;
	private String msg;
	private T data;

	/**
	 *  成功时候的调用
	 * */
	public static  <T> Result<T> success(T data){
		return new Result<T>(data);
	}
	


	/**
	 *  失败时候的调用
	 * */
	public static  <T> Result<T> error(BindingResult result){
		List<FieldError> fieldErrors = result.getFieldErrors();
		String message = "";
		for (FieldError f:fieldErrors) {
			message+=f.getDefaultMessage()+" ";
		}
		Result<T> tResult = new Result<T>(500,message);
		return tResult;
	}
	
	public Result(T data) {
		this.code=200;
		this.msg="success";
		this.data = data;
	}

	public Result(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	


	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
