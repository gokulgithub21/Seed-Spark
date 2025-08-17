package com.gokul.vegetable.User;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gokul.vegetable.R;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productPrice, productTotalQuantity, productDescription, productFeatures;
    private Button buttonAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        initializeViews();

        // Get the product ID from the Intent
        String productId = getIntent().getStringExtra("PRODUCT_ID");

        // Fetch and display product details using the product ID
        if (productId != null) {
            fetchProductDetails(productId);
        }

        buttonAddToCart.setOnClickListener(v -> {
            if (productId != null) {
                addToCart(productId);
            }
        });
    }

    private void initializeViews() {
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productTotalQuantity = findViewById(R.id.product_total_quantity);
        productDescription = findViewById(R.id.product_description);
        productFeatures = findViewById(R.id.product_observations);
        buttonAddToCart = findViewById(R.id.button_add_to_cart);
    }

    private void fetchProductDetails(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("VegetableEntry").child(productId);
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("Name").getValue(String.class);
                    String price = snapshot.child("Rate").getValue(String.class);
                    String rateWeight = snapshot.child("RateWeight").getValue(String.class); // Fetch the unit (e.g., 100g)
                    String totalQuantityString = snapshot.child("TotalQuantity").getValue(String.class);
                    String imageUrl = snapshot.child("ImageURl").getValue(String.class);
                    String description = snapshot.child("Description").getValue(String.class);
                    String features = snapshot.child("Features").getValue(String.class);

                    // Handle total quantity
                    updateTotalQuantityUI(totalQuantityString);

                    // Update UI with product details
                    productName.setText(name);

                    // Display price with the correct unit
                    productPrice.setText("Price: " + price + " Rs. "+" / " + rateWeight);

                    productDescription.setText(formatAsBulletPoints(description));
                    productFeatures.setText(formatAsBulletPoints(features));

                    // Use Picasso to load the image
                    Picasso.get().load(imageUrl).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("ProductDetailActivity", "Error fetching product details", error.toException());
            }
        });
    }

    private void updateTotalQuantityUI(String totalQuantityString) {
        double totalQuantity = Double.parseDouble(totalQuantityString);
        if (totalQuantity <= 0) {
            productTotalQuantity.setText("Out of Stock");
            productTotalQuantity.setTextColor(ContextCompat.getColor(this, R.color.stock_out)); // Change text color
            buttonAddToCart.setEnabled(false); // Disable the button
            buttonAddToCart.setBackgroundColor(ContextCompat.getColor(this, R.color.button_disabled)); // Change button color to disabled
        } else {
            productTotalQuantity.setText("Available Quantity: " + totalQuantityString);
            productTotalQuantity.setTextColor(ContextCompat.getColor(this, R.color.stock_available)); // Change text color
            buttonAddToCart.setEnabled(true); // Enable the button
            buttonAddToCart.setBackgroundColor(ContextCompat.getColor(this, R.color.button_enabled)); // Change button color to enabled
        }
    }


    private void addToCart(String productId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId).child(productId);

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("VegetableEntry").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imageUrl = snapshot.child("ImageURl").getValue(String.class);
                    String name = snapshot.child("Name").getValue(String.class);
                    String price = snapshot.child("Rate").getValue(String.class);
                    String quantity = snapshot.child("RateWeight").getValue(String.class);
                    String totalQuantityString = snapshot.child("TotalQuantity").getValue(String.class);

                    // Check if there's sufficient quantity available
                    double totalQuantity = Double.parseDouble(totalQuantityString);
                    if (totalQuantity <= 0) {
                        Toast.makeText(ProductDetailActivity.this, "Out of Stock", Toast.LENGTH_SHORT).show();
                        return; // Exit the method if out of stock
                    }

                    // Add to cart if in stock
                    cartRef.child("CardID").setValue(productId);
                    cartRef.child("CartImageURl").setValue(imageUrl);
                    cartRef.child("CartName").setValue(name);
                    cartRef.child("CartPrice").setValue(price);
                    cartRef.child("CartQuantity").setValue(quantity);

                    Toast.makeText(ProductDetailActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("ProductDetailActivity", "Error adding to cart", error.toException());
            }
        });
    }

    private String formatAsBulletPoints(String text) {
        if (text == null || text.isEmpty()) {
            return "No details available.";
        }
        // Replace any literal '\n' characters with a space and format as single-dot bullet points
        text = text.replace("\\n", " "); // This removes any "\n" found in the string
        String[] lines = text.split("\\.");
        StringBuilder formattedText = new StringBuilder();
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                formattedText.append("• ").append(line).append(".\n"); // Append a single dot at the end
            }
        }
        return formattedText.toString().trim(); // Remove trailing newline
    }

}
