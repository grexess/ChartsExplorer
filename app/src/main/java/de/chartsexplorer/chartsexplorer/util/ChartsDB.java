package de.chartsexplorer.chartsexplorer.util;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ChartsDB
{

    JSONObject top100DB;

    public ChartsDB(Context context) throws IOException, JSONException {
        top100DB = this.getDBasJSONObject(context);
    }

    private JSONObject getDBasJSONObject(Context context) throws JSONException, IOException {

        StringBuilder sb =  new StringBuilder();

        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("top100.json");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        char[] inputBuffer = new char[100];

        int charRead;

        while ((charRead = inputStreamReader.read(inputBuffer))>0){
            String readString = String.copyValueOf(inputBuffer,0, charRead);
            sb.append(readString);
        }

        return new JSONObject(sb.toString());
    }

    public List<String> getYears(){

        List<String> yearList = new ArrayList();

        Iterator<String> keys = top100DB.keys();
        while (keys.hasNext()){
            yearList.add(keys.next());
        }

        Collections.sort(yearList);
        Collections.reverse(yearList);

        return yearList;
    }

    public String getYearData(String selectedYear) throws JSONException {
            return top100DB.getJSONArray(selectedYear).toString();
    }

    public ArrayList<SongSearch> searchFor(String searchString) throws JSONException{

        ArrayList<SongSearch> searchResults = new ArrayList();

        Iterator<String> keys = top100DB.keys();
        while (keys.hasNext()){
            String yearKey = keys.next();

            JSONArray year = top100DB.getJSONArray(yearKey);

            for (int i = 0; i < year.length(); i++) {

                JSONObject jSong = (JSONObject) year.get(i);
                if((jSong.getString("interpret").toLowerCase().contains(searchString.toLowerCase())) || (jSong.getString("title").toLowerCase().contains(searchString.toLowerCase())) ){
                    SongSearch song = new SongSearch();
                    song.setInterpret(jSong.getString("interpret"));
                    song.setPosition(jSong.getString("pos"));
                    song.setYear(yearKey);
                    song.setTitle(jSong.getString("title"));
                    searchResults.add(song);
                }
            }
        }
        return searchResults;
    }
}
