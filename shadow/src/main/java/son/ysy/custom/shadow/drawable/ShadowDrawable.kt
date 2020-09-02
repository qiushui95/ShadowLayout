package son.ysy.custom.shadow.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import kotlin.math.min

class ShadowDrawable internal constructor(originDrawable: Drawable?) :
    DrawableWrapper(originDrawable) {

    object Config {
        const val DEFAULT_SHOW_START = true
        const val DEFAULT_SHOW_TOP = true
        const val DEFAULT_SHOW_END = true
        const val DEFAULT_SHOW_BOTTOM = true

        const val DEFAULT_RADIUS = 20
        const val DEFAULT_RADIUS_TOP_START = DEFAULT_RADIUS
        const val DEFAULT_RADIUS_TOP_END = DEFAULT_RADIUS
        const val DEFAULT_RADIUS_BOTTOM_START = DEFAULT_RADIUS
        const val DEFAULT_RADIUS_BOTTOM_END = DEFAULT_RADIUS

        const val DEFAULT_SHADOW_SIZE = 20

        val DEFAULT_SHADOW_COLOR = Color.parseColor("#30000000")
    }

    private var showStart: Boolean = Config.DEFAULT_SHOW_START
    private var showTop: Boolean = Config.DEFAULT_SHOW_TOP
    private var showEnd: Boolean = Config.DEFAULT_SHOW_END
    private var showBottom: Boolean = Config.DEFAULT_SHOW_BOTTOM
    private var topStartRadius: Int = Config.DEFAULT_RADIUS_TOP_START
    private var topEndRadius: Int = Config.DEFAULT_RADIUS_TOP_END
    private var bottomEndRadius: Int = Config.DEFAULT_RADIUS_BOTTOM_END
    private var bottomStartRadius: Int = Config.DEFAULT_RADIUS_BOTTOM_START
    private var shadowSize: Int = Config.DEFAULT_SHADOW_SIZE
    private var shadowColor: Int = Config.DEFAULT_SHADOW_COLOR

    private var dirty = true

    private val contentRegion = RectF()

    private val edgePaint = Paint()

    private val startEdgeRectF = RectF()
    private val topEdgeRectF = RectF()
    private val endEdgeRectF = RectF()
    private val bottomEdgeRectF = RectF()

    private val topStartCornerPaint = Paint()
    private val topStartCornerPath = Path()

    private val topEndCornerPaint = Paint()
    private val topEndCornerPath = Path()

    private val bottomEndCornerPaint = Paint()
    private val bottomEndCornerPath = Path()

    private val bottomStartCornerPaint = Paint()
    private val bottomStartCornerPath = Path()

    private val rect = Rect()

    private val rectF = RectF()

    private fun propertyChanged(): ShadowDrawable {
        dirty = true
        return this
    }

    fun resetShowStart(show: Boolean): ShadowDrawable {
        this.showStart = show
        return propertyChanged()
    }

    fun resetShowTop(show: Boolean): ShadowDrawable {
        this.showTop = show
        return propertyChanged()
    }

    fun resetShowEnd(show: Boolean): ShadowDrawable {
        this.showEnd = show
        return propertyChanged()
    }

    fun resetShowBottom(show: Boolean): ShadowDrawable {
        this.showBottom = show
        return propertyChanged()
    }

    fun resetRadius(@Px radius: Int): ShadowDrawable {
        this.topStartRadius = radius
        this.topEndRadius = radius
        this.bottomEndRadius = radius
        this.bottomStartRadius = radius
        return propertyChanged()
    }

    fun resetTopStartRadius(@Px radius: Int): ShadowDrawable {
        this.topStartRadius = radius
        return propertyChanged()
    }

    fun resetTopEndRadius(@Px radius: Int): ShadowDrawable {
        this.topEndRadius = radius
        return propertyChanged()
    }

    fun resetBottomEndRadius(@Px radius: Int): ShadowDrawable {
        this.bottomEndRadius = radius
        return propertyChanged()
    }

    fun resetBottomStartRadius(@Px radius: Int): ShadowDrawable {
        this.bottomStartRadius = radius
        return propertyChanged()
    }

    fun resetShadowSize(@Px size: Int): ShadowDrawable {
        this.shadowSize = size
        return propertyChanged()
    }

    fun resetShadowColor(@ColorInt color: Int): ShadowDrawable {
        this.shadowColor = color
        return propertyChanged()
    }

    override fun getPadding(padding: Rect): Boolean {
        val paddingStart = 0.takeUnless { showStart } ?: shadowSize
        val paddingTop = 0.takeUnless { showTop } ?: shadowSize
        val paddingEnd = 0.takeUnless { showEnd } ?: shadowSize
        val paddingBottom = 0.takeUnless { showBottom } ?: shadowSize
        padding.set(paddingStart, paddingTop, paddingEnd, paddingBottom)
        return true
    }

    override fun setAlpha(alpha: Int) {
        super.setAlpha(alpha)
        edgePaint.alpha = alpha
        topStartCornerPaint.alpha = alpha
        topEndCornerPaint.alpha = alpha
        bottomEndCornerPaint.alpha = alpha
        bottomStartCornerPaint.alpha = alpha
    }

    override fun onBoundsChange(bounds: Rect) {
        dirty = true
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun draw(canvas: Canvas) {
        if (dirty) {
            buildComponents(bounds)
            dirty = false
        }
        drawShadow(canvas)
        super.draw(canvas)
    }

    private fun buildComponents(bounds: Rect) {
        contentRegion.set(bounds)
        getPadding(rect)
        wrapperDrawable.setBounds(
            rect.left,
            rect.top,
            bounds.width() - rect.right,
            bounds.height() - rect.bottom
        )
        val maxRadius = min(bounds.width() / 2, bounds.height() / 2)
        val topStartRadius = min(topStartRadius, maxRadius)
        val topEndRadius = min(topEndRadius, maxRadius)
        val bottomEndRadius = min(bottomEndRadius, maxRadius)
        val bottomStartRadius = min(bottomStartRadius, maxRadius)
        buildEdgeComponents(
            bounds,
            topStartRadius,
            topEndRadius,
            bottomEndRadius,
            bottomStartRadius
        )
        buildCornerComponents(topStartRadius, topEndRadius, bottomEndRadius, bottomStartRadius)
    }

    private fun buildEdgeComponents(
        bounds: Rect,
        topStartRadius: Int,
        topEndRadius: Int,
        bottomEndRadius: Int,
        bottomStartRadius: Int
    ) {
        val endColor = Color.TRANSPARENT

        edgePaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            shadowSize * 1f,
            endColor,
            shadowColor,
            Shader.TileMode.CLAMP
        )


        if (showStart) {
            var start = shadowSize * 1f
            val top = 0f
            var end = bounds.height() * 1f - shadowSize
            val bottom = shadowSize * 1f

            if (showBottom) {
                start += bottomStartRadius
            } else {
                start -= shadowSize
            }
            if (showTop) {
                end -= topStartRadius
            } else {
                end += shadowSize
            }
            startEdgeRectF.set(start, top, end, bottom)
        } else {
            startEdgeRectF.setEmpty()
        }

        if (showTop) {
            var start = shadowSize * 1f
            val top = 0f
            var end = bounds.width() * 1f - shadowSize
            val bottom = shadowSize * 1f

            if (showStart) {
                start += topStartRadius
            } else {
                start -= shadowSize
            }
            if (showEnd) {
                end -= topEndRadius
            } else {
                end += shadowSize
            }
            topEdgeRectF.set(start, top, end, bottom)
        } else {
            topEdgeRectF.setEmpty()
        }

        if (showEnd) {
            var start = shadowSize * 1f
            val top = 0f
            var end = bounds.height() * 1f - shadowSize
            val bottom = shadowSize * 1f

            if (showTop) {
                start += topEndRadius
            } else {
                start -= shadowSize
            }
            if (showBottom) {
                end -= bottomEndRadius
            } else {
                end += shadowSize
            }
            endEdgeRectF.set(start, top, end, bottom)
        } else {
            endEdgeRectF.setEmpty()
        }

        if (showBottom) {
            var start = shadowSize * 1f
            val top = 0f
            var end = bounds.width() * 1f - shadowSize
            val bottom = shadowSize * 1f

            if (showEnd) {
                start += bottomEndRadius
            } else {
                start -= shadowSize
            }
            if (showStart) {
                end -= bottomStartRadius
            } else {
                end += shadowSize
            }
            bottomEdgeRectF.set(start, top, end, bottom)
        } else {
            bottomEdgeRectF.setEmpty()
        }
    }

    private fun buildCornerComponents(
        topStartRadius: Int,
        topEndRadius: Int,
        bottomEndRadius: Int,
        bottomStartRadius: Int
    ) {
        topStartCornerPath.reset()
        if (showStart && showTop) {
            val center = shadowSize * 1f + topStartRadius
            topStartCornerPaint.shader = buildRadialGradient(center, topStartRadius * 1f)
            resetPath(topStartCornerPath, center, topStartRadius * 1f)
        } else {
            topStartCornerPaint.shader = null
        }

        topEndCornerPath.reset()
        if (showTop && showEnd) {
            val center = shadowSize * 1f + topEndRadius
            topEndCornerPaint.shader = buildRadialGradient(center, topEndRadius * 1f)
            resetPath(topEndCornerPath, center, topEndRadius * 1f)
        } else {
            topEndCornerPaint.shader = null
        }

        bottomEndCornerPath.reset()
        if (showEnd && showBottom) {
            val center = shadowSize * 1f + bottomEndRadius
            bottomEndCornerPaint.shader = buildRadialGradient(center, bottomEndRadius * 1f)
            resetPath(bottomEndCornerPath, center, bottomEndRadius * 1f)

        } else {
            bottomEndCornerPaint.shader = null
        }

        bottomStartCornerPath.reset()
        if (showBottom && showStart) {
            val center = shadowSize * 1f + bottomStartRadius
            bottomStartCornerPaint.shader = buildRadialGradient(center, bottomStartRadius * 1f)
            resetPath(bottomStartCornerPath, center, bottomStartRadius * 1f)
        } else {
            bottomStartCornerPaint.shader = null
        }
    }

    private fun buildRadialGradient(center: Float, radius: Float): RadialGradient {
        val edgeColor = Color.TRANSPARENT

        return RadialGradient(
            center,
            center,
            center,
            intArrayOf(edgeColor, shadowColor, edgeColor),
            floatArrayOf(0f, radius / center, 1f),
            Shader.TileMode.CLAMP
        )
    }

    private fun resetPath(path: Path, center: Float, radius: Float) {
        path.moveTo(0f, center)
        rectF.set(0f, 0f, center * 2f, center * 2f)
        path.arcTo(rectF, 180f, 90f)
        path.rLineTo(0f, shadowSize * 1f)
        rectF.set(center - radius, center - radius, center + radius, center + radius)
        path.arcTo(rectF, -90f, -90f)
        path.close()
    }

    private fun drawShadow(canvas: Canvas) {
        drawEdge(canvas)
        drawCorner(canvas)
    }

    private fun drawEdge(canvas: Canvas) {
        //start edge
        if (!startEdgeRectF.isEmpty) {
            canvas.save()
            canvas.translate(0f, contentRegion.height())
            canvas.rotate(-90f)
            canvas.drawRect(startEdgeRectF, edgePaint)
            canvas.restore()
        }

        //top edge
        if (!topEdgeRectF.isEmpty) {
            canvas.drawRect(topEdgeRectF, edgePaint)
        }

        //end edge
        if (!endEdgeRectF.isEmpty) {
            canvas.save()
            canvas.translate(contentRegion.width(), 0f)
            canvas.rotate(90f)
            canvas.drawRect(endEdgeRectF, edgePaint)
            canvas.restore()
        }

        //bottom edge
        if (!bottomEdgeRectF.isEmpty) {
            canvas.save()
            canvas.translate(contentRegion.width(), contentRegion.height())
            canvas.rotate(180f)
            canvas.drawRect(bottomEdgeRectF, edgePaint)
            canvas.restore()
        }
    }

    private fun drawCorner(canvas: Canvas) {
        //top start corner
        if (showStart && showTop) {
            canvas.drawPath(topStartCornerPath, topStartCornerPaint)
        }

        //top end corner
        if (showTop && showEnd) {
            canvas.save()
            canvas.translate(contentRegion.width(), 0f)
            canvas.rotate(90f)
            canvas.drawPath(topEndCornerPath, topEndCornerPaint)
            canvas.restore()
        }

        //bottom end corner
        if (showEnd && showBottom) {
            canvas.save()
            canvas.translate(contentRegion.width(), contentRegion.height())
            canvas.rotate(180f)
            canvas.drawPath(bottomEndCornerPath, bottomEndCornerPaint)
            canvas.restore()
        }

        //bottom start corner
        if (showBottom && showStart) {
            canvas.save()
            canvas.translate(0f, contentRegion.height())
            canvas.rotate(270f)
            canvas.drawPath(bottomStartCornerPath, bottomStartCornerPaint)
            canvas.restore()
        }
    }
}