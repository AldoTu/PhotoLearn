package mx.com.atmen.photolearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Type extends AppCompatActivity {

    EditText editText;
    Button btn;
    classDB db = new classDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Topic");
        final int va = getIntent().getIntExtra("className", 0);
        editText = (EditText)findViewById(R.id.typeET);
        btn = (Button)findViewById(R.id.typeBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = editText.getText().toString();
                if(db.newType(type, db.getCategory()[va])){
                    Intent intent = new Intent(Type.this, Themes.class);
                    intent.putExtra("Value", va);
                    Type.this.startActivity(intent);
                }else{
                    Snackbar.make(v, "Something went wrong... Please try later", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
    }

}
