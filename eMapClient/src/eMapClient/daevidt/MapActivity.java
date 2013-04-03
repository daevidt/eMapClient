package eMapClient.daevidt;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TableLayout;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class MapActivity extends Activity {
	float histX = 0, histY=0;
	int canvasHistX = 0, canvasHistY = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);
        TableLayout mapCanvas = (TableLayout) this.findViewById(R.id.mapCanvas);
        
        //mapCanvas.setMovementMethod(new ScrollingMovementMethod());
        mapCanvas.setOnTouchListener(new OnTouchListener(){
        	@Override
        	public boolean onTouch(View v, MotionEvent event) {
        		switch (event.getAction())
        		{
        		case MotionEvent.ACTION_MOVE:
	        		{
	        			
	        		
	        		//System.out.printf("HistoricalXY: %d,%d", event.getHistoricalX(h), event.getHistoricalY(h));
		        		try
		        		{
		        			//for (int p = 0; p<event.getPointerCount(); ++p)
		        			//{
		        				//int pId =  event.getPointerId(p);

			        				//float histX = event.getHistoricalX(pId,0);
			        				//float histY = event.getHistoricalY(pId,0);
			        				
			        				v.scrollTo( canvasHistX + (int)histX - (int)event.getX(), canvasHistY + (int)histY - (int)event.getY());

		        			//}
		        			return true;
		        		}
		        		catch(Exception e)
		        		{
		        			Toast.makeText(getApplicationContext(), e.getMessage() + event.getHistorySize(), Toast.LENGTH_SHORT).show();
		        			return false;
		        		}
	        		}
        		case MotionEvent.ACTION_DOWN:
        			histX = event.getX();
        			histY = event.getY();
        			canvasHistX = v.getScrollX();
        			canvasHistY = v.getScrollY();
        			return true;
        		case MotionEvent.ACTION_UP:
        			return true;
	        	}
        		return false;
        	}
        	
        });

        
        
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mapmenu, menu);
		return true;
		
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.exit:
			finish();
			return true;
		default: return super.onOptionsItemSelected(item);
		}
		
		
	}
	
	
}
