package com.example.master.ahlalhdeeth.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.master.ahlalhdeeth.ConnectionDetector;
import com.example.master.ahlalhdeeth.Post;
import com.example.master.ahlalhdeeth.R;
import com.example.master.ahlalhdeeth.confab.BulletinBoard;
import com.example.master.ahlalhdeeth.confab.Forum;
import com.example.master.ahlalhdeeth.confab.ForumThread;
import com.example.master.ahlalhdeeth.confab.VBulletinParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ReadPostsFragment extends android.app.Fragment {

    private ArrayList<String> quotes = new ArrayList<>();
    private ArrayList<String> values = new ArrayList<>();
    private ArrayList<String> valuesWithoutHtml = new ArrayList<>();
    private ArrayList<String> authors = new ArrayList<>();
    private ArrayList<String> scrImage = new ArrayList<>();
    private ArrayList<String> lastMod = new ArrayList<>();
    private ArrayList<String> numRep = new ArrayList<>();
    private ArrayList<String> hrefs = new ArrayList<>();

    private List<Post> posts = new ArrayList<>();

    public int PAGES_NUMBERS = 1;
    public int PAGES_DOWNLOADED = 1;
    public ListView mListView;
    private int AideIf = 50;
    public View footer;
    int ProgOrNot = 1;
    CustomArrayAdapter mSubjectsAdapter;

    private String URL = null;

    public ReadPostsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Intent intent = getActivity().getIntent();
        if (intent.getData() != null) URL = intent.getDataString();
        else URL = intent.getStringExtra("url");
        String TITLE = intent.getStringExtra("title");

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(TITLE);
            actionBar.setSubtitle(intent.getStringExtra("titlebefore"));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        new FetchPostsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reading_subjects, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview_forecast_reading_subjects);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;

                if (lastVisibleItem == AideIf) {
                    AideIf = 1111;
                    //Check for the internet connection.
                    if (!checkNetworkConnection()) {
                        Crouton.makeText(getActivity(), "أنت غير متصل بالشبكة، فعل الشبكة ثم حاول مرة أخرى", Style.ALERT).show();
                    } else {
                        if (PAGES_DOWNLOADED <= PAGES_NUMBERS) {
                            addFooterToListView();

                            ProgOrNot = 3;
                            new FetchPostsTask().execute();
                        }
                    }
                }
            }
        });
        return rootView;
    }

    private void addFooterToListView() {
        footer = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.footer_detail, null);
        if (footer != null)
            mListView.addFooterView(footer);
    }

    private boolean checkNetworkConnection() {
        ConnectionDetector connection = new ConnectionDetector(getActivity());
        return connection.IsConnectedToInternet();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().finish();
        }
        if (id == R.id.action_settings) {
            values.clear();
            valuesWithoutHtml.clear();
            authors.clear();
            scrImage.clear();
            lastMod.clear();
            numRep.clear();
            hrefs.clear();
            PAGES_NUMBERS = 1;
            PAGES_DOWNLOADED = 1;

            new FetchPostsTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchPostsTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ProgOrNot == 1) {
                progressdialog = ProgressDialog.show(getActivity(), null, "تحمـيـل المواضيع من الموقع");
                progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            while (PAGES_DOWNLOADED <= PAGES_NUMBERS) {
            try {
                Document document = Jsoup.connect(URL + "&page=" + PAGES_DOWNLOADED).get();

                VBulletinParser parser = new VBulletinParser();
//                parser.parsePosts(document, new ForumThread(new Forum(BulletinBoard)))


                getPagesNumber(document);

                for (Element tdItem : document.select("td.alt1 div[id*=post_message]")) {

                    String post = tdItem.toString();
                    String quote = "";
                    String postWHtml = tdItem.text();
                    Elements elmts = tdItem.select(/*"td.alt1 div " +*/"div table tbody tr td.alt2");
                    for (Element elmt : elmts) {
                        quote = "<span style='border:1px;background:#E1E4F2;color:#ffffff;'><b>" + elmt.text() + "</b></span>";
                    }
                    values.add(post);
                    valuesWithoutHtml.add(tdItem.text());
                    quotes.add(quote);
                }
                for (Element tdItem : document.select("td a.bigusername"))
                    authors.add(tdItem.text());
                for (Element tdItem : document.select("td[nowrap=nowrap] img.inlineimg"))
                    scrImage.add(tdItem.attr("src"));
                for (Element tdItem : document.select("td.thead div.normal:has(img)"))
                    lastMod.add(tdItem.text());
                for (Element tdItem : document.select("td.thead div.normal[style*=float]"))
                    numRep.add(tdItem.text());

                AideIf = values.size();

            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (ProgOrNot == 1) progressdialog.dismiss();
            else if (ProgOrNot == 2) {
                mListView.setVisibility(View.VISIBLE);
                progressdialog.dismiss();
            } else if (ProgOrNot == 3) {
                mListView.removeFooterView(footer);
            }

            if (ProgOrNot == 1) {
                mSubjectsAdapter = new CustomArrayAdapter(getActivity(),
                        R.layout.list_subjects_forecast,
                        R.id.list_subjects_textview,
                        values);

                mListView.setAdapter(mSubjectsAdapter);
            } else
                mSubjectsAdapter.notifyDataSetChanged();

            ProgOrNot = 1;
            PAGES_DOWNLOADED++;
        }
    }

    private void getPagesNumber(Document document) {
        if (PAGES_DOWNLOADED == 1) {
//            Elements tdItems = document.select("div a[href*=page=]");
            Elements tdItems = document.select("html body div  div div table tbody tr td div a[href*=page=]");

            Set<String> docPages = new HashSet<>();
            for (Element tdItem : tdItems) {
                docPages.add(tdItem.text());
            }
            if (docPages.size() != 0)
                PAGES_NUMBERS = docPages.size();

            Log.i("Pages", String.valueOf(PAGES_NUMBERS));
        }
    }

    private class CustomArrayAdapter extends ArrayAdapter<String> {

        public CustomArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

//            String item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_subjects_forecast, parent, false);
            }

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Boolean html_content = sharedPref.getBoolean("html_content", false);
            int textSize = Integer.parseInt(sharedPref.getString("textsize", "0"));
            Log.e("Log RD Fragment", "" + textSize);

            final TextView Share = (TextView) convertView.findViewById(R.id.sharetextView);
            final int currentTextColor = Share.getCurrentTextColor();
            Share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Share.setTextColor(Color.BLUE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Share.setTextColor(currentTextColor);
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("text/plain");
                            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            share.putExtra(Intent.EXTRA_SUBJECT, "~" + getString(R.string.app_name) + "~\n");
                            share.putExtra(Intent.EXTRA_TEXT, authors.get(position) + "\n" + valuesWithoutHtml.get(position));
                            startActivity(Intent.createChooser(share, getString(R.string.app_name)));
                        }
                    }, 150);
                }
            });
            final TextView text_authors = (TextView) convertView.findViewById(R.id.list_autors_textview);
            text_authors.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_authors.setTextColor(Color.BLUE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            text_authors.setTextColor(Color.BLACK);
                        }
                    }, 150);
                }
            });
            TextView Values = (TextView) convertView.findViewById(R.id.list_subjects_textview);
            TextView Quotes = (TextView) convertView.findViewById(R.id.txt_quotes);
            TextView LastMod = (TextView) convertView.findViewById(R.id.textView2);
            TextView NumRep = (TextView) convertView.findViewById(R.id.textView8);
            ImageView Online = (ImageView) convertView.findViewById(R.id.status_imageview);

            if (textSize == 2) {
                float Xaide = Values.getTextSize() + 1.00f;
                Values.setTextSize(Xaide);
            }
            try {
                if (html_content) {
                    Values.setText(Html.fromHtml(values.get(position)));
                    Values.setMovementMethod(LinkMovementMethod.getInstance());
                    if (!quotes.get(position).equals("")) {
                        (convertView.findViewById(R.id.layout_quotes)).setVisibility(View.VISIBLE);
                        Quotes.setText(Html.fromHtml(quotes.get(position)));
                    }
                } else {
                    Values.setText(valuesWithoutHtml.get(position));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                values.add("????????????");
                Values.setText("????????????");
            }
            try {
                text_authors.setText(authors.get(position));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                authors.add("????????????");
                text_authors.setText("????????????");
            }

            try {
                LastMod.setText(lastMod.get(position));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                lastMod.add("????????????");
                LastMod.setText("????????????");
            }
            try {
                NumRep.setText(numRep.get(position));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                numRep.add("????????????");
                NumRep.setText("????????????");
            }
            String Caide = scrImage.get(position);
            if (Caide.length() == 34) {
                Online.setImageResource(R.drawable.offline_status);
            } else Online.setImageResource(R.drawable.online_status);

            return convertView;
        }
    }
}
