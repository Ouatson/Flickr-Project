package com.example.flickr;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentFavoris extends Fragment implements ImageOnClickListener.ClickListener {
    private static final String TAG = "FragmentFavoris";
    private ImageAdaptateur imageAdaptateur;
    private RecyclerView recyclerView;
    private TextView textView;
    private ArrayList<DonneesParImage> images;
    MaBaseDeDonnees maBD;
    FragmentActivity listener;

    // Cette methode sert à montrer l'association de ce fragment à l'activité Main
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.listener = (FragmentActivity) context;
            maBD = new MaBaseDeDonnees(context);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreateFavoris : starts");
        PreferenceManager.getDefaultSharedPreferences(requireActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateViewFavoris : starts");
        View view = inflater.inflate(R.layout.fragment_favoris, container, false);

        // On va d'abord receuillir la liste des images dans la base de donnee
        images = maBD.getAll();

        // On recuperer l'element text
        textView = view.findViewById(R.id.textNone);

        // On va proceder de la meme maniere que pour le fragment Accueil
        recyclerView = view.findViewById(R.id.MaListeDePhotosFavoris);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        // Si la liste des favoris est vide alors on montre le message de notification
        if (images.size() == 0) {
            textView.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Vous pouvez ajouter des elements aux favoris !", Toast.LENGTH_LONG).show();
        } else {
            textView.setVisibility(View.GONE);
        }

        recyclerView.addOnItemTouchListener(new ImageOnClickListener(getActivity(), recyclerView, this));
        imageAdaptateur = new ImageAdaptateur(images, getActivity());
        recyclerView.setAdapter(imageAdaptateur);

        FloatingActionButton rafraichir = requireActivity().findViewById(R.id.rafraichir);
        rafraichir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : Raffraichir Main");
                onResume();
            }
        });
        Log.d(TAG,"onCreateViewFavoris : ends");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResumeFavoris");
        imageAdaptateur.nouvellesDonnees(images);
    }

    // Cette méthode est appelée lorsque que le fragment est déconnecté de son Activité
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    // Lorsqu'un click sera effectué sur une image alors on va afficher les details de cette photo
    @Override
    public void ImageClickListener(View view, int position) {
        if (imageAdaptateur.getDonnee(position) != null) {

            Fragment fragment = DetailsPhoto.newInstance(imageAdaptateur.getDonnee(position));
            FragmentTransaction maTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

            // On effectue un hide pour ne pas que ça recharge notre fragment Accueil
            maTransaction.hide(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentByTag("FragmentFavoris")));
            maTransaction.add(R.id.conteneur_de_fragment, fragment);

            // Ceci est fait pour que l'utilisateur puisse revenir a la page precedente
            maTransaction.addToBackStack(null);
            maTransaction.commit();
        }
    }

    // Cette surcharge va permmetre à l'utilisateur s'il clique longtemps d'avoir quelques details de l'image
    @Override
    public void ImageLongClickListener(View view, int position) {
        if (imageAdaptateur.getDonnee(position) != null){
            DonneesParImage image = imageAdaptateur.getDonnee(position);
            Toast.makeText(getActivity(),"Titre Image : " + image.getTitre() + "\nAuteur : " + image.getAuteur(),Toast.LENGTH_LONG).show();
        }
    }
}