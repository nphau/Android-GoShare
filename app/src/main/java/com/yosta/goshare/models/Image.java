package com.yosta.goshare.models;

import android.graphics.Bitmap;

import com.yosta.goshare.modules.MapHelper;

/**
 * Created by dinhhieu on 3/26/16.
 */
public class Image extends Resource {

    public Image(String filePath, User owner, String caption) {
        super(owner, caption);
        this.type = ResourceType.IMAGE;
        this.localFilePath = filePath;
        this.directLink = filePath;
    }

    public Bitmap getBitmap() {
        return null;
    }


}
