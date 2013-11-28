package chat.client.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.inject.Inject;

import chat.client.gui.R;

public class SQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE = "vchat";
	private static final int DATABASE_VERSION = 1;

	private static SQLiteDatabase db;

	private Context context;

	@Inject
	public SQLiteHelper(Context context){
		super(context, DATABASE, null, DATABASE_VERSION);
		this.context = context;
		openDatabase();
	}

	public void openDatabase() {
		if(db == null || !db.isOpen()){
			db = getWritableDatabase();	
		}
		
	}
	
	public void closeDb(){
		if(db != null){ 
			db.close();
		}
	}

	public boolean isDatabaseOpen(){
		return db != null && db.isOpen();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] scripts = context.getResources().getStringArray(R.array.createDatabase);
		for (String sqlScript : scripts) {
			db.execSQL(sqlScript);
		}
		onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("INFO", "version " + oldVersion + " : " + newVersion);
		if(newVersion >= oldVersion){
			String[] scripts = context.getResources().getStringArray(R.array.updateDatabase);
			for (String sqlScript : scripts) {
				db.execSQL(sqlScript);
			}
		}
	}

	public long insert(String tableName, ContentValues values){
		return db.insert(tableName, "", values);
	}

	public int update(String tableName, ContentValues values, String where, String[] whereArgs) {
		return db.update(tableName, values, where, whereArgs);
	}

	public Cursor findById(String tableName, String[] columns, String id) {
		String where = "_id=?";
		String[] whereArgs = new String[]{id};
		return find(tableName, columns, where, whereArgs);
	}

	public Cursor findAll(String tableName, String[] columns) {
		return find(tableName, columns, null, null);
	}

	public void deleteAll(String tableName){
		db.delete(tableName, null, null);
	}

	public void delete(String tableName, String where, String[] whereArgs){
		db.delete(tableName, where, whereArgs);
	}

	public Cursor find(String tableName, String[] columns, String where, String[] whereArgs) {
		return db.query(tableName, columns, where, whereArgs, null, null, null);
	}

	public Cursor find(String tableName, String[] columns, String where, String[] whereArgs, String orderBy) {
		return db.query(tableName, columns, where, whereArgs, null, null, orderBy);
	}
	
	public static synchronized void closeDatabase() {
		if(db != null){
			db.close();
		}
	}
}
