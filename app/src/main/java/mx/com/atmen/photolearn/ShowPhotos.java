package mx.com.atmen.photolearn;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

public class ShowPhotos extends AppCompatActivity {

    int cat;
    int v;
    static DisplayMetrics displayMetrics = new DisplayMetrics();
    classDB db = new classDB(this);
    File mPrivateRootDir;
    File mImagesDir;
    File[] mImageFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photos);
        cat = getIntent().getIntExtra("Category", 0);
        v = getIntent().getIntExtra("Type", 0);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), db.getPhotos(db.getCategory()[cat], db.getType(cat)[v]).length, this, cat, v);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(getIntent().getIntExtra("Value", 0));
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Get the files/ subdirectory of internal storage
        mPrivateRootDir = getFilesDir();
        // Get the files/images subdirectory;
        mImagesDir = new File(mPrivateRootDir, "images");
        // Get the files in the images subdirectory
        mImageFiles = mImagesDir.listFiles();
        // Set the Activity's result to null to begin with
        setResult(Activity.RESULT_CANCELED, null);
        /*
         * Display the file names in the ListView mFileListView.
         * Back the ListView with the array mImageFilenames, which
         * you can create by iterating through mImageFiles and
         * calling File.getAbsolutePath() for each File
         */
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        Context context;
        int cat;
        int v;
        int position;

        public PlaceholderFragment(Context context, int cat, int v){
            this.context = context;
            this.cat = cat;
            this.v = v;
        }

        public PlaceholderFragment newInstance(int sectionNumber){
            position = sectionNumber;
            PlaceholderFragment fragment = new PlaceholderFragment(context, cat, v);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
            final classDB db = new classDB(context.getApplicationContext());
            View rootView = inflater.inflate(R.layout.fragment_show_photos, container, false);
            CoordinatorLayout linearLayout = (CoordinatorLayout)rootView.findViewById(R.id.lastIV);
            try{
                File file = new File(db.getPhotos(db.getCategory()[cat], db.getType(cat)[v])[getArguments().getInt(ARG_SECTION_NUMBER)]);
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                linearLayout.setBackground(drawable);
            }catch(Exception e){}
            FloatingActionButton share = (FloatingActionButton)rootView.findViewById(R.id.sharePhoto);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File requestFile = new File(db.getPhotos(db.getCategory()[cat], db.getType(cat)[v])[getArguments().getInt(ARG_SECTION_NUMBER)]);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    try {
                        Uri fileUri = FileProvider.getUriForFile(getActivity(), "mx.com.atmen.photolearn.ShowPhotos", requestFile);
                        if(fileUri != null){
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                            intent.setType("*/*");
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            startActivity(intent);
                        }else{
                            intent.setDataAndType(null, "");
                            getActivity().setResult(RESULT_CANCELED, intent);
                        }
                    }catch (IllegalArgumentException e) {
                        Log.e("File Selector", e.getMessage());
                    }
                }
            });
            FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.deletePhoto);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_dialog);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setCancelable(true);
                    Button btnBtmLeft = (Button)dialog.findViewById(R.id.btnFinalYes);
                    Button btnBtmRight = (Button)dialog.findViewById(R.id.btnFinalCancel);
                    btnBtmLeft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(db.deletePhoto(db.getPhotos(db.getCategory()[cat], db.getType(cat)[v])[getArguments().getInt(ARG_SECTION_NUMBER)])){
                                dialog.dismiss();
                                Intent intent = new Intent(getContext(), Photo.class);
                                intent.putExtra("Cat", cat);
                                intent.putExtra("Value", v);
                                startActivity(intent);
                            }else{
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Photo could not be deleted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    btnBtmRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            return rootView;
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        int lenght;
        Context context;
        int cat;
        int v;

        SectionsPagerAdapter(FragmentManager fm, int lenght, Context context, int cat, int v){
            super(fm);
            this.lenght = lenght;
            this.context = context;
            this.cat = cat;
            this.v = v;
        }

        @Override
        public Fragment getItem(int position) {
            PlaceholderFragment placeholderFragment = new PlaceholderFragment(context, cat, v);
            return placeholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return lenght;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ShowPhotos.this, Photo.class);
        intent.putExtra("Cat", cat);
        intent.putExtra("Value", v);
        startActivity(intent);
        finish();
    }

}
