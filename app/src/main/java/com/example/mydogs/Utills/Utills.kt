package com.example.mydogs.Utills
import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mydogs.R
val PERMISSION_SEND_SMS =234

fun getProgressDrawable(context: Context): CircularProgressDrawable{
    return CircularProgressDrawable(context).apply{
       strokeWidth = 100f
       centerRadius = 50f
       start()
    }
}
fun  ImageView.loadImage(uri:String?, progressDrawable: CircularProgressDrawable){
    val option: RequestOptions= RequestOptions()
        .placeholder(progressDrawable)
        .error(R.mipmap.ic_launcher)
    Glide.with(context)
        .setDefaultRequestOptions(option)
        .load(uri)
        .into(this)
}
@BindingAdapter("android:imageUrl")
fun  loadImage(binding: ImageView,url: String?){
    binding.loadImage(url, getProgressDrawable(binding.context))
}