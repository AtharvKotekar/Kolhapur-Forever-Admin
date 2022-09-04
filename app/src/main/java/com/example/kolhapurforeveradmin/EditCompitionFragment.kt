package com.example.kolhapurforeveradmin

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kolhapurforeveradmin.databinding.FragmentAddCompitionBinding
import com.example.kolhapurforeveradmin.databinding.FragmentEditCompitionBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class EditCompitionFragment : Fragment() {

    private lateinit var binding: FragmentEditCompitionBinding
    val competitionList = ArrayList<Compitition>()


    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditCompitionBinding.inflate(inflater,container,false)


        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialog.show()


        getCompititions()



        return binding.root
    }


    fun getCompititions(){

        FirebaseDatabase.getInstance().getReference("Competition")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    competitionList.clear()
                    if(snapshot.exists()){
                        for (i in snapshot.children){
                            val compition = i.getValue(Compitition::class.java)
                            competitionList.add(compition!!)
                        }

                        upadateUI(competitionList)

                    }else{
                        Toast.makeText(requireContext(), "Something  Wents Wrong.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    val snackBar = Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Something Wents Wrong",
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setBackgroundTint(resources.getColor(android.R.color.holo_red_dark))
                    snackBar.setTextColor(resources.getColor(R.color.white))
                    snackBar.show()
                }

            })
    }

    fun upadateUI(iteamList:ArrayList<Compitition>){
        val adaptor = CompitionAdaptor(this@EditCompitionFragment,iteamList)
        binding.recyclerView.adapter = adaptor
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)

        dialog.dismiss()
    }


}