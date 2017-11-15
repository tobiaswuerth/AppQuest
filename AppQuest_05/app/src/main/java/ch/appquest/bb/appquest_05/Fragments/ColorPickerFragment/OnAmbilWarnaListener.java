package ch.appquest.bb.appquest_05.Fragments.ColorPickerFragment;

/**
 * Interface with callback methods for AmbilWarna dialog.
 */
public interface OnAmbilWarnaListener {
    void onColorPickerCancel(AmbilWarnaDialogFragment dialogFragment);

    void onColorPickerOk(AmbilWarnaDialogFragment dialogFragment, int color);
}