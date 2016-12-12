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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {

    private String[] values;
    private ArrayAdapter<String> mAdapter;
    private String[] Hrefs;
    private HashMap<String, String> valuesAndHrefs = new HashMap<>();
    private ListView mListView;
    DataSource datasource;

    public MainFragment() {
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

        Hrefs = datasource.FindHrefs();
        values = datasource.FindTitles();
        valuesAndHrefs = datasource.findValuesAndHrefs();

        ////////////////////////////////////////
//        int X = getActivity().getIntent().getIntExtra("position", 50);
//        if (X != 50) {
//            Intent OpenActivityIntent = new Intent(getActivity(), DetailActivity.class);
//            OpenActivityIntent.putExtra("alltitles", values);
//            OpenActivityIntent.putExtra("url", Hrefs[X]);
//            OpenActivityIntent.putExtra("title", values[X]);
//            Bundle translateBundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
//            startActivity(OpenActivityIntent, translateBundle);
//            getActivity().finish();
//        }
        ///////////////////////////////////////

//        if (Hrefs == null || values == null) {
        if (valuesAndHrefs.isEmpty()) {
            //check for the internet connection.
            ConnectionDetector connection = new ConnectionDetector(getActivity());
            boolean Check = connection.IsConnectedToInternet();
            if (!Check) {
                Toast.makeText(getActivity(), "غير متصل بالشبكة العنكبوتية، حاول مرة أخرى", Toast.LENGTH_LONG).show();
                Crouton.makeText(getActivity(), "غير متصل بالشبكة العنكبوتية، حاول مرة أخرى", Style.ALERT).show();
            } else new FetchLinkTask().execute();
        } else
            addAdapterToListView(mListView, Arrays.asList(values));

        addHeaderToListView(mListView);
        addFooterToListView(mListView);


        mListView.setOnItemClickListener(this);

        return rootView;
    }

    private void addAdapterToListView(ListView listview, List<String> values) {
        mAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                values);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //check for the internet connection.
        ConnectionDetector connection = new ConnectionDetector(getActivity());
        boolean Check = connection.IsConnectedToInternet();
        if (!Check) {
            Toast.makeText(getActivity(), "غير متصل بالشبكة العنكبوتية، حاول مرة أخرى", Toast.LENGTH_LONG).show();
            Crouton.makeText(getActivity(), "أنت غير متصل بالشبكة، فعل الشبكة ثم حاول مرة أخرى", Style.ALERT).show();
        } else {
            Intent OpenActivity = new Intent(getActivity(), DetailActivity.class);
            OpenActivity.putExtra("url", Hrefs[i - 1]); // -1 for the header I've added
            OpenActivity.putExtra("title", values[i - 1]); // -1 for the header I've added
            OpenActivity.putExtra("alltitles", values);
            Bundle translateBundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
            startActivity(OpenActivity, translateBundle);
        }
    }

    /**
     * The AsyncTask method for downloading HTML data
     */
    public class FetchLinkTask extends AsyncTask<Void, Void, HashMap<String, String>> {

        ProgressDialog progressdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressdialog = ProgressDialog.show(getActivity(), null, " Loading");
            progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            try {
                String url = "http://www.ahlalhdeeth.com/vb/index.php";
                Document document = Jsoup.connect(url).get();
//                String title = document.title();

                //Sending a response with titles in the forum with their links/.
                Elements tdItems = document.select("td.alt1Active a");

                datasource.create(tdItems);

//                values = new String[tdItems.size()];
//                Hrefs = new String[tdItems.size()];
//                int i = 0;
                for (Element tdItem : tdItems) {
                    valuesAndHrefs.put(tdItem.text(), "http://www.ahlalhdeeth.com/vb/" + tdItem.attr("href"));
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return valuesAndHrefs;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> valuesAndHrefs) {
            super.onPostExecute(valuesAndHrefs);
            progressdialog.dismiss();
//            for (String title : valuesAndHrefs.keySet())
//                mAdapter.add(title);

            List<String> values = new ArrayList<>();
            values.addAll(valuesAndHrefs.keySet());

            addAdapterToListView(mListView, values);

//            mListView.setAdapter(mAdapter);
        }
    }
}

