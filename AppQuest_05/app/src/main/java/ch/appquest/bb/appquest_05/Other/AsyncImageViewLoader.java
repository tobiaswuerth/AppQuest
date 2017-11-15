package ch.appquest.bb.appquest_05.Other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

public class AsyncImageViewLoader extends AsyncTask<File, Void, Bitmap> {

    //region Fields

    private final WeakReference<ImageView> imageViewReference;
    private final int imageWidth;
    private final int imageHeight;

    //endregion

    //region Constructor

    public AsyncImageViewLoader(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.imageWidth = imageView.getWidth();
        this.imageHeight = imageView.getHeight();
    }

    //endregion

    //region Events

    @Override
    protected Bitmap doInBackground(File... params) {
        final File imageFile = params[0];

        if (imageFile.exists()) {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.outWidth = imageWidth;
            bitmapOptions.outHeight = imageHeight;
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bitmapOptions);
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    //endregion

}
