package czernecki.mateusz.factoryapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import czernecki.mateusz.factoryapp.Enums.MachineState;
import czernecki.mateusz.factoryapp.Objects.ExpertInfo;
import czernecki.mateusz.factoryapp.Objects.Machine;

/**
 * Lists all machines in the factory and sorts them accordingly
 */
public class FactoryActivity extends AppCompatActivity {

    public Context context;

    List<Machine> machines = new ArrayList<Machine>();
    StableArrayAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    // aquire all machines from the Backend
    private void fetchMachines() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Machine");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    machines.clear();

                    // we receive parseObjects that need to be transformed into more concrete objects
                    for (ParseObject machine : results) {
                        String id = machine.getObjectId();
                        String name = machine.getString("name");
                        String desc = machine.getString("description");
                        String expertMail = machine.getString("expertmail");
                        boolean turnedOn = machine.getBoolean("turnedOn");
                        Number state = machine.getNumber("state");

                        // add machine to list that is displayed in the view
                        machines.add(new Machine(id, name, desc, turnedOn, MachineState.values()[state.intValue()], new ExpertInfo(expertMail), null));
                    }

                    // sort the machines using their sort values
                    Collections.sort(machines);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Updates the machines
     */
    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchMachines();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 0);
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        final ListView listview = (ListView) findViewById(R.id.listview);

        adapter = new StableArrayAdapter(this, machines);
        listview.setAdapter(adapter);

        refreshContent();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // on machine click -> open MachineActivity and send the objectId
                // to inform the following activity which machine to fetch from the database
                Machine currentListing = (Machine) view.getTag();
                Intent i = new Intent(context, MachineActivity.class);
                i.putExtra("objectId", currentListing.parseId);
                startActivity(i);
            }
        });
    }

    /**
     * Adapters are necessary for lists that are displayed in android
     * it handles all machine list items
     */
    private class StableArrayAdapter extends ArrayAdapter<Machine> {

        private final Context context;
        private final List<Machine> machines;

        public StableArrayAdapter(Context context, List<Machine> machines) {
            super(context, -1, machines);
            this.context = context;
            this.machines = machines;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = convertView;

            if(rowView == null) {
                rowView = inflater.inflate(R.layout.machine_list_item, parent, false);
            }

            Machine currentMachine = machines.get(position);

            // get title and description
            TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
            TextView secondLine = (TextView) rowView.findViewById(R.id.secondLine);

            // get icons
            ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
            ImageView statusIcon = (ImageView) rowView.findViewById(R.id.status);

            // set title and description
            firstLine.setText(currentMachine.name);
            secondLine.setText(currentMachine.description);

            // set state icon for every machine
            switch (currentMachine.state) {
                case unavailable:   statusIcon.setImageBitmap(null); break;
                case safe:          statusIcon.setImageResource(R.mipmap.ic_good); break;
                case at_risk:       statusIcon.setImageResource(R.mipmap.ic_danger); break;
                case critical:      statusIcon.setImageResource(R.mipmap.ic_error); break;
                default: break;
            }

            // set turnedOn/Off icon
            if (currentMachine.turnedOn) {
                icon.setImageResource(R.mipmap.ic_machine);
            } else {
                icon.setImageResource(R.mipmap.ic_machine_off);
                statusIcon.setImageBitmap(null);
            }

            rowView.setTag(currentMachine);

            return rowView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_factory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
