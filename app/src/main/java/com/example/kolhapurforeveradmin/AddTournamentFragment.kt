package com.example.kolhapurforeveradmin

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
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
import com.example.kolhapurforeveradmin.databinding.FragmentAddTournamentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class AddTournamentFragment : Fragment() {

    lateinit var logoUrl:String
    var pickedLogo : Uri? = null
    var pickedBitMapLogo : Bitmap? = null
    val logoRequestCode = 2
    lateinit var dialog: Dialog

    private lateinit var binding:FragmentAddTournamentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTournamentBinding.inflate(inflater,container,false)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        binding.addTournamentLogoBtn.setOnClickListener {
            pickPhoto(it,logoRequestCode)
            val imageLogoExtension =
                pickedLogo.toString().substring(pickedLogo.toString().lastIndexOf(".") + 1)
        }

        binding.addTournament.setOnClickListener {
            if(binding.tournamentNameEtv.text.toString() == ""){
                Toast.makeText(requireContext(), "Please Enter Tournament Name", Toast.LENGTH_SHORT).show()
            }else if (pickedLogo == null){
                Toast.makeText(requireContext(), "Please Pick the Logo", Toast.LENGTH_SHORT).show()
            }else{
                dialog.show()
                val imageLogoExtension =
                    pickedLogo.toString().substring(pickedLogo.toString().lastIndexOf(".") + 1)
                val ref = FirebaseStorage.getInstance().reference.child("/Tournament")


                ref.child(binding.tournamentNameEtv.text.toString() + "_" + "logo" + "_" + System.currentTimeMillis() + "." + imageLogoExtension.toString()).putFile(pickedLogo!!)
                    .addOnSuccessListener { takeSnapShot ->
                        takeSnapShot.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener {logo ->
                                logoUrl = logo.toString()


                                val tournamentId = getRandomString(25)

                                val tournament = Tournament(tournamentId,binding.tournamentNameEtv.text.toString(),logo.toString())

                                FirebaseDatabase.getInstance().getReference("Sports")
                                    .child("Football")
                                    .child("Tournament")
                                    .child(tournamentId)
                                    .setValue(tournament)
                                    .addOnSuccessListener {
                                        dialog.dismiss()
                                        activity?.onBackPressed()
                                        Toast.makeText(requireContext(), "Done...", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            }
                    }
            }
        }


        return binding.root
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
                    val source = ImageDecoder.createSource(
                        this.requireActivity().contentResolver,
                        pickedLogo!!
                    )
                    pickedBitMapLogo = ImageDecoder.decodeBitmap(source)
                    binding.tournamentLogo.setImageBitmap(pickedBitMapLogo)
                } else {
                    pickedBitMapLogo = MediaStore.Images.Media.getBitmap(
                        this.requireActivity().contentResolver,
                        pickedLogo
                    )
                    binding.tournamentLogo.setImageBitmap(pickedBitMapLogo)
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }




}