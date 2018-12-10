package edu.temple.webbrowser;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import java.util.ArrayList;


public class BrowserFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";//use the default template to initialize the fragment parameters
    private ArrayList<CharSequence> lstUrls;//use arraylist of urls to help manage different urls in the fragment
    //each fragment holds its own list and will not access to list of other fragments
    private int position;//url position in the url list
    private Button buttonBack;
    private Button buttonNext;
    private OnFragmentInteractionListener webListener;
    private WebView webView;

    public BrowserFragment() {
        lstUrls = new ArrayList<>();//everytime create a new fragment, will assign a new arraylist to use
    }
    public static BrowserFragment newInstance(String param) {
        BrowserFragment fragment = new BrowserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    //save the url in the textview to arraylist, also put int to store its position when fragment goes to background
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequenceArrayList("urls", lstUrls);
        outState.putInt("position", position);
    }
    //when the fragment goes active, get the urls and position as the last visted position user have.
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            lstUrls = savedInstanceState.getCharSequenceArrayList("urls");
            position = savedInstanceState.getInt("position");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the layout for the fragment
        View v = inflater.inflate(R.layout.fragment_browser, container, false);
        webView = (WebView) v.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);//enable java script in browser
        webView.setWebViewClient(new WebViewClient());
        //I also build 2 buttons inside the fragment, to help swap front and back of user typed Urls
        buttonBack = (Button) v.findViewById(R.id.page_back);
        buttonNext = (Button) v.findViewById(R.id.page_next);
        //click button back then go to the back url in the same fragment
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position >0){
                    position--;
                    onUrlChange();
                }
            }
        });
       //click next then go to next url in the same fragment
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position < lstUrls.size()-1){
                    position++;
                    onUrlChange();
                }
            }
        });
        return v;
    }
    //When fragment become visible, loads the last url from the list
    @Override
    public void onResume() {
        super.onResume();
        onUrlChange();
    }
    //create a method handle the url changes and load the webpage
    public void onUrlChange() {
        if(lstUrls.size() > 0) {
            webView.loadUrl(lstUrls.get(position).toString());
            if (webListener != null) {
                webListener.onFragmentInteraction(lstUrls.get(position).toString());
            }
        }else {
            if (webListener != null) {
                webListener.onFragmentInteraction("");//even nothing there, still have to update the url
                //in the text view.
            }
        }
    }
    @Override//use onAttach to associate the Browser which is the Main activity
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            webListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }
    //when the webListner is null, call on Detach to let fragment no long attach to main activity
    @Override
    public void onDetach() {
        super.onDetach();
        webListener = null;
    }
    //method to add and load the url, use for go button to generate new fragment
    public void addAndLoadUrl(String url) {
        if(url != null && !url.trim().isEmpty()) {
            lstUrls.add(url);
            position = lstUrls.size()-1;
            webView.loadUrl(url);
        }
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String uri);
    }
}
