package ch.apquest.bb.appquest_04.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ch.apquest.bb.appquest_04.Other.Constants;
import ch.apquest.bb.appquest_04.R;

public class MainActivity extends AppCompatActivity {

    //region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.btnScanPath)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, Constants.RC_SCAN_QR_CODE);
            }
        });

        ((TextView) findViewById(R.id.textViewNoQrCode)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActivityResult(Constants.RC_SCAN_QR_CODE, RESULT_OK, new Intent().putExtra("SCAN_RESULT", getString(R.string.jsonSamplePath)));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RC_SCAN_QR_CODE) {
            if (resultCode == RESULT_OK) {
                Intent i = new Intent(getBaseContext(), PathActivity.class);
                i.putExtra(Constants.IE_PATH, data.getStringExtra("SCAN_RESULT"));
                startActivity(i);
            }
        }
    }

    //endregion

}
