package czernecki.mateusz.factoryapp;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import czernecki.mateusz.factoryapp.Enums.MachineState;
import czernecki.mateusz.factoryapp.Objects.ExpertInfo;
import czernecki.mateusz.factoryapp.Objects.Machine;

/**
 * Information about a machine and its available functions
 */
public class MachineActivity extends AppCompatActivity {

    Machine mCurrentMachine;
    Toolbar mToolbar;

    /**
     * Cleans up the view
     * Async machine loading shall happen at the same time
     * View styling + set all values to loading
     */
    private void cleanView() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);

        window.setStatusBarColor(this.getResources().getColor(R.color.primary_dark));
        header.setBackgroundResource(R.color.primary);
        mToolbar.setBackgroundResource(R.color.primary);

        TextView name = (TextView) findViewById(R.id.name);
        TextView desc = (TextView) findViewById(R.id.desc);

        name.setText("Loading ...");
        desc.setText("Machine Data");

        ImageView img = (ImageView) findViewById(R.id.toggle);
        img.setImageResource(R.drawable.flash_off);
    }

    /**
     * set basic colors, text and state information
     * depending on the state of the machine
     */
    private void setUpView() {
        // top android bar styling
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);

        // set colors and icons depending on the machines state
        switch (mCurrentMachine.state) {
            case unavailable:   break;
            case safe:          window.setStatusBarColor(this.getResources().getColor(R.color.green_dark));
                header.setBackgroundResource(R.color.green);
                mToolbar.setBackgroundResource(R.color.green); break;
            case at_risk:       window.setStatusBarColor(this.getResources().getColor(R.color.orange_dark));
                header.setBackgroundResource(R.color.orange);
                mToolbar.setBackgroundResource(R.color.orange);break;
            case critical:      window.setStatusBarColor(this.getResources().getColor(R.color.red_dark));
                header.setBackgroundResource(R.color.red);
                mToolbar.setBackgroundResource(R.color.red);break;
            default: break;
        }

        TextView name = (TextView) findViewById(R.id.name);
        TextView desc = (TextView) findViewById(R.id.desc);

        name.setText(mCurrentMachine.name);
        desc.setText(mCurrentMachine.description);

        // set view colors depending on turnedOn state
        if(!mCurrentMachine.turnedOn) {
            window.setStatusBarColor(this.getResources().getColor(R.color.grey_dark));
            header.setBackgroundResource(R.color.grey);
            mToolbar.setBackgroundResource(R.color.grey);
        }

        // machine turned on or off? -> set appropriate icons
        ImageView img = (ImageView) findViewById(R.id.toggle);
        if(mCurrentMachine.turnedOn) {
            img.setImageResource(R.drawable.flash);
        }
        else {
            img.setImageResource(R.drawable.flash_off);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        cleanView();

        String objectId;

        // acquire objectId of selected machine
        Intent i = getIntent();
        if (i.hasExtra("objectId")){
            objectId = i.getStringExtra("objectId");
        }
        else {
            finish();
            return;
        }

        // get machine values from backend
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Machine");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject machine, ParseException e) {
                if (e == null) {
                    String id = machine.getObjectId();
                    String name = machine.getString("name");
                    String desc = machine.getString("description");
                    String expertMail = machine.getString("expertmail");
                    boolean turnedOn = machine.getBoolean("turnedOn");
                    Number state = machine.getNumber("state");

                    mCurrentMachine = new Machine(id, name, desc, turnedOn, MachineState.values()[state.intValue()], new ExpertInfo(expertMail), null);

                    setUpView();
                } else {
                    // something went wrong
                }
            }
        });
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
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleOnOff(View view) {
        ImageView img = (ImageView) view.findViewById(R.id.toggle);

        // turned on or off -> set icons
        if(mCurrentMachine.turnedOn) {
            img.setImageResource(R.drawable.flash_off);
            mCurrentMachine.turnedOn = false;
        }
        else {
            img.setImageResource(R.drawable.flash);
            mCurrentMachine.turnedOn = true;
        }

        // turn machine on or off
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Machine");
        query.getInBackground(mCurrentMachine.parseId, new GetCallback<ParseObject>() {
            public void done(ParseObject machine, ParseException e) {
                if (e == null) {
                    machine.put("turnedOn", mCurrentMachine.turnedOn);
                    machine.saveInBackground();
                }
            }
        });

        InvalidateView();
    }

    /**
     * updates the view depending on the machine information
     * includes a soft animation
     */
    public void InvalidateView() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        final RelativeLayout header = (RelativeLayout) findViewById(R.id.header);

        TextView name = (TextView) findViewById(R.id.name);
        TextView desc = (TextView) findViewById(R.id.desc);

        name.setText(mCurrentMachine.name);
        desc.setText(mCurrentMachine.description);

        if(!mCurrentMachine.turnedOn) {
            Integer colorFrom = getResources().getColor(R.color.red);
            Integer colorTo = getResources().getColor(R.color.grey);

            switch (mCurrentMachine.state) {
                case unavailable:   break;
                case safe:          window.setStatusBarColor(this.getResources().getColor(R.color.grey_dark));
                    colorFrom = getResources().getColor(R.color.green); break;
                case at_risk:       window.setStatusBarColor(this.getResources().getColor(R.color.grey_dark));
                    colorFrom = getResources().getColor(R.color.orange); break;
                case critical:      window.setStatusBarColor(this.getResources().getColor(R.color.grey_dark));
                    colorFrom = getResources().getColor(R.color.red); break;
                default: break;
            }

            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    header.setBackgroundColor((Integer)animator.getAnimatedValue());
                    mToolbar.setBackgroundColor((Integer) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();

            header.setBackgroundResource(R.color.grey);
            mToolbar.setBackgroundResource(R.color.grey);
        }
        else {
            Integer colorFrom = getResources().getColor(R.color.grey);
            Integer colorTo = getResources().getColor(R.color.grey);

            switch (mCurrentMachine.state) {
                case unavailable:   break;
                case safe:          window.setStatusBarColor(this.getResources().getColor(R.color.green_dark));
                    colorTo = getResources().getColor(R.color.green); break;
                case at_risk:       window.setStatusBarColor(this.getResources().getColor(R.color.orange_dark));
                    colorTo = getResources().getColor(R.color.orange); break;
                case critical:      window.setStatusBarColor(this.getResources().getColor(R.color.red_dark));
                    colorTo = getResources().getColor(R.color.red); break;
                default: break;
            }

            // Animation
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    header.setBackgroundColor((Integer)animator.getAnimatedValue());
                    mToolbar.setBackgroundColor((Integer) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();

            header.setBackgroundResource(R.color.grey);
            mToolbar.setBackgroundResource(R.color.grey);
        }
    }

    /**
     * this method demonstrates how an expert may be requested by mail
     * may be extended to support other types of communication
     * @param view
     */
    public void requestExpert(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mCurrentMachine.expert.getEmail(), null));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, mCurrentMachine.expert.getEmail());
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, mCurrentMachine.name);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "We need your help with the following machine: " + mCurrentMachine.name);
        startActivity(Intent.createChooser(emailIntent, "Send E-Mail"));
    }

    /**
     * Shows the Sensor View
     * @param view
     */
    public void showSensorView(View view) {
        Intent intent = new Intent(this, SensorActivity.class);
        this.startActivity(intent);
    }

    /**
     * Starts a new checklist
     * @param view
     */
    public void startCheck(View view) {
        Intent i = new Intent(this, ChecklistActivity.class);
        i.putExtra("objectId", mCurrentMachine.parseId);
        startActivity(i);
    }
}
