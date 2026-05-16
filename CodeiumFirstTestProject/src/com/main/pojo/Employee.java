package com.main.pojo;

public class Employee {
// create fields 
	private int id;
	private String name; 
	private String email;
	// create constructor
	public Employee(int id, String name, String email) {
		this.id=id;
		this.name=name;
		this.email=email;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setEmail(String email) {
		this.email= email;
	}
	public String getEmail() {
		return email;
	}
	
//create hashCode and equals
	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", email=" + email + "]";
	}
	
}
