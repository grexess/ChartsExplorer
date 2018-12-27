package de.chartsexplorer.chartsexplorer;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchSongActivity extends AppCompatActivity {

    ChartsDB chartsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_song);

        try {
            chartsDB = new ChartsDB(getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        setTitle("Suche");

        final TextInputEditText searchField = (TextInputEditText) findViewById(R.id.songSearch);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //do here your calculation
                String data = s.toString();
                if(data.length() > 2 ){

                    try { ArrayList<Song> searchResult = chartsDB.searchFor(data);
                        final ListView songsOfYearList = (ListView) findViewById(R.id.searchResult);
                        songsOfYearList.setAdapter(new CustomSongAdapter(findViewById(R.id.searchResult).getContext(), searchResult));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
