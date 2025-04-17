package com.example.o4ilastore.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.o4ilastore.R;
import com.example.o4ilastore.activities.ProductDetailActivity;
import com.example.o4ilastore.database.AppDatabase;
import com.example.o4ilastore.database.entities.CartItem;
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

    public void updateData(List<Glasses> newProducts) {
        this.products.clear();
        this.products.addAll(newProducts);
        notifyDataSetChanged();
    }



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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.id);
            context.startActivity(intent);
        });

        holder.addToCartButton.setOnClickListener(v -> {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(context);

                SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                int userId = prefs.getInt("userId", -1);

                if (userId == -1) {
                    ((Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Моля, влезте в профила си!", Toast.LENGTH_SHORT).show());
                    return;
                }

                CartItem cartItem = new CartItem();
                cartItem.setUserId(userId);
                cartItem.setGlassesId(product.id);
                db.cartItemDao().insert(cartItem);

                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Добавено в количката!", Toast.LENGTH_SHORT).show());
            }).start();
        });

        String imageUrl = resolveImageUrl(product.getImageUrl());
        Log.d("ProductAdapter", "Final image URL: " + imageUrl);

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.photo1)
                .error(R.drawable.photo3)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private String resolveImageUrl(String originalUrl) {
        if (originalUrl == null) return null;

        if (originalUrl.contains("drive.google.com")) {
            try {
                String id = originalUrl.split("/d/")[1].split("/")[0];
                return "https://drive.google.com/uc?export=download&id=" + id;
            } catch (Exception e) {
                Log.e("ProductAdapter", "Invalid Google Drive URL: " + originalUrl);
                return null;
            }
        }

        if (originalUrl.startsWith("http://") || originalUrl.startsWith("https://")) {
            return originalUrl;
        }

        return "file:///android_asset/" + originalUrl;
    }
}
