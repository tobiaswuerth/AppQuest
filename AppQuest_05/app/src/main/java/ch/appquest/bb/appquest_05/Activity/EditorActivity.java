package ch.appquest.bb.appquest_05.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import ch.appquest.bb.appquest_05.ColorSelector.ColorSelectorButton;
import ch.appquest.bb.appquest_05.ColorSelector.ColorSelectorController;
import ch.appquest.bb.appquest_05.ColorSelector.ColorSelectorHandler;
import ch.appquest.bb.appquest_05.Drawing.DrawingMode;
import ch.appquest.bb.appquest_05.Drawing.DrawingPitch;
import ch.appquest.bb.appquest_05.Drawing.DrawingPitchCell;
import ch.appquest.bb.appquest_05.Drawing.DrawingPitchHandler;
import ch.appquest.bb.appquest_05.Fragments.ColorPickerFragment.AmbilWarnaDialogFragment;
import ch.appquest.bb.appquest_05.Fragments.ColorPickerFragment.OnAmbilWarnaListener;
import ch.appquest.bb.appquest_05.Fragments.SeekBarFragment.SeekBarFragment;
import ch.appquest.bb.appquest_05.Fragments.SeekBarFragment.SeekBarFragmentListener;
import ch.appquest.bb.appquest_05.Other.Constants;
import ch.appquest.bb.appquest_05.R;

public class EditorActivity extends AppCompatActivity {

    //region Fields

    private DrawingPitch drawingPitch = null;

    private ColorSelectorController controllerColorSelector = null;
    private DrawingPitchHandler handlerDrawingPitch = null;

    private LinearLayout linearLayoutEditorContent = null;

    private ImageButton btnDraw = null;
    private ImageButton btnFill = null;
    private ImageButton btnSquare = null;
    private ImageButton btnSphere = null;

    //endregion

    //region General Methods

    private void drawingModeControlClicked(DrawingMode drawingMode) {
        switch (handlerDrawingPitch.getDrawingMode()) {
            case PEN:
                btnDraw.setImageResource(R.drawable.pen_gray);
                break;
            case FILL:
                btnFill.setImageResource(R.drawable.fill_gray);
                break;
            case SPHERE:
                btnSphere.setImageResource(R.drawable.sphere_gray);
                break;
            case SQUARE:
                btnSquare.setImageResource(R.drawable.square_gray);
                break;
        }

        handlerDrawingPitch.setDrawingMode(drawingMode);

        switch (drawingMode) {
            case PEN:
                btnDraw.setImageResource(R.drawable.pen_black);
                break;
            case FILL:
                btnFill.setImageResource(R.drawable.fill_black);
                break;
            case SPHERE:
                btnSphere.setImageResource(R.drawable.sphere_black);
                break;
            case SQUARE:
                btnSquare.setImageResource(R.drawable.square_black);
                break;
        }
    }

    private void startFragmentColorPicker(final ColorSelectorButton reference) {
        int initColor = (reference == null ? Color.WHITE : reference.getBackgroundColor());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AmbilWarnaDialogFragment fragment = AmbilWarnaDialogFragment.newInstance(initColor);

        OnAmbilWarnaListener onAmbilWarnaListener = new OnAmbilWarnaListener() {
            @Override
            public void onColorPickerCancel(AmbilWarnaDialogFragment dialogFragment) {
            }

            @Override
            public void onColorPickerOk(AmbilWarnaDialogFragment dialogFragment, int color) {
                if (reference == null) {
                    controllerColorSelector.addNewColorSelector(color);
                } else {
                    reference.setBackgroundColor(color);
                    if (reference.isSelected()) {
                        reference.callOnClick();
                    }
                }
            }
        };

        fragment.setOnAmbilWarnaListener(onAmbilWarnaListener);
        fragment.show(ft, AmbilWarnaDialogFragment.class.getSimpleName());
    }

    private void startFragmentSeekBar() {
        SeekBarFragment fragment = SeekBarFragment.newInstance(drawingPitch.getDrawingPitchSize());

        fragment.setListenerSeekBarFragment(new SeekBarFragmentListener() {
            @Override
            public void onSeekBarChanged(int value) {
                drawingPitch.setDrawingPitchSize(value);
            }
        });

        fragment.show(getFragmentManager(), SeekBarFragment.class.getSimpleName());
    }

    private void action_save_clicked() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.dialog_save_title), getString(R.string.dialog_save_body), false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File savingDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    savingDirectory.mkdirs();

                    int drawingPitchSize = drawingPitch.getDrawingPitchSize();
                    String savingFile = "pixelmaler_" + drawingPitchSize + "x" + drawingPitchSize + "_" + System.currentTimeMillis() + ".jpg";

                    drawingPitch.getDrawingPitchBitmap().compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(savingDirectory.getPath() + "/" + savingFile));

                    Snackbar.make(linearLayoutEditorContent, R.string.dialog_save_successful, Snackbar.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    Snackbar.make(linearLayoutEditorContent, R.string.dialog_save_failed, Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    private void startLoggingActivity() {
        Intent i = new Intent("ch.appquest.intent.LOG");

        if (getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("task", "Pixelmaler");
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("pixels", jsonArray);

            for (DrawingPitchCell drawingPitchCell : drawingPitch.getDrawingPitchCells()) {
                if (drawingPitchCell.getColor() == Color.WHITE) {
                    continue;
                }

                JSONObject cellObject = new JSONObject();
                cellObject.put("y", String.valueOf(drawingPitchCell.getPointInDrawingPitch().y));
                cellObject.put("x", String.valueOf(drawingPitchCell.getPointInDrawingPitch().x));
                cellObject.put("color", String.format("#%08X", drawingPitchCell.getColor()));
                jsonArray.put(cellObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        i.putExtra("ch.appquest.logmessage", jsonObject.toString());

        startActivity(i);
    }

    private void action_reset_clicked() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_reset_title)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_reset_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drawingPitch.setDrawingPitchSize(Constants.INITIAL_DRAWING_PITCH_SIZE);
                        handlerDrawingPitch.clearPaint();
                        controllerColorSelector.initialize();
                        btnDraw.callOnClick();
                    }
                })
                .setNegativeButton(R.string.dialog_reset_no, null)
                .create();
        alertDialog.show();
    }

    private void action_log_clicked() {
        if (drawingPitch.getDrawingPitchSize() > Constants.INITIAL_DRAWING_PITCH_SIZE) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.dialog_log_body)
                    .setTitle(R.string.dialog_log_title)
                    .setPositiveButton(R.string.dialog_log_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startLoggingActivity();
                        }
                    })
                    .setNegativeButton(R.string.dialog_log_no, null)
                    .create().show();
        } else {
            startLoggingActivity();
        }
    }

    //endregion

    //region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FrameLayout frameLayoutDrawingPitch = (FrameLayout) findViewById(R.id.frameLayoutDrawingPitch);
        GridView gridViewColorSelectors = (GridView) findViewById(R.id.linearLayoutColorSelectorControls);
        linearLayoutEditorContent = (LinearLayout) findViewById(R.id.linearLayoutContentEditor);
        btnDraw = (ImageButton) findViewById(R.id.btnDraw);
        ImageButton btnClear = (ImageButton) findViewById(R.id.btnClear);
        btnFill = (ImageButton) findViewById(R.id.btnFill);
        btnSquare = (ImageButton) findViewById(R.id.btnSquare);
        btnSphere = (ImageButton) findViewById(R.id.btnSphere);
        ImageButton btnLoadImage = (ImageButton) findViewById(R.id.btnLoadImage);

        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingModeControlClicked(DrawingMode.PEN);
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerDrawingPitch.clearPaint();
            }
        });
        btnFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingModeControlClicked(DrawingMode.FILL);
            }
        });
        btnSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingModeControlClicked(DrawingMode.SQUARE);
            }
        });
        btnSphere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingModeControlClicked(DrawingMode.SPHERE);
            }
        });
        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.dialog_select_picture)), Constants.KEY_LOAD_IMAGE);
            }
        });

        drawingPitch = new DrawingPitch(this, linearLayoutEditorContent);
        handlerDrawingPitch = new DrawingPitchHandler(this, drawingPitch);
        drawingPitch.setListenerDrawingPitch(handlerDrawingPitch);
        ColorSelectorHandler handlerColorSelector = new ColorSelectorHandler(this, handlerDrawingPitch);
        controllerColorSelector = new ColorSelectorController(gridViewColorSelectors, handlerColorSelector);

        frameLayoutDrawingPitch.addView(drawingPitch);

        Button btnAddColorSelector = (Button) findViewById(R.id.btnNewColorSelector);
        btnAddColorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentColorPicker(null);
            }
        });

        controllerColorSelector.initialize();

        btnDraw.callOnClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.KEY_LOAD_IMAGE) {
                handlerDrawingPitch.loadFromImage(data.getData());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.contextmenu_color_selector_button, menu);

        if (v instanceof ColorSelectorButton) {
            menu.findItem(R.id.action_change).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    startFragmentColorPicker((ColorSelectorButton) v);
                    return true;
                }
            });

            menu.findItem(R.id.action_remove).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    controllerColorSelector.remove((ColorSelectorButton) v);
                    return true;
                }
            });
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_reset:
                action_reset_clicked();
                break;
            case R.id.action_mapsize:
                startFragmentSeekBar();
                break;
            case R.id.action_save:
                action_save_clicked();
                break;
            case R.id.action_undo:
                handlerDrawingPitch.undo();
                break;
            case R.id.action_log:
                action_log_clicked();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //endregion

}
