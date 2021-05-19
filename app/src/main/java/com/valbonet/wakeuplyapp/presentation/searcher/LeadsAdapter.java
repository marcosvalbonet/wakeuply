package com.valbonet.wakeuplyapp.presentation.searcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.valbonet.wakeuplyapp.data.LoadImageTask;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.utils.Utils;

import java.util.List;

/**
 * Adaptador de leads
 */
public class LeadsAdapter extends ArrayAdapter<Lead> {
    private final Context context;
    private List<Lead> leads;
    private Integer listLayout;

    public LeadsAdapter(Context context, List<Lead> objects) {
        super(context, 0, objects);
        this.context =  context;
        this.leads = objects;
    }

    public LeadsAdapter(Context context, List<Lead> objects, int layout) {
        super(context, 0, objects);
        this.context =  context;
        this.leads = objects;
        this.listLayout = layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (convertView == null) {
            if (this.listLayout == null) {
                convertView = inflater.inflate(R.layout.list_item_lead, parent,false);
            }else{
                convertView = inflater.inflate(this.listLayout, parent,false);
            }

            holder = new ViewHolder() ;
            holder.imageMuser = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.nameMuser = (TextView) convertView.findViewById(R.id.tv_name);
            holder.nickMuser = (TextView) convertView.findViewById(R.id.tv_title);
            holder.moreInfo = (TextView) convertView.findViewById(R.id.tv_company);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        // Lead actual.
        Lead lead = getItem(position);

        LoadImageTask loadImage = new LoadImageTask(holder.imageMuser, lead.getNickname());
        loadImage.execute(Utils.reviewURL(lead.getURLImage()));
        holder.nameMuser.setText(lead.getName());
        holder.nickMuser.setText(lead.getNickname());
        holder.moreInfo.setText("");

        return convertView;
    }

    private class ViewHolder {
        ImageView imageMuser;
        TextView nameMuser ;
        TextView nickMuser ;
        TextView moreInfo ;

    }

    public void refreshLeads(List<Lead> leads) {
        this.leads.clear();
        this.leads.addAll(leads);
        notifyDataSetChanged();
    }
}
