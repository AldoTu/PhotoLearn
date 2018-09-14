package mx.com.atmen.photolearn;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;

class AllImagesAdapter extends BaseAdapter {

    private Context context;
    private final String[] mobileValues;
    Picasso picasso;

    AllImagesAdapter(Context context, String[] mobileValues){
        this.context = context;
        this.mobileValues = mobileValues;
        picasso = new Picasso.Builder(context).memoryCache(new LruCache(50000000)).build();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.mobile, null);

            // set image based on selected text
            ImageView ivPhoto = (ImageView)gridView.findViewById(R.id.grid_item_image);
            TextView textView = (TextView)gridView.findViewById(R.id.grid_item_label);
            textView.setVisibility(View.GONE);
            String mobile = mobileValues[position];
            try{
                File file = new File(mobile);
                picasso.with(context).load(file).into(ivPhoto);
            }
            catch (Exception e){
                Toast.makeText(context, "Can't load images", Toast.LENGTH_LONG).show();
            }
        } else {
            gridView = (View)convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        try {
            return mobileValues.length;
        }catch(Exception e){
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
