package ch.appquest.bb.appquest_05.ColorSelector;

import android.support.v7.app.AppCompatActivity;

import ch.appquest.bb.appquest_05.Drawing.DrawingPitchHandler;

public class ColorSelectorHandler implements ColorSelectorListener {

    //region Fields

    private DrawingPitchHandler handlerDrawingPitch = null;
    private AppCompatActivity parentActivity = null;

    //endregion

    //region Constructor

    public ColorSelectorHandler(AppCompatActivity parentActivity, DrawingPitchHandler handlerDrawingPitch) {
        this.handlerDrawingPitch = handlerDrawingPitch;
        this.parentActivity = parentActivity;
    }

    //endregion

    //region Events

    @Override
    public void onColorSelectorClicked(ColorSelectorButton colorSelectorButton) {
        handlerDrawingPitch.setCurrentDrawingColor(colorSelectorButton.getBackgroundColor());
    }

    @Override
    public void onColorSelectorAdded(ColorSelectorButton colorSelectorButton) {
        parentActivity.registerForContextMenu(colorSelectorButton);
    }

    //endregion

}
