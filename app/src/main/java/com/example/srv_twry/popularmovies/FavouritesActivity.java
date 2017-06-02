package com.example.srv_twry.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.srv_twry.popularmovies.Data.FavouritesDbContract;

public class FavouritesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = FavouritesActivity.class.getSimpleName();
    private final int LOADER_ID = 100;

    ProgressBar favouritesPb;
    RecyclerView favouritesRecyclerView;
    private FavouritesCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        setTitle(getResources().getString(R.string.Favourites));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        favouritesPb = (ProgressBar) findViewById(R.id.pb_loading_favourites);
        favouritesRecyclerView = (RecyclerView) findViewById(R.id.rv_favourites_list);

        mAdapter = new FavouritesCursorAdapter(this);
        favouritesRecyclerView.setAdapter(mAdapter);
        favouritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportLoaderManager().initLoader(LOADER_ID,null,this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id = (int) viewHolder.itemView.getTag();            //The tag was set in the adapter's onBindViewHolder method.

                String stringId = Integer.toString(id);
                Uri uri = FavouritesDbContract.FavouritesEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                Log.v(TAG,"Deleting "+uri.toString());
                getContentResolver().delete(uri,null,null);

                getSupportLoaderManager().restartLoader(LOADER_ID,null,FavouritesActivity.this);
            }
        }).attachToRecyclerView(favouritesRecyclerView);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mCursor = null;

            @Override
            protected void onStartLoading() {
                if (mCursor !=null){
                    deliverResult(mCursor);
                }else{
                    forceLoad();
                }
            }

            public void deliverResult(Cursor c) {
                mCursor=c;
                super.deliverResult(mCursor);
            }

            @Override
            public Cursor loadInBackground() {
               try{
                   return getContentResolver().query(FavouritesDbContract.FavouritesEntry.CONTENT_URI,null,null,null,null);
               } catch (Exception e) {
                   Log.e(TAG, "Failed to asynchronously load data.");
                   e.printStackTrace();
                   return null;
               }

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        favouritesPb.setVisibility(View.GONE);
        favouritesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
