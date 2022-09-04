package com.example.kolhapurforeveradmin

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kolhapurforeveradmin.databinding.FragmentAddCompitionBinding
import com.example.kolhapurforeveradmin.databinding.FragmentAddSponsorBinding
import com.example.kolhapurforeveradmin.databinding.FragmentEditSponsorBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.log


class AddSponsorFragment : Fragment() {
    private lateinit var binding: FragmentAddSponsorBinding
    lateinit var logoUrl:String
    lateinit var barcodeUrl:String
    var pickedLogo : Uri? = null
    var pickedBitMapLogo : Bitmap? = null
    var pickedBarcode : Uri? = null
    var pickedBitMapBarcode : Bitmap? = null
    lateinit var images:ArrayList<Uri?>
    lateinit var imagesUrlList:ArrayList<String>
    val logoRequestCode = 2
    val barcodeRequestCode = 3
    val imagesRequestCode = 4
    lateinit var adaptor: ImagesAdaptor
    lateinit var dialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddSponsorBinding.inflate(inflater,container,false)

        images = ArrayList()
        imagesUrlList = ArrayList()


        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        binding.loadLogoImage.setOnClickListener {
            pickPhoto(it,logoRequestCode)
        }

        binding.loadBarcodeImage.setOnClickListener {
            pickPhoto(it,barcodeRequestCode)
        }

        binding.addImageBtn.setOnClickListener {
            pickMultipleImages(it,imagesRequestCode)
        }

        binding.addSponsorBtn.setOnClickListener {
            when {
                binding.sponsorName.text.toString() == "" -> {
                    Toast.makeText(
                        requireContext(),
                        "Please Enter Sponsor Name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.sponsorAddress.text.toString() == "" -> {
                    Toast.makeText(
                        requireContext(),
                        "Please Enter Sponsor Address.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.sponsorAddressLink.text.toString() == "" -> {
                    Toast.makeText(
                        requireContext(),
                        "Please Enter Sponsor Address Link.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.sponsorContact.text.toString() == "" -> {
                    Toast.makeText(
                        requireContext(),
                        "Please Enter Sponsor Contact Number.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.sponsorOffer.text.toString() == "" -> {
                    Toast.makeText(
                        requireContext(),
                        "Please Enter Sponsor Offer.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.logoSponsorImage == null -> {
                    Toast.makeText(requireContext(), "Please Select Logo.", Toast.LENGTH_SHORT)
                        .show()
                }
                pickedBarcode == null -> {
                    Toast.makeText(
                        requireContext(),
                        "Please Please Select Barcode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                images.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please Select Images.", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    dialog.show()
                    val imageLogoExtension =
                        pickedLogo.toString().substring(pickedLogo.toString().lastIndexOf(".") + 1)
                    val ref = FirebaseStorage.getInstance().reference.child("/Sponsers")


                    ref.child(binding.sponsorName.text.toString() + "_" + "logo" + "_" + System.currentTimeMillis() + "." + imageLogoExtension.toString()).putFile(pickedLogo!!)

                        .addOnSuccessListener { takeSnapShot ->
                            takeSnapShot.metadata!!.reference!!.downloadUrl
                                .addOnSuccessListener {logo ->
                                    logoUrl = logo.toString()

                                    val imageBarcodeExtension =
                                        pickedBarcode.toString().substring(pickedBarcode.toString().lastIndexOf(".") + 1)

                                    ref.child(binding.sponsorName.text.toString() + "_" + "barcode" + "_" + System.currentTimeMillis() + "." + imageBarcodeExtension.toString()).putFile(pickedBarcode!!)
                                        .addOnSuccessListener { takeSnapShot ->
                                            takeSnapShot.metadata!!.reference!!.downloadUrl
                                                .addOnSuccessListener {barcode ->
                                                    barcodeUrl = barcode.toString()



                                                    for (i in images) {

                                                        ref.child(binding.sponsorName.text.toString() + "_" + getRandomString(5) + "_" + System.currentTimeMillis() + "." + imageExtension(i!!)).putFile(
                                                            i
                                                        )
                                                            .addOnSuccessListener { takeSnapShot ->
                                                                takeSnapShot.metadata!!.reference!!.downloadUrl
                                                                    .addOnSuccessListener { u ->
                                                                        imagesUrlList.add(u.toString())

                                                                        if(images.size == imagesUrlList.size){
                                                                            Log.e(TAG, "onCreateView: ${imagesUrlList}", )

                                                                            val id = getRandomString(25)
                                                                            val sponsor = Sponser(id,binding.sponsorName.text.toString(),binding.sponsorOffer.text.toString()
                                                                                ,logoUrl,imagesUrlList,binding.sponsorAddress.text.toString(),binding.sponsorContact.text.toString(),barcodeUrl,binding.sponsorAddressLink.text.toString(),System.currentTimeMillis())

                                                                            FirebaseDatabase.getInstance().getReference("Sponsors")
                                                                                .child(id)
                                                                                .setValue(sponsor)
                                                                                .addOnCompleteListener { task ->

                                                                                    if(task.isSuccessful){
                                                                                        Toast.makeText(
                                                                                            requireContext(),
                                                                                            "Done...",
                                                                                            Toast.LENGTH_LONG
                                                                                        ).show()
                                                                                        dialog.dismiss()
                                                                                        onDestroy()
                                                                                        findNavController().navigate(R.id.main_nav)
                                                                                    }else{
                                                                                        dialog.dismiss()
                                                                                        Toast.makeText(
                                                                                            requireContext(),
                                                                                            task.exception?.localizedMessage.toString(),
                                                                                            Toast.LENGTH_SHORT
                                                                                        ).show()
                                                                                    }



                                                                                }
                                                                        }
                                                                    }

                                                            }


                                                    }





                                                    

                                                }
                                                .addOnFailureListener {
                                                    dialog.dismiss()
                                                    val snackbar = Snackbar.make(
                                                        requireContext(),
                                                        requireView().rootView,
                                                        "${it.localizedMessage}",
                                                        Snackbar.LENGTH_LONG
                                                    )
                                                    val snackbarView = snackbar.view
                                                    snackbarView.setBackgroundColor(Color.RED)
                                                    snackbar.setTextColor(Color.WHITE)
                                                    snackbar.show()
                                                }
                                        }
                                        .addOnFailureListener {
                                            dialog.dismiss()
                                            val snackbar = Snackbar.make(
                                                requireContext(),
                                                requireView().rootView,
                                                "${it.localizedMessage}",
                                                Snackbar.LENGTH_LONG
                                            )
                                            val snackbarView = snackbar.view
                                            snackbarView.setBackgroundColor(Color.RED)
                                            snackbar.setTextColor(Color.WHITE)
                                            snackbar.show()
                                        }
                                }
                        }
                }
            }
        }


        return binding.root
    }

    fun imageExtension(uri:Uri):String{
        val extension =
            pickedBarcode.toString().substring(pickedBarcode.toString().lastIndexOf(".") + 1)

        return extension
    }



    fun pickMultipleImages(view: View,requestCode: Int){

        if (ContextCompat.checkSelfPermission(this.requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) { // izin alınmadıysa
            ActivityCompat.requestPermissions(this.requireActivity(),arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        } else {
            activity.run {
                val intent = Intent()
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent,"Select Multiple Images"),imagesRequestCode)
            }
        }
    }

    fun pickPhoto(view: View,requestCode: Int){
        if (ContextCompat.checkSelfPermission(this.requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) { // izin alınmadıysa
            ActivityCompat.requestPermissions(this.requireActivity(),arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        } else {
            val galeriIntext = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntext,requestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galeriIntext = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntext,requestCode)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == logoRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            pickedLogo = data.data
            if (pickedLogo != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.requireActivity().contentResolver,pickedLogo!!)
                    pickedBitMapLogo = ImageDecoder.decodeBitmap(source)
                    binding.logoSponsorImage.setImageBitmap(pickedBitMapLogo)
                }
                else {
                    pickedBitMapLogo = MediaStore.Images.Media.getBitmap(this.requireActivity().contentResolver,pickedLogo)
                    binding.logoSponsorImage.setImageBitmap(pickedBitMapLogo)
                }
            }
        }else if (requestCode == barcodeRequestCode && resultCode == Activity.RESULT_OK && data != null){
            pickedBarcode = data.data
            if (pickedBarcode != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.requireActivity().contentResolver,pickedBarcode!!)
                    pickedBitMapBarcode = ImageDecoder.decodeBitmap(source)
                    binding.barcodeSponsorImage.setImageBitmap(pickedBitMapBarcode)
                }
                else {
                    pickedBitMapLogo = MediaStore.Images.Media.getBitmap(this.requireActivity().contentResolver,pickedLogo)
                    binding.barcodeSponsorImage.setImageBitmap(pickedBitMapBarcode)
                }
            }
        }else if(requestCode == imagesRequestCode && resultCode == Activity.RESULT_OK && data != null){
            if(data.clipData != null){
                val count = data.clipData!!.itemCount

                for (i in 0 until count){
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    images.add(imageUri)
                    Log.e(TAG, "onActivityResult: ${imageUri}", )
                }
                adaptor = ImagesAdaptor(this@AddSponsorFragment,images)
                binding.recyclerView2.adapter = adaptor
                binding.recyclerView2.layoutManager = LinearLayoutManager(this.requireContext(),LinearLayoutManager.VERTICAL,false)



            }else{
                Toast.makeText(requireContext(), "Please Select Multiple Images", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }




}