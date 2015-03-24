package com.himebaugh.employeedirectory;

public class WidgetItem {
	Integer _empID;
	String _name;
	String _title;
	String _department;
	String _picture;

    public WidgetItem(Integer empID, String name, String title, String department, String picture) {
		this._empID = empID;
		this._name = name;
		this._title = title;
		this._department = department;
		this._picture = picture;
    }
}
