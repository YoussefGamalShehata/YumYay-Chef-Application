package com.example.yumyay_chef.homepage.homepagepresenter;

import com.example.yumyay_chef.homepage.homepageview.HomePageFragmentView;
import com.example.yumyay_chef.model.Category;
import com.example.yumyay_chef.model.Country;
import com.example.yumyay_chef.model.MealsRepository;
import com.example.yumyay_chef.model.Meal;
import com.example.yumyay_chef.network.NetworkCallBack;

import java.util.List;

public class HomePagePresenterImpl implements HomePagePresenter{

    private HomePageFragmentView _View;
    private MealsRepository _repo;


    public HomePagePresenterImpl(HomePageFragmentView view, MealsRepository repo){
        this._View = view;
        this._repo = repo;
    }


    @Override
    public void getRandomMealHP() {
        _repo.getRandomMeal(new RandomMealCallback());
    }

    @Override
    public void getCategoryMealHP() {
        _repo.getMealCategories(new CategoryMealCallback());
    }

    @Override
    public void getMealsByCategoryHP(String id) {
        _repo.getMealByCategory(id,new CategoryFoodCallback());
    }

<<<<<<< HEAD
    @Override
    public void getCountryHP() {
        _repo.getFlagCountries(new CountryCallback());
    }

    @Override
    public void getMealsByCountryHP(String country) {
        _repo.getMealByCountry(country,new MealByCountryCallback());
    }

    @Override
    public void getMealById(String id) {
        _repo.getMealById(id,new MealByIdCallback());
    }

    private class MealByIdCallback implements NetworkCallBack<Meal> {
        @Override
        public void onSuccessResult(List<Meal> pojo) {
            _View.showMealById(pojo);
        }
        @Override
        public void onFailureResult(String errorMsg) {
            _View.showMealByIdErrMsg(errorMsg);
        }
    }

=======
>>>>>>> 47c590402c826035d665cdbd9cfe354d32a47e3a
    private class RandomMealCallback implements NetworkCallBack<Meal> {
        @Override
        public void onSuccessResult(List<Meal> pojo) {
            _View.showRandomMealData(pojo);
        }

        @Override
        public void onFailureResult(String errorMsg) {
            _View.showRandomMealErrMsg(errorMsg);
        }
    }

    private class CategoryMealCallback implements NetworkCallBack<Category> {
        @Override
        public void onSuccessResult(List<Category> pojo) {
            _View.showCategoryData(pojo);
        }

        @Override
        public void onFailureResult(String errorMsg) {
            _View.showCategoryErrMsg(errorMsg);
        }
    }
    private class CategoryFoodCallback implements NetworkCallBack<Meal> {
<<<<<<< HEAD
=======

>>>>>>> 47c590402c826035d665cdbd9cfe354d32a47e3a
        @Override
        public void onSuccessResult(List<Meal> meal) {
            _View.showCategoryMeal(meal);
        }
<<<<<<< HEAD
=======

>>>>>>> 47c590402c826035d665cdbd9cfe354d32a47e3a
        @Override
        public void onFailureResult(String errorMsg) {
            _View.showCategoryMealErrMsg(errorMsg);
        }
<<<<<<< HEAD
=======

    }
>>>>>>> 47c590402c826035d665cdbd9cfe354d32a47e3a

    }
    private class CountryCallback implements NetworkCallBack<Country> {

        @Override
        public void onSuccessResult(List<Country> pojo) {
            _View.showCountryData(pojo);
        }

        @Override
        public void onFailureResult(String errorMsg) {
            _View.showCountryErrMsg(errorMsg);
        }
    }

    private class MealByCountryCallback implements NetworkCallBack<Meal> {
        @Override
        public void onSuccessResult(List<Meal> pojo) {
            _View.showMealsByCountryData(pojo);
        }

        @Override
        public void onFailureResult(String errorMsg) {
            _View.showMealsByCountryErrMsg(errorMsg);
        }
    }


}
