package mx.com.atmen.photolearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NewToClasses extends AppCompatActivity {

    EditText editText;
    Button btn;
    String s;
    String ss;
    classDB db = new classDB(this);
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_to_classes);
        final String r = getIntent().getStringExtra("Real");
        btn = (Button)findViewById(R.id.classBtn);
        if(!("".equals(r))){
            textView = (TextView)findViewById(R.id.tv);
            textView.setText("Name change of " + r + ":");
            btn.setText("Change");
        }
        editText = (EditText)findViewById(R.id.classET);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ss = editText.getText().toString();
                if(ss.equals("")){
                    Snackbar.make(v, "No name given", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }else{
                    if("".equals(r)){
                        if(db.newCategory(ss)){
                            Intent intent = new Intent(NewToClasses.this, MainActivity.class);
                            NewToClasses.this.startActivity(intent);
                        }else{
                            Snackbar.make(v, "Something went wrong... Please try later", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }else{
                        if(db.editCategory(ss, r)){
                            Intent intent = new Intent(NewToClasses.this, MainActivity.class);
                            NewToClasses.this.startActivity(intent);
                        }else{
                            Snackbar.make(v, "Something went wrong... Please try later", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }
                }
            }
        });
    }

}
