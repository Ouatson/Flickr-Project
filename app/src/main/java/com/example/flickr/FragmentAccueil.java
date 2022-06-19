package com.example.flickr;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class FragmentAccueil extends Fragment implements RecupFlickrJson.DonneesDisponibles, ImageOnClickListener.ClickListener {

    private static final String TAG = "FragmentAccueil";
    private ImageAdaptateur imageAdaptateur;
    private RecyclerView recyclerView;
    FragmentActivity listener;

    // Cette methode sert à montrer l'association de ce fragment à l'activité Main
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.listener = (FragmentActivity) context;
        }
    }

    // Cette partie sera a changer comme on a à faire à des images
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreateAccueil : starts");

        // Initialisation de la requete à null
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity().getApplicationContext());
        if (Objects.equals(sharedPreferences.getString(BaseActivity.FLICKR_QUERY, ""), "")) {
            Log.d(TAG, "Aucune donnée pour le moment");
        }
        Log.d(TAG,"onCreateAccueil : ends");
    }

    // La creation de la vue dans le conteneur
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateViewsAccueil : starts");

        View rootView = inflater.inflate(R.layout.fragment_accueil, container, false);

        // A cette etape on va creer le LayoutManager pour permettre au recycleView d'etre attache au layout du recycleView
        recyclerView = rootView.findViewById(R.id.MaListeDePhotos);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addOnItemTouchListener(new ImageOnClickListener(getActivity(), recyclerView, this));
        imageAdaptateur = new ImageAdaptateur(new ArrayList<DonneesParImage>(), getActivity());
        recyclerView.setAdapter(imageAdaptateur);

        FloatingActionButton rafraichir = requireActivity().findViewById(R.id.rafraichir);
        rafraichir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : Raffraichir Main");
                onResume();
            }
        });
        Log.d(TAG,"onCreateViewsAccueil : ends");

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResumeAccueil : starts");
        // On recuperer la requete fournie
        RecupFlickrJson flickrJson = new RecupFlickrJson(requireActivity(),"fr-fr", true, this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity().getApplicationContext());
        String queryTags = sharedPreferences.getString(BaseActivity.FLICKR_QUERY,"");
        Log.d(TAG, "onResumeAccueil : " + queryTags);
        if(queryTags.length()>0) {
            flickrJson.execute(queryTags);
        }
        Log.d(TAG, "onResumeAccueil : ends");
    }

    // Cette méthode est appelée lorsque que le fragment est déconnecté de son Activité
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    // Cette methode va s'executer lorsqu'on aura recupéré les donnees des photos de Flickr
    // C'est la surcharge de l'interface de RecupFlickrJson
    @Override
    public void donneesDisponibles(ArrayList<DonneesParImage> donnees, StatusTelechargement status) {
        if (status == StatusTelechargement.OK) {
            // Affichage du nombre d'elements
            Log.d(TAG, "donneesDisponibles: Le nombre d'éléments recus : " + donnees.size());
            imageAdaptateur.nouvellesDonnees(donnees);
            Toast.makeText(getActivity(),"Recherche effectuée ! ",Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, "donneesDisponibles: Telechargement echoué avec status: " + status);
        }
    }

    // Lorsqu'un click sera effectué sur une image alors on va afficher les details de cette photo
    @Override
    public void ImageClickListener(View view, int position) {
        if (imageAdaptateur.getDonnee(position) != null){

            Fragment fragment = DetailsPhoto.newInstance(imageAdaptateur.getDonnee(position));
            FragmentTransaction maTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

            // On effectue un hide pour ne pas que ça recharge notre fragment Accueil
            maTransaction.hide(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentByTag("FragmentAccueil")));
            maTransaction.add(R.id.conteneur_de_fragment, fragment);

            // Ceci est fait pour que l'utilisateur puisse revenir a la page precedente
            maTransaction.addToBackStack(null);
            maTransaction.commit();
        }
    }

    // Cette surcharge va permmetre à l'utilisateur s'il clique longtemps d'avoir quelques details de l'image
    @Override
    public void ImageLongClickListener(View view, int position) {
        if (imageAdaptateur.getDonnee(position) != null) {
            DonneesParImage image = imageAdaptateur.getDonnee(position);
            Toast.makeText(getActivity(), "Titre Image : " + image.getTitre() + "\nAuteur : " + image.getAuteur(), Toast.LENGTH_LONG).show();
        }
    }
}