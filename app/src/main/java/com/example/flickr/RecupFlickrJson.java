package com.example.flickr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

class RecupFlickrJson extends AsyncTask<String, Void, ArrayList<DonneesParImage>> implements DonneesBrutesJson.FinTelechargement {
    private static final String TAG = "TelechargementPhotos";
    private ArrayList<DonneesParImage> donneesParImageArrayList = new ArrayList<>();
    private static final String BASE_URL = "https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1";
    FragmentActivity fragmentActivity;
    private String langue = "fr-fr";
    private boolean MatchAll = true;
    private final DonneesDisponibles Callback;
    private boolean runningOnSameThread = false;
    private Bitmap bitmap;

    // Constructeur de la classe RecupFlickrJson
    public RecupFlickrJson(FragmentActivity activity, String langue, boolean matchAll, DonneesDisponibles callback) {
        Log.d(TAG, "RecupFlickrJson: called");
        this.langue = langue;
        MatchAll = matchAll;
        Callback = callback;
        fragmentActivity = activity;
    }

    interface DonneesDisponibles {
        void donneesDisponibles(ArrayList<DonneesParImage> donnees, StatusTelechargement status);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "OnPreExecute: starts");
        ProgressBar progressBar = fragmentActivity.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    // Cette methode va nous permettre de construire notre URI de deux manières
    // Soit pour avoir les données des photos récentes ou les données des photos publiques
    public String constructURI(String recherche, Boolean matchAll, String langue) {
        Log.d(TAG, "constructURI: starts");
        return Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("tags",recherche)
                .appendQueryParameter("tagmode", matchAll ? "ALL":"ANY")
                .appendQueryParameter("lang",langue).build().toString();
    }

    @Override
    protected ArrayList<DonneesParImage> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground : starts");
        runningOnSameThread = true;
        // String... est considéré comme un champ de plusieurs arguments
        String destinationUri = constructURI(strings[0], MatchAll, langue);
        DonneesBrutesJson donneesBrutesJson = new DonneesBrutesJson(this);
        donneesBrutesJson.runOnSameThread(destinationUri);
        Log.d(TAG, "doInBackground : ends");
        return donneesParImageArrayList;
    }

    // Cette classe nous permet d'ouvrir le lien de la requete, de telecharger le fichier JSON, et
    // de parser cette reponse JSON en DonneesParImages
    @Override
    public void finTelechargement(String donnee, StatusTelechargement status) {
        if (status == StatusTelechargement.OK) {
            Log.d(TAG, "finTelechargement : starts Status : " + status);
            try {
                JSONObject jsonObject = new JSONObject(donnee);
                // On recuperer le champ items qui est en fait un array
                JSONArray donneeItem = jsonObject.getJSONArray("items");
                // Une boucle est effectuee pour qu'on puisse recuperer tous les elements dans items
                for (int i = 0; i < donneeItem.length(); i++) {
                    JSONObject item = donneeItem.getJSONObject(i);
                    DonneesParImage photo = parseJsonEnDonneeImage(item);

                    if (photo != null) donneesParImageArrayList.add(photo);
                }
                Log.d(TAG, "Voici les donnees : " + donneesParImageArrayList);
            } catch (JSONException e) {
                Log.e(TAG, "FinTelechargement : Impossible de parser la reponse JSON : " + e.getMessage());
            }
        } if (runningOnSameThread && Callback != null) {
            // S'il y'a une erreur
            Log.d(TAG, "FinTelechargement : Execution dans le même thread : " + runningOnSameThread);
        } else {
            // Telechargement a echoué
            Log.e(TAG, "FinTelechargement : Telechargement a echoué : " + status);
        }
    }

    private DonneesParImage parseJsonEnDonneeImage(JSONObject jsonObject) {
        DonneesParImage donneesParImage = null;
        try {
            String regEx = "[^a-zA-Z0-9\\s]";
            String titre = jsonObject.getString("title");
            titre = titre.replaceAll(regEx, " ");
            String auteur = jsonObject.getString("author");
            auteur = auteur.replaceFirst("nobody@flickr.com", "");
            auteur = auteur.replaceAll(regEx, " ");
            String auteurID = jsonObject.getString("author_id");
            String publication = jsonObject.getString("published");
            // Ce champ est aussi un object qui contient un champ ayant comme valeur le lien
            // de l'image qu'on devra afficher
            JSONObject media = jsonObject.getJSONObject("media");
            String image = media.getString("m");
            String date = jsonObject.getString("date_taken");
            String tags = jsonObject.getString("tags");
            // L'image est affichee en minuscule avec le lien recupéré, en changeant la
            // chaine _m par _b on l'agrandit
            String grande_image = image.replaceFirst("_m","_b");

            // La on va recuperer le bitMap de l'image
            // Alors c'est ce message qui notifie que le bitmap a ete recupere
            Log.d(TAG, "doInBackground : recupBitmap");
            try {
                URL imageURL = new URL(grande_image);
                bitmap = BitmapFactory.decodeStream(imageURL.openStream());
            } catch (IOException e) {
                Log.e(TAG, "Erreur dans la reception de Bitmap" + e.getMessage());
            }
            if (bitmap == null) Log.e(TAG, "Probleme pour recuperer BitMap de l'image");
            donneesParImage = new DonneesParImage(titre, auteur, auteurID, publication, date, tags, grande_image, image, bitmap);
            return donneesParImage;
        } catch (JSONException e) {
            Log.e(TAG, "parseJsonEnDonneeImage : Impossible de parser la reponse JSON : " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<DonneesParImage> donneesParImages) {
        Log.d(TAG, "onPostExecute : starts");
        if (Callback != null) {
            Callback.donneesDisponibles(donneesParImages, StatusTelechargement.OK);
            ProgressBar progressBar = fragmentActivity.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
        Log.d(TAG, "onPostExecute : ends");
    }
}