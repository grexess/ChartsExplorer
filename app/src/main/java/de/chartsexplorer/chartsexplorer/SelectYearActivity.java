package de.chartsexplorer.chartsexplorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

public class SelectYearActivity extends AppCompatActivity {

    ChartsDB chartsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.year_list);

        try {
            chartsDB = new ChartsDB(getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        ListView yearList = (ListView) findViewById(R.id.yearList);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.year_item, R.id.yearItem, chartsDB.getYears());
        yearList.setAdapter(arrayAdapter);

        yearList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {

                String selectedYear = ((TextView) view.findViewById(R.id.yearItem)).getText().toString();
                try {


                    Intent i = new Intent(getApplicationContext(), ShowChartsOfYearActivity.class);
                    i.putExtra("year", selectedYear);

                    i.putExtra("yearData", chartsDB.getYearData(selectedYear));
                    startActivity(i);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}

