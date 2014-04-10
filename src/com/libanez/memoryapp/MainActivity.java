package com.libanez.memoryapp;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		Button refreshButton;
		Button createBitmapButton;
		Button recycleBitmapButton;
		Button nullButton;
		Button gcButton;
		TextView heapText;
		EditText bitmapSizeText;
		
		Bitmap largeWhiteBitmap;

		public PlaceholderFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.main_fragment, container, false);

			refreshButton =  (Button) rootView.findViewById(R.id.buttonRefresh);
			gcButton =  (Button) rootView.findViewById(R.id.gcButton);
			nullButton =  (Button) rootView.findViewById(R.id.nullButton);
			createBitmapButton =  (Button) rootView.findViewById(R.id.createBitmapButton);
			recycleBitmapButton =  (Button) rootView.findViewById(R.id.recycleButton);
			
			heapText =  (TextView) rootView.findViewById(R.id.textViewHeap);
			
			bitmapSizeText = (EditText) rootView.findViewById(R.id.bitmapSizeText);
			
			refreshButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					refreshInfo();
				}
			});
			
			createBitmapButton.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					largeWhiteBitmap = Bitmap.createBitmap((int) Math.sqrt(Double.parseDouble(bitmapSizeText.getText().toString())*1024/4), (int) Math.sqrt(Double.parseDouble(bitmapSizeText.getText().toString())*1024/4), Bitmap.Config.ARGB_8888);
					System.out.println("bitmap created side: "+(int) Math.sqrt(Double.parseDouble(bitmapSizeText.getText().toString())*1024/4));
					refreshInfo();
				}
			});
			
			recycleBitmapButton.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					// Immediately release the bitmap memory to avoid OutOfMemory exception
		            largeWhiteBitmap.recycle();
		            refreshInfo();
				}
			});
			
			nullButton.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					largeWhiteBitmap = null;
					refreshInfo();
				}
			});
			
			gcButton.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					System.gc();
					refreshInfo();
				}
			});
			
			return rootView;
		}
		
		public void refreshInfo()
		{
			double max = Runtime.getRuntime().maxMemory(); //the maximum memory the app can use
			double heapSize = Runtime.getRuntime().totalMemory(); //current heap size
			double heapRemaining = Runtime.getRuntime().freeMemory(); //amount available in heap
			double nativeUsage = Debug.getNativeHeapAllocatedSize(); //is this right? I only want to account for native memory that my app is being "charged" for.  Is this the proper way to account for that?

			//heapSize - heapRemaining = heapUsed + nativeUsage = totalUsage
			double remaining = max - (heapSize - heapRemaining + nativeUsage); 
			
			Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
			Debug.getMemoryInfo(memoryInfo);
			
			String heapMessage = String.format(
				"MaxMemory: %.2f MB\n"+
				"heap size: %.2f MB\n"+
				"heap remaining: %.2f KB\n"+
				"native usage: %.2f KB\n"+
				"remaining: %.2f MB\n",
				max/(1024.0*1024.0),
				heapSize/(1024.0*1024.0),
				heapRemaining/1024.0,
				nativeUsage/1024.0,
				remaining/(1024.0*1024.0));
			
			heapText.setText(heapMessage);

			String memMessage = String.format(
			    "Memory: \nPss=%.2f MB \nPrivate=%.2f MB \nShared=%.2f MB",
			    memoryInfo.getTotalPss() / 1024.0,
			    memoryInfo.getTotalPrivateDirty() / 1024.0,
			    memoryInfo.getTotalSharedDirty() / 1024.0);
			
			//heapText.append("\n"+memMessage);
		}
		
		@Override
		public void onResume()
		{
			super.onResume();
			refreshInfo();
		}
	}
}
