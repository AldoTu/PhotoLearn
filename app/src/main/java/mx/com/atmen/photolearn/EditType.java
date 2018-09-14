package mx.com.atmen.photolearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditType extends AppCompatActivity {

    TextView textView;
    classDB db = new classDB(this);
    Button btn;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final int cat = getIntent().getIntExtra("Cat", 0);
        final int v = getIntent().getIntExtra("Value", 0);
        textView = (TextView)findViewById(R.id.tvType);
        textView.setText("Name change of " + db.getType(cat)[v] + ":");
        editText = (EditText)findViewById(R.id.classETType);
        btn = (Button)findViewById(R.id.classBtnType);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String res = editText.getText().toString();
                if(res.equals("")){
                    Snackbar.make(view, "Name not given", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }else{
                    if(db.editType(res, db.getType(cat)[v])){
                        Intent intent = new Intent(EditType.this, Themes.class);
                        intent.putExtra("Value", cat);
                        EditType.this.startActivity(intent);
                    }else{
                        Snackbar.make(view, "Something went wrong... Please try later", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }
        });
    }

}
