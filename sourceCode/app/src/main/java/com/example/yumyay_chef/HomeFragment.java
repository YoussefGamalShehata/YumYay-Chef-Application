package com.example.yumyay_chef;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yumyay_chef.homepage.homepagepresenter.HomePagePresenter;
import com.example.yumyay_chef.homepage.homepagepresenter.HomePagePresenterImpl;
import com.example.yumyay_chef.homepage.homepageview.HomaPageActivityCategoryMealsView;
import com.example.yumyay_chef.homepage.homepageview.HomePageActivityRandomMealsView;
import com.example.yumyay_chef.homepage.homepageview.HomePageAdapterCategory;
import com.example.yumyay_chef.homepage.homepageview.HomePageAdapterRandomMeals;
import com.example.yumyay_chef.homepage.homepageview.HomePagePageActivity;
import com.example.yumyay_chef.model.Category;
import com.example.yumyay_chef.model.Meal;
import com.example.yumyay_chef.model.MealsRepositoryImpl;
import com.example.yumyay_chef.network.MealRemoteDataSourceImpl;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements HomePageActivityRandomMealsView, HomaPageActivityCategoryMealsView {

    public static final String TAG = "HomeActivity";
    private RecyclerView recyclerView;
    private HomePageAdapterRandomMeals homePageAdapterRandomMeals;
    private HomePageAdapterCategory homePageAdapterCategory;
    HomePagePresenter homePagePresenter;
    LinearLayoutManager linearLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        initUI(view);
        recyclerView.setHasFixedSize(true);
        linearLayout = new LinearLayoutManager(getActivity());
        homePageAdapterRandomMeals = new HomePageAdapterRandomMeals(getActivity(),new ArrayList<>());
        homePagePresenter = new HomePagePresenterImpl(this,this , MealsRepositoryImpl.getInstance(MealRemoteDataSourceImpl.getInstance()));
        linearLayout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);
        recyclerView.setAdapter(homePageAdapterRandomMeals);
        homePagePresenter.getRandomMealHP();
        return  view;
    }

    private void initUI(View v){
        recyclerView = v.findViewById(R.id.recView);
    }


    @Override
    public void showRandomMealData(List<Meal> meals) {
        homePageAdapterRandomMeals.setList(meals);
        homePageAdapterRandomMeals.notifyDataSetChanged();
    }

    @Override
    public void showRandomMealErrMsg(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(error).setTitle("An Error Occurred");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showCategoryData(List<Category> meals) {
        homePageAdapterCategory.setList(meals);
        homePageAdapterCategory.notifyDataSetChanged();
    }

    @Override
    public void showCategoryErrMsg(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(error).setTitle("An Error Occurred");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}