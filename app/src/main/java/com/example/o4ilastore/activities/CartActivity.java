package com.example.o4ilastore.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.o4ilastore.R;
import com.example.o4ilastore.adapters.CartAdapter;
import com.example.o4ilastore.database.AppDatabase;

import com.example.o4ilastore.database.entities.Glasses;
import com.example.o4ilastore.database.entities.Order;
import com.example.o4ilastore.database.entities.OrderDetails;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
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
    private int userId;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private boolean shouldShowDeliveryDialog = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Моля, влезте в профила си, за да направите поръчка.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

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
            return db.cartItemDao().getGlassesInCart(userId);
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

    @SuppressLint("StaticFieldLeak")
    private void removeProductFromCart(Glasses product) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase.getInstance(CartActivity.this)
                        .cartItemDao().deleteItem(userId, product.getId());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                loadCartProducts();
            }
        }.execute();
    }

    private void updateTotalAmount() {
        double total = 0;
        for (Glasses product : cartProducts) {
            total += product.getPrice();
        }
        totalAmountTextView.setText(String.format("Обща сума: %.2f лв.", total));
    }

    private void showDeliveryDialog() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            shouldShowDeliveryDialog = true;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        View view = getLayoutInflater().inflate(R.layout.dialog_delivery_form, null);
        EditText addressEditText = view.findViewById(R.id.addressEditText);
        EditText phoneEditText = view.findViewById(R.id.phoneEditText);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Данни за доставка")
                .setView(view)
                .setPositiveButton("Потвърди", (d, which) -> {
                    String address = addressEditText.getText().toString().trim();
                    String phone = phoneEditText.getText().toString().trim();

                    if (address.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(this, "Моля, попълнете всички полета!", Toast.LENGTH_SHORT).show();
                    } else {
                        new ConfirmOrderTask(address, phone).execute();
                    }
                })
                .setNegativeButton("Отказ", null)
                .create();

        dialog.show();

        getLastKnownLocation(addressEditText);
    }



    private void getLastKnownLocation(EditText addressEditText) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                addressEditText.setText(address.getLocality() != null ? address.getLocality() : "Не може да се определи местоположение");
                            } else {
                                addressEditText.setText("Не може да се определи местоположение");
                            }
                        } catch (IOException e) {
                            Toast.makeText(this, "Грешка при получаване на местоположение", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Не може да се получи местоположение", Toast.LENGTH_SHORT).show();
                        addressEditText.setText("Не може да се определи местоположение");
                    }
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(this, "Грешка при заявка на местоположение", Toast.LENGTH_SHORT).show();
                    addressEditText.setText("Не може да се определи местоположение");
                });
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

            for (Glasses product : cartProducts) {
                if (product.getQuantity() <= 0) {
                    outOfStock = true;
                    return false;
                }
            }

            try {
                for (Glasses product : cartProducts) {
                    product.setQuantity(product.getQuantity() - 1);
                    db.glassesDao().insert(product);
                }

                Order newOrder = new Order();
                newOrder.setUserId(userId);
                newOrder.setDate(getCurrentDate());

                long newOrderId = db.orderDao().insert(newOrder);

                for (Glasses product : cartProducts) {
                    OrderDetails details = new OrderDetails();
                    details.setOrderId((int) newOrderId);
                    details.setGlassesId(product.getId());
                    db.orderDetailsDao().insert(details);
                }

                db.cartItemDao().clearCart(userId);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CartActivity.this, "Поръчката е потвърдена!", Toast.LENGTH_SHORT).show();
                loadCartProducts();
            } else if (outOfStock) {
                Toast.makeText(CartActivity.this, "Някои продукти са изчерпани!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CartActivity.this, "Възникна проблем при поръчката!", Toast.LENGTH_LONG).show();
            }
        }

        private String getCurrentDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.format(new Date());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (shouldShowDeliveryDialog) {
                    showDeliveryDialog();
                    shouldShowDeliveryDialog = false;
                }
            } else {
                Toast.makeText(this, "Няма разрешение за използване на местоположение.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
