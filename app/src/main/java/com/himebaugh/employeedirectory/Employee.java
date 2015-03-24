package com.himebaugh.employeedirectory;

public class Employee {
	Integer _empID;
	String _firstName;
	String _lastName;
	String _title;
	String _department;
	String _city;
	String _officePhone;
	String _mobilePhone;
	String _email;
	String _picture;

	// constructor
	public Employee() {

	}

	// constructor with parameters
	public Employee(Integer empID, String firstName, String lastName, String title, String department, String city, String officePhone, String mobilePhone, String email, String picture) {
		this._empID = empID;
		this._firstName = firstName;
		this._lastName = lastName;
		this._title = title;
		this._department = department;
		this._city = city;
		this._officePhone = officePhone;
		this._mobilePhone = mobilePhone;
		this._email = email;
		this._picture = picture;
	}

	// All set methods

	public void setId(Integer empID) {
		this._empID = empID;
	}

	public void setFirstName(String firstName) {
		this._firstName = firstName;
	}

	public void setLastName(String lastName) {
		this._lastName = lastName;
	}

	public void setTitle(String title) {
		this._title = title;
	}

	public void setDepartment(String department) {
		this._department = department;
	}

	public void setCity(String city) {
		this._city = city;
	}

	public void setOfficePhone(String officePhone) {
		this._officePhone = officePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this._mobilePhone = mobilePhone;
	}

	public void setEmail(String email) {
		this._email = email;
	}

	public void setPicture(String picture) {
		this._picture = picture;
	}
	
	// All get methods

	public Integer getId() {
		return this._empID;
	}

	public String getFirstName() {
		return this._firstName;
	}

	public String getLastName() {
		return this._lastName;
	}

	public String getTitle() {
		return this._title;
	}

	public String getDepartment() {
		return this._department;
	}

	public String getCity() {
		return this._city;
	}

	public String getOfficePhone() {
		return this._officePhone;
	}

	public String getMobilePhone() {
		return this._mobilePhone;
	}

	public String getPicture() {
		return this._picture;
	}
	
	//
	@Override
	public String toString() {
		return _firstName + " " + _lastName + "\n" + _title;
	}


}