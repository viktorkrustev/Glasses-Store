package com.example.o4ilastore.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.o4ilastore.R;
import com.example.o4ilastore.activities.ProductDetailActivity;
import com.example.o4ilastore.database.AppDatabase;
import com.example.o4ilastore.database.entities.Glasses;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Glasses> products;
    private final Context context;

    public ProductAdapter(Context context, List<Glasses> products) {
        this.context = context;
        this.products = products;
    }

    // Method to update the data in the adapter
    public void updateData(List<Glasses> newProducts) {
        this.products = new ArrayList<>(newProducts);
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameView, priceView;
        Button addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImage);
            nameView = itemView.findViewById(R.id.productName);
            priceView = itemView.findViewById(R.id.productPrice);
            addToCartButton = itemView.findViewById(R.id.btnAddToCart);
        }
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Glasses product = products.get(position);

        holder.nameView.setText(product.name);
        holder.priceView.setText(String.format("%.2f лв", product.price));

        // Set click listener for opening product details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.id);
            context.startActivity(intent);
        });

        // Set click listener for "Add to Cart"
        holder.addToCartButton.setOnClickListener(v -> {
            product.setInCart(1); // Маркираме го, че е в количката

            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(context);
                db.glassesDao().insert(product); // Запис в базата

                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Добавено в количката!", Toast.LENGTH_SHORT).show()
                );
            }).start();
        });

        // Load product image from assets
        String assetPath = "file:///android_asset/" + product.imageUrl;
        Glide.with(context)
                .load(assetPath)
                .placeholder(R.drawable.photo1)
                .error(R.drawable.photo2)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
