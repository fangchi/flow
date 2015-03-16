package com.whty.flow.pojo;

import java.util.ArrayList;

public class TestBean {

	private String proA;
	
	private Integer proB;
	
	private ArrayList<TestBeanTwo> proC = new ArrayList<TestBeanTwo>();
	
	public TestBean(String proA, Integer proB) {
		super();
		this.proA = proA;
		this.proB = proB;
	}

	public String getProA() {
		return proA;
	}

	public void setProA(String proA) {
		this.proA = proA;
	}

	public Integer getProB() {
		return proB;
	}

	public void setProB(Integer proB) {
		this.proB = proB;
	}

	public ArrayList<TestBeanTwo> getProC() {
		return proC;
	}

	public void setProC(ArrayList<TestBeanTwo> proC) {
		this.proC = proC;
	}
	
}
