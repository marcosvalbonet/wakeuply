package com.valbonet.wakeuplyapp.presentation.navigationmenu;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.data.connection.Events;

import java.util.List;
import java.util.Locale;

public class ItemShareFragment extends Fragment implements View.OnClickListener{

    public static ItemShareFragment newInstance() {
        ItemShareFragment fragment = new ItemShareFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_share, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().findViewById(R.id.facebook).setOnClickListener(this);
        getActivity().findViewById(R.id.twitter).setOnClickListener(this);
        getActivity().findViewById(R.id.instagram).setOnClickListener(this);
        getActivity().findViewById(R.id.share).setOnClickListener(this);

//        getActivity().findViewById(R.id.cancel).setOnClickListener(this);

        LinearLayout layout = getActivity().findViewById(R.id.shareWakeuply);
        layout.setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.sharePanel).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        String button = (String) v.getTag();
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        if (button.equalsIgnoreCase("facebook")) {

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

            Events.logEvent(Events.SHARE, "share_facebook");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            getActivity().startActivity(browserIntent);


        }else if (button.equalsIgnoreCase("twitter")) {

            String lang = Locale.getDefault().getLanguage();
            String url = "https://twitter.com/home?status=Wakeuply%3A%20Wake%20up%20with%20your%20favourite%20tiktokers.%20Download%20now%20goo.gl/YQGCDU%20%23wakeuply%20%23android%20%23alarmclock%20%23tiktok";
            if (lang.equals("es")){
                url= "https://twitter.com/home?status=Wakeuply%3A%20Despierta%20con%20tus%20tiktokers%20favoritos.%20Descargala%20ya%20goo.gl/YQGCDU%20%23wakeuply%20%23android%20%23despertador%20%23tiktok";
            }
            Events.logEvent(Events.SHARE, "share_twitter");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            getActivity().startActivity(browserIntent);

        }else if (button.equalsIgnoreCase("instagram")) {
            Intent instagramIntent = new Intent(Intent.ACTION_SEND);
            instagramIntent.setType("image/*");

            String lang = Locale.getDefault().getLanguage();
            String img = "wakeuply_promo_en";
            if (lang.equals("es")){
                img = "wakeuply_promo_es";
            }

            Events.logEvent(Events.SHARE, "share_instagram");
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
                getActivity().startActivity(instagramIntent);
            }else{
                Toast.makeText(getActivity(), "Instagram App is not installed", Toast.LENGTH_LONG).show();
            }

        }else if (button.equalsIgnoreCase("share")) {
            Events.logEvent(Events.SHARE, "share_all_options");
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Compartir URL");
            i.putExtra(Intent.EXTRA_TEXT, "http://www.wakeuly.com/");
            getActivity().startActivity(Intent.createChooser(i, "Compartir URL"));

        }

    }
}
