package mx.com.atmen.photolearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;

class ImageAdapter extends BaseAdapter {

    private Context context;
    private final String[] mobileValues;
    Picasso picasso;

    ImageAdapter(Context context, String[] mobileValues) {
        this.context = context;
        this.mobileValues = mobileValues;
        picasso = new Picasso.Builder(context).memoryCache(new LruCache(50000000)).build();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        classDB db = new classDB(context);

        if (convertView == null) {

            gridView = new View(context);

            gridView = inflater.inflate(R.layout.mobile, null);

            TextView textView = (TextView)gridView.findViewById(R.id.grid_item_label);
            textView.setText(mobileValues[position]);

            ImageView imageView = (ImageView)gridView.findViewById(R.id.grid_item_image);

            String mobile = mobileValues[position];

            if(mobile.length() != 0){
                try{
                    File file = new File(db.getLastPhotoCategory(mobile));
                    picasso.with(context).load(file).into(imageView);
                }catch(Exception e){}
            }

        } else {
            gridView = (View) convertView;
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
