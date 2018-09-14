package mx.com.atmen.photolearn;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    classDB db = new classDB(this);
    //FloatingActionButton fabCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewToClasses.class);
                intent.putExtra("Real", "");
                startActivity(intent);
            }
        });
        //fabCamera = (FloatingActionButton)findViewById(R.id.fabCamera);
        //fabCamera.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, CameraActivity.class));
            //}
        //});
        if(db.getCategory().length != 0){
            TextView textView = (TextView)findViewById(R.id.msg1);
            textView.setVisibility(View.GONE);
            gridView = (GridView)findViewById(R.id.myGrid);
            gridView.setAdapter(new ImageAdapter(this, db.getCategory()));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, Themes.class);
                    intent.putExtra("Value", position);
                    MainActivity.this.startActivity(intent);
                }
            });
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onBackPressed(){}

}
