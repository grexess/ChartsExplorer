package de.chartsexplorer.chartsexplorer;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import de.chartsexplorer.chartsexplorer.adapters.CustomSongSearchAdapter;
import de.chartsexplorer.chartsexplorer.util.ChartsDB;
import de.chartsexplorer.chartsexplorer.util.SongSearch;

public class SearchSongActivity extends AppCompatActivity {

    ChartsDB chartsDB;

    ArrayList<SongSearch> searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_song);

        try {
            chartsDB = new ChartsDB(getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        setTitle(getResources().getString(R.string.search));

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

                    try { searchResult = chartsDB.searchFor(data);
                        final ListView songsOfYearList = (ListView) findViewById(R.id.searchResult);
                        songsOfYearList.setAdapter(new CustomSongSearchAdapter(findViewById(R.id.searchResult).getContext(), searchResult));

                        songsOfYearList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                                    long arg3) {

                                String selectedYear = ((TextView) view.findViewById(R.id.songYear)).getText().toString();
                                int position = Integer.parseInt(((TextView) view.findViewById(R.id.songPosition)).getText().toString());
                                try {

                                    Intent i = new Intent(getApplicationContext(), ShowChartsOfYearActivity.class);
                                    i.putExtra("year", selectedYear);
                                    i.putExtra("scrollTo", position);

                                    i.putExtra("yearData", chartsDB.getYearData(selectedYear));
                                    startActivity(i);

                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    final ListView songsOfYearList = (ListView) findViewById(R.id.searchResult);
                   searchResult = new ArrayList();
                    songsOfYearList.setAdapter(new CustomSongSearchAdapter(findViewById(R.id.searchResult).getContext(), searchResult));
                }
            }
        });
    }
}
