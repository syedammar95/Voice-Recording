package com.example.onpressrecording

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class AdapterForRecordings(private val UsersData: ArrayList<UsersData>,
                           private val onVoiceClicked: OnVoiceClicked):
                            RecyclerView.Adapter<AdapterForRecordings.viewholder>() {


    class viewholder(items: View) : RecyclerView.ViewHolder(items) {

        val button: Button = items.findViewById(R.id.btnPlayRecording)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {

        val items_for_recordings = LayoutInflater.from(parent.context)
            .inflate(R.layout.items_for_recordings, parent, false)

        return viewholder(items_for_recordings)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        holder.button.setOnClickListener {
            onVoiceClicked.onVoiceClick(UsersData[position].UserAudioSavePathInDevice)
        }
    }

    override fun getItemCount(): Int {

        return UsersData.size
    }
}