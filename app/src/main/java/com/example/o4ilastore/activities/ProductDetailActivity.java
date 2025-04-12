package com.example.o4ilastore.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.o4ilastore.R;
import com.example.o4ilastore.database.AppDatabase;
import com.example.o4ilastore.database.entities.Glasses;
import com.example.o4ilastore.database.entities.Review;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView nameView, priceView, descriptionView, manufacturerView;
    private EditText reviewInput;
    private Button btnSubmitReview, btnEditProduct, btnAddToCart;
    private LinearLayout reviewsContainer;
    private RatingBar ratingBar;

    private AppDatabase db;
    private int productId;
    private boolean isAdmin = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Toolbar стрелка
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Детайли за продукта");
        }

        imageView = findViewById(R.id.imageViewDetail);
        nameView = findViewById(R.id.textName);
        priceView = findViewById(R.id.textPrice);
        descriptionView = findViewById(R.id.textDescription);
        manufacturerView = findViewById(R.id.textManufacturer);
        reviewInput = findViewById(R.id.editReview);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        reviewsContainer = findViewById(R.id.reviewsContainer);
        ratingBar = findViewById(R.id.ratingBar);
        btnEditProduct = findViewById(R.id.btnEditProduct);
        btnAddToCart = findViewById(R.id.btnAddToCart); // ⬅️ Нов бутон

        db = AppDatabase.getInstance(this);
        productId = getIntent().getIntExtra("product_id", -1);
        isAdmin = getIntent().getBooleanExtra("admin", false);

        if (isAdmin) {
            btnEditProduct.setVisibility(View.VISIBLE);
        }

        loadProductDetails();
        loadReviews();

        btnSubmitReview.setOnClickListener(v -> {
            String comment = reviewInput.getText().toString();
            int rating = (int) ratingBar.getRating();
            if (!comment.isEmpty()) {
                addReview(comment, rating);
            }
        });

        btnEditProduct.setOnClickListener(v -> enableEditing());

        btnAddToCart.setOnClickListener(v -> addToCart()); // ⬅️ Слушател за бутона
    }

    public void onBackPressed(View view) {
        finish();
    }

    private void enableEditing() {
        nameView.setOnClickListener(v -> editField("name", nameView.getText().toString()));
        priceView.setOnClickListener(v -> editField("price", priceView.getText().toString()));
        descriptionView.setOnClickListener(v -> editField("description", descriptionView.getText().toString()));
    }

    private void editField(String fieldName, String currentValue) {
        EditText input = new EditText(this);
        input.setText(currentValue);

        new AlertDialog.Builder(this)
                .setTitle("Редактирай " + fieldName)
                .setView(input)
                .setPositiveButton("Запази", (dialog, which) -> {
                    String newValue = input.getText().toString();
                    new Thread(() -> {
                        Glasses product = db.glassesDao().getById(productId);
                        switch (fieldName) {
                            case "name":
                                product.setName(newValue);
                                break;
                            case "price":
                                try {
                                    product.setPrice(Double.parseDouble(newValue.replace(",", ".")));
                                } catch (NumberFormatException e) {
                                    runOnUiThread(() -> Toast.makeText(this, "Невалидна цена", Toast.LENGTH_SHORT).show());
                                    return;
                                }
                                break;
                            case "description":
                                product.setDescription(newValue);
                                break;
                        }
                        db.glassesDao().update(product);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Записано", Toast.LENGTH_SHORT).show();
                            loadProductDetails();
                        });
                    }).start();
                })
                .setNegativeButton("Отказ", null)
                .show();
    }

    private void loadProductDetails() {
        new Thread(() -> {
            Glasses product = db.glassesDao().getById(productId);
            String manufacturer = db.manufacturerDao().getById(product.getManufacturerId()).getName();

            runOnUiThread(() -> {
                nameView.setText(product.getName());
                priceView.setText(String.format("%.2f лв", product.getPrice()));
                descriptionView.setText(product.getDescription());
                manufacturerView.setText("Производител: " + manufacturer);

                String assetPath = "file:///android_asset/" + product.getImageUrl();
                Glide.with(this)
                        .load(assetPath)
                        .into(imageView);

                if (product.getInCart() == 1) {
                    btnAddToCart.setEnabled(false);
                    btnAddToCart.setText("Вече в количката");
                }
            });
        }).start();
    }

    private void loadReviews() {
        new Thread(() -> {
            List<Review> reviews = db.reviewDao().getForProduct(productId);
            runOnUiThread(() -> {
                reviewsContainer.removeAllViews();
                for (Review r : reviews) {
                    TextView reviewView = new TextView(this);
                    String ratingStars = "⭐".repeat(Math.max(1, r.getRating()));
                    reviewView.setText(ratingStars + "\n" + r.getText());
                    reviewView.setPadding(12, 8, 12, 8);
                    reviewsContainer.addView(reviewView);
                }
            });
        }).start();
    }

    private void addReview(String comment, int rating) {
        Review review = new Review(productId, comment, rating);
        new Thread(() -> {
            db.reviewDao().insert(review);
            runOnUiThread(() -> {
                reviewInput.setText("");
                ratingBar.setRating(5);
                loadReviews();
                Toast.makeText(this, "Благодарим за отзива!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void addToCart() {
        new Thread(() -> {
            Glasses product = db.glassesDao().getById(productId);
            product.setInCart(1); // ⬅️ Маркира като в количката
            db.glassesDao().insert(product);

            runOnUiThread(() -> {
                Toast.makeText(this, "Продуктът е добавен в количката", Toast.LENGTH_SHORT).show();
                btnAddToCart.setEnabled(false);
                btnAddToCart.setText("Вече в количката");
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
