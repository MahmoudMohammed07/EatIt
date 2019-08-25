package com.android.eatit;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.eatit.Common.Common;
import com.android.eatit.Database.Database;
import com.android.eatit.Model.Food;
import com.android.eatit.Model.Order;
import com.android.eatit.Model.Rating;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView foodName, foodPrice, foodDescription;
    ImageView foodImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fabCart, fabRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;

    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTable;

    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        ratingTable = database.getReference("Rating");

        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        fabCart = (FloatingActionButton) findViewById(R.id.fab_cart);
        fabRating = (FloatingActionButton) findViewById(R.id.fab_Rating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        fabRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));
                Toast.makeText(FoodDetail.this, "Added To Cart", Toast.LENGTH_SHORT).show();
            }
        });

        foodDescription = (TextView) findViewById(R.id.food_description);
        foodName = (TextView) findViewById(R.id.food_name);
        foodPrice = (TextView) findViewById(R.id.food_price);
        foodImage = (ImageView) findViewById(R.id.img_food);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()) {
            if (Common.isConnectedToInternet(getBaseContext())) {
                getDetailFood(foodId);
                getRatingFood(foodId);
            } else {
                Toast.makeText(FoodDetail.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    private void getRatingFood(String foodId) {
        Query foodRating = ratingTable.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite Ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage())
                        .into(foodImage);

                collapsingToolbarLayout.setTitle(currentFood.getName());

                foodPrice.setText(currentFood.getPrice());

                foodName.setText(currentFood.getName());

                foodDescription.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comments) {
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                foodId, String.valueOf(value), comments);
        ratingTable.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists()) {
                    //Remove old value
                    ratingTable.child(Common.currentUser.getPhone()).removeValue();
                    //Update new value
                    ratingTable.child(Common.currentUser.getPhone()).setValue(rating);
                } else {
                    //Update new value
                    ratingTable.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(FoodDetail.this, "Thank you for submit rating", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
