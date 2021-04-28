package com.techyourchance.coroutines.demonstrations.concurrentcoroutines

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.techyourchance.coroutines.R
import com.techyourchance.coroutines.common.BaseFragment
import com.techyourchance.coroutines.common.ThreadInfoLogger
import com.techyourchance.coroutines.home.ScreenReachableFromHome
import kotlinx.coroutines.*
import org.json.JSONException

//video 14
class ConcurrentCoroutinesDemoFragment : BaseFragment() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    override val screenTitle get() = ScreenReachableFromHome.CONCURRENT_COROUTINES_DEMO.description

    private lateinit var btnStart: Button
    private lateinit var txtRemainingTime: TextView

    private var jobCounter: Job? = null
    private var job: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_loop_iterations_demo, container, false)

        txtRemainingTime = view.findViewById(R.id.txt_remaining_time)

        btnStart = view.findViewById(R.id.btn_start)
        btnStart.setOnClickListener {
            logThreadInfo("button callback")

            val benchmarkDurationSeconds = 5


            // Creating another coroutine to run concurrently the
            // updateRemainingTime and the Benchmark
            jobCounter = coroutineScope.launch {
                updateRemainingTime(benchmarkDurationSeconds)
                println("result: update remaining coroutine")
            }

            // This coroutine or "Job is running on the Main thread
            job = coroutineScope.launch {
                btnStart.isEnabled = false
                val iterationsCount = executeBenchmark(benchmarkDurationSeconds)
                Toast.makeText(requireContext(), "$iterationsCount", Toast.LENGTH_SHORT).show()
                btnStart.isEnabled = true
            }
        }

        return view
    }

    override fun onStop() {
        logThreadInfo("onStop()")
        super.onStop()
        job?.cancel()
        btnStart.isEnabled = true

        // If jobCounter Has ever been started or Launch, the apply
        // cancel and show done!
        jobCounter?.apply {
            cancel()
            txtRemainingTime.text = "done!"
        }
    }

    private suspend fun executeBenchmark(benchmarkDurationSeconds: Int) = withContext(Dispatchers.Default) {
        logThreadInfo("benchmark started")

        val stopTimeNano = System.nanoTime() + benchmarkDurationSeconds * 1_000_000_000L

        var iterationsCount: Long = 0
        while (System.nanoTime() < stopTimeNano) {
            iterationsCount++
        }

        logThreadInfo("benchmark completed")

        iterationsCount
    }

    private suspend fun updateRemainingTime(remainingTimeSeconds: Int) {
        for (time in remainingTimeSeconds downTo 0) {
            if (time > 0) {
                logThreadInfo("updateRemainingTime: $time seconds")
                txtRemainingTime.text = "$time seconds remaining"
                delay(1000)
            } else {
                txtRemainingTime.text = "done!"
            }
        }
    }

/*    private fun requestFun() {

        val requestQueue = Volley.newRequestQueue(context)

        val url = "https://pastebin.com/raw/Em972E5s"

        val jsonArray = JsonArrayRequest(url,
            { response ->
                try {
                    for (i in 0 until response.length()) {
                        val myObject = response.getJSONObject(i)

                        val firstName = myObject.getString("firstname")

                        Log.d("object ${i+1} is:", "$myObject")
                        Log.d("Firstname is:", firstName)
                    }

                }catch (e: JSONException) {e.printStackTrace()}
            },
            { error ->
                    Log.d("Error is:", error.toString())
            })

        requestQueue?.add(jsonArray)

    }*/

    private fun logThreadInfo(message: String) {
        ThreadInfoLogger.logThreadInfo(message)
    }

    companion object {
        fun newInstance(): Fragment {
            return ConcurrentCoroutinesDemoFragment()
        }
    }
}