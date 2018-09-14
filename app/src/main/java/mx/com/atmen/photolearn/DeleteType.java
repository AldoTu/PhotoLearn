package mx.com.atmen.photolearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DeleteType extends AppCompatActivity {

    int cat;
    int v;
    classDB db = new classDB(this);
    EditText editText;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cat = getIntent().getIntExtra("Cat", 0);
        v = getIntent().getIntExtra("Value", 0);
        getSupportActionBar().setTitle(db.getType(cat)[v]);
        editText = (EditText)findViewById(R.id.deleteType);
        btn = (Button)findViewById(R.id.deleteBtnType);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sure = editText.getText().toString();
                String n1 = sure.replace(" ", "");
                String n2 = db.getType(cat)[v].replace(" ", "");
                if(n1.equals(n2)){
                    if(db.deleteType(db.getType(cat)[v], db.getCategory()[cat])){
                        Intent intent = new Intent(DeleteType.this, MainActivity.class);
                        startActivity(intent);
                    }else{
                        Snackbar.make(view, "Something went wrong... Please try later", Snackbar.LENGTH_SHORT).setAction("ACTION", null).show();
                    }
                }else{
                    Snackbar.make(view, "Incorrect name", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
    }

}
