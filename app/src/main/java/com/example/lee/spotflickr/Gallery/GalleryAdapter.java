package com.example.lee.spotflickr.Gallery;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.lee.spotflickr.R;

import java.util.ArrayList;
import java.util.Iterator;

public class GalleryAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater inflater;
    private ImageView ivGallery;
    ArrayList<Image> mArrayImgs;
    public GalleryAdapter(Context ctx, ArrayList<Image> imgs) {

        this.ctx = ctx;
        this.mArrayImgs = imgs;
    }

    @Override
    public int getCount() {
        return mArrayImgs.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayImgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.gv_item, parent, false);

        ivGallery = (ImageView) itemView.findViewById(R.id.ivGallery);

        ivGallery.setImageBitmap(mArrayImgs.get(position).getImg());

        if (mArrayImgs.get(position).isChecked()) {
            ivGallery.setColorFilter(Color.BLUE, PorterDuff.Mode.LIGHTEN);
            Log.d("Debug", "HJ Debug: Greyed"+position);
        } else {
            ivGallery.setColorFilter(null);
            Log.d("Debug", "HJ Debug: nonGreyed"+position);
        }

        return itemView;
    }
    public void clearChecks() {
        for(Image iv: mArrayImgs) {
            if(iv.isChecked())
                iv.toggleChecked();
        }
        this.notifyDataSetChanged();
    }
    public ArrayList<Image> popCheckedImage() {
        ArrayList<Image> res = new ArrayList<Image>();
        Iterator<Image> iv = mArrayImgs.iterator();
        while(iv.hasNext()) {
            Image i = iv.next();
            if(i.isChecked()) {
                res.add(i);
                iv.remove();
            }
        }
        return res;
    }


}