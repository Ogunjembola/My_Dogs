package com.example.mydogs.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity
data class DogBreeds(
    @ColumnInfo(name = "breed_ID")
    @SerializedName("id")
    val breedID : String,

    @ColumnInfo(name = "dog_name")
    @SerializedName("name")
    val dogBreed: String,


    @ColumnInfo(name = "life_Span")
    @SerializedName("life_span")
    val lifeSpan:String ?,

    @ColumnInfo(name = "breed_group")
    @SerializedName("breed_group")
    val breedGroup : String ?,

    @ColumnInfo(name = "breed_for")
    @SerializedName("breed_for")
    val breedFor : String ? ,


    @SerializedName("temperament")
    val Temperament : String?,

    @ColumnInfo(name = "dog_url")
    @SerializedName("url")
    val imageUrl: String ?


){
    @PrimaryKey(autoGenerate = true)
    var uuid =0
}
data class  DogPalette(var color : Int)

data class SmsInfo(
    var to : String,
    var text: String,
    var imageUrl: String?
)