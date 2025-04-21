package com.example.o4ilastore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.o4ilastore.R;
import com.example.o4ilastore.database.entities.Glasses;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Glasses> cartProducts;
    private UpdateTotalAmountListener updateTotalAmountListener;
    private RemoveProductListener removeProductListener;

    public CartAdapter(Context context, List<Glasses> cartProducts,
                       UpdateTotalAmountListener updateTotalAmountListener,
                       RemoveProductListener removeProductListener) {
        this.context = context;
        this.cartProducts = cartProducts;
        this.updateTotalAmountListener = updateTotalAmountListener;
        this.removeProductListener = removeProductListener;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        Glasses product = cartProducts.get(position);

        if (product.getQuantity() == 0) {
            holder.nameTextView.setText(product.getName() + " (Изчерпан!)");
            holder.removeButton.setEnabled(true);
            holder.removeButton.setText("Премахни");
        } else {
            holder.nameTextView.setText(product.getName());
            holder.removeButton.setEnabled(true);
            holder.removeButton.setText("Премахни");
        }

        holder.priceTextView.setText(String.format("%.2f лв", product.getPrice()));

        String assetPath = "file:///android_asset/" + product.getImageUrl();
        Glide.with(context)
                .load(assetPath)
                .placeholder(R.drawable.photo1)
                .error(R.drawable.photo3)
                .into(holder.imageView);

        holder.removeButton.setOnClickListener(v -> {
            removeProductListener.onRemoveProduct(product);
            updateTotalAmountListener.onUpdateTotalAmount();
        });
    }


    @Override
    public int getItemCount() {
        return cartProducts.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView;
        Button removeButton;
        ImageView imageView;

        public CartViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public interface UpdateTotalAmountListener {
        void onUpdateTotalAmount();
    }

    public interface RemoveProductListener {
        void onRemoveProduct(Glasses product);
    }
}
