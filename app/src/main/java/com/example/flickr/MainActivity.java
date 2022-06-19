package com.example.flickr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends BaseActivity implements FragmentTheme.MyChangeTheme {
    private static final String TAG = "MainActivity";
    FragmentManager monFragmentManager;
    FragmentAccueil fragmentAccueil = new FragmentAccueil();
    FragmentFavoris fragmentFavoris = new FragmentFavoris();
    FragmentTheme fragmentTheme = new FragmentTheme();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreateMain : execution");

        // Pour récupérer le thème choisi auparavant et l'appliquer
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        changeDeTheme(sharedPreferences.getBoolean("ModeClairChoisi", true));

        // Une initialisation à null de la requete pour chaque demarrage
        sharedPreferences.edit().putString(FLICKR_QUERY, "").apply();

        // Pour définir le contenu de la vue
        setContentView(R.layout.activity_main);

        // Cette condition va permettre de notifier l'utilisateur s'il n'a pas accès à internet
        // En fonction de ça soit l'accueil lui sera afficher soit une image de non connection
        if (!isConnected()) {
            Toast.makeText(this, "Pas d'accès à Internet!\nVeuillez vous connecter.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Bienvenue dans l'application !", Toast.LENGTH_LONG).show();
        }

        // TODO: Checker quelle fragment est affiche d'abord (si aucun ne l'est alors on affiche le fragment par defaut qui est l'accueil)
        // Pour cette partie on va initialiser pour afficher le fragment par defaut qui sera l'accueil
        monFragmentManager = getSupportFragmentManager();
        FragmentTransaction maTransaction = monFragmentManager.beginTransaction();
        maTransaction.replace(R.id.conteneur_de_fragment, fragmentAccueil, "FragmentAccueil").commit();
    }

    // Pour lier les menus au fichier XML de l'activité
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        getMenuInflater().inflate(R.menu.option_recherche, menu);
        Log.d(TAG, "onCreateOptionsMenu : Menu créé");
        return super.onCreateOptionsMenu(menu);
    }

    // Pour dire quoi faire si les items du menu sont sélectionnés
    // Quatres cas sont presents
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected : Une option cliqué");
        switch (item.getItemId()) {
            case R.id.action_accueil:
                // Pour les trois prochains cas on a un chargement de fragments dans le conteneur precisé
                getSupportFragmentManager().beginTransaction().replace(R.id.conteneur_de_fragment, fragmentAccueil, "FragmentAccueil").commit();
                return true;

            case R.id.action_favoris:
                getSupportFragmentManager().beginTransaction().replace(R.id.conteneur_de_fragment, fragmentFavoris, "FragmentFavoris").commit();
                return true;

            case R.id.action_themes:
                getSupportFragmentManager().beginTransaction().replace(R.id.conteneur_de_fragment, fragmentTheme).commit();
                return true;

            case R.id.action_recherche:
                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                SearchView searchView = (SearchView) item.getActionView();
                SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
                searchView.setSearchableInfo(searchableInfo);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        Log.d(TAG, "onQueryTextSubmit: called");
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        sharedPreferences.edit().putString(FLICKR_QUERY,query).apply();
                        searchView.clearFocus();
                        fragmentAccueil.onResume();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Log.d(TAG, "onQueryTextChange: called");
                        return false;
                    }
                });
                searchView.setOnCloseListener(() -> false);
                return true;

            default:
                // Si l'action n'est pas reconnue
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    // Cette methode va permettre de tester si oui ou non on a acces à Internet
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void changeDeTheme(Boolean themeClair) {
        Log.d(TAG,"changeDeTheme");
        // On change le thème
        if(themeClair)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // On enregistre le thème pour pouvoir le récupérer plus tard
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putBoolean("ModeClairChoisi", themeClair).apply();
    }
}