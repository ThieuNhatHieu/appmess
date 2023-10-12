@file:Suppress("DEPRECATION")

package com.example.messenger.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.messenger.R
import com.example.messenger.Utils.Utils
import com.example.messenger.databinding.FragmentSettingBinding
import com.example.messenger.mvvm.ChatAppViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*

class SettingFragment : Fragment() {

    private lateinit var settingBinding: FragmentSettingBinding
    lateinit var settingviewModel: ChatAppViewModel
    private lateinit var storageRef: StorageReference
    lateinit var storage: FirebaseStorage
    var uri: Uri? = null
    lateinit var bitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        settingBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        return settingBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingviewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        settingBinding.viewModel = settingviewModel
        settingBinding.lifecycleOwner = viewLifecycleOwner

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        settingviewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            loadImage(it)
        })

        settingBinding.settingBackBtn.setOnClickListener {

            view?.findNavController()?.navigate(R.id.action_settingFragment_to_homeFragment)
        }

        settingBinding.settingUpdateImage.setOnClickListener {

            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {

                        takePhotoWithCamera()


                    }
                    options[item] == "Choose from Gallery" -> {
                        pickImageFromGallery()
                    }
                    options[item] == "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()
        }

        settingBinding.settingUpdateButton.setOnClickListener {
            settingviewModel.updateProfile()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun pickImageFromGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Utils.REQUEST_IMAGE_PICK)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhotoWithCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE)
    }


    private fun loadImage(it: String?) {

        Glide.with(requireContext()).load(it).placeholder(R.drawable.person).dontAnimate()
            .into(settingBinding.settingUpdateImage)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                Utils.REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap

                    uploadImageToFirebaseStorage(imageBitmap)
                }
                Utils.REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    val imageBitmap =
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                    uploadImageToFirebaseStorage(imageBitmap)
                }
            }
        }


    }

    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?) {

        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        bitmap = imageBitmap!!

        settingBinding.settingUpdateImage.setImageBitmap(imageBitmap)

        val storagePath = storageRef.child("Photos/${UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)
        uploadTask.addOnSuccessListener {

            val task = it.metadata?.reference?.downloadUrl

            task?.addOnSuccessListener {

                uri = it
                settingviewModel.imageUrl.value = uri.toString()

            }

            Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()

        settingviewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            loadImage(it)
        })
    }

}