package com.example.master.ahlalhdeeth.fragment;


import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.master.ahlalhdeeth.R;
import com.example.master.ahlalhdeeth.ConnectionDetector;
import com.example.master.ahlalhdeeth.ReadingSubjects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends android.app.Fragment {

    private PullToRefreshLayout mPullToRefreshLayout;

    public AlertDialog.Builder builder;

    public ArrayList<String> values = new ArrayList<>();
    private ArrayList<String> hrefs = new ArrayList<>();
    private ArrayList<String> authors = new ArrayList<>();
    private ArrayList<String> lastMod = new ArrayList<>();
    private ArrayList<String> views = new ArrayList<>();
    private ArrayList<String> replies = new ArrayList<>();
    private ArrayList<String> previews = new ArrayList<>();
    private int[] attachements = null;
    private int spinned = 0;
    private int AideIf = 11;
    public int mPAGE = 1;

    private String URL = null;
    private String TITLE = null;

    public ListView listview;
    public View footer;

    int ProgOrNot = 1;
    public ProgressBar progressbar;

    private List<String> ListForCast;
    private CustomArrayAdapter mForecastAdapter;

    public Bundle translateBundle;

    public DetailFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        translateBundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_left, R.anim.slide_out_left).toBundle();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        URL = getActivity().getIntent().getStringExtra("url");
        TITLE = getActivity().getIntent().getStringExtra("title");

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TITLE);
        //Check for the internet connection.
        ConnectionDetector connection = new ConnectionDetector(getActivity());
        boolean Check = connection.IsConnectedToInternet();
        if (!Check) {
            Toast.makeText(getActivity(), "غير متصل بالشبكة العنكبوتية، حاول مرة أخرى", Toast.LENGTH_LONG).show();
            Crouton.makeText(getActivity(), "غير متصل بالشبكة العنكبوتية، حاول مرة أخرى", Style.ALERT).show();
            //finish();
        } else new FetchLinkTask().execute();
        ///////////////////////////////////////

        listview = (ListView) rootView.findViewById(R.id.listview_forecast_detail);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Check for the internet connection.
                ConnectionDetector connection = new ConnectionDetector(getActivity());
                boolean Check = connection.IsConnectedToInternet();
                if (!Check) {
                    Toast.makeText(getActivity(), "غير متصل بالشبكة العنكبوتية، حاول مرة أخرى", Toast.LENGTH_LONG).show();
                    Crouton.makeText(getActivity(), "أنت غير متصل بالشبكة، فعل الشبكة ثم حاول مرة أخرى", Style.ALERT).show();
                } else {
                    Intent OpenActivity = new Intent(getActivity(), ReadingSubjects.class);
                    OpenActivity.putExtra("url", hrefs.get(i));
                    OpenActivity.putExtra("title", values.get(i));
                    OpenActivity.putExtra("titlebefore", TITLE);
                    //Bundle translateBundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_left, R.anim.slide_out_left);
                    startActivity(OpenActivity, translateBundle);
                }
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int i = position;
                String[] builderArray = {"فتح الموضوع في المتصفح", "مشاركة الموضوع", "حفظ الموضوع"};
                builder = new AlertDialog.Builder(getActivity());
                builder.setItems(builderArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                Intent BrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(hrefs.get(i)));
                                startActivity(BrowserIntent);
                                break;
                            case 1:
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("text/plain");
                                share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                share.putExtra(Intent.EXTRA_SUBJECT, "~" + getString(R.string.app_name) + "~\n");
                                share.putExtra(Intent.EXTRA_TEXT, "تفضل بقراءة هذا الموضوع: \n " + values.get(i) + "\n" + hrefs.get(i));
                                startActivity(Intent.createChooser(share, getString(R.string.app_name)));
                                break;
                            case 2:
                                Toast.makeText(getActivity(), "قريــبا إن شاء الله", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                    }
                }).create();
                builder.show();
                return true;
            }
        });
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                Log.i("thelog", "you are in the item" + visibleItemCount);
                Log.i("thelog", "you are in the item" + lastVisibleItem);
                if (lastVisibleItem == AideIf) {
                    AideIf = 1111;
                    //Check for the internet connection.
                    ConnectionDetector connection = new ConnectionDetector(getActivity());
                    boolean Check = connection.IsConnectedToInternet();
                    if (!Check) {
                        Crouton.makeText(getActivity(), "أنت غير متصل بالشبكة، فعل الشبكة ثم حاول مرة أخرى", Style.ALERT).show();
                    } else {
                        View rootView = getActivity().getLayoutInflater().inflate(R.layout.list_title_sujects_forecast, null);
                        listview = (ListView) getActivity().findViewById(R.id.listview_forecast_detail);
                        footer = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.footer_detail, null);
                        listview.addFooterView(footer);
                        mPAGE++;
                        ProgOrNot = 3;
                        new FetchLinkTask().execute();
                    }
                }
            }
        });
        // Now find the PullToRefreshLayout to setup
        mPullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.ptr_layout);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                // Set a OnRefreshListener
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {

                        progressbar = (ProgressBar) getActivity().findViewById(R.id.progress);
                        progressbar.setVisibility(View.VISIBLE);
                        listview.setVisibility(View.INVISIBLE);

                        ProgOrNot = 2;
                        values.clear();
                        hrefs.clear();
                        authors.clear();
                        views.clear();
                        replies.clear();
                        lastMod.clear();

                        new FetchLinkTask().execute();
                    }
                })
                // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);
        return rootView;
    }

    public class FetchLinkTask extends AsyncTask<Void, Void, Void> {

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
            try {
                Document document = Jsoup.connect(URL + "&page=" + mPAGE).get();
                String title = document.title();

                Elements tdItems = document.select("a[id*=thread_title]");
                Elements td1Items = document.select("div.smallfont span[style*=cursor]"); //subject autor
                Elements td2Items = document.select("td.alt2 div.smallfont");//last modified autor and date
                Elements td3Items = document.select("td.alt2[align=center]");//views
                Elements td4Items = document.select("td.alt1[align=center]");//replies
                Elements td5Items = document.select("td.alt1[id*=td_threadtitle]");//replies
                Elements td6Items = document.select("tbody[id*=threadbits] font");// spinned or not
                Elements td7Items = document.select("td.alt1 img[src=images/misc/paperclip.gif]");// spinned or not
                spinned = td6Items.size();

                int i = 0;
                for (Element tdItem : tdItems) {
                    values.add(tdItem.text());
                    hrefs.add("http://www.ahlalhdeeth.com/vb/" + tdItem.attr("href"));
                    i++;
                }
                AideIf = values.size();

                for (Element tdItem : td1Items) {
                    authors.add(tdItem.text());
                }
                for (Element tdItem : td2Items) {
                    lastMod.add(tdItem.text());
                }
                for (Element tdItem : td3Items) {
                    views.add(tdItem.text());
                    i++;
                }
                i = 0;
                for (Element tdItem : td4Items) {
                    replies.add(tdItem.text());
                    i++;
                }
                i = 0;
                for (Element tdItem : td5Items) {
                    previews.add(tdItem.attr("title"));
                    i++;
                }
                i = 0;
                attachements = new int[tdItems.size()];
                for (Element tdItem : td7Items) {
                    String tAideIf = tdItem.attr("alt");
                    if (tAideIf != null) attachements[i] = 1;
                    else attachements[i] = 0;
                    Log.i("thelog", " " + attachements[i]);
                    i++;
                }
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
                mPullToRefreshLayout.setRefreshComplete();
                listview.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.INVISIBLE);
            } else if (ProgOrNot == 3) {
                listview.removeFooterView(footer);
            }

            if (ProgOrNot == 1) {
                mForecastAdapter = new DetailFragment.CustomArrayAdapter(getActivity(),
                        R.layout.list_title_sujects_forecast,
                        R.id.list_item_forecast_textview,
                        values);
                listview.setAdapter(mForecastAdapter);
            } else mForecastAdapter.notifyDataSetChanged();
            ProgOrNot = 1;
        }
    }

    public class CustomArrayAdapter extends ArrayAdapter<String> {

        public CustomArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            String item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_title_sujects_forecast, parent, false);
            }

            TextView Values = (TextView) convertView.findViewById(R.id.list_item_forecast_textview);
            TextView Autors = (TextView) convertView.findViewById(R.id.footertextView);
            TextView Views = (TextView) convertView.findViewById(R.id.textView2);
            TextView Replies = (TextView) convertView.findViewById(R.id.textView3);
            TextView LastMod = (TextView) convertView.findViewById(R.id.textView4);
            ImageView imagePinned = (ImageView) convertView.findViewById(R.id.imageViewSpinned);

            Log.i("thelogc", "Values| " + values.size() + " |Autors| " + authors.size() + " |LastMod| " + lastMod.size() + " |Views| " + views.size() + " |Replies| " + replies.size());
            //Toast.makeText(getActivity(), "Values| "+values.size()+" |Autors| "+authors.size()+" |LastMod| "+lastMod.size()+" |Views| "+views.size()+" |Replies| "+replies.size(),Toast.LENGTH_SHORT).show();

            try {
                Values.setText(values.get(position));
                int AideIf = spinned - 1;
                if (position <= AideIf) {
                    Values.setTextColor(Color.RED);
                    imagePinned.setImageDrawable(getResources().getDrawable(R.drawable.push_pin));
                } else {
                    Values.setTextColor(Color.BLACK);
                    imagePinned.setImageDrawable(null);
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                values.add("????????????");
                Values.setText("????????????");
            }
            try {
                Autors.setText(authors.get(position));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                authors.add("????????????");
                Autors.setText("????????????");
            }
            try {
                Views.setText(views.get(position));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                views.add("????????????");
                Views.setText("????????????");
            }
            try {
                Replies.setText(replies.get(position));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                replies.add("????????????");
                Replies.setText("????????????");
            }
            try {
                LastMod.setText(lastMod.get(position));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                lastMod.add("????????????");
                LastMod.setText("????????????");
            }
            return convertView;
        }
    }
}
