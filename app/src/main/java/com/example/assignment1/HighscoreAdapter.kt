package com.example.assignment1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HighscoreAdapter(
    private val highscores: List<Highscore>,
    private val clickListener: (Highscore) -> Unit
) : RecyclerView.Adapter<HighscoreAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textScore: TextView = view.findViewById(R.id.textScore)
        val textDate: TextView = view.findViewById(R.id.textDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_highscore, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = highscores.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val highscore = highscores[position]
        holder.textScore.text = "Score: ${highscore.score}"
        holder.textDate.text = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", highscore.timestamp)

        holder.itemView.setOnClickListener {
            clickListener(highscore)
        }
    }
}
