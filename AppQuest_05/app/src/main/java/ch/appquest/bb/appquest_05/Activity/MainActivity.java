package ch.appquest.bb.appquest_05.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

import ch.appquest.bb.appquest_05.Gallery.GalleryAdapter;
import ch.appquest.bb.appquest_05.Gallery.GalleryAdapterListener;
import ch.appquest.bb.appquest_05.R;

public class MainActivity extends AppCompatActivity implements GalleryAdapterListener {

    //region Fields

    private ListView listViewGallery = null;
    private TextView textViewNoImages = null;

    //endregion

    //region General Methods

    private void updateGallery() {
        final File imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (imageDir != null) {
            imageDir.mkdirs();

            final File[] savedImages = imageDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getAbsolutePath().toLowerCase().endsWith(".jpg");
                }
            });

            if (savedImages.length > 0) {
                GalleryAdapter galleryAdapter = new GalleryAdapter(MainActivity.this, R.layout.gallery_list_item, new ArrayList<File>(Arrays.asList(savedImages)));
                galleryAdapter.setListenerGalleryAdapter(this);
                listViewGallery.setAdapter(galleryAdapter);
                textViewNoImages.setVisibility(View.GONE);
                listViewGallery.setVisibility(View.VISIBLE);
            } else {
                listViewGallery.setVisibility(View.GONE);
                textViewNoImages.setVisibility(View.VISIBLE);
            }
        }
    }

    //endregion

    //region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listViewGallery = (ListView) findViewById(R.id.listViewGallery);
        textViewNoImages = (TextView) findViewById(R.id.txtNoImagesSaved);

        findViewById(R.id.floatingActionButton_Add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), EditorActivity.class);
                startActivity(i);
            }
        });

        listViewGallery.setFastScrollEnabled(true);

        updateGallery();
    }

    @Override
    public void itemDeleted(int dataSize) {
        if (dataSize == 0) {
            updateGallery();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGallery();
    }

    //endregion

}
