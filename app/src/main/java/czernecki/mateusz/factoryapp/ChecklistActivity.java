package czernecki.mateusz.factoryapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SyncFailedException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Displays the checklist and generates a pdf report
 */
public class ChecklistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set title and back button for action bar
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Checklist");
        }

        // get objectId of machine to acquire the right questions
        String objectId;
        Intent i = getIntent();
        if (i.hasExtra("objectId")){
            objectId = i.getStringExtra("objectId");
        }
        else {
            finish();
            return;
        }

        final Activity activity = this;

        final LinearLayout parent = (LinearLayout) findViewById(R.id.linearLayout);

        // set date for checklist
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        ((TextView)findViewById(R.id.dateandtime)).setText(date);

        // get machine from Backend
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Machine");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject machine, ParseException e) {
                if (e == null) {
                    JSONArray questions = machine.getJSONArray("Questions");
                    String title = machine.getString("name");

                    ((TextView) findViewById(R.id.title)).setText(title);

                    // for every question draw a checkbox and its question
                    // + styling
                    for(int i = 0; i < questions.length(); i++)
                    {
                        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        LinearLayout LL = new LinearLayout(activity);
                        LLParams.setMargins(8, i == 0 ? 0 : 16, 32, 0);
                        LL.setOrientation(LinearLayout.HORIZONTAL);

                        LL.setLayoutParams(LLParams);

                        CheckBox checkBox = new CheckBox(activity);
                        checkBox.setPadding(8, 8, 8, 8);
                        try {
                            checkBox.setText(questions.getString(i));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        checkBox.setTextColor(Color.rgb(60,60,60));
                        checkBox.setTextSize(18);
                        LL.addView(checkBox);

                        parent.addView(LL);
                    }


                } else {
                    // something went wrong
                }
            }
        });

        parent.invalidate();
    }

    /**
     * generates the pdf file with build in android functions
     * it just takes a "screenshot" of the view and then prints
     * it into a pdf file and stores it on the device
     * @param view
     */
    public void generatePDF(View view) {

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        ((TextView)findViewById(R.id.dateandtime)).setText(date);

        PrintAttributes.Builder printAttrsBuilder = new PrintAttributes.Builder();
        printAttrsBuilder.setMediaSize(PrintAttributes.MediaSize.ISO_A1);
        printAttrsBuilder.setMinMargins(new PrintAttributes.Margins(5, 5, 5, 5));

        // open a new document
        PrintedPdfDocument document = new PrintedPdfDocument(this, printAttrsBuilder.build());

        // PDF document generation
        PdfDocument.Page page = document.startPage(1); // set page one
        Canvas pdfCanvas = page.getCanvas(); // get canvas of view
        findViewById(R.id.pdfView).draw(pdfCanvas); // get canvas that will be drawn into pdf
        document.finishPage(page);
        File result = null; // create file object to store pdf on device
        FileOutputStream fos = null; // required for storing
        BufferedOutputStream bos = null; // required for storing
        FileDescriptor descriptor = null; // required for storing
        try {
            // if already exists -> replace
            if(new File(Environment.getExternalStorageDirectory() +  "/result.pdf").exists())
                new File(Environment.getExternalStorageDirectory() +  "/result.pdf").delete();

            // store on external storage path
            result = new File(Environment.getExternalStorageDirectory() +  "/result.pdf");
            fos = new FileOutputStream(result);
            bos = new BufferedOutputStream(fos);
            document.writeTo(bos);
            descriptor = fos.getFD();
            descriptor.sync();
        } catch (Exception e) {
            // Error handling
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            try {
                // clean file store objects
                if (bos != null) { bos.flush(); bos.close(); }
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        document.close();

        // open pdf directly after creating it
        Intent i = new Intent();
        i.setAction(android.content.Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(result), "application/pdf");
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed(); return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
