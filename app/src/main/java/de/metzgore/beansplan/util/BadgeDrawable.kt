package de.metzgore.beansplan.util

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import de.metzgore.beansplan.R

class BadgeDrawable(context: Context) : Drawable() {

    private val textSize: Float = context.resources.getDimension(R.dimen.badge_drawable_text_size)
    private val textPaint: Paint = Paint()
    private val textRect = Rect()

    private var number = ""
    private var willDraw = false

    init {

        textPaint.color = Color.WHITE
        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.textSize = textSize
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        if (!willDraw) {
            return
        }

        val bounds = bounds
        val width = (bounds.right - bounds.left).toFloat()
        val height = (bounds.bottom - bounds.top).toFloat()

        // Position the badge in the top-right quadrant of the icon.
        val centerX = width / 2
        val centerY = height / 2

        // Draw badge count text inside the circle.
        textPaint.getTextBounds(number, 0, number.length, textRect)
        val textHeight = (textRect.bottom - textRect.top).toFloat()
        val textY = centerY + textHeight / 2f
        canvas.drawText(number, centerX, textY, textPaint)
    }

    fun setNumber(number: Int) {
        this.number = Integer.toString(number)

        // Only draw a badge if there are notifications.
        willDraw = number in 1..31
        invalidateSelf()
    }

    override fun setAlpha(i: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }
}