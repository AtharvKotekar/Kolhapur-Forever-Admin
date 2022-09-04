package com.example.kolhapurforeveradmin

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class CompitionAdaptor(private var fragment: EditCompitionFragment, private var list: ArrayList<Compitition>) : RecyclerView.Adapter<CompitionAdaptor.MyViewHolder>()   {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val title = view.findViewById<TextView>(R.id.title_text)
        val startDate = view.findViewById<TextView>(R.id.start_date_text)
        val endDate = view.findViewById<TextView>(R.id.end_date_text)
        val timeStatus = view.findViewById<TextView>(R.id.time_status_text)
        val countDownTime = view.findViewById<TextView>(R.id.countdown_text)
        val voteBtn = view.findViewById<TextView>(R.id.vote_btn)
        val leaderoardBtn = view.findViewById<TextView>(R.id.leaderboard_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.compitition_iteam, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        var startDate = model.startDate
        var spfStart = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss")
        val newStartDate: Date = spfStart.parse(startDate) as Date
        spfStart = SimpleDateFormat("dd/MM/yyyy")
        startDate = spfStart.format(newStartDate)

        var endDate = model.endDate
        var spfEnd = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss")
        val newEndDate: Date = spfEnd.parse(endDate) as Date
        spfEnd = SimpleDateFormat("dd/MM/yyyy")
        endDate = spfEnd.format(newEndDate)

        holder.startDate.text = startDate.toString()
        holder.endDate.text = endDate.toString()
        holder.title.text = model.name.toString()

        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")
        val formattedDate = currentDate.format(formatter)



        val dateFormat = SimpleDateFormat(
            "dd.MM.yyyy, HH:mm:ss"
        )
        var convertedStartDate = Date()
        var convertedEndDateDate = Date()
        var convertedCurrentDateDate = Date()
        try {
            convertedStartDate = dateFormat.parse(model.startDate) as Date
            convertedEndDateDate = dateFormat.parse(model.endDate) as Date
            convertedCurrentDateDate = dateFormat.parse(formattedDate) as Date

            Log.e(TAG, "onBindViewHolder: $convertedStartDate", )
            Log.e(TAG, "onBindViewHolder: $convertedEndDateDate", )
            Log.e(TAG, "onBindViewHolder: $convertedCurrentDateDate", )

            if (convertedCurrentDateDate.after(convertedStartDate)) {
                if(convertedCurrentDateDate.before(convertedEndDateDate)){
                    holder.timeStatus.text = "Ends In"

                    val diffrence = convertedEndDateDate.time - convertedCurrentDateDate.time

                    val countDownTimer = object : CountDownTimer(diffrence,1000){
                        override fun onTick(millisUntilFinished: Long) {
                            var diff = millisUntilFinished
                            val secondsInMilli: Long = 1000
                            val minutesInMilli = secondsInMilli * 60
                            val hoursInMilli = minutesInMilli * 60



                            val elapsedHours = diff / hoursInMilli
                            diff %= hoursInMilli

                            val elapsedMinutes = diff / minutesInMilli
                            diff %= minutesInMilli

                            val elapsedSeconds = diff / secondsInMilli

                            if(elapsedHours < 1){
                                holder.countDownTime.setTextColor(Color.parseColor("#DF0513"))
                            }

                            holder.countDownTime.text = "${elapsedHours}:${elapsedMinutes}:${elapsedSeconds}"

                            holder.voteBtn.setOnClickListener {
                                val bundle = Bundle()
                                bundle.putString("comp id",model.id)
                                bundle.putString("comp name",model.name)
                                bundle.putString("comp type",model.type)
                                fragment.findNavController().navigate(R.id.action_editCompitionFragment_to_addParticipantFragment,bundle)
                            }



                            holder.leaderoardBtn.setOnClickListener {
                                val bundle = Bundle()
                                bundle.putString("name",model.name)
                                bundle.putString("id",model.id)
                                bundle.putString("firstPrize",model.firstPrice)
                                bundle.putString("secondPrize",model.secondPrice)
                                bundle.putString("thirdPrize",model.thirdPrice)
                                bundle.putString("startDate",model.startDate)
                                bundle.putString("endDate",model.endDate)
                                bundle.putString("type",model.type)
                                bundle.putLong("timeStamp",model.timestamp)
                                fragment.findNavController().navigate(R.id.action_editCompitionFragment_to_editSpecificCompititionFragment,bundle)
                            }
                        }

                        override fun onFinish() {
                            holder.timeStatus.text = "Time Up"
                            holder.countDownTime.text = "00:00:00"
                            holder.countDownTime.setTextColor(Color.parseColor("#DF0513"))
                            holder.voteBtn.setBackgroundResource(R.drawable.gray_btn_bg)
                            holder.voteBtn.setOnClickListener {
                                Toast.makeText(fragment.context, "बास कर! सपला की वेळ.", Toast.LENGTH_SHORT).show()
                            }

                            holder.leaderoardBtn.setOnClickListener {
                                val bundle = Bundle()
                                bundle.putString("name",model.name)
                                bundle.putString("id",model.id)
                                bundle.putString("firstPrize",model.firstPrice)
                                bundle.putString("secondPrize",model.secondPrice)
                                bundle.putString("thirdPrize",model.thirdPrice)
                                bundle.putString("startDate",model.startDate)
                                bundle.putString("endDate",model.endDate)
                                bundle.putString("type",model.type)
                                fragment.findNavController().navigate(R.id.action_editCompitionFragment_to_editSpecificCompititionFragment,bundle)
                            }
                        }

                    }.start()

                }else{
                    holder.timeStatus.text = "Time Up"
                    holder.countDownTime.text = "00:00:00"
                    holder.countDownTime.setTextColor(Color.parseColor("#DF0513"))
                    holder.voteBtn.setBackgroundResource(R.drawable.gray_btn_bg)
                    holder.voteBtn.setOnClickListener {
                        Toast.makeText(fragment.context, "बास कर! सपला की वेळ.", Toast.LENGTH_SHORT).show()
                    }

                    holder.leaderoardBtn.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("name",model.name)
                        bundle.putString("id",model.id)
                        bundle.putString("firstPrize",model.firstPrice)
                        bundle.putString("secondPrize",model.secondPrice)
                        bundle.putString("thirdPrize",model.thirdPrice)
                        bundle.putString("startDate",model.startDate)
                        bundle.putString("endDate",model.endDate)
                        bundle.putString("type",model.type)
                        fragment.findNavController().navigate(R.id.action_editCompitionFragment_to_editSpecificCompititionFragment,bundle)
                    }

                }
            } else {
                holder.timeStatus.text = "Starts In"

                holder.voteBtn.setBackgroundResource(R.drawable.gray_btn_bg)
                holder.voteBtn.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("comp id",model.id)
                    bundle.putString("comp name",model.name)
                    bundle.putString("comp type",model.type)
                    fragment.findNavController().navigate(R.id.action_editCompitionFragment_to_addParticipantFragment,bundle)
                }

                holder.leaderoardBtn.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("name",model.name)
                    bundle.putString("id",model.id)
                    bundle.putString("firstPrize",model.firstPrice)
                    bundle.putString("secondPrize",model.secondPrice)
                    bundle.putString("thirdPrize",model.thirdPrice)
                    bundle.putString("startDate",model.startDate)
                    bundle.putString("endDate",model.endDate)
                    bundle.putString("type",model.type)
                    fragment.findNavController().navigate(R.id.action_editCompitionFragment_to_editSpecificCompititionFragment,bundle)
                }

                val diffrence = convertedStartDate.time - convertedCurrentDateDate.time


                val countDownTimer = object :CountDownTimer(diffrence,1000){
                    override fun onTick(millisUntilFinished: Long) {
                        var diff = millisUntilFinished
                        val secondsInMilli: Long = 1000
                        val minutesInMilli = secondsInMilli * 60
                        val hoursInMilli = minutesInMilli * 60



                        val elapsedHours = diff / hoursInMilli
                        diff %= hoursInMilli

                        val elapsedMinutes = diff / minutesInMilli
                        diff %= minutesInMilli

                        val elapsedSeconds = diff / secondsInMilli

                        holder.countDownTime.text = "${elapsedHours}:${elapsedMinutes}:${elapsedSeconds}"
                    }

                    override fun onFinish() {

                        fragment.getCompititions()

                    }

                }.start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "onBindViewHolder: ${e.localizedMessage}", )
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}