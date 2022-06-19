package com.example.flickr;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

enum StatusTelechargement{IDLE, PROCESSING, NOT_INITIALIZED, FAILED_OR_EMPTY, OK}

class DonneesBrutesJson extends AsyncTask<String, Void, String> {
    private static final String TAG = "DonneesBrutesJson";
    private final FinTelechargement Callback;
    private StatusTelechargement statusTelechargement;

    interface FinTelechargement {
        void finTelechargement(String donnee, StatusTelechargement status);
    }

    public DonneesBrutesJson(FinTelechargement callback) {
        statusTelechargement = StatusTelechargement.IDLE;
        Callback = callback;
    }

    void runOnSameThread(String s) {
        Log.d(TAG, "runOnSameThread: starts");
        if (Callback != null) {
            Callback.finTelechargement(doInBackground(s), statusTelechargement);
        }
        Log.d(TAG, "runOnSameThread: ends");
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // Si aucune requete n'est encore faite
        if (strings == null) {
            statusTelechargement = StatusTelechargement.NOT_INITIALIZED;
            return null;
        }
        // Le telechargement des données debute
        try {
            statusTelechargement = StatusTelechargement.PROCESSING;
            // Ouverture requete url avec le protocol HTTP
            URL urlObjet = new URL(strings[0]);
            urlConnection = (HttpURLConnection)urlObjet.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int reponse = urlConnection.getResponseCode();
            Log.d(TAG, "doInBackground : Response : " + reponse);
            StringBuilder builder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while (null != (line = reader.readLine())) {
                builder.append(line).append("\n");
            }
            statusTelechargement = StatusTelechargement.OK;
            return builder.toString();
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: Erreur " + e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            } if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error en fermant le stream " + e.getMessage());
                }
            }
        }
        statusTelechargement = StatusTelechargement.FAILED_OR_EMPTY;
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (Callback != null) {
            Callback.finTelechargement(s, statusTelechargement);
        }
        Log.d(TAG, "onPostExecute: TERMINE");
    }
}
