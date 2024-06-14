package com.example.tunisiepromoclient;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Bind data to views
        holder.productID.setText(product.getProductId());
        holder.productName.setText(product.getName());
        SpannableString old= new SpannableString(String.valueOf(product.getPriceOld())+" DT");
        old.setSpan(new StrikethroughSpan(), 0, old.length(), 0);
        holder.productPriceOld.setText(old);
        holder.productPriceNew.setText(String.valueOf(product.getPriceNew())+" DT TTC");

        // Use Glide to load the image from the URL
        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productID;
        TextView productName;
        TextView productPriceOld;
        TextView productPriceNew;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productID = itemView.findViewById(R.id.TextViewProductID);
            productImage = itemView.findViewById(R.id.imageViewProduct);
            productName = itemView.findViewById(R.id.textViewProductName);
            productPriceOld = itemView.findViewById(R.id.textViewProductPriceOld);
            productPriceNew = itemView.findViewById(R.id.textViewProductPriceNew);
        }
    }
}
