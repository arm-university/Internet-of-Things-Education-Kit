package inf.mobileintelligent.wearableapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.DownloadManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hanks.htextview.base.HTextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PredictionActivity extends AppCompatActivity {

    private HTextView predictionText;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        predictionText = (HTextView) findViewById(R.id.prediction_text);
        timer = new Timer();
        final BarChart chart = (BarChart) findViewById(R.id.barchart);
        final ArrayList<BarEntry> oldEntries = new ArrayList<>();
        final ArrayList<BarEntry> newEntries = new ArrayList<>();
        final HashMap<Integer, String> labelMap = new HashMap<>();
        labelMap.put(0, "Walk");
        labelMap.put(1, "Run");
        labelMap.put(2, "Still");

        oldEntries.add(new BarEntry(.5f, 0f));
        oldEntries.add(new BarEntry(1.5f, 0f));
        oldEntries.add(new BarEntry(2.5f, 0f));

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Walk");
        labels.add("Run");
        labels.add("Still");

        XAxis xAxis = chart.getXAxis();
        YAxis rightAxis = chart.getAxisRight();
        YAxis leftAxis = chart.getAxisLeft();
        chart.getAxisRight().setEnabled(false);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawZeroLine(false);
        rightAxis.setDrawAxisLine(false); // no axis line
        rightAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getDescription().setEnabled(false);
        chart.setFitBars(true);


        BarDataSet barDataSet = new BarDataSet(oldEntries, "Real Time Prediction");
        BarData data = new BarData(barDataSet);
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        chart.setData(data);


        final RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://35.197.232.87/real_time_pred";

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String cleanElements = response.replaceAll("^\\[|]|\n$", "");
                        List<String> probStringList = new ArrayList<>(Arrays.asList(cleanElements.split(",")));
                        List<Float> probList = new ArrayList<>();
                        for (String item : probStringList){
                            probList.add(Float.parseFloat(item));
                        }
                        int max_index = 0;
                        float max_value = 0;
                        for (Float i: probList){
                            if (i > max_value){
                                max_value = i;
                                max_index = probList.indexOf(i);
                            }
                        }
                        if (! predictionText.getText().toString().equals(labelMap.get(max_index)))
                        {
                            predictionText.animateText(labelMap.get(max_index));
                        }

                        ArrayList probEntries = new ArrayList();
                        newEntries.add(new BarEntry(0.5f, probList.get(0)*100));
                        newEntries.add(new BarEntry(1.5f, probList.get(1)*100));
                        newEntries.add(new BarEntry(2.5f, probList.get(2)*100));

                        AnimateDataSetChanged changer = new AnimateDataSetChanged(1000, chart, oldEntries, newEntries);
                        changer.setInterpolator(new AccelerateInterpolator()); // optionally set the Interpolator
                        changer.run();
                        oldEntries.clear();
                        newEntries.clear();
                        oldEntries.add(new BarEntry(0.5f, probList.get(0)*100));
                        oldEntries.add(new BarEntry(1.5f, probList.get(1)*100));
                        oldEntries.add(new BarEntry(2.5f, probList.get(2)*100));


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("PredicionActivity", error.toString());
                }
        });



        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                queue.add(stringRequest);
            }
        }, 0, 2 * 1000);

    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }
}
