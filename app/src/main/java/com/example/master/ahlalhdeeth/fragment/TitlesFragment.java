package com.example.master.ahlalhdeeth.fragment;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.master.ahlalhdeeth.R;
import com.example.master.ahlalhdeeth.ConnectionDetector;
import com.example.master.ahlalhdeeth.DetailActivity;
import com.example.master.ahlalhdeeth.SplashScreen;
import com.example.master.ahlalhdeeth.db.DataSource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class TitlesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private List<String> mTitles = new ArrayList<>();
    private List<String> mLinks = new ArrayList<>();
    private ListView mListView;
    private DataSource datasource;
    public static String URL = "url";
    public static String TITLE = "title";
    public static String ALL_TITLES = "all_titles";

    public TitlesFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);

        datasource = new DataSource(getActivity());
        datasource.open();

        mLinks = datasource.findLinks();
        mTitles = datasource.findTitles();

            if(mTitles.isEmpty() || mLinks.isEmpty()) {
            if (!checkNetworkConnection()) {
                Toast.makeText(getActivity(), "غير متصل بالشبكة العنكبوتية، حاول مرة أخرى", Toast.LENGTH_LONG).show();
                Crouton.makeText(getActivity(), "غير متصل بالشبكة العنكبوتية، حاول مرة أخرى", Style.ALERT).show();
            } else new FetchLinkTask().execute();
        } else
            addAdapterToListView(mListView, mTitles);

        addHeaderToListView(mListView);
        addFooterToListView(mListView);


        mListView.setOnItemClickListener(this);

        return rootView;
    }

    private void addAdapterToListView(ListView listview, List<String> titles) {
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                titles);
        listview.setAdapter(mAdapter);
    }

    private void addFooterToListView(ListView listview) {
        RelativeLayout footer = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.footer_main, null);
        TextView footerTextView = (TextView) footer.findViewById(R.id.footertextView);
        if (SplashScreen.stats != null)
            footerTextView.setText(Html.fromHtml(SplashScreen.stats));
        listview.addFooterView(footer);
    }

    private void addHeaderToListView(ListView listview) {
        RelativeLayout header = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.hearder_main, null);
        TextView headerTextView = (TextView) header.findViewById(R.id.header_main);
        String headerText = getString(R.string.header);
        headerTextView.setText(Html.fromHtml(headerText));
        headerTextView.setMovementMethod(LinkMovementMethod.getInstance());
        listview.addHeaderView(header);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (!checkNetworkConnection()) {
            Toast.makeText(getActivity(), "غير متصل بالشبكة العنكبوتية، حاول مرة أخرى", Toast.LENGTH_LONG).show();
            Crouton.makeText(getActivity(), "أنت غير متصل بالشبكة، فعل الشبكة ثم حاول مرة أخرى", Style.ALERT).show();
        } else {
            Intent OpenActivity = new Intent(getActivity(), DetailActivity.class);
            OpenActivity.putExtra(URL, mLinks.get(position - 1)); // -1 for the header I added
            OpenActivity.putExtra(TITLE, mTitles.get(position - 1)); // -1 for the header I added
            OpenActivity.putExtra(ALL_TITLES, mTitles.toArray(new String[mTitles.size()]));
            Bundle translateBundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
            startActivity(OpenActivity, translateBundle);
        }
    }

    private boolean checkNetworkConnection() {
        ConnectionDetector connection = new ConnectionDetector(getActivity());
        return connection.IsConnectedToInternet();
    }

    /**
     * The AsyncTask method for downloading HTML data
     */
    public class FetchLinkTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressdialog = ProgressDialog.show(getActivity(), null, " Loading");
            progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String url = "http://www.ahlalhdeeth.com/vb/index.php";
                Document document = Jsoup.connect(url).get();

                //Sending a response with titles in the forum with their links/.
                Elements tdItems = document.select("td.alt1Active a");

                datasource.create(tdItems);

                for (Element tdItem : tdItems) {
                    mTitles.add(tdItem.text());
                    mLinks.add("http://www.ahlalhdeeth.com/vb/" + tdItem.attr("href"));
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressdialog.dismiss();

            addAdapterToListView(mListView, mTitles);
        }
    }
}

