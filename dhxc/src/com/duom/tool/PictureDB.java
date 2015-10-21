package com.duom.tool;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
public class PictureDB {
	private static SQLiteDatabase db;
	private Context context;
	public PictureDB(Context context) {
		this.context = context;		
	}
	private void openConn() {   
		if(db == null) {
			db = new PictureOpenHelper(context).getWritableDatabase();
		}
	}	
	public void close() {
		if(db != null) {
			db.close();
			db = null;
		}
	}
	
	public boolean insert(Picture item){
		if(db == null) {
			openConn();
		}	
		ContentValues values = new ContentValues();
		values.put(Const.KEY_ZHUTI, item.getZhuti());
		values.put(Const.KEY_DIZHI, item.getDizhi());
		values.put(Const.KEY_BG , item.getBg());
		values.put(Const.KEY_MUSIC, item.getMusic());
		values.put(Const.KEY_PATH, item.getPath());
		values.put(Const.KEY_ROUTE, item.getRoute());
		values.put(Const.KEY_HUMOR, item.getXinqing());
		long rowId = db.insert(Const.TABLE_NAME, null, values);
	
		close();
		return rowId > -1;		
	}
	
	public boolean delete(int id){
		if(db == null) {
			openConn();
		}
		int rows=db.delete(Const.TABLE_NAME, Const.KEY_ID+"="+id, null);
		close();
		return rows > 0;
	}
	public boolean deleteIds(int[] ids) {
		if(ids == null || ids.length == 0) {
			return false;
		}	
		String idStr = "";
		for(int i=0;i<ids.length;i++) {
			idStr += ids[i] + ",";
		}		
		if(db == null) {
			openConn();
		}
		int rows=db.delete(Const.TABLE_NAME, Const.KEY_ID+" in("+idStr+")", null);
		close();
		return rows > 0;
	}
	public boolean deleteAll(String zhuti ,String route) {
		if(db == null) {
			openConn();
		}
		int rows = db.delete(Const.TABLE_NAME, Const.KEY_ZHUTI+"='"+zhuti+"' and "+Const.KEY_ROUTE+"='"+route+"'", null);
		close();
		return rows >= 0;
	}	
	public boolean delete(String path ,String zhuti){
		if(db == null) {
			openConn();
		}
		int rows=db.delete(Const.TABLE_NAME, Const.KEY_PATH+"='"+path+"' and "+Const.KEY_ZHUTI+"='"+zhuti+"'", null);
		close();
		return rows >= 0;
	}
	public boolean delete(String route ){
		if(db == null) {
			openConn();
		}
		int rows=db.delete(Const.TABLE_NAME, Const.KEY_ROUTE+"='"+route+"'", null);
		close();
		return rows >= 0;
	}
	public Picture getItem(String path){
		Picture item ;
		if(db == null) {
			openConn();
		}
		Cursor c = db.query(Const.TABLE_NAME, null, Const.KEY_PATH+"='"+path+"'", null, null, null, null);
		c.moveToNext();
		item = new Picture(c.getString(1), c.getString(2), c.getInt(3), c.getInt(4), c.getString(5), c.getString(6),c.getString(7));
		c.close();
		close();
		return item;
	}
	public String getRoute(String path){
		if(db == null) {
			openConn();
		}
		String route = null;
		Cursor c = db.query(Const.TABLE_NAME, null, Const.KEY_PATH+"='"+path+"'", null, null, null, null);
		if(c.moveToNext()){
//		item = new Picture(c.getString(1), c.getString(2), c.getInt(3), c.getInt(4), c.getString(5), c.getString(6),c.getString(7));
		 route = c.getString(6);
		}
		c.close();
		close();
		return route;
	}
	
	public List<String> qurryAllRoute(){
	       openConn();
	       String route;
	 List<String> routes = new ArrayList<String>();
	 Cursor c = db.query(Const.TABLE_NAME, null,null, null, null, null, null);
	  boolean ret = c.moveToFirst(); 
		 while(ret)
		 {
			 route = c.getString(6);
			if(!routes.contains(route)){
				routes.add(route);
			}
		  ret =c.moveToNext();
		 }
		 c.close();
		 close();
				return routes;	 
	}	
	public List<String> qurryAllSubject(){
	       openConn();
	       String zhuti;
	 List<String> zhutis = new ArrayList<String>();
	 Cursor c = db.query(Const.TABLE_NAME, null,null, null, null, null, null);
	  boolean ret = c.moveToFirst(); 
		 while(ret)
		 {
			 zhuti = c.getString(1);
			
			if(!zhutis.contains(zhuti) &&  zhuti!= null){
				zhutis.add(zhuti);
			}
		  ret =c.moveToNext();
		 }
		 c.close();
		 close();
		  Log.e("111111112222222wodeshen", zhutis.size()+"");
				return zhutis;	 
	}
	public Picture getItem(String zhuti ,String path){
		Picture item ;
		if(db == null) {
			openConn();
		}
		Cursor c = db.query(Const.TABLE_NAME, null, Const.KEY_PATH+"='"+path+"' and "+Const.KEY_ZHUTI+"='"+zhuti+"'", null, null, null, null);
		c.moveToNext();
		item = new Picture(c.getString(1), c.getString(2), c.getInt(3), c.getInt(4), c.getString(5), c.getString(6),c.getString(7));
		c.close();
		close();
		return item;
	}
	public boolean update(Picture item ,String path){ 
		if(db == null) {
			openConn() ;
		}
		ContentValues values = new ContentValues();
		values.put(Const.KEY_ZHUTI, item.getZhuti());
		values.put(Const.KEY_DIZHI, item.getDizhi());
	    values.put(Const.KEY_ROUTE, item.getRoute());
		values.put(Const.KEY_MUSIC, item.getMusic());
		values.put(Const.KEY_BG, item.getBg());
		values.put(Const.KEY_PATH, item.getPath());
		values.put(Const.KEY_ROUTE, item.getRoute());
		values.put(Const.KEY_HUMOR, item.getXinqing());
		int rows = db.update(Const.TABLE_NAME, values,  Const.KEY_PATH+"='"+path+"'", null);
		close();
		return rows > 0;
	}
	public boolean updatealbum(Picture item ,String route){ 
		if(db == null) {
			openConn() ;
		}
		ContentValues values = new ContentValues();
		values.put(Const.KEY_ZHUTI, item.getZhuti());
		values.put(Const.KEY_DIZHI, item.getDizhi());
	    values.put(Const.KEY_ROUTE, item.getRoute());
		values.put(Const.KEY_MUSIC, item.getMusic());
		values.put(Const.KEY_BG, item.getBg());
		values.put(Const.KEY_PATH, item.getPath());
		values.put(Const.KEY_HUMOR, item.getXinqing());
		int rows = db.update(Const.TABLE_NAME, values,  Const.KEY_ROUTE+"='"+route+"'", null);
		close();
		return rows > 0;
	}	

	public List<String> querry(String zhuti ,String route){
	       openConn();
	       String path;
	 List<String> paths = new ArrayList<String>();
	 Cursor c = db.query(Const.TABLE_NAME, null, Const.KEY_ZHUTI+"='"+zhuti+"' and "+Const.KEY_ROUTE+"='"+route+"'", null, null, null, null);	 
     boolean ret = c.moveToFirst(); 
	 while(ret)
	 {
		 path = c.getString(5);
		 Log.e("0000000000001", path + "");
	  if(path == null){
		  ret =c.moveToNext();
		  continue;
	  }
		paths.add(path);
	    ret =c.moveToNext();
	 }
	 c.close();
	 close();
			return paths;
			
	}
	public List<String> querry1(String zhuti){
	       openConn();
	       String path;
	 List<String> paths = new ArrayList<String>();
	 Cursor c = db.query(Const.TABLE_NAME, null, Const.KEY_ZHUTI+"='"+zhuti+"'", null, null, null, null);	 
     boolean ret = c.moveToFirst(); 
	 while(ret)
	 {
		 path = c.getString(5);
	  if(path == null){
		  ret =c.moveToNext();
		  continue;
	  }
		paths.add(path);
	    ret =c.moveToNext();
	 }
	 c.close();
	 close();
			return paths;
			
	}
	public Cursor selectmore(String k){
		if(db == null) {
			openConn();
		}	
		return db.query(Const.TABLE_NAME,null, k, null, null, null, null);
		
	}
	
	public boolean curry(String route){
		openConn();
		Cursor c = db.query(Const.TABLE_NAME, null, Const.KEY_ROUTE+"='"+route+"'", null, null, null, null);
		if(c.getCount()>0) {
			c.moveToNext();
			c.close();    
			close();
			return true;		
		}
		else {
			c.close();
			close();
			return false;			
		}			
	}
	public Picture curryroute(String route){
		openConn();
		Picture item ;
		Cursor c = db.query(Const.TABLE_NAME, null, Const.KEY_ROUTE+"='"+route+"'", null, null, null, null);
			c.moveToFirst();
			item = new Picture(c.getString(1), c.getString(2), c.getInt(3), c.getInt(4), c.getString(5), c.getString(6),c.getString(7));	 
			if(c.moveToNext()){
			item = new Picture(c.getString(1), c.getString(2), c.getInt(3), c.getInt(4), c.getString(5), c.getString(6),c.getString(7));	
			}
			c.close();	
		    close();
		 return item;
	}
	public Picture selectOne(String zhuti){
		Picture item=null;
		if(db == null) {
			openConn();
		}	
		Cursor c = db.query(Const.TABLE_NAME, null, Const.KEY_ZHUTI+"='"+zhuti+"'", null, null, null, null);
		if(c.moveToFirst())
		{
			item = new Picture(c.getString(1), c.getString(2), c.getInt(3), c.getInt(4), c.getString(5), c.getString(6),c.getString(7));
		}
		c.close();
		close();
		return item;	
	}

}
