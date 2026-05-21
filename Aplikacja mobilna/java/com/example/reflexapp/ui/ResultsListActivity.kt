package com.example.reflexapp.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.reflexapp.R
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class ResultsListActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_list)

        container = findViewById(R.id.container)

        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        val userId = intent.getIntExtra("user_id", -1)

        if (userId == -1) {
            Log.e("RESULTS", "Brak userId")
            return
        }

        fetchResults(userId)
    }

    private fun fetchResults(userId: Int) {

        val url = "http://192.168.0.216/reflexapp/get_scores.php"

        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->

                Log.d("RESULTS", response)

                try {
                    val jsonArray = JSONArray(response)

                    container.removeAllViews()

                    for (i in 0 until jsonArray.length()) {

                        val obj = jsonArray.getJSONObject(i)

                        val card = LinearLayout(this)
                        card.orientation = LinearLayout.VERTICAL
                        card.setPadding(40, 40, 40, 40)

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 0, 0, 30)
                        card.layoutParams = params

                        card.setBackgroundResource(R.drawable.bg_card)


                        val dateText = TextView(this)
                        dateText.text = obj.getString("date")
                        dateText.setTextColor(getColor(R.color.textSecondary))
                        dateText.textSize = 14f


                        val avgText = TextView(this)
                        avgText.text = "${obj.getInt("avg")} ms"
                        avgText.setTextColor(getColor(R.color.primary))
                        avgText.textSize = 32f


                        val avgLabel = TextView(this)
                        avgLabel.text = "ŚREDNI CZAS"
                        avgLabel.setTextColor(getColor(R.color.textSecondary))
                        avgLabel.textSize = 12f


                        val row = LinearLayout(this)
                        row.orientation = LinearLayout.HORIZONTAL

                        val minText = TextView(this)
                        minText.text = "MIN\n${obj.getInt("min")} ms"
                        minText.setTextColor(getColor(R.color.textMain))
                        minText.textSize = 16f
                        minText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

                        val maxText = TextView(this)
                        maxText.text = "MAX\n${obj.getInt("max")} ms"
                        maxText.setTextColor(getColor(R.color.textMain))
                        maxText.textSize = 16f
                        maxText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

                        row.addView(minText)
                        row.addView(maxText)

                        card.addView(dateText)
                        card.addView(avgText)
                        card.addView(avgLabel)
                        card.addView(row)

                        container.addView(card)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            },
            Response.ErrorListener { error ->
                Log.e("RESULTS", "Błąd: ${error.message}")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userId.toString()
                return params
            }
        }

        queue.add(request)
    }
}