package com.example.o4ilastore.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.o4ilastore.R;
import com.example.o4ilastore.adapters.ProductAdapter;
import com.example.o4ilastore.database.AppDatabase;
import com.example.o4ilastore.database.entities.Glasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private Button btnFilter, btnSort, btnNext, btnPrevious;
    private EditText searchEditText;
    private TextView pageIndicator;

    private AppDatabase db;
    private List<Glasses> allProducts = new ArrayList<>();
    private List<Glasses> filteredProducts = new ArrayList<>();
    private List<Glasses> currentPageProducts = new ArrayList<>();

    private boolean isFiltered = false;
    private int currentPage = 1;
    private int itemsPerPage = 6;
    private int totalPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        db = AppDatabase.getInstance(this);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        btnFilter = findViewById(R.id.btnFilter);
        btnSort = findViewById(R.id.btnSort);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        pageIndicator = findViewById(R.id.pageIndicator);
        searchEditText = findViewById(R.id.searchEditText);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, currentPageProducts);
        recyclerView.setAdapter(productAdapter);

        loadProducts();

        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnCart = findViewById(R.id.btnCart);

        btnBack.setOnClickListener(v -> finish());

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, CartActivity.class);
            startActivity(intent);
        });

        btnFilter.setOnClickListener(v -> showFilterDialog());
        btnSort.setOnClickListener(v -> showSortDialog());

        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                updateProductsPage();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updateProductsPage();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProductsBySearch(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProducts() {
        new Thread(() -> {
            try {
                allProducts = db.glassesDao().getAll();
                runOnUiThread(() -> {
                    updateProductsPage();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "ГРЕШКА при зареждане на продукти: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void updateProductsPage() {
        List<Glasses> source = isFiltered ? filteredProducts : allProducts;
        if (source == null || source.isEmpty()) return;

        totalPages = (int) Math.ceil((double) source.size() / itemsPerPage);
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;

        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, source.size());

        currentPageProducts.clear();
        currentPageProducts.addAll(source.subList(start, end));
        productAdapter.notifyDataSetChanged();

        pageIndicator.setText("Страница " + currentPage);
        btnPrevious.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Филтриране на продукти");

        Spinner categorySpinner = new Spinner(this);
        List<String> categories = new ArrayList<>();
        categories.add("Мъжки");
        categories.add("Женски");
        categories.add("Детски");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        Spinner formSpinner = new Spinner(this);
        List<String> forms = new ArrayList<>();
        forms.add("Авиатор");
        forms.add("Квадратни");
        forms.add("Кръгли");
        forms.add("Диоптрични");
        ArrayAdapter<String> formAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, forms);
        formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formSpinner.setAdapter(formAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                formSpinner.setEnabled(!categorySpinner.getSelectedItem().equals("Детски"));
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(categorySpinner);
        layout.addView(formSpinner);
        builder.setView(layout);

        builder.setPositiveButton("Филтрирай", (dialog, which) -> {
            String selectedCategory = (String) categorySpinner.getSelectedItem();
            String selectedForm = (String) formSpinner.getSelectedItem();
            filterProducts(selectedCategory, selectedForm);
        });

        builder.setNegativeButton("Отказ", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void filterProducts(String category, String form) {
        filteredProducts.clear();
        int categoryId = -1;

        switch (category) {
            case "Мъжки": categoryId = 1; break;
            case "Женски": categoryId = 2; break;
            case "Детски": categoryId = 3; break;
        }

        for (Glasses glasses : allProducts) {
            boolean categoryMatches = glasses.getCategoryId() == categoryId;
            boolean formMatches = true;

            if (categoryId != 3) {
                formMatches = glasses.getForm().equalsIgnoreCase(form);
            }

            if (categoryMatches && formMatches) {
                filteredProducts.add(glasses);
            }
        }

        isFiltered = true;
        currentPage = 1;
        updateProductsPage();
    }

    private void filterProductsBySearch(String query) {
        filteredProducts.clear();
        for (Glasses glasses : allProducts) {
            if (glasses.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredProducts.add(glasses);
            }
        }
        isFiltered = true;
        currentPage = 1;
        updateProductsPage();
    }

    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Сортиране на продукти");

        Spinner sortSpinner = new Spinner(this);
        List<String> sortOptions = new ArrayList<>();
        sortOptions.add("По име (А - Я)");
        sortOptions.add("По цена (нис. -> вис.)");
        sortOptions.add("По цена (вис. -> нис.)");

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(sortSpinner);
        builder.setView(layout);

        builder.setPositiveButton("Сортирай", (dialog, which) -> {
            String selectedSortOption = (String) sortSpinner.getSelectedItem();
            sortProducts(selectedSortOption);
        });

        builder.setNegativeButton("Отказ", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void sortProducts(String sortOption) {
        List<Glasses> source = isFiltered ? filteredProducts : allProducts;

        switch (sortOption) {
            case "По име (А - Я)":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(source, Comparator.comparing(Glasses::getName, String.CASE_INSENSITIVE_ORDER));
                }
                break;
            case "По цена (нис. -> вис.)":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(source, Comparator.comparingDouble(Glasses::getPrice));
                }
                break;
            case "По цена (вис. -> нис.)":
                Collections.sort(source, (g1, g2) -> Double.compare(g2.getPrice(), g1.getPrice()));
                break;
        }

        currentPage = 1;
        updateProductsPage();
    }
}
