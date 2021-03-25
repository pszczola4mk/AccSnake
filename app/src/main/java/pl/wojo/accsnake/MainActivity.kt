package pl.wojo.accsnake

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.reflect.KMutableProperty


class MainActivity : AppCompatActivity(), SensorEventListener {

    var txtSensor: TextView? = null
    var imgView: ImageView? = null


    val screenWidth: Int = 1000
    val screenHeight: Int = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        this.txtSensor = findViewById<View>(R.id.txt_sensor) as TextView
        this.imgView = findViewById<View>(R.id.img_snake) as ImageView
        reset()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        this.txtSensor?.text = "Zniknij obrazek ;)"
    }

    fun reset() {
        val params = this.imgView!!.layoutParams as ConstraintLayout.LayoutParams
        params.leftMargin = 0
        params.rightMargin = 0
        params.topMargin = 0
        params.bottomMargin = 0
        this.imgView!!.layoutParams = params
    }

    override fun onSensorChanged(event: SensorEvent) {
        val gravity = arrayOf(0f, 0f, 0f)
        val linear_acceleration = arrayOf(0f, 0f, 0f)
        val alpha: Float = 0.8f
        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0]
        linear_acceleration[1] = event.values[1] - gravity[1]
        linear_acceleration[2] = event.values[2] - gravity[2]
        //
        val params = this.imgView!!.layoutParams as ConstraintLayout.LayoutParams
        moveDroid(linear_acceleration[0],params::rightMargin,params::leftMargin,this.screenWidth)
        moveDroid(linear_acceleration[1],params::topMargin,params::bottomMargin,this.screenHeight)

        this.imgView!!.layoutParams = params
        //

        //
        this.txtSensor?.text = String.format(
            "Gravity: %f, %f, %f, Acc: %f, %f, %f, Size: PM: %d, LM: %d, GM: %d, DM: %d, wymiary: %d x %d ",
            gravity[0],
            gravity[1],
            gravity[2],
            linear_acceleration[0],
            linear_acceleration[1],
            linear_acceleration[2],
            params.rightMargin,
            params.leftMargin,
            params.topMargin,
            params.bottomMargin,
            this.screenHeight,this.screenWidth
        )

    }

    private fun moveDroid(fl: Float, marginOne: KMutableProperty<Int>, marginTwo: KMutableProperty<Int>, border : Int) {
        val value = (fl * 10).toInt()
        if(value>0) {//przesuwamy w lewo
            //zwiększamy prawy margines
            var newValRight = marginOne.getter.call() + value
            if (newValRight > border) {
                newValRight = border
            }
            marginOne.setter.call(newValRight)
            //zmniejszamy lewy
            var newValLeft = marginTwo.getter.call() + ((-1) * value)
            if (newValLeft < 0) {
                newValLeft = 0
            }
            marginTwo.setter.call(newValLeft)
        }else{//przesuwamy w prawo
            //zwiększamy lewy margines
            var newValLeft = marginTwo.getter.call() + ((-1)*value)
            if (newValLeft > border) {
                newValLeft = border
            }
            marginTwo.setter.call(newValLeft)
            //zmniejszamy prawy
            var newValRight = marginOne.getter.call() + value
            if (newValRight < 0) {
                newValRight = 0
            }
            marginOne.setter.call(newValRight);
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}