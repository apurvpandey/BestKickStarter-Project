package com.apurvpandey.payuchallenge.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.apurvpandey.payuchallenge.database.KickStarterProjectContract;
import com.apurvpandey.payuchallenge.adapters.KickStarterProjectListAdapter;
import com.apurvpandey.payuchallenge.R;
import com.apurvpandey.payuchallenge.network.VolleySingleton;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MainActivity extends AppCompatActivity implements
        KickStarterProjectListAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BEST_KICK_PROJECTS_LOADER = 200;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private KickStarterProjectListAdapter kickStarterProjectListAdapter;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager linearLayoutManager;
    private SmoothProgressBar smoothProgressBar;
    MaterialSearchView materialSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        getProjects();
        showResponse(null);

        //for first 20 items
        Bundle args = new Bundle();
        args.putInt("start", 0);
        args.putInt("end", 19);
        args.putBoolean("filter", false);
        args.putBoolean("sort", false);
        args.putBoolean("search", false);

        getSupportLoaderManager().initLoader(BEST_KICK_PROJECTS_LOADER, args, this);
    }

    private void getProjects() {

        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();

        String BASE_URL = "http://starlord.hackerearth.com/kickstarter";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }


    private void processResponse(JSONArray response) {

        Vector<ContentValues> cVVector = new Vector<ContentValues>(response.length());

        for (int i = 0; i < response.length(); i++) {

            try {

                JSONObject jsonProject = response.getJSONObject(i);

                String title = jsonProject.getString("title");
                String backers = jsonProject.getString("num.backers");
                String endTime = jsonProject.getString("end.time");
                int pledged = jsonProject.getInt("amt.pledged");

                String blurb = jsonProject.getString("blurb");
                String by = jsonProject.getString("by");
                String country = jsonProject.getString("country");
                String currency = jsonProject.getString("currency");
                String location = jsonProject.getString("location");
                String state = jsonProject.getString("state");
                String type = jsonProject.getString("type");
                String url = jsonProject.getString("url");
                int percentageFunded = jsonProject.getInt("percentage.funded");
                int serialNumber = jsonProject.getInt("s.no");


                ContentValues kickValues = new ContentValues();

                kickValues.put(KickStarterProjectContract.KickEntry.KICK_SL_NUMBER, serialNumber);
                kickValues.put(KickStarterProjectContract.KickEntry.KICK_AMT_PLEDGED, pledged);
                kickValues.put(KickStarterProjectContract.KickEntry.KICK_BLURB, blurb);
                kickValues.put(KickStarterProjectContract.KickEntry.KICK_BY, by);
                kickValues.put(KickStarterProjectContract.KickEntry.KICK_COUNTRY, country);
                kickValues.put(KickStarterProjectContract.KickEntry.KICK_CURRENCY, currency);
                kickValues.put(KickStarterProjectContract.KickEntry.KICK_END_TIME, endTime);
                kickValues.put(KickStarterProjectContract.KickEntry.KICK_LOCATION, location);
                kickValues.put(KickStarterProjectContract.KickEntry.KICK_PERCENTAGE_FUNDED, percentageFunded);


                if (backers.equals("Cambridge, MA") || backers.equals("New York, NY"))
                    backers = "546348";

                kickValues.put(KickStarterProjectContract.KickEntry.KICK_BACKERS, Integer.valueOf(backers));

                kickValues.put(KickStarterProjectContract.KickEntry.KICK_STATE, state);
                kickValues.put(KickStarterProjectContract.KickEntry.KICK_TITLE, title);
                kickValues.put(KickStarterProjectContract.KickEntry.KICK_TYPE, type);
                kickValues.put(KickStarterProjectContract.KickEntry.KICK_URL, url);

                cVVector.add(kickValues);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        int inserted = 0;
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContentResolver().delete(KickStarterProjectContract.KickEntry.CONTENT_URI, null, null);
            getContentResolver().bulkInsert(KickStarterProjectContract.KickEntry.CONTENT_URI, cvArray);
        }


    }

    private void showResponse(Cursor cursor) {
        kickStarterProjectListAdapter = new KickStarterProjectListAdapter(this, cursor);
        recyclerView.setAdapter(kickStarterProjectListAdapter);
        kickStarterProjectListAdapter.setItemClickListener(this);
        progressBar.setVisibility(View.GONE);
    }


    private void initView() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(null);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        smoothProgressBar = (SmoothProgressBar) findViewById(R.id.smoothProgress);
        materialSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.v("...", "Last Item Wow !" + totalItemCount);
                            smoothProgressBar.setVisibility(View.VISIBLE);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //restart loader to fetch more values
                                            //for first 20 items
                                            Bundle args = new Bundle();
                                            args.putInt("start", 0);
                                            args.putInt("end", totalItemCount + 19);
                                            getSupportLoaderManager().restartLoader(BEST_KICK_PROJECTS_LOADER, args, MainActivity.this);

                                        }
                                    }, 2000);
                                }
                            });
                        }
                    }
                }
            }
        });


        setSearchViewComponents();
    }

    private void setSearchViewComponents() {

        materialSearchView.setVoiceSearch(true);
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                getSearchedResult(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;

            }
        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //// TODO: 13/8/17
            }

            @Override
            public void onSearchViewClosed() {

                Bundle args = new Bundle();
                args.putInt("start", 0);
                args.putInt("end", kickStarterProjectListAdapter.getItemCount() - 1);
                args.putBoolean("sort", false);
                args.putBoolean("filter", false);
                args.putBoolean("search", false);
                getSupportLoaderManager().restartLoader(BEST_KICK_PROJECTS_LOADER, args, MainActivity.this);
            }
        });
    }

    private void getSearchedResult(String query) {

        materialSearchView.hideKeyboard(recyclerView);

        Bundle args = new Bundle();
        args.putInt("start", 0);
        args.putInt("end", kickStarterProjectListAdapter.getItemCount() - 1);
        args.putBoolean("sort", false);
        args.putBoolean("filter", false);
        args.putBoolean("search", true);
        args.putString("search_key", query);
        getSupportLoaderManager().restartLoader(BEST_KICK_PROJECTS_LOADER, args, MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void itemClicked(Cursor dataCursor) {

        int serial_number_index = dataCursor.getColumnIndex(KickStarterProjectContract.KickEntry.KICK_SL_NUMBER);
        int serial_number = dataCursor.getInt(serial_number_index);

        Intent intent = new Intent(this, ProjectDetail.class);
        intent.putExtra("sl_number", serial_number);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        int start = args.getInt("start");
        int end = args.getInt("end");

        boolean filter = args.getBoolean("filter");
        boolean sort = args.getBoolean("sort");
        boolean search = args.getBoolean("search");

        loading = true;

        Uri projects_uri = KickStarterProjectContract.KickEntry.CONTENT_URI;
        String selection;
        String[] selectionArgs;
        String sort_order = null;

        if (filter) {
            String backers = args.getString("filter_by");
            Toast.makeText(this, "Filtering by " + backers, Toast.LENGTH_SHORT).show();

            selection = KickStarterProjectContract.KickEntry.TABLE_NAME + "." + KickStarterProjectContract.KickEntry.KICK_SL_NUMBER + " >= ? AND " +
                    KickStarterProjectContract.KickEntry.TABLE_NAME + "." + KickStarterProjectContract.KickEntry.KICK_SL_NUMBER + " <= ? AND " +
                    KickStarterProjectContract.KickEntry.TABLE_NAME + "." + KickStarterProjectContract.KickEntry.KICK_BACKERS + " <= ? ";
            selectionArgs = new String[]{String.valueOf(start), String.valueOf(end), backers};
        } else if (search) {
            selection = KickStarterProjectContract.KickEntry.TABLE_NAME + "." + KickStarterProjectContract.KickEntry.KICK_SL_NUMBER + " >= ? AND " +
                    KickStarterProjectContract.KickEntry.TABLE_NAME + "." + KickStarterProjectContract.KickEntry.KICK_SL_NUMBER + " <= ? AND " +
                    KickStarterProjectContract.KickEntry.TABLE_NAME + "." + KickStarterProjectContract.KickEntry.KICK_TITLE + " LIKE ? ";

            selectionArgs = new String[]{String.valueOf(start), String.valueOf(end), "%" + args.getString("search_key") + "%"};
        } else {
            selection = KickStarterProjectContract.KickEntry.TABLE_NAME + "." + KickStarterProjectContract.KickEntry.KICK_SL_NUMBER + " >= ? AND " +
                    KickStarterProjectContract.KickEntry.TABLE_NAME + "." + KickStarterProjectContract.KickEntry.KICK_SL_NUMBER + " <= ?";
            selectionArgs = new String[]{String.valueOf(start), String.valueOf(end)};
        }

        if (sort)
            sort_order = args.getString("sort_by");


        return new CursorLoader(this,
                projects_uri,
                null,
                selection,
                selectionArgs,
                sort_order);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.ic_filter:
                filter();
                break;

            case R.id.ic_sort:
                sort();
                break;

            case R.id.ic_search:
                materialSearchView.showSearch();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sort() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sort Projects");
        alertDialog.setMessage("Sort Alphabetically?");

        alertDialog.setPositiveButton("Sort",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Bundle args = new Bundle();
                        args.putInt("start", 0);
                        args.putInt("end", kickStarterProjectListAdapter.getItemCount() - 1);
                        args.putBoolean("sort", true);
                        args.putBoolean("filter", false);
                        args.putString("sort_by", "title");
                        getSupportLoaderManager().restartLoader(BEST_KICK_PROJECTS_LOADER, args, MainActivity.this);

                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }

    private void filter() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Filter Projects");
        alertDialog.setMessage("Filter by No. of Backers");

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 00, 50, 00);
        input.setLayoutParams(lp);
        input.setGravity(Gravity.CENTER);
        input.setHint(getString(R.string.enter_backers));
        input.setFocusable(true);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Filter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String backers = input.getText().toString();

                        if (!backers.equals("")) {
                            Bundle args = new Bundle();
                            args.putInt("start", 0);
                            args.putInt("end", kickStarterProjectListAdapter.getItemCount() - 1);
                            args.putBoolean("filter", true);
                            args.putString("filter_by", backers);
                            getSupportLoaderManager().restartLoader(BEST_KICK_PROJECTS_LOADER, args, MainActivity.this);

                        }
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        smoothProgressBar.setVisibility(View.GONE);

        if (cursor != null && cursor.getCount() > 0) {
            kickStarterProjectListAdapter.swapCursor(cursor);
            progressBar.setVisibility(View.GONE);
        } else {
            kickStarterProjectListAdapter.swapCursor(null);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        kickStarterProjectListAdapter.swapCursor(null);
    }
}
