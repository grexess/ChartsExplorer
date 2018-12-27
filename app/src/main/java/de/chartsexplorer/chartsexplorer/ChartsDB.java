package de.chartsexplorer.chartsexplorer;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
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
}
