package ch.appquest.toby.appquest_01;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LogBuchLogger {

    //region Fields

    private Activity parent;

    //endregion

    //region Constructor

    public LogBuchLogger(Activity parent) {
        this.parent = parent;
    }

    //endregion

    //region General Methods

    public void log(String qrCode) {
        Intent intent = new Intent("ch.appquest.intent.LOG");

        if (parent.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(parent, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }

        // result which is beeing submitted to the AppQuest-Logbuch App
        JSONObject jsonResult = new JSONObject();

        try {
            jsonResult.put("task", "Metalldetektor");
            jsonResult.put("solution", qrCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        intent.putExtra("ch.appquest.logmessage", jsonResult.toString());

        parent.startActivity(intent);
    }

    //endregion
}
