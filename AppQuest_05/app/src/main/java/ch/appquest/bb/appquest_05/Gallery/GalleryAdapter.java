package ch.appquest.bb.appquest_05.Gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import ch.appquest.bb.appquest_05.Other.AsyncImageViewLoader;
import ch.appquest.bb.appquest_05.Other.Utility;
import ch.appquest.bb.appquest_05.R;

public class GalleryAdapter extends ArrayAdapter<File> {

    //region Fields

    private Context context;
    private int layoutResourceId;
    private GalleryAdapterListener listenerGalleryAdapter = null;

    //endregion

    //region Constructor

    public GalleryAdapter(Context context, int layoutResourceId, ArrayList<File> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }

    //endregion

    //region General Methods

    public void setListenerGalleryAdapter(GalleryAdapterListener listenerGalleryAdapter) {
        this.listenerGalleryAdapter = listenerGalleryAdapter;
    }

    private void prepareHolder(final int position, GalleryHolder holder) {
        final AsyncImageViewLoader asyncImageViewLoader = new AsyncImageViewLoader(holder.imageView);
        asyncImageViewLoader.execute(getItem(position));
        final File imageFile = getItem(position);

        holder.imageView.setTag(imageFile.getAbsolutePath());
        holder.imageFilePath.setText(imageFile.getAbsolutePath());
        holder.imageFileSize.setText(Utility.byteToReadableFormat(imageFile.length(), true));

        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getItem(position)));
                shareIntent.setType(context.getString(R.string.share_intent_type));
                context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.dialog_share_image_title)));
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.dialog_delete_confirm_title)
                        .setCancelable(false)
                        .setPositiveButton(R.string.dialog_delete_confirm_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean successful = getItem(position).delete();

                                if (successful) {
                                    remove(imageFile);
                                    if (listenerGalleryAdapter != null) {
                                        listenerGalleryAdapter.itemDeleted(getCount());
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_delete_confirm_no, null)
                        .create();

                alertDialog.show();
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(getItem(position)), context.getString(R.string.share_intent_type));
                context.startActivity(intent);
            }
        });
    }

    //endregion

    //region Events

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GalleryHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new GalleryHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.imageFilePath = (TextView) convertView.findViewById(R.id.txtFilePath);
            holder.imageFileSize = (TextView) convertView.findViewById(R.id.txtFileSize);
            holder.btnDelete = (ImageButton) convertView.findViewById(R.id.btnDelete);
            holder.btnShare = (ImageButton) convertView.findViewById(R.id.btnShare);
            prepareHolder(position, holder);
            convertView.setTag(holder);
        } else {
            holder = (GalleryHolder) convertView.getTag();
        }

        if (!holder.imageView.getTag().equals(getItem(position).getAbsolutePath())) {
            holder.imageView.setImageBitmap(null);
            prepareHolder(position, holder);
        }
        return convertView;
    }

    //endregion

}
