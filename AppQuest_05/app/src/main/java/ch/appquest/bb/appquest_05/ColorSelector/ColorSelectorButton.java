package ch.appquest.bb.appquest_05.ColorSelector;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import android.widget.LinearLayout;

import ch.appquest.bb.appquest_05.Other.Constants;
import ch.appquest.bb.appquest_05.R;

public class ColorSelectorButton extends Button {

    //region Fields

    private boolean isSelected = false;
    private int backgroundColor = Constants.COLOR_SELECTOR_DEFAULT_BACKGROUND_COLOR;

    //endregion

    //region Constructor

    public ColorSelectorButton(Context context) {
        super(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Constants.COLOR_SELECTOR_SIZE, Constants.COLOR_SELECTOR_SIZE);
        setLayoutParams(params);

        setBackground(context.getDrawable(R.drawable.color_selector_button_style));
    }

    //endregion

    //region General Methods

    public void select(boolean isSelected) {
        this.isSelected = isSelected;
        this.setText((isSelected ? getContext().getString(R.string.text_color_selector_button_selected) : getContext().getString(R.string.text_color_selector_button_not_selected)));
    }

    public boolean isSelected() {
        return isSelected;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    //endregion

    //region Events

    @Override
    public void setBackgroundColor(int color) {
        Drawable background = getBackground();
        if (background instanceof GradientDrawable) {
            GradientDrawable sd = (GradientDrawable) background;
            backgroundColor = color;
            sd.setColor(backgroundColor);
            if (backgroundColor == Color.BLACK) {
                this.setTextColor(Color.WHITE);
            } else {
                this.setTextColor(Color.BLACK);
            }
        }
    }

    //endregion

}
