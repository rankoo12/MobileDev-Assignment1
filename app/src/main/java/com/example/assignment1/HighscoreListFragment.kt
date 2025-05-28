package com.example.assignment1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HighscoreListFragment : Fragment() {

    interface OnHighscoreSelectedListener {
        fun onHighscoreSelected(highscore: Highscore)
    }

    private var listener: OnHighscoreSelectedListener? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HighscoreAdapter
    private var highscores: List<Highscore> = emptyList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHighscoreSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnHighscoreSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null // prevent memory leaks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_highscore_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewHighscores)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Load highscores from storage
        context?.let {
            highscores = HighscoreStorage.loadHighscores(it)
        }
        adapter = HighscoreAdapter(highscores) { selectedHighscore ->
            listener?.onHighscoreSelected(selectedHighscore)
        }
        recyclerView.adapter = adapter

        return view
    }
}
