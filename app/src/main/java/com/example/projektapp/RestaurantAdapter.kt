package com.example.projektapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.lib.Restaurant
import com.example.projektapp.databinding.RestaurantRowBinding
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable


class RestaurantAdapter(
    private val restaurantsList: List<Restaurant>,
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    private val shimmer = Shimmer.AlphaHighlightBuilder()
        .setDuration(1200) // how long the shimmering animation takes to do one full sweep
        .setBaseAlpha(0.9f) // alpha of background (light gray)
        .setHighlightAlpha(1f) // alpha of shimmer (white)
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

    // P L A C E H O L D E R   F O R   I M A G E V I E W
    private val shimmerDrawable = ShimmerDrawable().apply {
        setShimmer(shimmer)
    }

    class ViewHolder(binding: RestaurantRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivRestaurantPhoto = binding.ivRestaurantPhoto
        val tvRestaurantName = binding.tvRestaurantName
        val tvMealPrice = binding.tvMealPrice
        val tvMealSurcharge = binding.tvMealSurcharge
        val tvRestaurantCapacity = binding.tvRestaurantCapacity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RestaurantRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurantsList[position]

        // Load the new image
        Glide.with(holder.itemView.context)
            .load("${BuildConfig.API_URL}${restaurant.photo.URL}")
            .placeholder(shimmerDrawable)
            .error(R.drawable.error)
            .into(holder.ivRestaurantPhoto)
        holder.tvRestaurantName.text = restaurant.name
        holder.tvMealPrice.text = holder.itemView.context.getString(R.string.meal_price, restaurant.mealPrice)
        holder.tvMealSurcharge.text = holder.itemView.context.getString(R.string.meal_surcharge, restaurant.mealSurcharge)
        holder.tvRestaurantCapacity.text = holder.itemView.context.getString(R.string.restaurant_capacity, restaurant.lastCapacity, restaurant.maxCapacity)
    }

    override fun getItemCount() = restaurantsList.size
}