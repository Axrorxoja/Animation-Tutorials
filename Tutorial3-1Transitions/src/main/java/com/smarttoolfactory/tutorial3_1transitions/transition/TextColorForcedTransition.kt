package com.smarttoolfactory.tutorial3_1transitions.transition

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.smarttoolfactory.tutorial3_1transitions.R

/**
 *
 *  Transition that forces view state(property) to be set for starting and ending scenes.
 *
 * ```  transitionValues.values[PROPERTY_NAME] = property``` directly
 * sets property on **View** after capturing start values which causes
 * [captureEndValues] to be invoked with different set of values.
 *
 * * If starting scene and ending scene is equal in alpha this transition will not start because
 * [captureEndValues] will not capture anything other than start values.
 *
 */
class TextColorForcedTransition : Transition {

    private var startColor: Int = Color.BLACK
    private var endColor: Int = Color.WHITE


    /**
     * Forces start end values to be set on view such as setting a view's visibility as View.VISIBLE
     * at start and View.INVISIBLE for the end scene which forces scenes to have different values
     * and transition to start.
     *
     * * Has public visibility to debug scenes with [debugMode], with and without forced values.
     */
    var forceValues: Boolean = true

    /**
     * Logs lifecycle and parameters to console when set to true
     */
    var debugMode = false

    constructor(startColor: Int, endColor: Int, forceValues: Boolean = true) {
        this.startColor = startColor
        this.endColor = endColor
        this.forceValues = forceValues
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TextColorChange)
        startColor = a.getInteger(R.styleable.TextColorChange_startTextColor, startColor)
        endColor = a.getInteger(R.styleable.TextColorChange_endTextColor, endColor)
        a.recycle()
    }

    override fun captureStartValues(transitionValues: TransitionValues) {

        if (forceValues) {
            (transitionValues.view as? TextView)?.setTextColor(startColor)
        }

        captureValues(transitionValues)

        if (debugMode) {
            println("⚠️ ${this::class.java.simpleName}  captureStartValues() view: ${transitionValues.view} ")
            transitionValues.values.forEach { (key, value) ->
                println("Key: $key, value: $value")
            }
        }
    }

    // Capture the value of property for a target in the ending Scene.
    override fun captureEndValues(transitionValues: TransitionValues) {

        if (forceValues) {
            (transitionValues.view as? TextView)?.setTextColor(endColor)
        }

        captureValues(transitionValues)

        if (debugMode) {
            println("🔥 ${this::class.java.simpleName}  captureEndValues() view: ${transitionValues.view} ")
            transitionValues.values.forEach { (key, value) ->
                println("Key: $key, value: $value")
            }
        }
    }

    private fun captureValues(transitionValues: TransitionValues) {
        (transitionValues.view as? TextView)?.apply {
            transitionValues.values[PROPNAME_TEXT_COLOR] = this.currentTextColor
        }
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {

        if (debugMode) {
            println(
                "🎃 ${this::class.java.simpleName}  createAnimator() " +
                        "\nSTART VALUES: $startValues" +
                        "\nEND VALUES: $endValues "
            )
        }

        // This transition can only be applied to views that are on both starting and ending scenes.
        if (endValues == null || startValues == null) return null // no values

        // Store a convenient reference to the target. Both the starting and ending layout have the
        // same target.
        val view = (startValues.view as? TextView) ?: return null

        val startTextColor = startValues.values[PROPNAME_TEXT_COLOR] as Int
        val endTextColor = endValues.values[PROPNAME_TEXT_COLOR] as Int

        val animator: ValueAnimator =
            ValueAnimator.ofObject(ArgbEvaluator(), startTextColor, endTextColor)
        animator.addUpdateListener { animation ->
            view.setTextColor(animation.animatedValue as Int)
        }

        return animator
    }

    companion object {
        private const val PROPNAME_TEXT_COLOR = "PROPNAME_TEXT_COLOR"
    }

}