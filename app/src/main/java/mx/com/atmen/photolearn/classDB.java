package mx.com.atmen.photolearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

class classDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_NAME_CLASS = "class";
    private static final String TABLE_NAME_MATERIAL = "material";
    private static final String TABLE_NAME_ITEM = "item";
    private static final String TABLE_CREATE_CLASS =
            "CREATE TABLE " + TABLE_NAME_CLASS + " (category TEXT);";
    private static final String TABLE_CREATE_MATERIAL =
            "CREATE TABLE " + TABLE_NAME_MATERIAL + " (type TEXT, category TEXT);";
    private static final String TABLE_CREATE_ITEM =
            "CREATE TABLE " + TABLE_NAME_ITEM + " (orientation TEXT, photo TEXT, type TEXT, category TEXT);";
    private static final String DATABASE_NAME = "classDB.db";
    private Context context;
    private String f;

    classDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_CLASS);
        db.execSQL(TABLE_CREATE_MATERIAL);
        db.execSQL(TABLE_CREATE_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion == 3){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ITEM + ";");
            db.execSQL(TABLE_CREATE_ITEM);
        }
    }

    boolean newCategory(String newCategory){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category", newCategory);
        try{
            db.insert(TABLE_NAME_CLASS, null, values);
            db.close();
            return true;
        }catch(Exception e){
            db.close();
            return false;
        }
    }

    boolean newType(String newType, String category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", newType);
        values.put("category", category);
        try {
            db.insert(TABLE_NAME_MATERIAL, null, values);
            db.close();
            return true;
        }catch(Exception e){
            db.close();
            return false;
        }
    }

    boolean newPhoto(String newPhoto, String type, String category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("orientation", 1);
        values.put("photo", newPhoto);
        values.put("type", type);
        values.put("category", category);
        try{
            db.insert(TABLE_NAME_ITEM, null, values);
            db.close();
            return true;
        }catch(Exception e){
            db.close();
            return false;
        }
    }

    boolean editCategory(String newCategory, String oldCategory){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category", newCategory);
        try{
            db.update(TABLE_NAME_CLASS, values, "category = '" + oldCategory + "';", null);
            db.update(TABLE_NAME_MATERIAL, values, "category = '" + oldCategory + "';", null);
            db.update(TABLE_NAME_ITEM, values, "category = '" + oldCategory + "';", null);
            db.close();
            return true;
        }catch(Exception e){
            db.close();
            return false;
        }
    }

    boolean editType(String newType, String oldType){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", newType);
        try{
            db.update(TABLE_NAME_MATERIAL, values, "type = '" + oldType + "';", null);
            db.update(TABLE_NAME_ITEM, values, "type = '" + oldType + "';", null);
            db.close();
            return true;
        }catch(Exception e){
            db.close();
            return false;
        }
    }

    boolean deleteCategory(String category){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_ITEM + " WHERE category = '" + category + "';", null);
            while(cursor.moveToNext()){
                File file = new File(cursor.getString(cursor.getColumnIndex("photo")));
                if(!file.delete()){
                    Toast.makeText(context, "Images could not be deleted", Toast.LENGTH_LONG).show();
                }
            }
            db.delete(TABLE_NAME_CLASS, "category = '" + category + "';", null);
            db.delete(TABLE_NAME_MATERIAL, "category = '" + category + "';", null);
            db.delete(TABLE_NAME_ITEM, "category = '" + category + "';", null);
            db.close();
            return true;
        }catch(Exception e){
            db.close();
            return false;
        }
    }

    boolean deleteType(String type, String category){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_ITEM + " WHERE type = '" + type + "' AND category = '" + category + "';", null);
            while(cursor.moveToNext()){
                File file = new File(cursor.getString(cursor.getColumnIndex("photo")));
                if(!file.delete()){
                    Toast.makeText(context, "Images could not be deleted", Toast.LENGTH_LONG).show();
                }
            }
            db.delete(TABLE_NAME_MATERIAL, "type = '" + type + "';", null);
            db.delete(TABLE_NAME_ITEM, "type = '" + type + "';", null);
            db.close();
            return true;
        }catch(Exception e){
            db.close();
            return false;
        }
    }

    boolean deletePhoto(String photo){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(TABLE_NAME_ITEM, "photo = '" + photo + "';", null);
            db.close();
            File file = new File(photo);
            if(!file.delete()){
                Toast.makeText(context, "Image could not be deleted", Toast.LENGTH_LONG).show();
            }
            return true;
        }catch(Exception e){
            db.close();
            return false;
        }
    }

    String[] getCategory(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_CLASS, null);
        String[] array = new String[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()){
            String res = cursor.getString(cursor.getColumnIndex("category"));
            array[i] = res;
            i++;
        }
        cursor.close();
        return array;
    }

    String[] getType(int position){
        SQLiteDatabase db = this.getReadableDatabase();
        String myClass = getCategory()[position];
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_MATERIAL + " WHERE category = '" + myClass + "';", null);
        String[] array = new String[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()){
            String res = cursor.getString(cursor.getColumnIndex("type"));
            array[i] = res;
            i++;
        }
        cursor.close();
        return array;
    }

    String[] getPhotos(String category, String type){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_ITEM + " WHERE category = '" + category + "' AND type = '" + type + "';", null);
        String[] array = new String[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()){
            String res = cursor.getString(cursor.getColumnIndex("photo"));
            array[i] = res;
            i++;
        }
        cursor.close();
        return array;
    }

    String getLastPhotoCategory(String category){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_ITEM + " WHERE category = '" + category + "';", null);
        String[] array = new String[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()){
            String res = cursor.getString(cursor.getColumnIndex("photo"));
            array[i] = res;
            i++;
        }
        cursor.close();
        return array[array.length - 1];
    }

    String getLastPhotoType(String category, String type){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_ITEM + " WHERE category = '" + category + "' AND type = '" + type + "';", null);
        String[] array = new String[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()){
            String res = cursor.getString(cursor.getColumnIndex("photo"));
            array[i] = res;
            i++;
        }
        cursor.close();
        return array[array.length - 1];
    }

    int getOrientation(String photo){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_ITEM + " WHERE photo = '" + photo + "';", null);
        int[] array = new int[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()){
            int res = cursor.getInt(cursor.getColumnIndex("orientation"));
            array[i] = res;
            i++;
        }
        cursor.close();
        return array[array.length - 1];
    }

}