package com.example.kolhapurforeveradmin

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.kolhapurforeveradmin.databinding.FragmentAddSpecificBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_add_specific.*
import kotlinx.android.synthetic.main.fragment_add_sponsor.*


class AddSpecificFragment : Fragment() {

    private val pickImage = 100
    private var imageUri: Uri? = null
    lateinit var imageUrl:String
    lateinit var compitionId:String
    lateinit var compitionType:String
    lateinit var sponsorlist:HashMap<String,String>
    lateinit var dialog: Dialog
    var sponsorId = ""


    private lateinit var binding:FragmentAddSpecificBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddSpecificBinding.inflate(inflater,container,false)
        imageUrl = ""

        compitionId  = arguments?.getString("comp id").toString()
        compitionType = arguments?.getString("comp type").toString()

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        sponsorlist = HashMap()

        if(compitionType == "Mandal"){
            binding.spinner.visibility = View.VISIBLE
            binding.textInputLayout8.visibility = View.VISIBLE
            binding.textInputLayout9.visibility = View.VISIBLE


            FirebaseDatabase.getInstance().getReference("Sponsors")
                .get()
                .addOnSuccessListener {
                    if(it.exists()){
                        sponsorlist.clear()
                        sponsorlist.put("hint","Select Sponsor")


                        for (i in it.children){
                            val sponsor = i.getValue(Sponser::class.java)
                            sponsorlist.put(sponsor?.id!!,sponsor.name)
                        }

                        val sponsorNameList = ArrayList<String>()
                        for (i in sponsorlist.values){
                            sponsorNameList.add(i)
                        }



                        val adaptor = ArrayAdapter(this.requireContext(),R.layout.dropdown_item,sponsorNameList)
                        spinner.adapter = adaptor

                    }
                }
        }




        binding.loadImageBtn.setOnClickListener {
            if(binding.nameTextParticipant.text.toString() == ""){
                Toast.makeText(requireContext(), "Please Enter Name In Marathi", Toast.LENGTH_SHORT).show()
            }else{

                if(binding.nameinEngTextParticipant.text.toString() == ""){
                    Toast.makeText(requireContext(), "Please Enter Name In English", Toast.LENGTH_SHORT).show()

                }else{
                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, pickImage)
                }
            }
        }

        binding.button3.setOnClickListener {

            if(compitionType == "Mandal"){
                if(imageUrl == ""){
                    Toast.makeText(requireContext(), "Please Select The Image", Toast.LENGTH_SHORT).show()
                }else{
                    if(binding.mandalSinceText.text.toString() == ""){
                        Toast.makeText(requireContext(), "Please Enter Mandal Since", Toast.LENGTH_SHORT).show()
                    }else{
                        if(binding.mandalAddressText.text.toString() == ""){
                            Toast.makeText(requireContext(), "Please Enter Mandal Address", Toast.LENGTH_SHORT).show()
                        }else{
                            if(binding.spinner.selectedItem.toString() == "Select Sponsor"){
                                Toast.makeText(requireContext(), "Please Select Sponsor", Toast.LENGTH_SHORT).show()
                            }else{
                                dialog.show()


                                sponsorId = sponsorlist.entries.find { it.value == binding.spinner.selectedItem.toString() }?.key.toString()

                                FirebaseDatabase.getInstance().getReference("Participants")
                                    .child(compitionId)
                                    .get()
                                    .addOnSuccessListener {
                                        val no = (it.childrenCount.toString().toInt())

                                        val id = getRandomString(25)

                                        val participant = Participant(id,no,binding.nameTextParticipant.text.toString(),binding.nameinEngTextParticipant.text.toString().lowercase(),imageUrl)
                                        val mandal = Mandal(binding.mandalAddressText.text.toString(),binding.mandalSinceText.text.toString(),sponsorId)

                                        val participantlist = ArrayList<Participant>()

                                        FirebaseDatabase.getInstance().getReference("Participants")
                                            .child(compitionId)
                                            .get()
                                            .addOnSuccessListener {

                                                participantlist.clear()
                                                for (i in it.children){
                                                    val participant = i.getValue(Participant::class.java)
                                                    participantlist.add(participant!!)
                                                }
                                                participantlist.add(participant)

                                                FirebaseDatabase.getInstance().getReference("Participants")
                                                    .child(compitionId)
                                                    .setValue(participantlist)
                                                    .addOnSuccessListener {
                                                        dialog.dismiss()
                                                        activity?.onBackPressed()
                                                        Toast.makeText(requireContext(), "Done...", Toast.LENGTH_LONG)
                                                            .show()




                                                        FirebaseDatabase.getInstance().getReference("Mandal")
                                                            .child(compitionId)
                                                            .child(id)
                                                            .setValue(mandal)

                                                    }

                                            }
                            }
                            

                                }


                        }
                    }
                }
            }else{
                if(imageUrl == ""){
                    Toast.makeText(requireContext(), "Please Select The Image", Toast.LENGTH_SHORT).show()
                }else{
                    dialog.show()
                    FirebaseDatabase.getInstance().getReference("Participants")
                        .child(compitionId)
                        .get()
                        .addOnSuccessListener {
                                val no = it.childrenCount.toString().toInt()

                                val participant = Participant(getRandomString(25),no,binding.nameTextParticipant.text.toString(),binding.nameinEngTextParticipant.text.toString().lowercase(),imageUrl)

                                val participantlist = ArrayList<Participant>()

                                FirebaseDatabase.getInstance().getReference("Participants")
                                    .child(compitionId)
                                    .get()
                                    .addOnSuccessListener {
                                            participantlist.clear()
                                            for (i in it.children){
                                                val participant = i.getValue(Participant::class.java)
                                                participantlist.add(participant!!)
                                            }
                                            participantlist.add(participant)

                                            FirebaseDatabase.getInstance().getReference("Participants")
                                                .child(compitionId)
                                                .setValue(participantlist)
                                                .addOnSuccessListener {
                                                    dialog.dismiss()
                                                    activity?.onBackPressed()
                                                    Toast.makeText(requireContext(), "Done...", Toast.LENGTH_LONG)
                                                        .show()
                                                }

                                    }

                        }


                }
            }

        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            if (data?.data == null){
                Toast.makeText(requireContext(), "Please Select Image", Toast.LENGTH_SHORT).show()
            }else{
                imageUri = data.data
                val imageExtension = imageUri.toString().substring(imageUri.toString().lastIndexOf(".")+1)
                val sRef = FirebaseStorage.getInstance().reference.child("/Compition Photo").child(
                    binding.nameinEngTextParticipant.text.toString() + "_" + System.currentTimeMillis()+ "." + imageExtension.toString()
                )

                sRef.putFile(imageUri!!)
                    .addOnSuccessListener {takeSnapShot ->
                        takeSnapShot.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener {
                                imageUrl = it.toString()
                                binding.imageView.setImageURI(imageUri)
                            }
                            .addOnFailureListener {
                                val snackbar = Snackbar.make(requireContext(),requireView().rootView, "${it.localizedMessage}", Snackbar.LENGTH_LONG)
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