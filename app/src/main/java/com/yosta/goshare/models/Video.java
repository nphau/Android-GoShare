package com.yosta.goshare.models;

import android.provider.MediaStore;
import com.yosta.goshare.modules.MapHelper;
/**
 * Created by dinhhieu on 3/26/16.
 */
public class Video extends Resource {

    public Video(String filePath, User owner,String caption) {
        super(owner, caption);
        this.localFilePath = filePath;
        this.type = ResourceType.VIDEO;
    }

    public MediaStore.Video getVideo() {
        return null;
    }


}
