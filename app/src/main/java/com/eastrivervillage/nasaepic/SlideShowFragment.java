package com.eastrivervillage.nasaepic;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SlideShowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SlideShowFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = SlideShowFragment.class.getSimpleName();

    private List<CardData> cardDataList;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private int selectedPos = 0;
    private CardData cardData;

    private Button btn_wallpaper;
    private Button btn_share;


    public SlideShowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SlideShowFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SlideShowFragment newInstance() {
        SlideShowFragment fragment = new SlideShowFragment();
        return fragment;
    }

    public void onButtonClick() {
        Log.e(TAG, "Button Clicked!");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dialog dialog = getDialog();
        /* For full-screen view */
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slide_show, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.vp_pager);
        cardDataList = (ArrayList<CardData>) getArguments().getSerializable("cardDataList");
        selectedPos = (int) getArguments().getInt("selectedPosition");

        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPos);

        return view;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            //displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public ViewPagerAdapter() {}

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        /**
         * On click Listener for the buttons used in ViewPager
         *
         * As ViewPager doesn't have onClick callback, we have to create an object and pass it to
         * the buttons themselves
         */
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.b_share:
                        actionOnImage(Intent.ACTION_SEND);
                        break;
                    case R.id.b_wallpaper:
                        actionOnImage(Intent.ACTION_ATTACH_DATA);
                        break;
                }
            }

            public void actionOnImage(final String action) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap image = (Bitmap) Glide
                                    .with(getActivity())
                                    .load(cardData.image)
                                    .asBitmap()
                                    .into(-1, -1)
                                    .get();

                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                            f.createNewFile();
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(bytes.toByteArray());

                            Intent intent = new Intent(action);

                            if (action == Intent.ACTION_SEND) {
                                intent.setType("image/jpeg");
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
                                startActivity(Intent.createChooser(intent, "Share Image"));
                            } else if (action == Intent.ACTION_ATTACH_DATA) {
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setDataAndType(Uri.parse("file:///sdcard/temporary_file.jpg"), "image/jpeg");
                                intent.putExtra("mimeType", "image/jpeg");
                                startActivity(Intent.createChooser(intent, "Set as:"));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e + " 99 " + e.getMessage());
                        }
                    }
                });

                thread.start();
            }
        };

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fulscreen_preview, container, false);

            Typeface font = Typeface.createFromAsset( getActivity().getAssets(), "fontawesome-webfont.ttf" );

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);
            btn_wallpaper = (Button) view.findViewById(R.id.b_wallpaper);
            btn_share = (Button) view.findViewById(R.id.b_share);
            btn_wallpaper.setTypeface(font);
            btn_share.setTypeface(font);
            btn_wallpaper.setOnClickListener(btnClickListener);
            btn_share.setOnClickListener(btnClickListener);

            cardData = cardDataList.get(position);

            Log.e(TAG, cardData.image);

            /* Image is too big to load */
            Glide.with(getContext())
                    .load(cardData.image)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return cardDataList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }
    }
}
