package com.example.o4ilastore.activities;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.o4ilastore.R;
import com.example.o4ilastore.adapters.CartAdapter;
import com.example.o4ilastore.database.AppDatabase;
import com.example.o4ilastore.database.entities.Glasses;
import com.example.o4ilastore.database.entities.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private List<Glasses> cartProducts;
    private TextView totalAmountTextView;
    private Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        buyButton = findViewById(R.id.buyButton);

        loadCartProducts();

        buyButton.setOnClickListener(v -> showDeliveryDialog());
    }

    void loadCartProducts() {
        new LoadCartProductsTask().execute();
    }

    private class LoadCartProductsTask extends AsyncTask<Void, Void, List<Glasses>> {
        @Override
        protected List<Glasses> doInBackground(Void... voids) {
            AppDatabase db = AppDatabase.getInstance(CartActivity.this);
            return db.glassesDao().getCartItems();
        }

        @Override
        protected void onPostExecute(List<Glasses> glasses) {
            cartProducts = glasses;
            cartAdapter = new CartAdapter(CartActivity.this, cartProducts,
                    () -> updateTotalAmount(),
                    product -> removeProductFromCart(product)
            );
            recyclerViewCart.setAdapter(cartAdapter);
            updateTotalAmount();
        }
    }

    private void removeProductFromCart(Glasses product) {
        product.setInCart(0);
        new UpdateProductTask().execute(product);
    }

    private class UpdateProductTask extends AsyncTask<Glasses, Void, Void> {
        @Override
        protected Void doInBackground(Glasses... glasses) {
            AppDatabase db = AppDatabase.getInstance(CartActivity.this);
            db.glassesDao().insert(glasses[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadCartProducts();
        }
    }

    private void updateTotalAmount() {
        double total = 0;
        for (Glasses product : cartProducts) {
            if (product.getQuantity() > 0) {  // Включваме само наличните продукти
                total += product.getPrice();
            }
        }
        totalAmountTextView.setText(String.format("Обща сума: %.2f лв.", total));
    }

    private void showDeliveryDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_delivery_form, null);
        EditText addressEditText = view.findViewById(R.id.addressEditText);
        EditText phoneEditText = view.findViewById(R.id.phoneEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Данни за доставка")
                .setView(view)
                .setPositiveButton("Потвърди", (dialog, which) -> {
                    String address = addressEditText.getText().toString().trim();
                    String phone = phoneEditText.getText().toString().trim();

                    if (address.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(CartActivity.this, "Моля, попълнете всички полета!", Toast.LENGTH_SHORT).show();
                    } else {
                        new ConfirmOrderTask(address, phone).execute();
                    }
                })
                .setNegativeButton("Отказ", null)
                .create()
                .show();
    }

    private class ConfirmOrderTask extends AsyncTask<Void, Void, Boolean> {
        private final String address, phone;
        private boolean outOfStock = false;

        public ConfirmOrderTask(String address, String phone) {
            this.address = address;
            this.phone = phone;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            AppDatabase db = AppDatabase.getInstance(CartActivity.this);

            // Проверка за изчерпани продукти
            for (Glasses product : cartProducts) {
                if (product.getQuantity() <= 0) {
                    outOfStock = true;
                    return false; // Спира ако има изчерпан продукт
                }
            }

            // Намаляване на количеството и премахване от количката
            for (Glasses product : cartProducts) {
                int newStock = product.getQuantity() - 1;
                product.setQuantity(newStock);
                product.setInCart(0); // Премахване от количката
                db.glassesDao().insert(product);  // Записваме актуализирания продукт в базата
            }

            // Създаване на нова поръчка и добавяне в базата
            Order newOrder = new Order();
            newOrder.userId = getCurrentUserId(); // Добавяме идентификатора на текущия потребител
            newOrder.date = getCurrentDate();     // Може да използваш актуалната дата

            db.orderDao().insert(newOrder);  // Добавяме поръчката в базата данни

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CartActivity.this, "Поръчката е потвърдена!", Toast.LENGTH_SHORT).show();
                loadCartProducts();
            } else if (outOfStock) {
                Toast.makeText(CartActivity.this, "Някои продукти са изчерпани! Моля, обновете количката.", Toast.LENGTH_LONG).show();
            }
        }

        private String getCurrentDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.format(new Date()); // Връща текущата дата
        }

        private int getCurrentUserId() {
            // Тук можете да използвате SharedPreferences или някакъв друг начин за получаване на идентификатора на потребителя
            return 1; // Например, твърдо зададен id (зависимост от Вашето приложение)
        }
    }
}
