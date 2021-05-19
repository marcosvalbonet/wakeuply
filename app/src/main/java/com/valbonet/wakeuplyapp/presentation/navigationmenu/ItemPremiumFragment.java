package com.valbonet.wakeuplyapp.presentation.navigationmenu;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.utils.Wakeuply_Saving;
import com.valbonet.wakeuplyapp.data.connection.Events;

import java.util.List;
import java.util.Locale;

public class ItemPremiumFragment extends Fragment implements View.OnClickListener {

    public static ItemPremiumFragment newInstance() {
        ItemPremiumFragment fragment = new ItemPremiumFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_premium, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Wakeuply_Saving wakeuply_saving = new Wakeuply_Saving(getActivity());

        if (wakeuply_saving.isPremium()){

            Resources res = getActivity().getApplicationContext().getResources();
            String text = res.getString(R.string.is_premium_text, wakeuply_saving.getDaysLeft());
            TextView isTextPremium = getActivity().findViewById(R.id.isTextPremium);
            isTextPremium.setText(text);
            getActivity().findViewById(R.id.isPremiumWakeuply).setVisibility(View.VISIBLE);
        }else{
            getActivity().findViewById(R.id.premiumWakeuply).setVisibility(View.VISIBLE);
        }

        getActivity().findViewById(R.id.premiumPanel).setVisibility(View.VISIBLE);

        getActivity().findViewById(R.id.premiumFacebook).setOnClickListener(this);
        getActivity().findViewById(R.id.premiumInstagram).setOnClickListener(this);

       // getActivity().findViewById(R.id.premiumCancel).setOnClickListener(this);
        //getActivity().findViewById(R.id.isPremiumCancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String button = (String) v.getTag();
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        Wakeuply_Saving wakeuply_saving = new Wakeuply_Saving(getActivity());

        if (button.equalsIgnoreCase("premiumFacebook")) {

            String lang = Locale.getDefault().getLanguage();
            String url= "https://m.facebook.com/sharer.php?u=http://www.wakeuply.com/?lang=en";
            if (lang.equals("es")){
                url= "https://m.facebook.com/sharer.php?u=http://www.wakeuply.com/";
            }

            PackageManager pm = getActivity().getPackageManager();
            Uri uri = Uri.parse(url);
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
                if (applicationInfo.enabled) {
                    // http://stackoverflow.com/a/24547437/1048340
                    uri = Uri.parse("fb://facewebmodal/f?href=" + url);
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }

            Events.logEvent(Events.PREMIUM, "share_facebook");
            wakeuply_saving.setPremiumDate();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            getActivity().startActivity(browserIntent);

        }else if (button.equalsIgnoreCase("premiumInstagram")) {
            Intent instagramIntent = new Intent(Intent.ACTION_SEND);
            instagramIntent.setType("image/*");

            String lang = Locale.getDefault().getLanguage();
            String img = "wakeuply_promo_en";
            if (lang.equals("es")){
                img = "wakeuply_promo_es";
            }

            Uri uri = Uri.parse("android.resource://com.valbonet.wakeuplyapp/drawable/"+img);
            instagramIntent.putExtra(Intent.EXTRA_STREAM, uri);
            // No funciona :: instagramIntent.putExtra(Intent.EXTRA_TEXT,"Wake up incomes: Te despertamos y te premiamos por hacerlo. Introduce el código:\" codeID \" #diquedescarguen #android #iphone #despertador #premios");
            instagramIntent.setPackage("com.instagram.android");

            PackageManager packManager = getActivity().getPackageManager();
            List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(instagramIntent,  PackageManager.MATCH_DEFAULT_ONLY);

            boolean resolved = false;
            for(ResolveInfo resolveInfo: resolvedInfoList){
                if(resolveInfo.activityInfo.packageName.startsWith("com.instagram.android")){
                    instagramIntent.setClassName(
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name );
                    resolved = true;
                    break;
                }
            }
            if(resolved){
                Events.logEvent(Events.PREMIUM, "share_instagram");
                wakeuply_saving.setPremiumDate();
                getActivity().startActivity(instagramIntent);
            }else{
                Toast.makeText(getActivity(), "Instagram App is not installed", Toast.LENGTH_LONG).show();
            }
        }
    }
}
