package com.example.kolhapurforeveradmin

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kolhapurforeveradmin.databinding.FragmentEditSponsorBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class EditSponsorFragment : Fragment() {

   private lateinit var binding: FragmentEditSponsorBinding
    lateinit var dialog: Dialog
    lateinit var adaptor:SponsorAdaptor
    lateinit var sponserList:ArrayList<Sponser>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditSponsorBinding.inflate(inflater,container,false)

        sponserList = ArrayList()

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()


        getData()


        return binding.root
    }

    fun updateUi(itemList:ArrayList<Sponser>){
        adaptor = SponsorAdaptor(this@EditSponsorFragment,itemList)
        binding.recyclerView.adapter = adaptor
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
        dialog.dismiss()
    }

    fun getData(){
        FirebaseDatabase.getInstance().getReference("Sponsors")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        sponserList.clear()
                        for (i in snapshot.children){
                            val sponsor = i.getValue(Sponser::class.java)
                            sponserList.add(sponsor!!)
                        }
                        updateUi(sponserList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

}