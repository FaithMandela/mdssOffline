package com.dewcis.mdss.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dewcis.mdss.adapters.MotherDraftAdapter;
import com.dewcis.mdss.constants.Constant;
import com.dewcis.mdss.databases.DssDetailsDb;
import com.dewcis.mdss.d_model.DraftModel;
import com.dewcis.mdss.d_model.MotherModel;
import com.dewcis.mdss.d_model.PostpartumQModel;
import com.dewcis.mdss.d_model.PregnantQModel;
import com.dewcis.mdss.R;
import com.dewcis.mdss.utils.I_fragmentlistener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Arwin KIsh on 7/13/2017.
 */
public class MotherDraftFragment extends Fragment {

    ArrayList<MotherModel> motherModels;
    MotherDraftAdapter draftsAdapter;
    ListView listView;
    MotherModel motherModel;
    JSONObject json;
    JSONArray jsonArray;
    I_fragmentlistener<MotherModel, PostpartumQModel, PregnantQModel, DraftModel> complete_listener;
    MotherDssDetails dss;
    Gson gson;
    String search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_draft, container, false);

        listView = (ListView) rootView.findViewById(R.id.mlist);
        DssDetailsDb motherDetailsDb = DssDetailsDb.getInstance(getActivity());

        gson = new Gson();
        motherModel = new MotherModel();
        motherModels = new ArrayList<>();

        jsonArray = new JSONArray();

        try {
            jsonArray = motherDetailsDb.getMotherJson();

            motherModels = MotherModel.makeArraylist(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        draftsAdapter = new MotherDraftAdapter(getActivity(), motherModels);
        listView.setAdapter(draftsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search = motherModels.get(position).getClient_rand();

                DssDetailsDb clientDb = DssDetailsDb.getInstance(getActivity());
                clientDb.getWritableDatabase();

                if (clientDb.getRowCount() > 0) {
                    motherModel = clientDb.getMother(search);

                    if (motherModel != null) {
                        complete_listener.isDraft(true);
                    }
                }
                change_fragment();

            }
        });


        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            complete_listener = (I_fragmentlistener<MotherModel, PostpartumQModel, PregnantQModel, DraftModel>) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void doTask(String rand) {

    }

    public void change_fragment() {
        Fragment fragchange = null;

        fragchange = new MotherDssDetails();
        dss = new MotherDssDetails();

        // Replace the current fragment
        if (fragchange != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_view, fragchange).commit();
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.HOUSEHOLD_INFORMATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.DRAFT_KEY, search);
        editor.putString(Constant.DRAFT_VALUE, gson.toJson(motherModel, MotherModel.class));
        editor.apply();

    }
}
