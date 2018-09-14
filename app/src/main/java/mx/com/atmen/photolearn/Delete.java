package mx.com.atmen.photolearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Delete extends AppCompatActivity {

    Button btn;
    int del;
    EditText editText;
    classDB db = new classDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        del = getIntent().getIntExtra("name2Delete", 0);
        getSupportActionBar().setTitle(db.getCategory()[del]);
        editText = (EditText)findViewById(R.id.deleteET);
        btn = (Button)findViewById(R.id.deleteBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sure = editText.getText().toString();
                del = getIntent().getIntExtra("name2Delete", 0);
                String n1 = sure.replace(" ", "");
                String n2 = db.getCategory()[del].replace(" ", "");
                if(n1.equals(n2)){
                    if(db.deleteCategory(db.getCategory()[del])){
                        Intent intent = new Intent(Delete.this, MainActivity.class);
                        Delete.this.startActivity(intent);
                    }else{
                        Snackbar.make(v, "Something went wrong... Please try later", Snackbar.LENGTH_SHORT).setAction("ACTION", null).show();
                    }
                }else{
                    Snackbar.make(v, "Incorrect name", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
    }

}
