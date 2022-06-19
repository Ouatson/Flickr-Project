package com.example.flickr;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class DonneesParImage implements Serializable {

    private static final long serialVersionUID= 1L;

    private String titre;
    private String auteur;
    private String auteur_ID;
    private String publication;
    private String date;
    private String tags;
    private String grande_image_URL;
    private String image;
    private Bitmap bitmap;

    public DonneesParImage() {
    }

    public DonneesParImage(String titre,
                           String auteur,
                           String auteur_ID,
                           String publication,
                           String date,
                           String tags,
                           String grande_image_URL,
                           String image,
                           Bitmap bitmap) {
        this.titre = titre;
        this.auteur = auteur;
        this.auteur_ID = auteur_ID;
        this.publication = publication;
        this.date = date;
        this.tags = tags;
        this.grande_image_URL = grande_image_URL;
        this.image = image;
        this.bitmap = bitmap;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getAuteur_ID() {
        return auteur_ID;
    }

    public void setAuteur_ID(String auteur_ID) {
        this.auteur_ID = auteur_ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getGrande_image_URL() {
        return grande_image_URL;
    }

    public void setGrande_image_URL(String grande_image_URL) {
        this.grande_image_URL = grande_image_URL;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "DonneesParImage{" +
                "titre='" + titre + '\'' +
                ", auteur='" + auteur + '\'' +
                ", auteur_ID='" + auteur_ID + '\'' +
                ", publication='" + publication + '\'' +
                ", date='" + date + '\'' +
                ", tags='" + tags + '\'' +
                ", grande_image_URL='" + grande_image_URL + '\'' +
                ", image='" + image + '\'' +
                ", bitmap=" + bitmap +
                '}';
    }
}
