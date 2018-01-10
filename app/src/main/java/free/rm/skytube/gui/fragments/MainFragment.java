package free.rm.skytube.gui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.LruCache;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import free.rm.skytube.R;

import free.rm.skytube.businessobjects.Logger;
import free.rm.skytube.businessobjects.db.BookmarksDb;
import free.rm.skytube.businessobjects.db.DownloadedVideosDb;
import free.rm.skytube.gui.businessobjects.MainActivityListener;
import free.rm.skytube.gui.businessobjects.adapters.SubsAdapter;
import free.rm.skytube.gui.businessobjects.fragments.FragmentEx;

public class MainFragment extends FragmentEx {
	private RecyclerView				subsListView = null;
	private SubsAdapter					subsAdapter  = null;
	private ActionBarDrawerToggle subsDrawerToggle;
	private TabLayout                   tabLayout = null;
	private DrawerLayout 				subsDrawerLayout = null;

	/** List of fragments that will be displayed as tabs. */
	private List<VideosGridFragment>	videoGridFragmentsList = new ArrayList<>();
	private FeaturedVideosFragment		featuredVideosFragment = null;
	private MostPopularVideosFragment	mostPopularVideosFragment = null;
	private SubscriptionsFeedFragment   subscriptionsFeedFragment = null;
	private BookmarksFragment			bookmarksFragment = null;
	private DownloadedVideosFragment    downloadedVideosFragment = null;

	// Constants for saving the state of this Fragment's child Fragments
	public static final String FEATURED_VIDEOS_FRAGMENT = "MainFragment.featuredVideosFragment";
	public static final String MOST_POPULAR_VIDEOS_FRAGMENT = "MainFragment.mostPopularVideosFragment";
	public static final String SUBSCRIPTIONS_FEED_FRAGMENT = "MainFragment.subscriptionsFeedFragment";
	public static final String BOOKMARKS_FRAGMENT = "MainFragment.bookmarksFragment";
	public static final String DOWNLOADED_VIDEOS_FRAGMENT = "MainFragment.downloadedVideosFragment";

	private VideosPagerAdapter			videosPagerAdapter = null;
	private ViewPager					viewPager;

	public static final String SHOULD_SELECTED_FEED_TAB = "MainFragment.SHOULD_SELECTED_FEED_TAB";


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState != null) {
			featuredVideosFragment = (FeaturedVideosFragment) getChildFragmentManager().getFragment(savedInstanceState, FEATURED_VIDEOS_FRAGMENT);
			mostPopularVideosFragment = (MostPopularVideosFragment) getChildFragmentManager().getFragment(savedInstanceState, MOST_POPULAR_VIDEOS_FRAGMENT);
			subscriptionsFeedFragment = (SubscriptionsFeedFragment)getChildFragmentManager().getFragment(savedInstanceState, SUBSCRIPTIONS_FEED_FRAGMENT);
			bookmarksFragment = (BookmarksFragment) getChildFragmentManager().getFragment(savedInstanceState, BOOKMARKS_FRAGMENT);
			downloadedVideosFragment = (DownloadedVideosFragment) getChildFragmentManager().getFragment(savedInstanceState, DOWNLOADED_VIDEOS_FRAGMENT);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		// setup the toolbar / actionbar
		Toolbar toolbar = view.findViewById(R.id.toolbar);

		// Added by Edward not working
//		toolbar.setNavigationIcon(R.drawable.ic_new_tube_launch);

		setSupportActionBar(toolbar);


		SpannableString title = new SpannableString("NewTube");
		title.setSpan(new TypefaceSpan(getContext(), "JLSDataGothicC_NC.otf"),
				0, title.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		final ActionBar actionBar = getSupportActionBar();

		actionBar.setTitle(title);

		// indicate that this fragment has an action bar menu
		setHasOptionsMenu(true);

		subsDrawerLayout = view.findViewById(R.id.subs_drawer_layout);
		subsDrawerToggle = new ActionBarDrawerToggle(
						getActivity(),
						subsDrawerLayout,
						R.drawable.ic_new_tube_launch,
						R.string.app_name_1,
						R.string.app_name_1);

		subsDrawerToggle.setDrawerIndicatorEnabled(true);

		// Code not working
//		subsDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_new_tube_launch);

//		// added by Edward
//		subsDrawerToggle.setDrawerIndicatorEnabled(false);
//		Drawable drawable = ResourcesCompat.getDrawable(getResources(),
//				R.drawable.ic_new_tube_launch, getActivity().getTheme());
//		subsDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_new_tube_launch);
//		subsDrawerToggle.setHomeAsUpIndicator(drawable);
//		subsDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View view) {
//				Toast.makeText(getContext(),
//						"ToolBarNavigation Indicator clicked.", Toast.LENGTH_SHORT);
//				if (subsDrawerLayout.isDrawerVisible(GravityCompat.START)){
//					subsDrawerLayout.closeDrawer(GravityCompat.START);
//				} else {
//					subsDrawerLayout.openDrawer(GravityCompat.START);
//				}
//			}
//		});

		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
		}

		subsListView = view.findViewById(R.id.subs_drawer);
		if (subsAdapter == null) {
			subsAdapter = SubsAdapter.get(getActivity(),
					view.findViewById(R.id.subs_drawer_progress_bar));
		} else {
			subsAdapter.setContext(getActivity());
		}
		subsAdapter.setListener((MainActivityListener)getActivity());

		subsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
		subsListView.setAdapter(subsAdapter);

		videosPagerAdapter = new VideosPagerAdapter(getChildFragmentManager());
		viewPager = view.findViewById(R.id.pager);
		viewPager.setOffscreenPageLimit(videoGridFragmentsList.size() - 1);
		viewPager.setAdapter(videosPagerAdapter);

		tabLayout = view.findViewById(R.id.tab_layout);
		tabLayout.setupWithViewPager(viewPager);

		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				viewPager.setCurrentItem(tab.getPosition());
				videoGridFragmentsList.get(tab.getPosition()).onFragmentSelected();
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
				videoGridFragmentsList.get(tab.getPosition()).onFragmentUnselected();
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
			}
		});

		// select the default tab:  the default tab is defined by the user through the Preferences
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

		// If the app is being opened via the Notification that new videos from Subscribed channels have been found, select the Subscriptions Feed Fragment
		Bundle args = getArguments();
		if(args != null && args.getBoolean(SHOULD_SELECTED_FEED_TAB, false)) {
			viewPager.setCurrentItem(videoGridFragmentsList.indexOf(subscriptionsFeedFragment));
		} else {
			viewPager.setCurrentItem(Integer.parseInt(sp.getString(getString(R.string.pref_key_default_tab), "0")));
		}

		// Set the current viewpager fragment as selected, as when the Activity is recreated, the Fragment
		// won't know that it's selected. When the Feeds fragment is the default tab, this will prevent the
		// refresh dialog from showing when an automatic refresh happens.
		videoGridFragmentsList.get(viewPager.getCurrentItem()).onFragmentSelected();

		return view;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		subsDrawerToggle.syncState();
	}


	@Override
	public void onResume() {
		super.onResume();

		// when the MainFragment is resumed (e.g. after Preferences is minimized), inform the
		// current fragment that it is selected.
		if (videoGridFragmentsList != null  &&  tabLayout != null) {
			Logger.d(this, "MAINFRAGMENT RESUMED " + tabLayout.getSelectedTabPosition());
			videoGridFragmentsList.get(tabLayout.getSelectedTabPosition()).onFragmentSelected();
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app
		// icon touch event
		if (subsDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle your other action bar items...
		return super.onOptionsItemSelected(item);
	}



	private class VideosPagerAdapter extends FragmentPagerAdapter {

		public VideosPagerAdapter(FragmentManager fm) {
			super(fm);

			// initialise fragments
			if (featuredVideosFragment == null)
				featuredVideosFragment = new FeaturedVideosFragment();

			if (mostPopularVideosFragment == null)
				mostPopularVideosFragment = new MostPopularVideosFragment();

			// Commented By Edward
			if (subscriptionsFeedFragment == null)
				subscriptionsFeedFragment = new SubscriptionsFeedFragment();

			if (bookmarksFragment == null) {
				bookmarksFragment = new BookmarksFragment();
				BookmarksDb.getBookmarksDb().addListener(bookmarksFragment);
			}

			if(downloadedVideosFragment == null) {
				downloadedVideosFragment = new DownloadedVideosFragment();
				DownloadedVideosDb.getVideoDownloadsDb().setListener(downloadedVideosFragment);
			}

			// add fragments to list:  do NOT forget to ***UPDATE*** @string/default_tab and @string/default_tab_values
			videoGridFragmentsList.clear();
			videoGridFragmentsList.add(featuredVideosFragment);
			videoGridFragmentsList.add(mostPopularVideosFragment);
			videoGridFragmentsList.add(subscriptionsFeedFragment);
			videoGridFragmentsList.add(bookmarksFragment);
			videoGridFragmentsList.add(downloadedVideosFragment);
		}

		@Override
		public int getCount() {
			return videoGridFragmentsList.size();
		}

		@Override
		public Fragment getItem(int position) {
			return videoGridFragmentsList.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return videoGridFragmentsList.get(position).getFragmentName();
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if(featuredVideosFragment != null)
			getChildFragmentManager().putFragment(outState, FEATURED_VIDEOS_FRAGMENT, featuredVideosFragment);
		if(mostPopularVideosFragment != null)
			getChildFragmentManager().putFragment(outState, MOST_POPULAR_VIDEOS_FRAGMENT, mostPopularVideosFragment);
		if(subscriptionsFeedFragment != null)
			getChildFragmentManager().putFragment(outState, SUBSCRIPTIONS_FEED_FRAGMENT, subscriptionsFeedFragment);
		if(bookmarksFragment != null)
			getChildFragmentManager().putFragment(outState, BOOKMARKS_FRAGMENT, bookmarksFragment);
		if(downloadedVideosFragment != null)
			getChildFragmentManager().putFragment(outState, DOWNLOADED_VIDEOS_FRAGMENT, downloadedVideosFragment);

		super.onSaveInstanceState(outState);
	}

	/**
	 * Returns true if the subscriptions drawer is opened.
	 */
	public boolean isDrawerOpen() {
		return subsDrawerLayout.isDrawerOpen(GravityCompat.START);
	}


	/**
	 * Close the subscriptions drawer.
	 */
	public void closeDrawer() {
		subsDrawerLayout.closeDrawer(GravityCompat.START);
	}
}

class TypefaceSpan extends MetricAffectingSpan {

	/** An <code>LruCache</code> for previously loaded typefaces. */
	private static LruCache<String, Typeface> sTypefaceCache =
			new LruCache<String, Typeface>(12);

	private Typeface mTypeface;

	/**
	 * Load the {@link Typeface} and apply to a {@link Spannable}.
	 */
	public TypefaceSpan(Context context, String typefaceName) {
		mTypeface = sTypefaceCache.get(typefaceName);

		if (mTypeface == null) {
			mTypeface = Typeface.createFromAsset(context.getApplicationContext()
					.getAssets(), String.format("fonts/%s", typefaceName));

			// Cache the loaded Typeface
			sTypefaceCache.put(typefaceName, mTypeface);
		}
	}


	@Override
	public void updateMeasureState(TextPaint textPaint) {
		textPaint.setTypeface(mTypeface);

		// Note: This flag is required for proper typeface rendering
		textPaint.setFlags(textPaint.getFlags() | Paint.SUBPIXEL_TEXT_FLAG );

	}

	@Override
	public void updateDrawState(TextPaint textPaint) {
		textPaint.setTypeface(mTypeface);

		// Note: This flag is required for proper typeface rendering
		textPaint.setFlags(textPaint.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	}
}
