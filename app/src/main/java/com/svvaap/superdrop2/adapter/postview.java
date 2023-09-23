package com.svvaap.superdrop2.adapter;
import android.view.View;
import android.widget.Button;

import com.svvaap.superdrop2.R;

import java.lang.*;

public class postview {

    String mname;
   String mimageurl;

    public postview(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mname = name;
        mimageurl = imageUrl;
    }

    public String getName() {
        return mname;
    }

    public void setName(String postimage) {
        mname = postimage;
    }

    public String getImageUrl() {
      return mimageurl;
    }

   public void setImageUrl(String posttext) {mimageurl= posttext;
   }
}
