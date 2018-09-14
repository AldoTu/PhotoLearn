package mx.com.atmen.photolearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class Themes extends AppCompatActivity {

    Intent intent;
    classDB db = new classDB(this);
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final int va = getIntent().getIntExtra("Value", 0);
        getSupportActionBar().setTitle(db.getCategory()[va]);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fabTh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Themes.this, Type.class);
                intent.putExtra("className", va);
                Themes.this.startActivity(intent);
            }
        });
        if(db.getType(va).length != 0){
            TextView textView = (TextView)findViewById(R.id.msg2);
            textView.setVisibility(View.GONE);
            gridView = (GridView)findViewById(R.id.myGridTh);
            gridView.setAdapter(new ImageAdapterTheme(this, db.getType(va), db.getCategory()[va]));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Intent intent = new Intent(Themes.this, Photo.class);
                    intent.putExtra("Cat", va);
                    intent.putExtra("Value", position);
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int v = getIntent().getIntExtra("Value", 0);
        switch (item.getItemId()){
            case R.id.action_edit:
                intent = new Intent(Themes.this, NewToClasses.class);
                intent.putExtra("Real", db.getCategory()[v]);
                Themes.this.startActivity(intent);
                return true;
            case R.id.action_delete:
                intent = new Intent(Themes.this, Delete.class);
                intent.putExtra("name2Delete", v);
                Themes.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(Themes.this, MainActivity.class));
    }

}
