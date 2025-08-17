package com.gokul.vegetable.Utilities.AdminHome;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gokul.vegetable.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Holder extends RecyclerView.ViewHolder {

    // Home Page Holder Components...
    public TextView textViewDate, textViewName, textViewPrice, textViewQuanty, textViewTotalQuanty;
    public CircleImageView circleImageViewHome;

    // Cart page Holder Components...
    public ImageView imageViewMinusCart, imageViewPlusCart, imageViewCheck;
    public TextView textViewTitleCart, textViewPriceCart, textViewWeightCart, textViewUserQuantityCard, textViewfrom_end, cart_totalRate;

    // Cart History Item Recy..
    public TextView CartItemName, CartItemWeight, CartItemRate;

    public Holder(@NonNull View itemView) {
        super(itemView);

        this.textViewName = itemView.findViewById(R.id.Title_card);
        this.textViewPrice = itemView.findViewById(R.id.Price_card);
        this.textViewDate = itemView.findViewById(R.id.Date_card);
        this.textViewQuanty = itemView.findViewById(R.id.Quantity_card);
        this.textViewTotalQuanty = itemView.findViewById(R.id.TotalQuantity_card);

        this.circleImageViewHome = itemView.findViewById(R.id.imageViewHome);

        this.imageViewMinusCart = itemView.findViewById(R.id.cart_minus_img);
        this.imageViewPlusCart = itemView.findViewById(R.id.cart_plus_img);

        // Cart
        this.imageViewCheck = itemView.findViewById(R.id.action_check);
        this.cart_totalRate = itemView.findViewById(R.id.cart_totalRate);
        this.textViewTitleCart = itemView.findViewById(R.id.from_name);
        this.textViewfrom_end = itemView.findViewById(R.id.from_end);
        this.textViewPriceCart = itemView.findViewById(R.id.plist_price_text);
        this.textViewWeightCart = itemView.findViewById(R.id.plist_weight_text);
        this.textViewUserQuantityCard = itemView.findViewById(R.id.cart_product_quantity);

        // Cart History Item Recy..
        this.CartItemName = itemView.findViewById(R.id.cartHisItemNameUser);
        this.CartItemWeight = itemView.findViewById(R.id.cartHisItemWeightUser);
        this.CartItemRate = itemView.findViewById(R.id.cartHisRateUser);
    }

    // Home Section
    public void setTxtName(String name) {
        textViewName.setText(name);
    }

    public void setTxtPrice(String price) {
        textViewPrice.setText("Rs " + price);
    }

    public void setTxtUserRate(String price, String quantity) {
        textViewPrice.setText("Rs " + price + " / " + quantity);
    }

    public void setTxtDate(String date) {
        textViewDate.setText(date);
    }

    public void setTxtQuantity(String quantity) {
        textViewQuanty.setText("Per " + quantity);
    }

    public void setTxtTotalQuantity(String quantity, String weight) {
        textViewTotalQuanty.setText(quantity + " (" + weight + ")");
    }

    public void setTxtTotalQuantity(String status) {
        textViewTotalQuanty.setText(status);
        textViewTotalQuanty.setTextColor(itemView.getContext().getResources().getColor(R.color.red)); // Set color for "Out of Stock"
    }

    public void setImgURL(String url) {
        Picasso.get().load(url).resize(500, 500).centerCrop().into(circleImageViewHome);
    }

    // Cart Section
    public void setTxtTotalRate(String rate) {
        cart_totalRate.setText(rate);
    }

    public void setTxtTitleCart(String title) {
        textViewTitleCart.setText(title);
    }

    public void setTxtRateCart(String rate) {
        textViewPriceCart.setText("Rs. " + rate);
    }

    public void setTxtWeightCart(String weight) {
        textViewWeightCart.setText("/" + weight);
    }

    public void setTxtUserQuantCart(String quantity) {
        textViewUserQuantityCard.setText(quantity);
    }

    public void setTxtFromEndQuantity(String quantity, String weight) {
        textViewfrom_end.setText("Available Qty:"+ " " +quantity+ " " +" / "+ weight);
    }

    public void setTxtUserItemNameCart(String name) {
        CartItemName.setText(name);
    }

    public void setTxtUserItemWeightCart(String weight) {
        CartItemWeight.setText(weight);
    }

    public void setTxtUserItemRateCart(String rate) {
        CartItemRate.setText(rate + "Rs");
    }
}
