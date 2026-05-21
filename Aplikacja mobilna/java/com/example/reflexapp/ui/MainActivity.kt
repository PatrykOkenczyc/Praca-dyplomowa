package com.example.reflexapp.ui

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.reflexapp.R
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.annotation.SuppressLint
import android.widget.Toast
import android.os.Handler
import android.os.Looper


import com.example.reflexapp.bluetooth.BLEManager



class MainActivity : AppCompatActivity(), BLEManager.BLEListener {

    private var isConnected = false

    private var resultHandled  = false
    private var gameMode = ""
    private var buffer  = ""
    private lateinit var btnConnect: ImageView

    private lateinit var btnSettings: ImageView

    private lateinit var ledConnected: ImageView
    private lateinit var ledDisconnected: ImageView

    private lateinit var btnStartTest: ImageView
    private lateinit var btnOnePlayer: ImageView
    private lateinit var btnTwoPlayers: ImageView
    private lateinit var btnInfo: ImageView


    private lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bleManager: BLEManager

    override fun onCreate(savedInstanceState: Bundle?) {
        testConnection()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        btnSettings = findViewById(R.id.imgSettings)

        btnConnect = findViewById(R.id.btnConnect)

        ledConnected = findViewById(R.id.LedConnected)
        ledDisconnected = findViewById(R.id.LedDisconnceted)

        btnStartTest = findViewById(R.id.btnStartTest)
        btnOnePlayer = findViewById(R.id.btnOnePlayer)
        btnTwoPlayers = findViewById(R.id.btnTwoPlayers)
        btnInfo = findViewById(R.id.btnInfo)


        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        bleManager = BLEManager(this , this)



        btnConnect.setOnClickListener {
            startScan()
        }

        btnSettings.setOnClickListener {
            bleManager.sendCommand("devicetest")
        }

        btnStartTest.setOnClickListener {
            bleManager.sendCommand("test")
        }

        btnOnePlayer.setOnClickListener {
            buffer = ""
            resultHandled = false
            gameMode = "single"
            bleManager.sendCommand("single")
        }

        btnTwoPlayers.setOnClickListener {
            buffer = ""
            resultHandled = false
            gameMode = "multi"
            bleManager.sendCommand("multi")
        }

        btnInfo.setOnClickListener {
            val prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE)
            val userId = prefs.getInt("user_id", -1)
            val intent = Intent(this, ResultsListActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

    }

    override fun onConnected() {
        runOnUiThread {
            updateConnectionStatus(true)
        }
    }

    override fun onDisconnected() {
        runOnUiThread {
            updateConnectionStatus(false)
        }
    }

    override fun onResultReceived(result: String) {

        runOnUiThread {

            Log.d("BLE", "Czesc: $result")

            buffer += result

            if (gameMode == "single") {
                if (buffer.count { it == ',' } < 2) {
                    return@runOnUiThread
                }
            } else if (gameMode == "multi") {
                if (buffer.count { it == ',' } < 5) {
                    return@runOnUiThread
                }
            }

            if (resultHandled) return@runOnUiThread
            resultHandled = true

            val fullResult = buffer
            buffer = ""

            Log.d("BLE", "Dane: $fullResult")

            val parts = fullResult.split(",")
            if (gameMode == "single" && parts.size < 3) {
                Log.e("BLE", "Niepełne dane SINGLE: $fullResult")
                return@runOnUiThread
            }

            if (gameMode == "multi" && parts.size < 6) {
                Log.e("BLE", "Niepełne dane MULTI: $fullResult")
                return@runOnUiThread
            }

            if (gameMode == "single") {

                sendResultToServer(fullResult)

                val intent = Intent(this, ResultsSingleActivity::class.java)
                intent.putExtra("wynik", fullResult)
                startActivity(intent)

            } else if (gameMode == "multi") {

                val intent = Intent(this, ResultsMultiActivity::class.java)
                intent.putExtra("wynik", fullResult)
                startActivity(intent)
            }
        }
    }

    private fun updateConnectionStatus(connected: Boolean) {
        isConnected = connected
        if (isConnected) {
            ledConnected.visibility = View.VISIBLE
            ledDisconnected.visibility = View.INVISIBLE
        } else {
            ledConnected.visibility = View.INVISIBLE
            ledDisconnected.visibility = View.VISIBLE
        }
    }



    private fun testConnection() {
        val url = "http://192.168.0.216/reflexapp/test_connection.php"

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.d("DB_TEST", "Server response: $response")
            },
            { error ->
                Log.e("DB_TEST", "Error: ${error.message}")
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    @SuppressLint("MissingPermission")
    private fun startScan() {

        val scanner = bluetoothAdapter.bluetoothLeScanner
        var deviceFound = false

        val callback = object : ScanCallback() {

            override fun onScanResult(callbackType: Int, result: ScanResult) {

                val device = result.device

                if (device.name == "ReflexDevice") {

                    deviceFound = true
                    scanner.stopScan(this)
                    bleManager.connect(device)
                    Log.d("BLE", "Łączenie z Arduino")
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("BLE", "Scan failed: $errorCode")
            }
        }

        scanner.startScan(callback)
        Handler(Looper.getMainLooper()).postDelayed({

            scanner.stopScan(callback)

            if (!deviceFound) {
                Toast.makeText(
                    this@MainActivity,
                    "Nie znaleziono urządzenia!",
                    Toast.LENGTH_LONG
                ).show()
            }

        }, 5000)
    }

    private fun sendResultToServer(result: String) {

        val parts = result.split(",")

        val avg = parts[0]
        val min = parts[1]
        val max = parts[2]

        val prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE)
        val userId = prefs.getInt("user_id", -1)

        val url = "http://192.168.0.216/reflexapp/save_score.php"

        val request = object : StringRequest(
            Method.POST,
            url,
            { response ->
                Log.d("DB", "Server response: $response")
            },
            { error ->
                Log.e("DB", "Error: ${error.message}")
            }
        ) {

            override fun getParams(): MutableMap<String, String> {

                val params = HashMap<String, String>()

                params["user_id"] = userId.toString()
                params["avg"] = avg
                params["min"] = min
                params["max"] = max

                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

}

