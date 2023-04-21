package inf.mobileintelligent.wearableapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button predBtn;
    private Button statBtn;
   // private Button sensorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        predBtn = (Button) findViewById(R.id.real_time_pred_btn);
        statBtn = (Button) findViewById(R.id.stat_btn);
       // sensorBtn = (Button) findViewById(R.id.reading_btn);

        predBtn.setOnClickListener(this);
        statBtn.setOnClickListener(this);
       // sensorBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.real_time_pred_btn:
                Intent intent = new Intent(this, PredictionActivity.class);
                startActivity(intent);
                break;
            case R.id.stat_btn:
                Intent histIntent = new Intent(this, HistoryActivity.class);
                startActivity(histIntent);
                break;
           // case R.id.reading_btn:
               // break;
        }
    }
}
