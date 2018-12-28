package de.chartsexplorer.chartsexplorer.util;

import android.graphics.Bitmap;

public class ImagePoJo {

    private Bitmap artistImage;
    private Bitmap coverImage;
    private String previewURl;

    public Bitmap getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Bitmap coverImage) {
        this.coverImage = coverImage;
    }

    public Bitmap getArtistImage() {
        return artistImage;
    }

    public void setArtistImage(Bitmap artistImage) {
        this.artistImage = artistImage;
    }

    public String getPreviewURl() {
        return previewURl;
    }

    public void setPreviewURl(String previewURl) {
        this.previewURl = previewURl;
    }





}
