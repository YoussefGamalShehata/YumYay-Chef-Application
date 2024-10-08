package com.example.yumyay_chef.mealsDetails.view;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.yumyay_chef.R;
import com.example.yumyay_chef.db.MealsLocalDataSourceImpl;
import com.example.yumyay_chef.model.Converter;
import com.example.yumyay_chef.model.Ingrediant;
import com.example.yumyay_chef.model.Meal;
import com.example.yumyay_chef.model.MealPlan;
import com.example.yumyay_chef.model.MealsRepositoryImpl;
import com.example.yumyay_chef.network.MealRemoteDataSourceImpl;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MealContentFragment extends Fragment implements OnClickAddToFavListener ,MealContentFragmentView{

    private static final String MEAL_KEY = "MEAL_KEY";
    private static final String YOUTUBE_PREFIX = "https://www.youtube.com/";
    private static final String TAG = "MealContentFragment";

    private Meal meal;
    private MealPlan mealPlan;

    private ImageView mealImage;
    private TextView mealTitle;
    private TextView mealDesc;
    private WebView mealVideo;
    private Button addBtn;
    private Button addPlan;
    private ImageButton imgFav;
    private ImageButton imgPlan;
    private TextView textArea;
    private String selectedDate;
    private MealsRepositoryImpl _repo;


    private RecyclerView recyclerView;
    private IngrediantsAdapter ingrediantsAdapter;
    LinearLayoutManager linearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            meal = (Meal) getArguments().getSerializable(MEAL_KEY);
            _repo = MealsRepositoryImpl.getInstance(MealRemoteDataSourceImpl.getInstance(), MealsLocalDataSourceImpl.getInstance(getContext()));
            onCheckClick(meal);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_content, container, false);
        initUI(view);
        addPlan = view.findViewById(R.id.btnAddPlan);
        addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainCalenderFragment dialogFragment = new MainCalenderFragment();
                dialogFragment.setOnDateSelectedListener(selectedDate -> {
                    // Use the selected date to create a food plan and update the database
                    Toast.makeText(getContext(), "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();
                    mealPlan = Converter.convertToPlanClass(meal, selectedDate);
                    _repo.insertMealPlan(mealPlan);
                });

                dialogFragment.show(getParentFragmentManager(), "calendarDialog");
            }
        });
        imgPlan = view.findViewById(R.id.imgBtnPlan);
        imgPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainCalenderFragment dialogFragment = new MainCalenderFragment();
                dialogFragment.setOnDateSelectedListener(selectedDate -> {
                    // Use the selected date to create a food plan and update the database
                    Toast.makeText(getContext(), "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();
                    mealPlan = Converter.convertToPlanClass(meal, selectedDate);
                    _repo.insertMealPlan(mealPlan);
                    saveMealToCalendar(meal.getMealName(),stringToDate(selectedDate),meal.getInstructions());
                });

                dialogFragment.show(getParentFragmentManager(), "calendarDialog");
            }
        });
        ingrediantsAdapter = new IngrediantsAdapter(getActivity(),new ArrayList<>(),this);
        recyclerView.setHasFixedSize(true);
        linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);
        imgFav = view.findViewById(R.id.imgBtnFav);
        mealImage = view.findViewById(R.id.mealImg);
        mealTitle = view.findViewById(R.id.txtName);
        mealDesc = view.findViewById(R.id.txtInstruction);
        mealVideo = view.findViewById(R.id.webViedo);
        addBtn = view.findViewById(R.id.btnAddFav);
        addPlan = view.findViewById(R.id.btnAddPlan);
        textArea = view.findViewById(R.id.mealArea);
        setupWebView();
        loadImage();
        setMealDetails();
        List<Ingrediant> ingredientPojos = getIngList(meal);

        ingrediantsAdapter.setList2(ingredientPojos);
        recyclerView.setAdapter(ingrediantsAdapter);
        ingrediantsAdapter.notifyDataSetChanged();

        mealVideo = view.findViewById(R.id.webViedo);
        WebSettings webSettings = mealVideo.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mealVideo.setWebViewClient(new WebViewClient());
        Glide.with(this).load(meal.getMealThumbnail()).apply(new RequestOptions().override(200,200)
                        .placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_foreground))
                .into(mealImage);

        //For Parce response
        String[] paragraphs = meal.getInstructions().split("\\.");
        StringBuilder formattedText = new StringBuilder();
        for (int i = 0; i < paragraphs.length; i++) {
            if (!paragraphs[i].trim().isEmpty()) {
                // Add numbered bullet points
                formattedText.append((i + 1) + "- " + paragraphs[i].trim() + "\n\n");
            }
        }


        mealTitle.setText(meal.getMealName());
        textArea.setText(meal.getArea());
        mealDesc.setText(formattedText.toString().trim());

        String youtubeEmbedUrl = "https://www.youtube.com/embed/" + getYoutubeVideoId(meal.getYoutubeUrl());
        mealVideo.loadUrl(youtubeEmbedUrl);
        if(meal.isFav()){
            addBtn.setEnabled(false);
            imgFav.setImageResource(R.drawable.heart_filled);
        }
        else{
            addBtn.setEnabled(true);
            imgFav.setImageResource(R.drawable.hearto);
        }
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!meal.isFav()) {
                    Toast.makeText(getContext(), "Meal Added To Favorites", Toast.LENGTH_SHORT).show();
                    onAddToFavClick(meal);
                    addBtn.setEnabled(false);
                    imgFav.setImageResource(R.drawable.heart_filled);
                }
                else
                {
                    Toast.makeText(getContext(), meal.getMealName()+" Removed To Favourite", Toast.LENGTH_SHORT).show();
                    onRemoveFavClick(meal);
                    addBtn.setEnabled(true);
                    imgFav.setImageResource(R.drawable.hearto);
                }
            }
        });
        imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!meal.isFav()) {
                    Toast.makeText(getContext(), "Meal Added To Favorites", Toast.LENGTH_SHORT).show();
                    onAddToFavClick(meal);
                    addBtn.setEnabled(false);
                    imgFav.setImageResource(R.drawable.heart_filled);
                }
                else
                {
                    Toast.makeText(getContext(), meal.getMealName()+" Removed To Favourite", Toast.LENGTH_SHORT).show();
                    onRemoveFavClick(meal);
                    addBtn.setEnabled(true);
                    imgFav.setImageResource(R.drawable.hearto);
                }
            }
        });
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = mealVideo.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mealVideo.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return !(url.startsWith(YOUTUBE_PREFIX + "watch") || url.startsWith(YOUTUBE_PREFIX + "embed/"));
            }
        });

        String youtubeEmbedUrl = "https://www.youtube.com/embed/" + getYoutubeVideoId(meal.getYoutubeUrl());
        mealVideo.loadUrl(youtubeEmbedUrl);
    }

    private void loadImage() {
        Glide.with(this)
                .load(meal.getMealThumbnail())
                .apply(new RequestOptions()
                        .override(200, 200)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_foreground))
                .into(mealImage);
    }

    private void setMealDetails() {
        mealTitle.setText(meal.getMealName());
        mealDesc.setText(meal.getInstructions());
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"add to fav btn",Toast.LENGTH_SHORT).show();
                addToFavMeals(meal);
                }
            });
    }

    public static MealContentFragment getCurrentMeal(Meal meal) {
        MealContentFragment fragment = new MealContentFragment();
        Bundle args = new Bundle();
        args.putSerializable(MEAL_KEY, meal);
        fragment.setArguments(args);
        return fragment;
    }
    private void saveMealToCalendar(String mealTitle, Date selectedDate, String Description) {
        // Convert Date to milliseconds
        long startTime = selectedDate.getTime();

        // Create the intent to insert the event
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, mealTitle)
                .putExtra(CalendarContract.Events.DESCRIPTION,  Description)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startTime)
                .putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        // Check if any app can handle the intent and launch it
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "No calendar app available", Toast.LENGTH_SHORT).show();
        }
    }
    private Date stringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy"); // Define the expected date format
        Date date = null;
        try {
            date = dateFormat.parse(dateString); // Attempt to parse the string into a Date object
        } catch (ParseException e) {
            e.printStackTrace(); // Print stack trace for debugging
        }
        return date; // Return the Date object (or null if parsing failed)
    }
    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.rec2);
    }
    private String getYoutubeVideoId(String url) {
        if (TextUtils.isEmpty(url)) return "";

        Uri uri = Uri.parse(url);
        String videoId = uri.getQueryParameter("v");

        if (videoId == null) {
            videoId = uri.getLastPathSegment();
        }
        return videoId != null ? videoId : "";
    }

    public void addToFavMeals(Meal meal) {
        _repo.insertMeal(meal);
    }

    private List<Ingrediant> getIngList(Meal meal) {
        List<Ingrediant> ingList =  new ArrayList<>();
        for (int i =1 ; i <= 20; i++) {
            try {
                String ingredient = (String) meal.getClass().getMethod("getIngredient" + i).invoke(meal);
                String measure = (String) meal.getClass().getMethod("getMeasure" + i).invoke(meal);
                if (ingredient != null && !ingredient.isEmpty() && measure != null && !measure.isEmpty()) {
                    String imageUrl = "https://www.themealdb.com/images/ingredients/" + ingredient + ".png";
                    ingList.add(new Ingrediant(ingredient, imageUrl,measure));
                    Log.i(TAG, "getIngList: " + ingList);
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e )
            {
                Log.i(TAG, "u in catch RUN ");
                // Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return ingList;
    }

    @Override
    public void showIngData(List<Meal> meal) {

    }

    @Override
    public void showIngErrMsg(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(error).setTitle("An Error Occurred");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onLayoutClick(Meal meal) {
        Toast.makeText(getContext(), meal.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddToFavClick(Meal meal) {
        meal.setFav(true);
        _repo.insertMeal(meal);
    }

    @Override
    public void onCheckClick(Meal meal) {
        _repo.checkFoodExists(meal);
    }

    @Override
    public void onRemoveFavClick(Meal meal) {
        meal.setFav(false);
        _repo.deleteMeal(meal);
    }
}