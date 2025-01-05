package com.example.projektapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lib.Restaurant
import com.example.projektapp.databinding.RestaurantRowBinding

class RestaurantAdapter(
    private val restaurantsList: List<Restaurant>,
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

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

        holder.ivRestaurantPhoto.setImageResource(R.drawable.ic_launcher_background) // TODO: replace with restaurants image
        holder.tvRestaurantName.text = restaurant.name
        holder.tvMealPrice.text = holder.itemView.context.getString(R.string.meal_price, restaurant.mealPrice)
        holder.tvMealSurcharge.text = holder.itemView.context.getString(R.string.meal_surcharge, restaurant.mealSurcharge)
        holder.tvRestaurantCapacity.text = holder.itemView.context.getString(R.string.restaurant_capacity, restaurant.lastCapacity, restaurant.maxCapacity)
    }

    override fun getItemCount() = restaurantsList.size
}