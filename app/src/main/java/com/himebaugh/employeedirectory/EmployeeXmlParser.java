package com.himebaugh.employeedirectory;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

public class EmployeeXmlParser {

	// names of the XML tags
	static final String EMPLOYEES = "employees";
	static final String EMPLOYEE = "employee";
	static final String ID = "id";
	static final String FIRSTNAME = "firstName";
	static final String LASTNAME = "lastName";
	static final String TITLE = "title";
	static final String DEPARTMENT = "department";
	static final String CITY = "city";
	static final String OFFICEPHONE = "officePhone";
	static final String MOBILEPHONE = "mobilePhone";
	static final String EMAIL = "email";
	static final String PICTURE = "picture";

	ArrayList<Employee> employeeList = null;
	private Employee currentEmployee = null;
	private boolean done = false;
	private String currentTag = null;

	public ArrayList<Employee> parse(Context context) {

		XmlPullParser parser = context.getResources().getXml(R.xml.employee_list);

		try {

			int eventType = parser.getEventType();

			// Following logic modified from http://www.ibm.com/developerworks/library/x-android/
			// Also look at http://developer.android.com/training/basics/network-ops/xml.html

			while (eventType != XmlPullParser.END_DOCUMENT && !done) {

				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					employeeList = new ArrayList<Employee>();
					break;
				case XmlPullParser.START_TAG:
					currentTag = parser.getName();
					if (currentTag.equalsIgnoreCase(EMPLOYEE)) {
						currentEmployee = new Employee();
					} else if (currentEmployee != null) {
						if (currentTag.equalsIgnoreCase(ID)) {
							currentEmployee.setId(Integer.parseInt(parser.nextText())); 	
							// currentEmployee.setId(parser.nextText());
						} else if (currentTag.equalsIgnoreCase(FIRSTNAME)) {
							currentEmployee.setFirstName(parser.nextText());
						} else if (currentTag.equalsIgnoreCase(LASTNAME)) {
							currentEmployee.setLastName(parser.nextText());
						} else if (currentTag.equalsIgnoreCase(TITLE)) {
							currentEmployee.setTitle(parser.nextText());
						} else if (currentTag.equalsIgnoreCase(DEPARTMENT)) {
							currentEmployee.setDepartment(parser.nextText());
						} else if (currentTag.equalsIgnoreCase(CITY)) {
							currentEmployee.setCity(parser.nextText());
						} else if (currentTag.equalsIgnoreCase(OFFICEPHONE)) {
							currentEmployee.setOfficePhone(parser.nextText());
						} else if (currentTag.equalsIgnoreCase(MOBILEPHONE)) {
							currentEmployee.setMobilePhone(parser.nextText());
						} else if (currentTag.equalsIgnoreCase(EMAIL)) {
							currentEmployee.setEmail(parser.nextText());
						} else if (currentTag.equalsIgnoreCase(PICTURE)) {
							currentEmployee.setPicture(parser.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					currentTag = parser.getName();
					if (currentTag.equalsIgnoreCase(EMPLOYEE) && currentEmployee != null) {
						employeeList.add(currentEmployee);
					} else if (currentTag.equalsIgnoreCase(EMPLOYEES)) {
						done = true;
					}
					break;
				}
				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return employeeList;

	}

}
