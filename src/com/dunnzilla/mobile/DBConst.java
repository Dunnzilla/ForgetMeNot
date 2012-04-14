package com.dunnzilla.mobile;

/*
 * DB Version history:
 * 1: Test
 * 2: Basic functionality
 * 3: tables: Add first preference column, pref_contact_type 
 */
public class DBConst {
	public static final String DBNAME="forgetmenot.db";
	public static final int    VERSION=3;
	public static final String TABLE = "reminders";
	public static final String f_ID = "_id";
	public static final String f_CONTACT_ID = "contact_id";
	public static final String f_DATETIME_C = "datetime_c";
	public static final String f_DATETIME_M = "datetime_m";
	public static final String f_DATETIME_START = "datetime_start";
	public static final String f_DATETIME_STOP = "datetime_stop";
	public static final String f_DATETIME_NEXT = "datetime_next";
	public static final String f_PERIOD = "period";   
	public static final String f_PREF_CONTACT_TYPE = "pref_contact_type";
	public static final String f_URI_ACTION = "action";
	public static final String f_NOTE = "note";
}
