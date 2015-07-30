package czernecki.mateusz.factoryapp;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;

import java.text.DecimalFormat;

/**
 * Creates and Draws the Charts
 * In this prototype still hardcoded
 * Requires an actual Backend Connection to get the newest sensor data
 */
public class SensorActivity extends AppCompatActivity {

    //--------------------------------------------------------------------
    // hardcoded chart values
    //--------------------------------------------------------------------
    private final static int LINE_MAX = 10;
    private final static int LINE_MIN = -10;
    private final static String[] lineLabels = {"", "ANT", "GNU", "OWL", "APE", "JAY", ""};
    private final static float[][] lineValues = { {-5f, 6f, 2f, 9f, 0f, 1f, 5f}, {-9f, -2f, -4f, -3f, -7f, -5f, -3f} };
    //--------------------------------------------------------------------

    private void setUpLineChart(int id) {
        LineChartView mLineChart = (LineChartView) findViewById(id);

        // chart style using a Paint object
        Paint mLineGridPaint = new Paint();
        mLineGridPaint.setColor(Color.WHITE);
        mLineGridPaint.setPathEffect(new DashPathEffect(new float[] {5,5}, 0));
        mLineGridPaint.setStyle(Paint.Style.STROKE);
        mLineGridPaint.setAntiAlias(true);
        mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        // LineSet holds the data + additional styling
        LineSet dataSet = new LineSet();
        dataSet.addPoints(lineLabels, lineValues[0]);
        dataSet.setDots(true)
                .setDotsColor(Color.WHITE)
                .setDotsRadius(Tools.fromDpToPx(5))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.WHITE)
                .setLineColor(Color.WHITE)
                .setLineThickness(Tools.fromDpToPx(3))
                .beginAt(1).endAt(lineLabels.length - 1);
        mLineChart.addData(dataSet);

        // data for a line chart type
        dataSet = new LineSet();
        dataSet.addPoints(lineLabels, lineValues[1]);
        dataSet.setLineColor(Color.WHITE)
                .setLineThickness(Tools.fromDpToPx(3))
                .setSmooth(true)
                .setDashed(true);
        mLineChart.addData(dataSet);

        // additional styling
        mLineChart.setBorderSpacing(Tools.fromDpToPx(4))
                .setGrid(LineChartView.GridType.HORIZONTAL, mLineGridPaint)
                .setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setAxisBorderValues(LINE_MIN, LINE_MAX, 5)
                .setLabelsFormat(new DecimalFormat("##'u'"))
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // set up action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            // show back button in action bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // action bar title
            getSupportActionBar().setTitle("Sensor Data");
        }

        // setup 5 line charts
        setUpLineChart(R.id.linechart);
        setUpLineChart(R.id.linechart2);
        setUpLineChart(R.id.linechart3);
        setUpLineChart(R.id.linechart4);
        setUpLineChart(R.id.linechart5);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            // return to last view
            case android.R.id.home:
                onBackPressed(); return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
