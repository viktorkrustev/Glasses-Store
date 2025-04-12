package com.example.o4ilastore.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.example.o4ilastore.database.entities.OrderDetails;

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
            if (product.getQuantity() > 0) {
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
        private boolean invalidUser = false;

        public ConfirmOrderTask(String address, String phone) {
            this.address = address;
            this.phone = phone;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            AppDatabase db = AppDatabase.getInstance(CartActivity.this);
            int userId = getCurrentUserId();

            // Проверка за валиден потребител
            if (userId == -1) {
                invalidUser = true;
                Log.e("ConfirmOrderTask", "Невалиден потребител. userId = -1");
                return false;
            }

            // Проверка за наличност
            for (Glasses product : cartProducts) {
                if (product.getQuantity() <= 0) {
                    outOfStock = true;
                    Log.w("ConfirmOrderTask", "Продукт с ID " + product.getId() + " е изчерпан.");
                    return false;
                }
            }

            try {
                // Актуализиране на продуктите (намаляване на quantity и премахване от количка)
                for (Glasses product : cartProducts) {
                    product.setQuantity(product.getQuantity() - 1);
                    product.setInCart(0);
                    db.glassesDao().insert(product);
                }

                // Създаване на нова поръчка
                Order newOrder = new Order();
                newOrder.setUserId(userId);
                newOrder.setDate(getCurrentDate());

                long newOrderId = db.orderDao().insert(newOrder);
                Log.d("ConfirmOrderTask", "Добавена нова поръчка с ID: " + newOrderId);

                // Добавяне на детайли за поръчката
                for (Glasses product : cartProducts) {
                    OrderDetails details = new OrderDetails();
                    details.setOrderId((int) newOrderId);
                    details.setGlassesId(product.getId());
                    db.orderDetailsDao().insert(details);
                    Log.d("ConfirmOrderTask", "Добавен детайл за продукт с ID: " + product.getId());
                }

                return true;

            } catch (Exception e) {
                Log.e("ConfirmOrderTask", "Грешка при запис на поръчка", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CartActivity.this, "Поръчката е потвърдена!", Toast.LENGTH_SHORT).show();
                Log.d("ConfirmOrderTask", "Поръчката е успешно записана.");
                loadCartProducts();
            } else if (invalidUser) {
                Toast.makeText(CartActivity.this, "Грешка: потребител не е влязъл в системата!", Toast.LENGTH_LONG).show();
            } else if (outOfStock) {
                Toast.makeText(CartActivity.this, "Някои продукти са изчерпани! Моля, обновете количката.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CartActivity.this, "Възникна проблем при записа на поръчката!", Toast.LENGTH_LONG).show();
            }
        }

        private String getCurrentDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.format(new Date());
        }

        private int getCurrentUserId() {
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            return prefs.getInt("userId", -1);
        }
    }

}
