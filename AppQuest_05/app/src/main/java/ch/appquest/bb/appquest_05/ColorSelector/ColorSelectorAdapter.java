package ch.appquest.bb.appquest_05.ColorSelector;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class ColorSelectorAdapter extends ArrayAdapter<ColorSelectorButton> {

    //region Constructor

    public ColorSelectorAdapter(Context context, List<ColorSelectorButton> objects) {
        super(context, 0, objects);
    }

    //endregion

    //region Events

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position);
    }

    //endregion

}
