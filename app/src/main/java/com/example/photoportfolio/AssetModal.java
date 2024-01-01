package com.example.photoportfolio;

import android.net.Uri;

public class AssetModal {

    private String image;
    private Uri imgid;

    private String u;

    private String type;

    public AssetModal(String image, Uri imgid, String u, String type) {
        this.image = image;
        this.imgid = imgid;
        this.u = u;
        this.type = type;
    }

    public String getSectionName() {
        return image;
    }

    public void setSection_name(String image) {
        this.image = image;
    }

    public Uri getImgid() {

        return imgid;
    }

    public void setImgid(Uri imgid) {
        this.imgid = imgid;
    }

    public void setU(String u){
        this.u = u;
    }

    public String getU(){
        return this.u;
    }

    public String getType(){
        return this.type;
    }


}
