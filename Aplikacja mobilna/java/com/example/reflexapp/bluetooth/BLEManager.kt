package com.example.reflexapp.bluetooth


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.util.Log
import java.util.UUID

@SuppressLint("MissingPermission")
class BLEManager(private val context: Context, private val listener: BLEListener) {

    interface BLEListener {
        fun onConnected()
        fun onDisconnected()
        fun onResultReceived(result: String)
    }

    private val SERVICE_UUID = UUID.fromString("12345678-1234-1234-1234-1234567890ab")
    private val MODE_UUID = UUID.fromString("12345678-1234-1234-1234-1234567890ac")

    private val RESULT_UUID = UUID.fromString("12345678-1234-1234-1234-1234567890ad")

    var bluetoothGatt: BluetoothGatt? = null



    fun connect(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(
            context,
            false,
            gattCallback
        )
    }


    fun sendCommand( value: String) {

        val service = bluetoothGatt?.getService(SERVICE_UUID)
        val characteristic = service?.getCharacteristic(MODE_UUID)

        val data = value.toByteArray()

        if (characteristic != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                bluetoothGatt?.writeCharacteristic(
                    characteristic,
                    data,
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )

            } else {

                characteristic.value = data
                bluetoothGatt?.writeCharacteristic(characteristic)

            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {

                listener.onConnected()
                gatt.discoverServices()

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                listener.onDisconnected()
                Log.d("BLE", "Rozłączono")

            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {

            val service = gatt.getService(SERVICE_UUID)

            val resultChar = service?.getCharacteristic(RESULT_UUID)

            if (resultChar != null) {

                gatt.setCharacteristicNotification(resultChar, true)

                val descriptor = resultChar.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                )

                if (descriptor != null) {

                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)

                }

                Log.d("BLE", "Notify włączone")

            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {

            val result = String(characteristic.value)

            Log.d("BLE", "Wynik z Arduino: $result")

            listener.onResultReceived(result)
        }
    }
}