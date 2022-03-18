package com.example.mydogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.mydogs.R

import com.example.mydogs.Utills.getProgressDrawable
import com.example.mydogs.Utills.loadImage
import com.example.mydogs.databinding.FragmentDetailsBinding
import com.example.mydogs.databinding.FragmentListBinding
import com.example.mydogs.databinding.ItemDogsBinding
import com.example.mydogs.model.DogBreeds
import java.util.ArrayList

class DogsListAdapter(val dogList: ArrayList<DogBreeds>) :
    RecyclerView.Adapter<DogsListAdapter.DogViewHolder>() {


    inner class DogViewHolder(val binding: ItemDogsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemDogsBinding>(inflater, R.layout.item_dogs, parent, false)
        return DogViewHolder(binding)
    }
    /*  return DogViewHolder(
        ItemDogsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false

        )
    )*/


    override fun getItemCount(): Int {
        return dogList.size
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.binding.dog = dogList[position]
        // holder.binding.listener = this
        holder.binding.root.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToDetailsFragment()
            action.dogUId = dogList[position].uuid
            Navigation.findNavController(it).navigate(action)
        }
    }

    /*override fun onDogClicked(v: View ) {

        val uuid = binding.dogId.text.toString().toInt()
        val action = ListFragmentDirections.actionListFragmentToDetailsFragment()
        action.dogUId=uuid
            Navigation.findNavController(binding.root).navigate(action)
    }*/


    fun updateDogsList(newDogList: List<DogBreeds>) {
        dogList.clear()
        dogList.addAll(newDogList)
        notifyDataSetChanged()
    }
}