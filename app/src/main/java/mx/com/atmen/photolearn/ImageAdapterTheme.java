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

class ImageAdapterTheme extends BaseAdapter {

    private Context context;
    private String[] mobileValues;
    private String category;
    Picasso picasso;

    ImageAdapterTheme(Context context, String[] mobileValues, String category){
        this.context = context;
        this.mobileValues = mobileValues;
        this.category = category;
        picasso = new Picasso.Builder(context).memoryCache(new LruCache(50000000)).build();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        classDB db = new classDB(context);

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.mobile, null);

            // set value into textview
            TextView textView = (TextView) gridView
                    .findViewById(R.id.grid_item_label);
            textView.setText(mobileValues[position]);

            // set image based on selected text
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.grid_item_image);

            String mobile = mobileValues[position];

            if(mobile.length() != 0){
                try{
                    File file = new File(db.getLastPhotoType(category, mobile));
                    picasso.with(context).load(file).into(imageView);
                }catch(Exception e){}
            }

        } else {
            gridView = convertView;
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
