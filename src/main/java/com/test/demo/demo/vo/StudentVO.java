package com.test.demo.demo.vo;

public class StudentVO {
	private String name;
	
	private String age;

	public StudentVO() {
		super();
	}

	public StudentVO(String name, String age) {
		super();
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}
	
}
