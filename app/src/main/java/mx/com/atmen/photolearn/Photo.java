package mx.com.atmen.photolearn;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

public class Photo extends AppCompatActivity {

    Intent intent;
    classDB db = new classDB(this);
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final int cat = getIntent().getIntExtra("Cat", 0);
        final int v = getIntent().getIntExtra("Value", 0);
        getSupportActionBar().setTitle(db.getType(cat)[v]);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabPhoto);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(Photo.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Photo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Photo.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    Intent intent = new Intent(Photo.this, CameraActivity.class);
                    intent.putExtra("c", cat);
                    intent.putExtra("t", v);
                    startActivity(intent);
                }
            }
        });
        if(db.getPhotos(db.getCategory()[cat], db.getType(cat)[v]).length != 0){
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.msg3);
            linearLayout.setVisibility(View.GONE);
            gridView = null;
            gridView = (GridView)findViewById(R.id.imageGrid);
            gridView.setAdapter(new AllImagesAdapter(this, db.getPhotos(db.getCategory()[cat], db.getType(cat)[v])));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Photo.this, ShowPhotos.class);
                    intent.putExtra("Value", position);
                    intent.putExtra("Category", cat);
                    intent.putExtra("Type", v);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int cat = getIntent().getIntExtra("Cat", 0);
        int v = getIntent().getIntExtra("Value", 0);
        switch (item.getItemId()){
            case R.id.action_edit:
                intent = new Intent(Photo.this, EditType.class);
                intent.putExtra("Cat", cat);
                intent.putExtra("Value", v);
                Photo.this.startActivity(intent);
                return true;
            case R.id.action_delete:
                intent = new Intent(Photo.this, DeleteType.class);
                intent.putExtra("Cat", cat);
                intent.putExtra("Value", v);
                Photo.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Photo.this, Themes.class);
        intent.putExtra("Value", getIntent().getIntExtra("Cat", 0));
        startActivity(intent);
    }
}
