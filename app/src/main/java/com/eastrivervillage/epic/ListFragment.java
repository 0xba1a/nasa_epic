package com.eastrivervillage.epic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private List<CardData> cardDataList;

    private OnFragmentInteractionListener mListener;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_container);
        setOnclickListener(recyclerView);
        return view;
    }

    public void setOnclickListener(RecyclerView recyclerView) {

        recyclerView.addOnItemTouchListener(new CardAdapter.RecyclerTouchListener(getContext(), recyclerView, new CardAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("cardDataList", (Serializable)cardDataList);
                bundle.putInt("selectedPosition", position);

                SlideShowFragment newFragment = SlideShowFragment.newInstance();
                newFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.rl_container, newFragment)
                        .addToBackStack("transition")
                        .commit();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void setArrayList(List<CardData> cardDataList) {
        this.cardDataList = cardDataList;
        cardAdapter = new CardAdapter(this.getActivity(), this.cardDataList);
        RecyclerView.LayoutManager mLayoutManager;

//        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
//        if (rotation == Surface.ROTATION_0 ||
//                rotation == Surface.ROTATION_180) {
//            mLayoutManager = new GridLayoutManager(this.getActivity(), 2);
//        } else {
//            mLayoutManager = new GridLayoutManager(this.getActivity(), 3);
//        }

        mLayoutManager = new GridLayoutManager(this.getActivity(), getActivity().getResources().getInteger(R.integer.grid_column_count));
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);

        cardAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
