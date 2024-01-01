package com.example.photoportfolio;

public class SectionModal {

    private String section_name;
    private int imgid;

    public SectionModal(String section_name, int imgid) {
        this.section_name = section_name;
        this.imgid = imgid;
    }

    public String getSectionName() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    public int getImgid() {
        return imgid;
    }

    public void setImgid(int imgid) {
        this.imgid = imgid;
    }

}
