package com.example.cred


import android.content.ClipData
import android.content.ClipDescription
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.cred.model.NetworkCalls
import com.example.loadinganimation.LoadingAnimation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity constructor(private val networkCalls: NetworkCalls = NetworkCalls()) :
    AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dragEvent()
        toggleListener()
        networkCalls.lambda = fun(success: Boolean) {
            if (success) {
                runOnUiThread() {
                    cardView.textMsg.text = "Success"
                }
            } else (runOnUiThread() {
                cardView.textMsg.text = "Failure"
            })
            val loadingAnim = findViewById<LoadingAnimation>(R.id.loadingAnim);
            loadingAnim.visibility = View.INVISIBLE
        }
    }


    private fun dragEvent() {
        ll1.setOnDragListener(dragListener())
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 500
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        val arrow = findViewById<ImageView>(R.id.downArrow)
        arrow.startAnimation(anim)
        dragIcon.setOnTouchListener { view, motionEvent ->
            val clipText = "Please Wait"
            val item = ClipData.Item(clipText)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clipText, mimeTypes, item)
            val dragShadowBuilder = View.DragShadowBuilder(view)
            view.startDragAndDrop(data, dragShadowBuilder, view, 0)
            view.visibility = View.INVISIBLE
            true
        }
    }


    private fun dragListener(): View.OnDragListener {
        val dragListener = View.OnDragListener { view, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    view.translationX = 0F
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val arrow = findViewById<ImageView>(R.id.downArrow)
                    arrow.visibility = View.INVISIBLE
                    arrow.clearAnimation()
                    val item = event.clipData.getItemAt(0)
                    val dragData = item.text
                    Toast.makeText(this, dragData, Toast.LENGTH_SHORT).show()

                    val v = event.localState as View
                    view.invalidate()
                    val owner = v.parent as ViewGroup
                    owner.removeView(v)

                    val destination = view as LinearLayout
                    destination.addView(v)
                    v.visibility = View.VISIBLE
                    item
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    val v = event.localState as View
                    v.visibility = View.VISIBLE
                    view.invalidate()
                    val loadingAnim = findViewById<LoadingAnimation>(R.id.loadingAnim);
                    when (event.result) {
                        true -> {
                            val toggle: SwitchCompat = findViewById(R.id.toggleButton)
                            Handler().postDelayed(({
                                cardView.textMsg.text = "Loading..."
                                val dragIcon: ImageView = findViewById(R.id.dragIcon)
                                dragIcon.visibility = View.INVISIBLE
                                val container: ImageView = findViewById(R.id.container)
                                container.visibility = View.INVISIBLE
                            }), 1000)

                            networkCalls.makeApiCall(toggle.isChecked)
                            loadingAnim.visibility = View.VISIBLE
                            loadingAnim.setProgressVector(resources.getDrawable(R.drawable.loading2))
                            loadingAnim.setEnlarge(5)
                        }
                        false -> {
                            Toast.makeText(this, "Try Again!!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    // Returns true; the value is ignored.
                    true

                }
                else -> false
            }
        }
        return dragListener
    }

    private fun toggleListener() {
        val toggleButton: SwitchCompat = findViewById(R.id.toggleButton)
        toggleButton.setOnClickListener {
            recreate()
        }
    }
}
