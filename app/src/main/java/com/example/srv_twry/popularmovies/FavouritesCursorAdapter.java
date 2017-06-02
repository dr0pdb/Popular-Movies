package com.example.srv_twry.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.srv_twry.popularmovies.Data.FavouritesDbContract;

import java.io.ByteArrayInputStream;

/**
 * Created by srv_twry on 2/6/17.
 * The cursor adapter for the Recyclerview of the favourite movies activity.
 */

public class FavouritesCursorAdapter extends RecyclerView.Adapter<FavouritesCursorAdapter.FavouritesViewHolder> {

    private final Context mContext;
    private Cursor mCursor;

    public FavouritesCursorAdapter(Context context){
        mContext=context;
    }

    //On its first run it initializes the cursor.
    @SuppressWarnings("UnusedReturnValue")
    public Cursor swapCursor(Cursor c){
        if (mCursor==c){
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c !=null){
            this.notifyDataSetChanged();
        }
        return temp;
    }

    @Override
    public FavouritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.single_movie_layout,parent,false);
        return new FavouritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouritesViewHolder holder, int position) {
        int posterIndex = mCursor.getColumnIndex(FavouritesDbContract.FavouritesEntry.COLUMN_POSTER);
        int db_id_index = mCursor.getColumnIndex(FavouritesDbContract.FavouritesEntry._ID);
        mCursor.moveToPosition(position);

        byte[] posterBytes = mCursor.getBlob(posterIndex);
        int id_index= mCursor.getInt(db_id_index);

        //converting the byte stream to a bitmap
        ByteArrayInputStream inputStream = new ByteArrayInputStream(posterBytes);
        Bitmap posterBitmap = BitmapFactory.decodeStream(inputStream);

        //Setting the Image to holder
        holder.itemView.setTag(id_index);
        holder.posterView.setImageBitmap(posterBitmap);

    }

    @Override
    public int getItemCount() {
        if (mCursor == null){
            return 0;
        }
        return mCursor.getCount();
    }

    public class FavouritesViewHolder extends RecyclerView.ViewHolder{

        final ImageView posterView;

        public FavouritesViewHolder(View view){
            super(view);
            posterView = (ImageView) view.findViewById(R.id.iv_poster);
        }

    }
}
