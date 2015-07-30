package czernecki.mateusz.factoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Responsible for the secure Backend Connection
 * In this prototype it is only a placeholder
 * Extend depending on your requirements
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        // todo to start the app you must provide your own Parse Backend conncetion
        // additionally it needs the table structure shown on Figure 37 to function properly
        Parse.initialize(this, "YOUR_APPLICATION_ID", "YOUR_CLIENT_ID");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // action bar menu
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Use this function to create a log-in functionallity
     * at this state it only shows the following screen: FactoryActivity
     * @param view
     */
    public void login(View view) {
        Intent intent = new Intent(this, FactoryActivity.class);
        this.startActivity(intent);
    }
}
