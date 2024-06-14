package com.example.tunisiepromoclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.ProductViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private List<publication> pubList;
    private Context context;


    public PublicationAdapter(List<publication> pubList, Context context,RecyclerViewInterface recyclerViewInterface) {
        this.pubList = pubList;
        this.context = context;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pub, parent, false);
        return new ProductViewHolder(view,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        publication pub = pubList.get(position);

        // Bind data to views
        holder.pubCname.setText(pub.getcName());
        holder.promoName.setText(pub.getPromoName());
        holder.pubDated.setText("Start Date: "+String.valueOf(pub.getDateDebut()));
        holder.pubDatef.setText("End Date: "+String.valueOf(pub.getDateFin()));


        // Use Glide to load the image from the URL
        Glide.with(context)
                .load(pub.getImageUrl())
                .into(holder.pubImage);
    }

    @Override
    public int getItemCount() {
        return pubList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView pubImage;
        TextView pubCname;
        TextView pubDated;
        TextView pubDatef;
        TextView promoName;

        public ProductViewHolder(@NonNull View itemView,RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            pubImage = itemView.findViewById(R.id.imageViewProduct);
            pubCname = itemView.findViewById(R.id.textViewProductName);
            pubDated = itemView.findViewById(R.id.textViewPubDated);
            pubDatef=itemView.findViewById(R.id.textViewPubDatef);
            promoName=itemView.findViewById(R.id.textViewPromoName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if(recyclerViewInterface!= null){
                       int position=getAdapterPosition();
                       if (position!=RecyclerView.NO_POSITION){
                           recyclerViewInterface.onItemClick(position);
                       }
                   }
                }
            });
        }
    }
}