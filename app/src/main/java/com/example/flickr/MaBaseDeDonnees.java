package com.example.flickr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MaBaseDeDonnees extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_TABLE_NAME = "images";
    public MaBaseDeDonnees(Context context) {
        super(context, DATABASE_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " +
                DATABASE_TABLE_NAME +
                "(titre varchar(255)," +
                " auteur varchar(255)," +
                " auteur_ID varchar(255)," +
                " publication varchar(255)," +
                " date varchar(255)," +
                " tags varchar(255)," +
                " image_URL varchar(255)," +
                " image varchar(255)," +
                " bitmap varchar(255)" +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public boolean search(String urlImage) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT image_URL FROM " + DATABASE_TABLE_NAME + " WHERE image_URL='"+urlImage+"'";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        return cursor.moveToFirst();
    }

    // Methode pour recuperer toutes les donnees presentes dans la base de donnee
    @SuppressLint("Range")
    public ArrayList<DonneesParImage> getAll() {
        ArrayList<DonneesParImage> images = new ArrayList<>();
        DonneesParImage image;
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + DATABASE_TABLE_NAME;
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                // Chaque champ est recupéré
                image = new DonneesParImage();
                image.setTitre(cursor.getString(cursor.getColumnIndex("titre")));
                image.setAuteur(cursor.getString(cursor.getColumnIndex("auteur")));
                image.setAuteur_ID(cursor.getString(cursor.getColumnIndex("auteur_ID")));
                image.setPublication(cursor.getString(cursor.getColumnIndex("publication")));
                image.setDate(cursor.getString(cursor.getColumnIndex("date")));
                image.setTags(cursor.getString(cursor.getColumnIndex("tags")));
                image.setGrande_image_URL(cursor.getString(cursor.getColumnIndex("image_URL")));
                image.setImage(cursor.getString(cursor.getColumnIndex("image")));
                String btmap = cursor.getString(cursor.getColumnIndex("bitmap"));
                byte[] encodeByte;
                encodeByte = Base64.decode(btmap, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                image.setBitmap(bmp);

                //Et on l'insere dans notre liste
                images.add(image);
            } while (cursor.moveToNext());
        }
        return images;
    }

    public void insert(DonneesParImage image){
        SQLiteDatabase db = getWritableDatabase();
        // Encodage du bitMap en String
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String result = Base64.encodeToString(byteArray, Base64.DEFAULT);

        String query = "INSERT INTO " + DATABASE_TABLE_NAME + "(titre, auteur, auteur_ID, publication, date, tags, image_URL, image, bitmap) " +
                "VALUES('"+image.getTitre()+"'," +
                " '"+image.getAuteur()+"'," +
                " '"+image.getAuteur_ID()+"'," +
                " '"+image.getPublication()+"'," +
                " '"+image.getDate()+"'," +
                " '"+image.getTags()+"'," +
                " '"+image.getGrande_image_URL()+"'," +
                " '"+image.getImage()+"'," +
                " '"+result+"" +
                "')";
        db.execSQL(query);
    }
}
