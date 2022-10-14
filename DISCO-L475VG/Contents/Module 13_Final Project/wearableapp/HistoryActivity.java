package inf.mobileintelligent.wearableapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.data.BarEntry;
import com.ramotion.foldingcell.FoldingCell;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        final ListView listView = (ListView) findViewById(R.id.listView);


        final RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://35.197.232.87//history";

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("HistoryActivity", Integer.toString(response.length()));
                Log.i("HistoryActivity", response.toString());
                try{
                    ArrayList<HistoryItem> items = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++){
                        JSONArray item = response.getJSONArray(i);
                        items.add(new HistoryItem(item.get(1).toString(), item.get(2).toString(), item.get(3).toString(), Float.parseFloat(item.get(4).toString()), item.get(0).toString(), item.get(5).toString()));
                    }

                    final FoldingCellListAdapter adapter = new FoldingCellListAdapter(getApplicationContext(), items);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ((FoldingCell) view).toggle(false);
                            adapter.registerToggle(position);
                        }
                    });

                }
                catch (org.json.JSONException error){
                    Log.i("HistoryActivity", error.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);

    }
}
