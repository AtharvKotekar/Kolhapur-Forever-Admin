package com.example.kolhapurforeveradmin

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MatchAdaptor(private var fragment: Fragment, private var list: ArrayList<Match>) : RecyclerView.Adapter<MatchAdaptor.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tournamentName = view.findViewById<TextView>(R.id.tournament_name_match)
        val team1Name = view.findViewById<TextView>(R.id.team_1_name)
        val team2Name = view.findViewById<TextView>(R.id.team_2_name)
        val team1Score = view.findViewById<TextView>(R.id.score_team_1)
        val team2Score = view.findViewById<TextView>(R.id.score_team_2)
        val time = view.findViewById<TextView>(R.id.time_text)
        val team1Logo = view.findViewById<ImageView>(R.id.team1_logo)
        val team2Logo = view.findViewById<ImageView>(R.id.team2_logo)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.match_item, parent, false)
        return MatchAdaptor.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        Glide
            .with(fragment.requireContext())
            .load(model.team1Logo)
            .into(holder.team1Logo)

        Glide
            .with(fragment.requireContext())
            .load(model.team2Logo)
            .into(holder.team2Logo)

        holder.tournamentName.text = model.tournamentName
        holder.team1Score.text = model.team1Score.toString()
        holder.team2Score.text = model.team2Score.toString()
        holder.team1Name.text = model.team1Name
        holder.team2Name.text = model.team2Name


                val startDate = model.startTime
                val spfStart = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss")
                val displayDate = SimpleDateFormat("dd MMM yy, hh:mm aa")
                val newStartDate: Date = spfStart.parse(startDate) as Date


                val displayStringDate = displayDate.format(newStartDate)


                val currentDate = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")
                val formattedDate = currentDate.format(formatter)


                val dateFormat: SimpleDateFormat = SimpleDateFormat(
                    "dd.MM.yyyy, HH:mm:ss"
                )
                var convertedStartDate = Date()
                var convertedCurrentDate = Date()

                Log.e(TAG, "onBindViewHolder: StartDate $startDate",)
                Log.e(TAG, "onBindViewHolder: CurrentDate $formattedDate",)

                try {
                    convertedStartDate = dateFormat.parse(startDate) as Date
                    convertedCurrentDate = dateFormat.parse(formattedDate) as Date

                    if (convertedCurrentDate.after(convertedStartDate)) {
                        holder.time.text = "Live"
                        holder.time.setTextColor(Color.parseColor("#DF0513"))
                    } else {
                        holder.time.text = displayStringDate.toString()

                        val diffrence = convertedStartDate.time - convertedCurrentDate.time

                        val countDownTimer = object : CountDownTimer(diffrence, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                var diff = millisUntilFinished
                                val secondsInMilli: Long = 1000
                                val minutesInMilli = secondsInMilli * 60
                                val hoursInMilli = minutesInMilli * 60

                            }

                            override fun onFinish() {
                                holder.time.text = "Live"
                                holder.time.setTextColor(Color.parseColor("#DF0513"))
                            }

                        }.start()
                    }


                } catch (e: Exception) {
                    Log.e(ContentValues.TAG, "onBindViewHolder: ${e.localizedMessage}",)
                }


                holder.itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("tournamentId",model.tournamentId)
//                    bundle.putString("tournamentName",model.tournamentName)
//                    bundle.putString("matchId",model.matchId)
//                    bundle.putString("team1Id",model.team1Id)
//                    bundle.putString("team2Id",model.team2Id)
//                    bundle.putString("team1Name",model.team1Name)
//                    bundle.putString("team2Name",model.team2Name)
//                    bundle.putString("team1Logo",model.team1Logo)
//                    bundle.putString("team2Logo",model.team2Logo)
//                    bundle.putString("status","Upcoming")
//                    bundle.putString("sponsorID",model.sponserId)
                    fragment.findNavController().navigate(R.id.action_tournamentDetail_to_matchDetailFragment,bundle)
                }



        }


    override fun getItemCount(): Int {
        return  list.size
    }
}