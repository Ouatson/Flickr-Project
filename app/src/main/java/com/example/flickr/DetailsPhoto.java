package com.example.flickr;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsPhoto extends Fragment {

    private static final String TAG = "";
    FragmentActivity listener;
    Button favoriBouton;
    MaBaseDeDonnees maBD;

    private DonneesParImage image;

    // Cette methode sert à montrer l'association de ce fragment à l'activité Main
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.listener = (FragmentActivity) context;
            maBD = new MaBaseDeDonnees(context);
        }
    }

    // Voici le constructeur de cette classe
    public DetailsPhoto(DonneesParImage image) {
        this.image = image;
    }

    // Une methode d'instanciation de la classe
    public static DetailsPhoto newInstance(DonneesParImage image) {
        return new DetailsPhoto(image);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(requireActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_photo, container, false);

        // On va alors faire pareil que pour la classe ImageAdaptateur et inserer les les valeurs
        // dans les champs requis
        TextView titreDetail = view.findViewById(R.id.titreDetail);
        String titreText = "Titre : " + this.image.getTitre();
        titreDetail.setText(titreText);

        TextView auteurDetail = view.findViewById(R.id.auteurDetail);
        String auteurText = "Auteur : " + this.image.getAuteur();
        auteurDetail.setText(auteurText);

        ImageView image = view.findViewById(R.id.imageDetail);
        image.setImageBitmap(this.image.getBitmap());

        TextView datePrise = view.findViewById(R.id.datePrise);
        String datePrText = "Date publication : " + this.image.getDate();
        String[] datesPrText = datePrText.split("T");
        datePrise.setText(datesPrText[0]);

        TextView datePubli = view.findViewById(R.id.datePubli);
        String datePuText = "Date publication : " + this.image.getPublication();
        String[] datesPuText = datePuText.split("T");
        datePubli.setText(datesPuText[0]);

        TextView urlImage = view.findViewById(R.id.lienImage);
        String urlText = "Lien :\n" + this.image.getGrande_image_URL();
        urlImage.setText(urlText);

        // Si l'utilisateur clique sur bouton pour ajouter en favoris
        favoriBouton = view.findViewById(R.id.boutonFavoris);
        if ( maBD.search(this.image.getGrande_image_URL()) ) {
            favoriBouton.setVisibility(View.GONE);
        }

        favoriBouton.setOnClickListener(v -> {
            maBD.insert(this.image);
            favoriBouton.setEnabled(false);
            Toast.makeText(getActivity(), "Photo bien ajoutée aux favoris !", Toast.LENGTH_LONG).show();
        });

        return view;
    }
}