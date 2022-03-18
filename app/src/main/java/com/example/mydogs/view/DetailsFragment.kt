package com.example.mydogs.view

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mydogs.R
import com.example.mydogs.Utills.getProgressDrawable
import com.example.mydogs.Utills.loadImage
import com.example.mydogs.databinding.FragmentDetailsBinding
import com.example.mydogs.databinding.SendSmsDialogBinding
import com.example.mydogs.model.DogBreeds
import com.example.mydogs.model.DogPalette
import com.example.mydogs.model.SmsInfo
import com.example.mydogs.viewModel.DetailsViewModel


class DetailsFragment : Fragment() {
    private lateinit var viewModel: DetailsViewModel
    private lateinit var binding: FragmentDetailsBinding
    private var sendSmsStarted = false
    private var currentDog: DogBreeds? = null

    // the initial was set to zero
    private var dogUuid = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_details, container, false)
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            dogUuid = DetailsFragmentArgs.fromBundle(it).dogUId
        }
        // getting data from view model
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
        // fetching the fetch class in view
        viewModel.fetch(dogUuid)
        observerViewModel()

    }

    // Observe the view model
    private fun observerViewModel() {
        viewModel.dogLiveData.observe(viewLifecycleOwner, Observer { dog ->
            // fetching for the needed data
            currentDog = dog
            dog?.let {
                binding.dog = dog
                it.imageUrl?.let {
                    setUpBackgroundColor(it)
                }
                /*  binding.dogNameDetails.text = dog.dogBreed
                  binding.dogPurpose.text = dog.breedFor
                  binding.dogTemperament.text = dog.Temperament
                  binding.dogLifespanDetails.text = dog.lifeSpan
                  //context?.let { binding.dogImage.loadImage(dog.imageUrl, getProgressDrawable(it))*/
            }

        })

    }

    private fun setUpBackgroundColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate { palette ->
                            val intColor = palette?.vibrantSwatch?.rgb ?: 0
                            val myPalette = DogPalette(intColor)
                            binding.palette = myPalette
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.send_sms -> {
                sendSmsStarted = true
                (activity as MainActivity).checkSmsPermission()

            }
            R.id.share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = " text/ plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this dog breed")
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "${currentDog?.dogBreed} bred for ${currentDog?.breedFor}"
                )
                intent.putExtra(Intent.EXTRA_STREAM, currentDog?.imageUrl)
                startActivity(Intent.createChooser(intent, "Share with"))

            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSmsStarted && permissionGranted) {
            context?.let {
                val smsInfo = SmsInfo(
                    "",
                    "${currentDog?.dogBreed} bred for ${currentDog?.breedFor}",
                    currentDog?.imageUrl
                )

                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(it),
                    R.layout.send_sms_dialog,
                    null,
                    false
                )
                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send SMS") { dialog, which ->
                        if (!dialogBinding.smsDestination.text.isNullOrEmpty()) {
                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSms(smsInfo)
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, which ->

                    }.show()
                dialogBinding.smsInfo = smsInfo
            }
        }

    }

    private fun sendSms(smsInfo: SmsInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, 0)
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(smsInfo.to, null, smsInfo.text, pi, null)
    }
}