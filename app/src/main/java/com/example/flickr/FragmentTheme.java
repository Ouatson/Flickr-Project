package com.example.flickr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class FragmentTheme extends Fragment {

    private static final String TAG = "FragmentDesThemes";
    private MyChangeTheme listener;
    ImageView imageView;
    SwitchCompat switchCompat;
    SharedPreferences sharedPreferences;
    Context context;

    public interface MyChangeTheme {
        void changeDeTheme(Boolean bolean);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof MainActivity) {
            this.listener = (MyChangeTheme) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreateTheme : starts");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity().getApplicationContext());
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // On récupère la vue et dont on va modifier les éléments
        View vue = inflater.inflate(R.layout.fragment_theme, container, false);

        imageView = vue.findViewById(R.id.imgTheme);
        switchCompat = vue.findViewById(R.id.switchNigth);
        TextView themeChoisi = vue.findViewById(R.id.idThemeChoisi);

        // Pour que le RadioButton sélectionné par défaut soit celui qu'on avait sélectionné auparavant
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity().getApplicationContext());
        // Si le mode clair est choisi
        if(sharedPreferences.getBoolean("ModeClairChoisi", true)) {
            switchCompat.setChecked(false);
            switchCompat.setText("Thème clair");
            themeChoisi.setText("Thème clair");
            imageView.setImageResource(R.drawable.day);
        }
        else{
            switchCompat.setChecked(true);
            switchCompat.setText("Thème Sombre");
            themeChoisi.setText("Thème Sombre");
            imageView.setImageResource(R.drawable.night);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG,"Thème sombre choisi");
                    switchCompat.setChecked(true);
                    switchCompat.setText("Thème Sombre");
                    imageView.setImageResource(R.drawable.night);
                    themeChoisi.setText("Thème Sombre");
                    listener.changeDeTheme(false);
//                    ((MainActivity) requireActivity()).changeDeTheme(false);
                } else {
                    Log.d(TAG,"Thème clair choisi");
                    switchCompat.setChecked(false);
                    switchCompat.setText("Thème Clair");
                    imageView.setImageResource(R.drawable.day);
                    themeChoisi.setText("Thème Clair");
                    listener.changeDeTheme(true);
//                    ((MainActivity) requireActivity()).changeDeTheme(true);
                }
            }
        });

        return vue;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreatedTheme");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResumeTheme");
        super.onResume();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity().getApplicationContext());
    }

    // Cette méthode est appelée lorsque que le fragment est déconnecté de son Activité
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }
}