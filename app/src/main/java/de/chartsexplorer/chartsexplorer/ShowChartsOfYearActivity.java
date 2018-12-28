package de.chartsexplorer.chartsexplorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import de.chartsexplorer.chartsexplorer.adapters.CustomSongAdapter;
import de.chartsexplorer.chartsexplorer.util.Song;

public class ShowChartsOfYearActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top100_list);


        Intent i = getIntent();
        final String selectedYear = i.getStringExtra("year");
        final int scrollTo = i.getIntExtra("scrollTo", 0);

        try {
            final JSONArray yearData = new JSONArray(i.getStringExtra("yearData"));

            setTitle("Top " + yearData.length() + " " + selectedYear);

            final ArrayList<Song> songList = new ArrayList<Song>();
            for (int j = 0; j < yearData.length(); ++j) {

                Song song = new Song();
                song.setInterpret(yearData.getJSONObject(j).getString("interpret"));
                song.setPosition(yearData.getJSONObject(j).getString("pos"));
                song.setTitle(yearData.getJSONObject(j).getString("title"));
                songList.add(song);
            }

            final ListView songsOfYearList = (ListView) findViewById(R.id.top100List);
            songsOfYearList.setAdapter(new CustomSongAdapter(this, songList));

            songsOfYearList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int clickedPositionAsInt,
                                        long clickedPositionAsString) {

                    try {

                        Intent i = new Intent(getApplicationContext(), ShowSongDetailsActivity.class);
                        i.putExtra("song", yearData.getJSONObject(clickedPositionAsInt).toString());
                        i.putExtra("year", selectedYear);
                        startActivity(i);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            songsOfYearList.setSelection(scrollTo -1);

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
