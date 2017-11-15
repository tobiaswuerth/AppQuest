package ch.apquest.bb.appquest_04.Path;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.apquest.bb.appquest_04.R;

public class PathElementAdapter extends ArrayAdapter<PathElement> {

    //region Fields

    private int resourceId;
    private Context context;
    private List<PathElement> objects;

    //endregion

    //region Constructor

    public PathElementAdapter(Context context, int resourceId, List<PathElement> objects) {
        super(context, resourceId, objects);

        this.context = context;
        this.resourceId = resourceId;
        this.objects = objects;
    }

    //endregion

    //region Events

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PathElementHolder peh = null;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resourceId, parent, false);

            peh = new PathElementHolder();
            peh.imageViewPathElement = (ImageView) convertView.findViewById(R.id.imageView);
            peh.textViewPathElementOrientation = (TextView) convertView.findViewById(R.id.textViewDescription);
            peh.textViewPathElementTotalStepsTodo = (TextView) convertView.findViewById(R.id.textViewAmount);

            convertView.setTag(peh);
        } else {
            peh = (PathElementHolder) convertView.getTag();
        }

        PathElement pe = objects.get(position);

        switch (pe.getOrientation()) {
            case FORWARD:
                peh.textViewPathElementOrientation.setText(R.string.orientationForward);
                peh.imageViewPathElement.setImageResource(R.drawable.arrow_forward);
                peh.textViewPathElementTotalStepsTodo.setText(pe.getTotalStepsTodo() + "x");
                peh.textViewPathElementTotalStepsTodo.setVisibility(View.VISIBLE);
                break;
            case RIGHT:
                peh.textViewPathElementOrientation.setText(R.string.orientationRight);
                peh.imageViewPathElement.setImageResource(R.drawable.arrow_right);
                peh.textViewPathElementTotalStepsTodo.setVisibility(View.GONE);
                break;
            case LEFT:
                peh.textViewPathElementOrientation.setText(R.string.orientationLeft);
                peh.imageViewPathElement.setImageResource(R.drawable.arrow_left);
                peh.textViewPathElementTotalStepsTodo.setVisibility(View.GONE);
                break;
        }

        if (pe.isDone()) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight));
        } else if (pe.isCurrent()) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    //endregion

}
