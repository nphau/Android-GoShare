package com.yosta.goshare.models;

/**
 * Created by dinhhieu on 3/26/16.
 */
public class Audio extends Resource {

    protected String name;
    protected String singer;

    public Audio(String filePath, User owner, String name, String singer, String caption) {
        super(owner, caption);
        this.name = name;
        this.singer = singer;
        this.type = ResourceType.AUDIO;
        this.localFilePath = filePath;
        this.directLink = filePath;
    }

    public String getCaption() {
        return this.caption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }
}
