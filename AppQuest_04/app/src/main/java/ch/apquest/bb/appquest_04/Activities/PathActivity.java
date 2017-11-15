package ch.apquest.bb.appquest_04.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.XYPlot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ch.apquest.bb.appquest_04.Other.Constants;
import ch.apquest.bb.appquest_04.Other.Enums;
import ch.apquest.bb.appquest_04.Path.PathElement;
import ch.apquest.bb.appquest_04.Path.PathElementAdapter;
import ch.apquest.bb.appquest_04.R;
import ch.apquest.bb.appquest_04.Step.StepController;
import ch.apquest.bb.appquest_04.Step.StepListener;
import ch.apquest.bb.appquest_04.TextToSpeech.TextToSpeechController;
import ch.apquest.bb.appquest_04.TextToSpeech.TextToSpeechListener;
import ch.apquest.bb.appquest_04.Turn.TurnController;
import ch.apquest.bb.appquest_04.Turn.TurnListener;

public class PathActivity extends AppCompatActivity implements StepListener, TurnListener, TextToSpeechListener {

    //region Fields

    private StepController stepController = null;
    private TurnController turnController = null;
    private TextToSpeechController textToSpeechController = null;

    private ImageView imageViewCurrentPathElement = null;
    private ListView listViewPath = null;
    private TextView textViewCurrentPathElement = null;
    private TextView textViewCurrentPathElementProgressIndicator = null;
    private ProgressBar progressbarCurrentPathElement = null;
    private XYPlot plotMovement = null;

    private Menu menu = null;

    private ArrayList<PathElement> pathElements = new ArrayList<>();
    private PathElementAdapter pathElementAdapter = null;

    private boolean settings_showPlot = false;

    private int startStation;

    //endregion

    //region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle intentExtras = getIntent().getExtras();
        String intentExtrasPathSequenceString = intentExtras.getString(Constants.IE_PATH);

        try {
            JSONObject jsonPathSequence = new JSONObject(intentExtrasPathSequenceString);
            JSONArray jsonPathSequenceElements = jsonPathSequence.getJSONArray("input");

            for (int i = 0; i < jsonPathSequenceElements.length(); i++) {
                String pathSequenceElement = jsonPathSequenceElements.getString(i);
                PathElement pathElement = new PathElement();
                switch (pathSequenceElement) {
                    case "links":
                        pathElement.setOrientation(Enums.ORIENTATION.LEFT);
                        break;
                    case "rechts":
                        pathElement.setOrientation(Enums.ORIENTATION.RIGHT);
                        break;
                    default:
                        pathElement.setOrientation(Enums.ORIENTATION.FORWARD);
                        pathElement.setTotalStepsTodo(Integer.parseInt(pathSequenceElement));
                        break;
                }
                pathElements.add(pathElement);
            }

            startStation = jsonPathSequence.getInt("startStation");
        } catch (JSONException e) {
            Toast.makeText(this, R.string.toast_invalidPathFormat, Toast.LENGTH_SHORT).show();
            finish();
        }

        imageViewCurrentPathElement = (ImageView) findViewById(R.id.imageViewCurrentPathElement);
        listViewPath = (ListView) findViewById(R.id.listViewPath);
        textViewCurrentPathElement = (TextView) findViewById(R.id.textViewCurrentMove);
        textViewCurrentPathElementProgressIndicator = (TextView) findViewById(R.id.textViewCurrentPathElementProgressIndicator);
        progressbarCurrentPathElement = (ProgressBar) findViewById(R.id.progressBarCurrentPathElement);
        plotMovement = (XYPlot) findViewById(R.id.plotSteps);

        pathElementAdapter = new PathElementAdapter(this, R.layout.path_element_list_item, pathElements);
        listViewPath.setAdapter(pathElementAdapter);

        initPathElement(0);
        initControllers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        getMenuInflater().inflate(R.menu.menu_path, menu);

        menu.findItem(R.id.action_showPlot).setChecked(settings_showPlot);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_log:
                return action_log_clicked();
            case R.id.action_skip:
                return action_skip_clicked();
            case R.id.action_showPlot:
                return action_showPlot_clicked(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        stepController.pause();
        turnController.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();

        updateControllers();
        updateDisplayMode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (textToSpeechController != null) {
            textToSpeechController.destroy();
        }
    }

    @Override
    public void onStep() {
        int selectedIndex = listViewPath.getCheckedItemPosition();

        if (selectedIndex > -1) {
            PathElement pe = pathElements.get(selectedIndex);

            // only relevant if current path element = { forward }
            if (pe.getOrientation().equals(Enums.ORIENTATION.FORWARD)) {
                if (pe.getStepsDone() < pe.getTotalStepsTodo()) {
                    // still moves to do
                    pe.setStepsDone(pe.getStepsDone() + 1);

                    // all moves done?
                    if (pe.getStepsDone() == pe.getTotalStepsTodo()) {
                        // finished with all the moves -> load next step
                        initNextPathElement();
                    } else {
                        updateDisplayMode();
                    }
                } else {
                    // finished with all the moves -> load next step
                    initNextPathElement();
                }
            }
        }
    }

    @Override
    public void onTurn() {
        int selectedIndex = listViewPath.getCheckedItemPosition();

        if (selectedIndex >= 0) {
            PathElement pe = pathElements.get(selectedIndex);

            // only relevant if path element = { left, right }
            if (!pe.getOrientation().equals(Enums.ORIENTATION.FORWARD)) {
                initNextPathElement();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RC_SCAN_QR_CODE) {
            if (resultCode == RESULT_OK) {
                String logMsg = data.getStringExtra("SCAN_RESULT");

                Intent i = new Intent("ch.appquest.intent.LOG");

                if (getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
                    Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject json = new JSONObject();
                try {
                    json.put("task", "Schrittzaehler");
                    json.put("startStation", startStation);
                    json.put("endStation", new JSONObject(logMsg).getInt("endStation"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                i.putExtra("ch.appquest.logmessage", json.toString());

                startActivity(i);
            }
        } else if (requestCode == Constants.RC_CHECK_TTS_DATA) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                textToSpeechController = new TextToSpeechController(this, this);
            } else {
                onTextToSpeechInit(TextToSpeech.ERROR);
            }
        }
    }

    @Override
    public void onTextToSpeechInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // callback once tts is successfully loaded
            ttsSpeakCurrentPathElement();
        } else {
            Toast.makeText(this, R.string.toast_ttsNotLoaded, Toast.LENGTH_SHORT).show();
        }
    }

    //endregion

    //region General Methods

    private void saveSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean(Constants.SP_SETTINGS_SHOWPLOT, settings_showPlot).apply();
    }

    private void loadSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        settings_showPlot = prefs.getBoolean(Constants.SP_SETTINGS_SHOWPLOT, false);
    }

    private boolean action_showPlot_clicked(MenuItem item) {
        item.setChecked(!item.isChecked());
        settings_showPlot = item.isChecked();
        saveSettings();
        updateDisplayMode();
        return true;
    }

    private boolean action_log_clicked() {
        startQrCodeScanIntent();
        return true;
    }

    private boolean action_skip_clicked() {
        int selectedIndex = listViewPath.getCheckedItemPosition();
        if (selectedIndex > -1) {
            PathElement pe = pathElements.get(selectedIndex);
            pe.setStepsDone(pe.getTotalStepsTodo());
            initNextPathElement();
            return true;
        }
        return false;
    }

    private void initControllers() {
        stepController = new StepController(this, this, plotMovement);
        turnController = new TurnController(this, this);

        // text to speech, data check
        Intent intent = new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, Constants.RC_CHECK_TTS_DATA);
    }

    private void initNextPathElement() {
        int selectedIndex = listViewPath.getCheckedItemPosition();
        if (selectedIndex > -1) {
            pathElements.get(selectedIndex).setDone(true).setIsCurrent(false);

            if (pathElements.size() > listViewPath.getCheckedItemPosition() + 1) {
                initPathElement(listViewPath.getCheckedItemPosition() + 1);
            } else {
                initPathDoneDialog();
            }
        } else {
            initPathElement(0);
        }
    }

    private void startQrCodeScanIntent() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, Constants.RC_SCAN_QR_CODE);
    }

    private void initPathDoneDialog() {
        updateDisplayMode();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_pathDone_message)
                .setPositiveButton(R.string.dialog_pathDone_okButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startQrCodeScanIntent();
                    }
                })
                .setNegativeButton(R.string.dialog_pathDone_cancelButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDisplayMode();
                    }
                })
                .show();
    }

    private void updateDisplayMode() {
        int selectedIndex = listViewPath.getCheckedItemPosition();

        if (selectedIndex > -1) {
            PathElement pe = pathElements.get(selectedIndex);

            switch (pe.getOrientation()) {
                case LEFT:
                    textViewCurrentPathElement.setText(R.string.orientationLeft);
                    progressbarCurrentPathElement.setVisibility(View.GONE);
                    textViewCurrentPathElementProgressIndicator.setVisibility(View.GONE);
                    plotMovement.setVisibility(View.GONE);
                    imageViewCurrentPathElement.setImageResource(R.drawable.arrow_left);
                    break;
                case RIGHT:
                    textViewCurrentPathElement.setText(R.string.orientationRight);
                    progressbarCurrentPathElement.setVisibility(View.GONE);
                    textViewCurrentPathElementProgressIndicator.setVisibility(View.GONE);
                    imageViewCurrentPathElement.setImageResource(R.drawable.arrow_right);
                    plotMovement.setVisibility(View.GONE);
                    break;
                case FORWARD:
                    textViewCurrentPathElement.setText(R.string.orientationForward);
                    progressbarCurrentPathElement.setVisibility(View.VISIBLE);
                    textViewCurrentPathElementProgressIndicator.setVisibility(View.VISIBLE);
                    imageViewCurrentPathElement.setImageResource(R.drawable.arrow_forward);
                    if (settings_showPlot) {
                        plotMovement.setVisibility(View.VISIBLE);
                    } else {
                        plotMovement.setVisibility(View.GONE);
                    }
                    progressbarCurrentPathElement.setMax(pe.getTotalStepsTodo());
                    progressbarCurrentPathElement.setProgress(pe.getStepsDone());
                    textViewCurrentPathElementProgressIndicator.setText(progressbarCurrentPathElement.getProgress() + "/" + progressbarCurrentPathElement.getMax());
                    break;
            }
        }
    }

    private void updateControllers() {
        if (stepController != null && turnController != null) {
            int selectedIndex = listViewPath.getCheckedItemPosition();

            if (selectedIndex > -1) {
                if (!pathElements.get(selectedIndex).getOrientation().equals(Enums.ORIENTATION.FORWARD)) {
                    // current path element = { left, right }
                    stepController.pause();
                    turnController.reset();
                    turnController.resume();
                } else {
                    // current path element = { forward }
                    stepController.resume();
                    turnController.pause();
                }
            }
        }
    }

    private void initPathElement(int position) {
        if (position > -1 && position < pathElements.size()) {
            listViewPath.setItemChecked(position, true);

            pathElements.get(position).setIsCurrent(true);
            ttsSpeakCurrentPathElement();

            updateControllers();
            updateDisplayMode();
        }
    }

    private void ttsSpeakCurrentPathElement() {
        if (textToSpeechController != null) {
            switch (pathElements.get(listViewPath.getCheckedItemPosition()).getOrientation()) {
                case FORWARD:
                    textToSpeechController.speak(String.format(getResources().getString(R.string.ttsForward), pathElements.get(listViewPath.getCheckedItemPosition()).getTotalStepsTodo()));
                    break;
                case LEFT:
                    textToSpeechController.speak(getResources().getString(R.string.ttsLeft));
                    break;
                case RIGHT:
                    textToSpeechController.speak(getResources().getString(R.string.ttsRight));
                    break;
            }
        }
    }

    //endregion

}
