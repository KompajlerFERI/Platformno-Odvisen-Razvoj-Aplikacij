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
import kotlin.random.Random


class RestaurantAdapter(
    private val restaurantsList: List<Restaurant>,
    private val itemClickListener: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    private val shimmer = Shimmer.AlphaHighlightBuilder()
        .setDuration(1200)
        .setBaseAlpha(0.9f)
        .setHighlightAlpha(1f)
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

    private val shimmerDrawable = ShimmerDrawable().apply {
        setShimmer(shimmer)
    }

    class ViewHolder(val binding: RestaurantRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RestaurantRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurantsList[position]
        with(holder.binding) {
            Glide.with(holder.itemView.context)
                .load("${BuildConfig.API_URL}${restaurant.photo.URL}")
                .placeholder(shimmerDrawable)
                .error(R.drawable.error)
                .into(ivRestaurantPhoto)

            tvRestaurantName.text = restaurant.name
            tvMealPrice.text = holder.itemView.context.getString(R.string.meal_price, restaurant.mealPrice)
            tvMealSurcharge.text = holder.itemView.context.getString(R.string.meal_surcharge, restaurant.mealSurcharge)
            val restaurantLastCapacity = if (restaurant.lastCapacity == -1) "?" else restaurant.lastCapacity.toString()
            tvRestaurantCapacity.text = holder.itemView.context.getString(R.string.restaurant_capacity, restaurantLastCapacity, restaurant.maxCapacity)

            root.setOnClickListener {
                itemClickListener(restaurant)
            }
        }
    }

    override fun getItemCount() = restaurantsList.size
}