package ch.appquest.bb.appquest_05.ColorSelector;

import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

import ch.appquest.bb.appquest_05.Other.Constants;

public class ColorSelectorController {

    //region Fields

    private GridView gridView = null;
    private ColorSelectorAdapter adapterColorSelector = null;
    private ColorSelectorHandler handlerColorSelector = null;

    //endregion

    //region Constructor

    public ColorSelectorController(GridView gridView, ColorSelectorHandler handlerColorSelector) {
        this.gridView = gridView;
        this.handlerColorSelector = handlerColorSelector;
        this.adapterColorSelector = new ColorSelectorAdapter(gridView.getContext(), new ArrayList<ColorSelectorButton>());
        this.gridView.setAdapter(this.adapterColorSelector);
        this.gridView.setColumnWidth(Constants.COLOR_SELECTOR_SIZE);
    }

    //endregion

    //region General Methods

    public void selectColorSelector(ColorSelectorButton colorSelectorButton) {
        for (int i = 0; i < adapterColorSelector.getCount(); i++) {
            adapterColorSelector.getItem(i).select(false);
        }
        (colorSelectorButton).select(true);
    }

    public void remove(ColorSelectorButton colorSelectorButton) {
        adapterColorSelector.remove(colorSelectorButton);

        if (colorSelectorButton.isSelected()) {
            if (adapterColorSelector.getCount() > 0) {
                adapterColorSelector.getItem(0).callOnClick();
            }
        }
    }

    public void addNewColorSelector(final int color) {
        final ColorSelectorButton colorSelectorButton = new ColorSelectorButton(gridView.getContext());
        colorSelectorButton.setBackgroundColor(color);
        colorSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorSelectorButton b = (ColorSelectorButton) v;
                if (handlerColorSelector != null) {
                    handlerColorSelector.onColorSelectorClicked(b);
                }
                selectColorSelector(b);
            }
        });

        if (handlerColorSelector != null) {
            handlerColorSelector.onColorSelectorAdded(colorSelectorButton);
        }

        adapterColorSelector.add(colorSelectorButton);
        colorSelectorButton.callOnClick();
    }

    public void initialize() {
        adapterColorSelector.clear();

        for (final Integer color : Constants.DEFAULT_COLORS) {
            addNewColorSelector(color);
        }

        if (adapterColorSelector.getCount() > 0) {
            adapterColorSelector.getItem(0).callOnClick();
        }
    }

    //endregion

}
