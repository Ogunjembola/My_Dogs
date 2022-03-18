package com.example.mydogs.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydogs.model.DogBreeds
import com.example.mydogs.model.DogDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DetailsViewModel(application :Application): BaseViewModel(application ) {

    val dogLiveData = MutableLiveData<DogBreeds>()
    fun fetch(uuid: Int) {
        /* val dog = DogBreeds("1","Corgi","15 Years","BreedGroup","breed For","Tempuarament","")
        dogLiveData.value= dog*/
      viewModelScope.launch (Dispatchers.IO){
            val dog: DogBreeds = DogDatabase(getApplication()).dogDao().getDogs(uuid)
            dogLiveData.postValue(dog)
        }
    }

}