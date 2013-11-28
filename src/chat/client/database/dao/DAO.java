package chat.client.database.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chat.client.database.SQLiteHelper;
import chat.client.database.model.Model;

public abstract class DAO<T extends Model> {

	private String tableName;
	private String[] columns;
	
	@Inject private SQLiteHelper dbHelper;

	public DAO(String tableName, String[] columns){
		this.tableName = tableName;
		this.columns = columns;
	}
	
	protected abstract ContentValues createValues(Model model);
	protected abstract T createModel(Cursor cursor);
	
	public long save(Model model) {
		verifyDatabaseOpen();
		long id = model.getId();
		if (id != 0 ){
			update(model);
		} else {
			id = insert(model);
		}
		return id;
	}

	public T findById(long id) {
		verifyDatabaseOpen();
		String idString = String.valueOf(id);
		Cursor cursor = dbHelper.findById(tableName, columns, idString);
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			T model = createModel(cursor);
			cursor.close();
			return model;
		}
		cursor.close();
		return null;
	}
	
	public Cursor findAllCursor(){
		verifyDatabaseOpen();
		return dbHelper.findAll(tableName, columns);	
	}
	
	public List<T> findAll(){
		verifyDatabaseOpen();
		Cursor cursor = dbHelper.findAll(tableName, columns);
		List<T> models = new ArrayList<T>();
		if(cursor.moveToFirst()){
			do{
				T model = createModel(cursor);
				models.add(model);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return models;
	}

	public Cursor findCursor(String where, String[] whereArgs, String orderBy){
		verifyDatabaseOpen();
		Cursor cursor = dbHelper.find(tableName, columns, where, whereArgs, orderBy);
		return cursor;
	}

	public List<T> find(String where, String[] whereArgs){
		verifyDatabaseOpen();
		return find(where, whereArgs, null);
	}
	
	public List<T> find(String where, String[] whereArgs, String orderBy){
		verifyDatabaseOpen();
		Cursor cursor = dbHelper.find(tableName, columns, where, whereArgs, orderBy);
		List<T> models = new ArrayList<T>();
		if(cursor.moveToFirst()){
			do{
				T model = createModel(cursor);
				models.add(model);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return models;
	}
	
	public void deleteAll(){
		verifyDatabaseOpen();
		dbHelper.deleteAll(tableName);
	}
	
	public void delete(String where, String[] whereArgs){
		verifyDatabaseOpen();
		dbHelper.delete(tableName, where, whereArgs);
	}

	protected int getColumnIndex(String column) {
			return Arrays.asList(columns).indexOf(column);
	}
	
	private long insert(Model model){
		ContentValues values = createValues(model);
		return dbHelper.insert(tableName, values);
	}

	private int update(Model model) {
		ContentValues values = createValues(model);
		String id = String.valueOf(model.getId());
		String where = Model._ID + "=?";
		String[] whereArgs = new String[]{id};
		return dbHelper.update(tableName, values, where, whereArgs);
	}
	
	private void verifyDatabaseOpen() {
		if(!dbHelper.isDatabaseOpen()){ 
			dbHelper.openDatabase();
		}
	}
	
}