package com.apurvpandey.payuchallenge.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.apurvpandey.payuchallenge.database.KickStarterProjectContract;
import com.apurvpandey.payuchallenge.R;

/**
 * Created by Apurv Pandey on 13/8/17.
 * apurvpandey@rocektmail.com
 * Rewardz Pte Ltd.
 * Contact No. - +91-8377887369
 */

public class KickStarterProjectListAdapter extends RecyclerView.Adapter<KickStarterProjectListAdapter.VH> {

    private Context context;
    private Cursor dataCursor;
    private ItemClickListener itemClickListener;

    public KickStarterProjectListAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.dataCursor = cursor;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.kick_project_single, parent, false);
        return new VH(view);
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {

        dataCursor.moveToPosition(position);


        int title_index = dataCursor.getColumnIndex(KickStarterProjectContract.KickEntry.KICK_TITLE);
        int pledged_index = dataCursor.getColumnIndex(KickStarterProjectContract.KickEntry.KICK_AMT_PLEDGED);
        int backers_index = dataCursor.getColumnIndex(KickStarterProjectContract.KickEntry.KICK_BACKERS);
        int no_of_days_index = dataCursor.getColumnIndex(KickStarterProjectContract.KickEntry.KICK_END_TIME);


        holder.tvProjectName.setText(dataCursor.getString(title_index));
        holder.tvPleadge.setText(context.getString(R.string.pledged_amount)+dataCursor.getInt(pledged_index));
        holder.tvBackers.setText(context.getString(R.string.backers_label)+dataCursor.getString(backers_index));
        holder.tvNoOfDaysTOGo.setText(context.getString(R.string.noOfDaysToGoLabel)+dataCursor.getString(no_of_days_index));

    }


    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }


    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    class VH extends RecyclerView.ViewHolder {

        TextView tvProjectName;
        TextView tvPleadge;
        TextView tvBackers;
        TextView tvNoOfDaysTOGo;
        FrameLayout mainLayout;

        VH(View itemView) {
            super(itemView);

            tvProjectName = (TextView) itemView.findViewById(R.id.projectName);
            tvPleadge = (TextView) itemView.findViewById(R.id.pleadge);
            tvBackers = (TextView) itemView.findViewById(R.id.backers);
            tvNoOfDaysTOGo = (TextView) itemView.findViewById(R.id.noOfDaysToGo);
            mainLayout = (FrameLayout) itemView.findViewById(R.id.main_layout);

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dataCursor.moveToPosition(getAdapterPosition());

                    if (itemClickListener!=null)
                        itemClickListener.itemClicked(dataCursor);
                }
            });

        }
    }

    public interface ItemClickListener{
        void itemClicked(Cursor dataCursor);
    }}