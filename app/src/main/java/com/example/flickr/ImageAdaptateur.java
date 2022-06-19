package com.example.flickr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class ImageAdaptateur extends RecyclerView.Adapter<ImageAdaptateur.RecycleViewHolder>{
    private static final String TAG = "ImageAdaptateur";
    private ArrayList<DonneesParImage> images;
    private Context context;

    public ImageAdaptateur(ArrayList<DonneesParImage> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageAdaptateur.RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Va s'effectuer lorsque l'utilisateur voudra une nouvelle vue (defiler)
        Log.d(TAG, "onCreateViewHolder : called ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image, parent, false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdaptateur.RecycleViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder : called ");

        // On check grace a getItemAccount si on a des donnees
        if (images.size() == 0) {
            Log.d(TAG, "onBindViewHolder : Aucune donnees");
            holder.imageView.setImageResource(R.drawable.camera);
        } else {
            // On va afficher les donnees dans la vue
            DonneesParImage image = images.get(position);
            Log.d(TAG, "onBindViewHolder : " + image.getTitre() + "--->" + position);
            holder.auteur.setText(image.getAuteur());
            holder.titre.setText(image.getTitre());
            holder.imageView.setImageBitmap(image.getBitmap());
        }
    }

    @Override
    public int getItemCount() {
        // On va retourner le nombre resultat sortis, si c'est null ou egal a 0 on retourne 1
        return ( ((images != null) && (images.size() != 0)) ? images.size() : 1);
    }

    public DonneesParImage getDonnee(int position){
        // Retournes l'index de la photo qui a ete touchee
        return ( (images != null) && (images.size() > 0) ? images.get(position) : null) ;
    }

    void nouvellesDonnees(ArrayList<DonneesParImage> images){
        Log.d(TAG, "NouvellesDonnees: nouvelle requete");
        this.images = images;
        notifyDataSetChanged();
    }

    static class RecycleViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "RecycleViewHolder";
        ImageView imageView;
        TextView titre;
        TextView auteur;

        // Ce constructeur permet de recuper les elements de la vue layout_image
        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "RecycleViewHolder: starts");
            imageView = itemView.findViewById(R.id.image);
            titre = itemView.findViewById(R.id.titre);
            auteur = itemView.findViewById(R.id.auteur);
        }
    }
}