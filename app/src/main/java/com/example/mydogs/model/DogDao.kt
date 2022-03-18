package com.example.mydogs.model

import androidx.room.*

@Dao
interface DogDao {


    @Insert
    fun insertAll(vararg dogs: DogBreeds): List<Long>

    @Query("SELECT * FROM dogBreeds")
    fun getAllDogs(): List<DogBreeds>


    @Query("SELECT * FROM dogBreeds  WHERE uuid = :dogId")
      fun getDogs(dogId: Int): DogBreeds

    @Query("DELETE FROM dogBreeds")
     fun deleteAllDogs()

}