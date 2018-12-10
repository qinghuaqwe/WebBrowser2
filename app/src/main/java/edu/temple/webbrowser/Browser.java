package edu.temple.webbrowser;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;

//use OnFragmentInteractionListener as an interface to communicate with browser fragments
//we need to reveive event call backs from the fragments (urls in this case)
public class Browser extends AppCompatActivity implements BrowserFragment.OnFragmentInteractionListener {
    private StatePageAdapter webAdapter;//set page adapter let fragment manage each page
    private ViewPager webPager;//viewpager will help move front and back
    private Button goButton;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_browser );

        goButton = (Button) findViewById(R.id.goBtn);
        editText = (EditText) findViewById(R.id.urlTextBox);
        webAdapter = new StatePageAdapter(getSupportFragmentManager());//use statePageAdapter to saving
        //and restoring of fragment's state. Useful for handle large mount of pages
        webPager = (ViewPager)findViewById(R.id.pager);
        webPager.setAdapter(webAdapter);
        //use newInstance() to provide user's choice back tot he fragment when creating the next fragment instance
        webAdapter.addFragment(BrowserFragment.newInstance(""+webAdapter.getCount()));
        //go button would load the url typed in text line
        //it will retrieve the current fragment from adapter and add new url to the url list of fragment
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = editText.getText().toString();
                addUrl(url);
            }
        });
        webPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {//add a listner invokes when the page changes
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                //call onUrlChanged method each time fragment is visted. So user will no longer to hit go
                //button and the page will load automatically.
                ((BrowserFragment)webAdapter.getItem(position)).onUrlChange();
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    //handle the url view request from external link
    //check if intent's action is view action.
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            //get url from intent
            String intentUri = intent.getDataString();
            //similar to String url = getIntent().getData().toString();
            addNewTab();
            //add url to current fragment
            addUrl(intentUri);
        }
    }
    private void addUrl(String url){
        BrowserFragment item = (BrowserFragment) webAdapter.getItem(webPager.getCurrentItem());
        item.addAndLoadUrl(url);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//inflate menu resource buttons.xml into the menu
        getMenuInflater().inflate(R.menu.buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //checks which action to be used
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.previous:
                webPager.setCurrentItem(webPager.getCurrentItem()-1);
                return true;
            //when create new page, it creates new fragment
            case R.id.newFrag:
                addNewTab();
                return true;

            case R.id.next:
                webPager.setCurrentItem(webPager.getCurrentItem()+1);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    private void addNewTab(){
        webAdapter.addFragment(BrowserFragment.newInstance(""+webAdapter.getCount()));
        webPager.setCurrentItem(webAdapter.getCount()-1);
    }
    //communicate with fragments with uri so each time fragment visible, it will return last visted url
    //in the fragment.
    @Override
    public void onFragmentInteraction(String uri) {
        editText.setText(uri);
    }
    //Hold the list of fragments in a list so user can move back and forward with the restored url.
    public static class StatePageAdapter extends FragmentStatePagerAdapter {
       //pass the data of list<Fragments>
        private List<BrowserFragment> fragments;

        public StatePageAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();//use arraylist to manage fragments
        }
        //add fragment to the list when new button is hit.
        public void addFragment(BrowserFragment fragment){
            fragments.add(fragment);
            notifyDataSetChanged();//when list is updated, notify the changes
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
        @Override
        public Fragment getItem(int position) {//return fragment from the list at specific position
            return fragments.get(position);
        }
    }
}

