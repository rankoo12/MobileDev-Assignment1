import android.content.Context
import com.example.assignment1.Highscore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HighscoreStorage {
    private const val PREFS_NAME = "highscores"
    private const val KEY_SCORES = "scores"

    private val gson = Gson()

    fun saveHighscores(context: Context, highscores: List<Highscore>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(highscores)
        prefs.edit().putString(KEY_SCORES, json).apply()
    }

    fun loadHighscores(context: Context): List<Highscore> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SCORES, null) ?: return emptyList()
        val type = object : TypeToken<List<Highscore>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addHighscore(context: Context, newScore: Highscore) {
        val list = loadHighscores(context).toMutableList()
        list.add(newScore)
        // Sort descending and keep top 10
        val top10 = list.sortedByDescending { it.score }.take(10)
        saveHighscores(context, top10)
    }
}
