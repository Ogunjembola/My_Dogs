package com.example.mydogs.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mydogs.Utills.NotificationHelper
import com.example.mydogs.Utills.SharedPreferencesHelper
import com.example.mydogs.model.DogBreeds
import com.example.mydogs.model.DogDao
import com.example.mydogs.model.DogDatabase
import com.example.mydogs.model.DogsAPIServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.lang.NumberFormatException

class ListViewModel(application: Application) : BaseViewModel(application) {
    private var prefHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L
    private val dogService = DogsAPIServices
    private val disposable = CompositeDisposable()
    val dogs = MutableLiveData<List<DogBreeds>>()
    val dogsLoaderError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()


    fun refresh() {
        checkCacheDuration()
        /* val dog1 = DogBreeds("1","Corgi","15 Years","BreedGroup","breed For","Tempuarament","")
         val dog2 = DogBreeds("2","Corgi","15 Years","BreedGroup","breed For","Tempuarament","")
         val dog3 = DogBreeds("3","Corgi","15 Years","BreedGroup","breed For","Tempuarament","")
         val dogsList= arrayListOf<DogBreeds>(dog1,dog2,dog3)

         dogs.value = dogsList
         dogsLoaderError.value = false
         loading.value = false*/
        val updateTime = prefHelper.getUpdateTime()
        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchFromDatabase()
            Toast.makeText(getApplication(), "Dogs retrieved  from database", Toast.LENGTH_SHORT)
                .show()
        } else {
            fetchRemoteData()
        }

        //fetchRemoteData()
    }

    private fun checkCacheDuration() {
        val cachePreference = prefHelper.getCacheDuration()
        try {
            val cachePreferenceInt = cachePreference?.toInt() ?: 5 * 60
            refreshTime = cachePreferenceInt.times(1000 * 1000 * 1000L)
        } catch (
            e: NumberFormatException
        ) {
            e.printStackTrace()
        }


    }

    private fun fetchFromDatabase() {
        viewModelScope.launch {
            loading.value = true
            val dogs = loadDogsFromDatabase()
            dogsRetrieved(dogs)

        }
    }

    private suspend fun loadDogsFromDatabase(): List<DogBreeds> = withContext(Dispatchers.IO) {
        val db = DogDatabase(getApplication())
        return@withContext db.dogDao().getAllDogs()
    }

    /*  private fun fetchFromDatabase() {
        launch {
            loading.value = true
            val dogs = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)

        }

     }*/
    fun refreshBypassCache() {
        fetchRemoteData()
    }

    private fun fetchRemoteData() {
        loading.value = true
        disposable.add(
            dogService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<DogBreeds>>() {
                    override fun onSuccess(dogList: List<DogBreeds>) {
                        storeDogsLocally(dogList)
                        Toast.makeText(
                            getApplication(),
                            "Dogs retrieved  from endPoint",
                            Toast.LENGTH_LONG
                        ).show()
                        NotificationHelper(getApplication()).createNotification()

                    }

                    override fun onError(e: Throwable) {
                        dogsLoaderError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }

                })
        )
    }

    private suspend fun performAction(list: List<DogBreeds>) {
        withContext(Dispatchers.IO) {
            val dao: DogDao = DogDatabase(getApplication()).dogDao()
            // val allDogs = dao.getAllDogs()
            //  Log.d("ListViewModel","allDogs $allDogs")
            dao.deleteAllDogs()
            val result: List<Long> = dao.insertAll(*list.toTypedArray())
            var i = 0
            while (i < list.size) {
                list[i].uuid = result[i].toInt()
                ++i

            }
        }

    }

    private fun dogsRetrieved(dogList: List<DogBreeds>) {
        dogs.value = dogList
        dogsLoaderError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(list: List<DogBreeds>) {
        viewModelScope.launch {
            performAction(list)
            dogsRetrieved(list)

        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}