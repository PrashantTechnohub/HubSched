package com.NakshatraTechnoHub.HubSched.Models;

import android.graphics.Bitmap;

public class SlideModel {
    Bitmap img;

    public SlideModel(Bitmap img) {
        this.img = img;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }
}
