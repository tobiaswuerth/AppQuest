package ch.appquest.bb.appquest_05.Fragments.SeekBarFragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import ch.appquest.bb.appquest_05.Other.Constants;
import ch.appquest.bb.appquest_05.R;

public class SeekBarFragment extends DialogFragment {

    //region Fields

    private TextView text = null;
    private SeekBar seekBar = null;
    private int currentValue = 0;
    private SeekBarFragmentListener listenerSeekBarFragment = null;

    //endregion

    //region Factory

    public static SeekBarFragment newInstance(int currentValue) {
        SeekBarFragment seekBarFragment = new SeekBarFragment();

        Bundle args = new Bundle();
        args.putInt(Constants.KEY_CURRENT_VALUE, currentValue);
        seekBarFragment.setArguments(args);

        return seekBarFragment;
    }

    //endregion

    //region General Methods

    public void setListenerSeekBarFragment(SeekBarFragmentListener listenerSeekBarFragment) {
        this.listenerSeekBarFragment = listenerSeekBarFragment;
    }

    private void updateGui() {
        currentValue = seekBar.getProgress() + Constants.DRAWING_PITCH_SIZE_MIN;
        text.setText(currentValue + " / " + (seekBar.getMax() + Constants.DRAWING_PITCH_SIZE_MIN));
    }

    //endregion

    //region Events

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(Constants.KEY_CURRENT_VALUE)) {
            currentValue = getArguments().getInt(Constants.KEY_CURRENT_VALUE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.KEY_CURRENT_VALUE, currentValue);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout vParent = (LinearLayout) inflater.inflate(R.layout.dialog_seekbar, container, false);
        seekBar = (SeekBar) vParent.findViewById(R.id.seekBar);
        text = (TextView) vParent.findViewById(R.id.txtValue);
        Button btnClose = (Button) vParent.findViewById(R.id.btnClose);

        seekBar.setMax(Constants.DRAWING_PITCH_SIZE_MAX - Constants.DRAWING_PITCH_SIZE_MIN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateGui();
                if (listenerSeekBarFragment != null) {
                    listenerSeekBarFragment.onSeekBarChanged(currentValue);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (currentValue - Constants.DRAWING_PITCH_SIZE_MIN == 0) {
            updateGui();
        } else {
            seekBar.setProgress(currentValue - Constants.DRAWING_PITCH_SIZE_MIN);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return vParent;
    }

    //endregion

}
