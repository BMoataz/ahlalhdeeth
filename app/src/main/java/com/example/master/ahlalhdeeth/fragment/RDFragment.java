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
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.master.ahlalhdeeth.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */

public class RDFragment extends android.app.Fragment {

    private ArrayList<String> quotes = new ArrayList<>();
    private ArrayList<String> values = new ArrayList<>();
    private ArrayList<String> valuesWithoutHtml = new ArrayList<>();
    private ArrayList<String> authors = new ArrayList<>();
    private ArrayList<String> scrImage = new ArrayList<>();
    private ArrayList<String> lastMod = new ArrayList<>();
    private ArrayList<String> numRep = new ArrayList<>();
    private ArrayList<String> hrefs = new ArrayList<>();
    public int N_Pages = 1;
    public int mPAGE = 1;

    private String URL = null;

    public RDFragment() {
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

        new FetchLinkTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reading_subjects, container, false);

        return rootView;
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
            N_Pages = 1;
            mPAGE = 1;

            new FetchLinkTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchLinkTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressdialog = ProgressDialog.show(getActivity(), null, "تحمــيل المشاركات من الموقع");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            do {
                Log.i("thelog", " mpage" + mPAGE + " npages" + N_Pages);
                try {
                    Document document = Jsoup.connect(URL + "&page=" + mPAGE).get();
                    String title = document.title();

                    Elements tdItems = document.select("td.alt1 div[id*=post_message]");
                    for (Element tdItem : tdItems) {

                        String post = tdItem.toString();
                        String quote = "";
                        Elements elmts = tdItem.select(/*"td.alt1 div " +*/"div table tbody tr td.alt2");
                        for (Element elmt : elmts) {
                            quote = "<span style=\"border:1px;background:#E1E4F2;color:#ffffff;\"><b>" + elmt.text() + "</b></span>";
                        }
                        values.add(post);
                        valuesWithoutHtml.add(tdItem.text());
                        quotes.add(quote);
                    }
                    tdItems = document.select("td a.bigusername");
                    for (Element tdItem : tdItems) {
                        authors.add(tdItem.text());
                    }
                    tdItems = document.select("td[nowrap=nowrap] img.inlineimg");
                    for (Element tdItem : tdItems) {
                        scrImage.add(tdItem.attr("src"));
                    }
                    tdItems = document.select("td.thead div.normal:has(img)");
                    for (Element tdItem : tdItems) {
                        lastMod.add(tdItem.text());
                    }
                    tdItems = document.select("td.thead div.normal[style*=float]");
                    for (Element tdItem : tdItems) {
                        numRep.add(tdItem.text());
                    }
                    if (mPAGE == 1) {
                        tdItems = document.select("div a[href*=page=]");
                        String[] docpages = new String[tdItems.size()];
                        int i = 0;
                        for (Element tdItem : tdItems) {
                            docpages[i] = tdItem.text();
                            i++;
                        }
                        if (docpages.length != 0)
                            N_Pages = (int) Integer.parseInt(docpages[i - 2]);
                        Log.i("thelog", " -- " + N_Pages);
                    }
                    Log.i("thelog", mPAGE + " loaded");
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                mPAGE++;
            } while (mPAGE <= N_Pages);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressdialog.dismiss();
            Toast.makeText(getActivity(), "تم تحميل " + (mPAGE - 1) + " صفحات، و " + values.size() + " مشاركات ", Toast.LENGTH_LONG).show();
            CustomArrayAdapter mSubjectsAdapter = new CustomArrayAdapter(getActivity(),
                    R.layout.list_subjects_forecast,
                    R.id.list_subjects_textview,
                    values);

            ListView listview = (ListView) getActivity().findViewById(R.id.listview_forecast_reading_subjects);
            listview.setAdapter(mSubjectsAdapter);
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
                    if(!quotes.get(position).equals("")){
                        (convertView.findViewById(R.id.layout_quotes)).setVisibility(View.VISIBLE);
                        Quotes.setText(Html.fromHtml(quotes.get(position)));
                    } else
                        (convertView.findViewById(R.id.layout_quotes)).setVisibility(View.GONE);
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
