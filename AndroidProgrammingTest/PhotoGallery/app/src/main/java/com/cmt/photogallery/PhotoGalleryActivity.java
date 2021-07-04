package com.cmt.photogallery;

import androidx.fragment.app.Fragment;


public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragmen.newInstance();
    }
}