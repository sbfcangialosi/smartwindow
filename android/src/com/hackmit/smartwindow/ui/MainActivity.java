package com.hackmit.smartwindow.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.smartwindow.R;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// action bar
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// returns fragment for each page
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// select corresponding tab on swipe
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// add tab in the action bar for each section
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// switch to page in pager when tab selected
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter implements RequestExecuter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public void executeRequest(String openOrClose) {
			new HttpRequestTask().execute(openOrClose);	
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new OpenCloseFragment();
			((OpenCloseFragment) fragment).setRequestExecuter(this);
			Bundle args = new Bundle();
			args.putInt(OpenCloseFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class OpenCloseFragment extends Fragment implements View.OnClickListener{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String OPEN = "Open Window";
		public static final String CLOSE = "Close Window";
		public static final String ARG_SECTION_NUMBER = "arg_section_number";
		public static RequestExecuter executer;

		public OpenCloseFragment() {
			
		}
		
		public void setRequestExecuter(RequestExecuter executer){
			this.executer = executer;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_openclose,
					container, false);
			Button openCloseButton = (Button) rootView.findViewById(R.id.openclose_button);
			openCloseButton.setOnClickListener(this);

				
			return rootView;
		}

		@Override
		public void onClick(View button) {
			String openOrClose = "close";
			if(((Button) button).getText().equals(OPEN)){
				openOrClose = "open";
				((Button) button).setText(CLOSE);
			} else {
				((Button) button).setText(OPEN);
			}
			
			executer.executeRequest(openOrClose);
		}
	}
	
	public class HttpRequestTask extends AsyncTask<String, Long, Integer>{

		@Override
		protected Integer doInBackground(String... openOrClose) {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://162.243.27.156");
			try {
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        if(openOrClose[0].equals("open")){
		        	 nameValuePairs.add(new BasicNameValuePair("json", "{\"stat\" : \"open\"}"));
		        } else {
		        	 nameValuePairs.add(new BasicNameValuePair("json", "{\"stat\" : \"close\"}"));
		        }
		       
		        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        HttpResponse response = client.execute(post);
		        System.out.println("=======RESPONSE: " + response + " =========");
		        
		    } catch (ClientProtocolException e) {
		        System.out.println("CLIENT PROTOCOL EXCEPTION: " + e);
		    } catch (IOException e) {
		        System.out.println("IO EXCEPTION: " + e);
		    }
			return Integer.valueOf(0);
		}
		
		 protected void onProgressUpdate(Integer... progress) {
	         
	     }

	     protected void onPostExecute(Long result) {
	     }
		
	}
}
