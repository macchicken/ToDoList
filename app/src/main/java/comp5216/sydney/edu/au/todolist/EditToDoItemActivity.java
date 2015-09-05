package comp5216.sydney.edu.au.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

import comp5216.sydney.edu.au.todolist.model.MesssageModel;
import comp5216.sydney.edu.au.todolist.service.FileItemsService;
import comp5216.sydney.edu.au.todolist.service.IItemsService;
import comp5216.sydney.edu.au.todolist.service.SqliteItemsService;


public class EditToDoItemActivity extends Activity {
	public int position=0;
	EditText etItem;
	private MesssageModel savedEditItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//populate the screen using the layout
		setContentView(R.layout.activity_edit_item);
		//Get the data from the main screen
		savedEditItem= (MesssageModel) getIntent().getSerializableExtra("item");
		position = getIntent().getIntExtra("position",-1);
		// show original content in the text field
		etItem = (EditText)findViewById(R.id.etEditItem);
		etItem.setText(savedEditItem.getContent());
	}

	public void onSubmit(View v) {
	  etItem = (EditText) findViewById(R.id.etEditItem);
	  // Prepare data intent for sending it back
	  Intent data = new Intent();
	  // Pass relevant data back as a result
	  data.putExtra("item", new MesssageModel(savedEditItem.getId(),savedEditItem.getCreatedTime(),etItem.getText().toString()));
	  data.putExtra("oldItem", savedEditItem);
	  data.putExtra("position", position);
	  // Activity finished ok, return the data
	  setResult(RESULT_OK, data); // set result code and bundle data for response
	  finish(); // closes the activity, pass data to parent
	}

	public void cancelAction(View v){
		finish();
	}
}
