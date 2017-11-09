package com.thereza.smartnotebook;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class NoteEdit extends Activity {
	public static int numTitle = 1;	
	public static String curDate = "";
	public static String curText = "";	
    private EditText mTitleText;
    private EditText mBodyText;
    private TextView mDateText;
    private Long mRowId;
    private Cursor note;
    private NoteDB mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new NoteDB(this);
        mDbHelper.open();   
		
		setContentView(R.layout.noteedit);
		 setTitle(R.string.app_name);

	        mTitleText = (EditText) findViewById(R.id.title);
	        mBodyText = (EditText) findViewById(R.id.body);
	        mDateText = (TextView) findViewById(R.id.notelist_date);
	        
	        long msTime = System.currentTimeMillis();  
	        Date curDateTime = new Date(msTime);
	 	
	        SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");  
	        curDate = formatter.format(curDateTime);        
	        
	        mDateText.setText(""+curDate);
	        

	        mRowId = (savedInstanceState == null) ? null :
	            (Long) savedInstanceState.getSerializable(NoteDB.KEY_ROWID);
	        if (mRowId == null) {
	            Bundle extras = getIntent().getExtras();
	            mRowId = extras != null ? extras.getLong(NoteDB.KEY_ROWID)
	                                    : null;
	        }

	        populateFields();
	}
	
	 public static class LineEditText extends EditText{
			// we need this constructor for LayoutInflater
			public LineEditText(Context context, AttributeSet attrs) {
				super(context, attrs);
					mRect = new Rect();
			        mPaint = new Paint();
			        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			        mPaint.setColor(Color.BLUE);
			}

			private Rect mRect;
		    private Paint mPaint;	    
		    
		    @Override
		    protected void onDraw(Canvas canvas) {
		  
		        int height = getHeight();
		        int line_height = getLineHeight();

		        int count = height / line_height;

		        if (getLineCount() > count)
		            count = getLineCount();

		        Rect r = mRect;
		        Paint paint = mPaint;
		        int baseline = getLineBounds(0, r);

		        for (int i = 0; i < count; i++) {

		            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
		            baseline += getLineHeight();

		        super.onDraw(canvas);
		    }

		}
	  }
	  
	  @Override
	    protected void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        saveState();
	        outState.putSerializable(NoteDB.KEY_ROWID, mRowId);
	    }
	    
	    @Override
	    protected void onPause() {
	        super.onPause();
	        saveState();
	    }
	    
	    @Override
	    protected void onResume() {
	        super.onResume();
	        populateFields();
	    }
	    
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.note_edit, menu);
			return true;		
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    case R.id.menu_about:
		          
		    	/* Here is the introduce about myself */	    	
		        AlertDialog.Builder dialog = new AlertDialog.Builder(NoteEdit.this);
		        dialog.setTitle("About");
		        dialog.setMessage("Hello! I'm Reza, the creator of this application. This application is created for learning." +"If there is any bug is found please freely e-mail me. "+
		           			"\n\nrejaul_cse12@yahoo.com"
		        		   );
		        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
		        	   @Override
		        	   public void onClick(DialogInterface dialog, int which) {
		        		   dialog.cancel();
						
		        	   }
		           });
		           dialog.show();	           
		           return true;
		    case R.id.menu_delete:
				if(note != null){
	    			note.close();
	    			note = null;
	    		}
	    		if(mRowId != null){
	    			mDbHelper.deleteNote(mRowId);
	    		}
	    		finish();
		    	
		        return true;
		    case R.id.menu_save:
	    		saveState();
	    		finish();	    	
		    default:
		    	return super.onOptionsItemSelected(item);
		    }
		}
	    
	    private void saveState() {
	        String title = mTitleText.getText().toString();
	        String body = mBodyText.getText().toString();

	        if(mRowId == null){
	        	long id = mDbHelper.createNote(title, body, curDate);
	        	if(id > 0){
	        		mRowId = id;
	        	}else{
	        		Log.e("saveState","failed to create note");
	        	}
	        }else{
	        	if(!mDbHelper.updateNote(mRowId, title, body, curDate)){
	        		Log.e("saveState","failed to update note");
	        	}
	        }
	    }
	    
	  
	    private void populateFields() {
	        if (mRowId != null) {
	            note = mDbHelper.fetchNote(mRowId);
	            startManagingCursor(note);
	            mTitleText.setText(note.getString(
	    	            note.getColumnIndexOrThrow(NoteDB.KEY_TITLE)));
	            mBodyText.setText(note.getString(
	                    note.getColumnIndexOrThrow(NoteDB.KEY_BODY)));
	            curText = note.getString(
	                    note.getColumnIndexOrThrow(NoteDB.KEY_BODY));
	        }
	    }

}
