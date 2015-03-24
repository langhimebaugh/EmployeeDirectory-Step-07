package com.himebaugh.employeedirectory;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EmployeeDatabase extends SQLiteOpenHelper {

	private Context context;
	ArrayList<Employee> employeeList = new ArrayList<Employee>();

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "employee_directory";
	public static final String TABLE_EMPLOYEES = "employees";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_FIRSTNAME = "first_name";
	public static final String COLUMN_LASTNAME = "last_name";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DEPARTMENT = "department";
	public static final String COLUMN_CITY = "city";
	public static final String COLUMN_OFFICE_PHONE = "office_phone";
	public static final String COLUMN_MOBILE_PHONE = "mobile_phone";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_PICTURE = "picture";

	private static final String CREATE_TABLE_EMPLOYEES = "CREATE TABLE " + TABLE_EMPLOYEES + " (" 
		+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
		+ COLUMN_FIRSTNAME + " TEXT NOT NULL, "
		+ COLUMN_LASTNAME + " TEXT NOT NULL, " 
		+ COLUMN_TITLE + " TEXT NOT NULL, " 
		+ COLUMN_DEPARTMENT + " TEXT NOT NULL, " 
		+ COLUMN_CITY + " TEXT NOT NULL, " 
		+ COLUMN_OFFICE_PHONE + " TEXT NOT NULL, " 
		+ COLUMN_MOBILE_PHONE + " TEXT NOT NULL, " 
		+ COLUMN_EMAIL + " TEXT NOT NULL, " 
		+ COLUMN_PICTURE + " TEXT NOT NULL);";

	public EmployeeDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;  // DO I NEED THIS???
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Called when the database is created for the first time. 
		// This is where the creation of tables and the initial population of the tables happens.
		db.execSQL(CREATE_TABLE_EMPLOYEES);
		seedData(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEES);
		onCreate(db);
	}

	private void seedData(SQLiteDatabase db) {

		// CALL XMLPULLPARSER & RETURN AN ArrayList OF EMPLOYEES
		EmployeeXmlParser parser = new EmployeeXmlParser();
		employeeList = parser.parse(context);

		// INSERT EACH EMPLOYEE TO THE DATABASE
		for (Employee employee : employeeList) {
			db.execSQL("INSERT INTO employees (" 
					+ COLUMN_FIRSTNAME + ", " 
					+ COLUMN_LASTNAME + ", " 
					+ COLUMN_TITLE + ", " 
					+ COLUMN_DEPARTMENT + ", " 
					+ COLUMN_CITY + ", " 
					+ COLUMN_OFFICE_PHONE + ", "
					+ COLUMN_MOBILE_PHONE + ", " 
					+ COLUMN_EMAIL + ", " 
					+ COLUMN_PICTURE + ")" 
					+ " values (\"" 
					+ String.valueOf(employee._firstName) 
					+ "\", \"" 
					+ String.valueOf(employee._lastName)
					+ "\", \"" 
					+ String.valueOf(employee._title) 
					+ "\", \"" 
					+ String.valueOf(employee._department) 
					+ "\", \"" 
					+ String.valueOf(employee._city) 
					+ "\", \""
					+ String.valueOf(employee._officePhone) 
					+ "\", \"" 
					+ String.valueOf(employee._mobilePhone) 
					+ "\", \"" 
					+ String.valueOf(employee._email) 
					+ "\", \""
					+ String.valueOf(employee._picture) + "\");");
		}

	}

}
