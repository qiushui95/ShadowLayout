package son.ysy.custom.shadow.drawable

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.Px
import java.lang.ref.WeakReference
import kotlin.math.min

class ShadowDrawable private constructor(
    private val builder: Builder
) : DrawableWrapper(builder.originDrawable) {

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

    class Builder(target: View) {

        private val targetReference = WeakReference(target)

        var originDrawable: Drawable
            private set

        init {
            val background = target.background

            originDrawable = when (background) {
                is DrawableWrapper -> {
                    background.wrapperDrawable
                }
                null -> {
                    ColorDrawable(Color.TRANSPARENT)
                }
                else -> {
                    background
                }
            }
        }

        var showStart: Boolean = Config.DEFAULT_SHOW_START
            private set
        var showTop: Boolean = Config.DEFAULT_SHOW_TOP
            private set
        var showEnd: Boolean = Config.DEFAULT_SHOW_END
            private set
        var showBottom: Boolean = Config.DEFAULT_SHOW_BOTTOM
            private set
        var topStartRadius: Int = Config.DEFAULT_RADIUS_TOP_START
            private set
        var topEndRadius: Int = Config.DEFAULT_RADIUS_TOP_END
            private set
        var bottomEndRadius: Int = Config.DEFAULT_RADIUS_BOTTOM_END
            private set
        var bottomStartRadius: Int = Config.DEFAULT_RADIUS_BOTTOM_START
            private set
        var shadowSize: Int = Config.DEFAULT_SHADOW_SIZE
            private set
        var shadowColor: Int = Config.DEFAULT_SHADOW_COLOR
            private set

        @CheckResult
        fun resetOriginDrawable(drawable: Drawable): Builder {
            this.originDrawable = drawable
            return this
        }

        @CheckResult
        fun resetShowStart(show: Boolean): Builder {
            this.showStart = show
            return this
        }

        @CheckResult
        fun resetShowTop(show: Boolean): Builder {
            this.showTop = show
            return this
        }

        @CheckResult
        fun resetShowEnd(show: Boolean): Builder {
            this.showEnd = show
            return this
        }

        @CheckResult
        fun resetShowBottom(show: Boolean): Builder {
            this.showBottom = show
            return this
        }

        @CheckResult
        fun resetRadius(@Px radius: Int): Builder {
            this.topStartRadius = radius
            this.topEndRadius = radius
            this.bottomEndRadius = radius
            this.bottomStartRadius = radius
            return this
        }

        @CheckResult
        fun resetTopStartRadius(@Px radius: Int): Builder {
            this.topStartRadius = radius
            return this
        }

        @CheckResult
        fun resetTopEndRadius(@Px radius: Int): Builder {
            this.topEndRadius = radius
            return this
        }

        @CheckResult
        fun resetBottomEndRadius(@Px radius: Int): Builder {
            this.bottomEndRadius = radius
            return this
        }

        @CheckResult
        fun resetBottomStartRadius(@Px radius: Int): Builder {
            this.bottomStartRadius = radius
            return this
        }

        @CheckResult
        fun resetShadowSize(@Px size: Int): Builder {
            this.shadowSize = size
            return this
        }

        @CheckResult
        fun resetShadowColor(@ColorInt color: Int): Builder {
            this.shadowColor = color
            return this
        }

        fun build() {
            targetReference.get()?.background = ShadowDrawable(this)
        }
    }

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


    override fun getPadding(padding: Rect): Boolean {
        val paddingStart = 0.takeUnless { builder.showStart } ?: builder.shadowSize
        val paddingTop = 0.takeUnless { builder.showTop } ?: builder.shadowSize
        val paddingEnd = 0.takeUnless { builder.showEnd } ?: builder.shadowSize
        val paddingBottom = 0.takeUnless { builder.showBottom } ?: builder.shadowSize
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
        val topStartRadius = min(builder.topStartRadius, maxRadius)
        val topEndRadius = min(builder.topEndRadius, maxRadius)
        val bottomEndRadius = min(builder.bottomEndRadius, maxRadius)
        val bottomStartRadius = min(builder.bottomStartRadius, maxRadius)
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
            builder.shadowSize * 1f,
            endColor,
            builder.shadowColor,
            Shader.TileMode.CLAMP
        )


        if (builder.showStart) {
            var start = builder.shadowSize * 1f
            val top = 0f
            var end = bounds.height() * 1f - builder.shadowSize
            val bottom = builder.shadowSize * 1f

            if (builder.showBottom) {
                start += bottomStartRadius
            } else {
                start -= builder.shadowSize
            }
            if (builder.showTop) {
                end -= topStartRadius
            } else {
                end += builder.shadowSize
            }
            startEdgeRectF.set(start, top, end, bottom)
        } else {
            startEdgeRectF.setEmpty()
        }

        if (builder.showTop) {
            var start = builder.shadowSize * 1f
            val top = 0f
            var end = bounds.width() * 1f - builder.shadowSize
            val bottom = builder.shadowSize * 1f

            if (builder.showStart) {
                start += topStartRadius
            } else {
                start -= builder.shadowSize
            }
            if (builder.showEnd) {
                end -= topEndRadius
            } else {
                end += builder.shadowSize
            }
            topEdgeRectF.set(start, top, end, bottom)
        } else {
            topEdgeRectF.setEmpty()
        }

        if (builder.showEnd) {
            var start = builder.shadowSize * 1f
            val top = 0f
            var end = bounds.height() * 1f - builder.shadowSize
            val bottom = builder.shadowSize * 1f

            if (builder.showTop) {
                start += topEndRadius
            } else {
                start -= builder.shadowSize
            }
            if (builder.showBottom) {
                end -= bottomEndRadius
            } else {
                end += builder.shadowSize
            }
            endEdgeRectF.set(start, top, end, bottom)
        } else {
            endEdgeRectF.setEmpty()
        }

        if (builder.showBottom) {
            var start = builder.shadowSize * 1f
            val top = 0f
            var end = bounds.width() * 1f - builder.shadowSize
            val bottom = builder.shadowSize * 1f

            if (builder.showEnd) {
                start += bottomEndRadius
            } else {
                start -= builder.shadowSize
            }
            if (builder.showStart) {
                end -= bottomStartRadius
            } else {
                end += builder.shadowSize
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
        if (builder.showStart && builder.showTop) {
            val center = builder.shadowSize * 1f + topStartRadius
            topStartCornerPaint.shader = buildRadialGradient(center, topStartRadius * 1f)
            resetPath(topStartCornerPath, center, topStartRadius * 1f)
        } else {
            topStartCornerPaint.shader = null
        }

        topEndCornerPath.reset()
        if (builder.showTop && builder.showEnd) {
            val center = builder.shadowSize * 1f + topEndRadius
            topEndCornerPaint.shader = buildRadialGradient(center, topEndRadius * 1f)
            resetPath(topEndCornerPath, center, topEndRadius * 1f)
        } else {
            topEndCornerPaint.shader = null
        }

        bottomEndCornerPath.reset()
        if (builder.showEnd && builder.showBottom) {
            val center = builder.shadowSize * 1f + bottomEndRadius
            bottomEndCornerPaint.shader = buildRadialGradient(center, bottomEndRadius * 1f)
            resetPath(bottomEndCornerPath, center, bottomEndRadius * 1f)

        } else {
            bottomEndCornerPaint.shader = null
        }

        bottomStartCornerPath.reset()
        if (builder.showBottom && builder.showStart) {
            val center = builder.shadowSize * 1f + bottomStartRadius
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
            intArrayOf(edgeColor, builder.shadowColor, edgeColor),
            floatArrayOf(0f, radius / center, 1f),
            Shader.TileMode.CLAMP
        )
    }

    private fun resetPath(path: Path, center: Float, radius: Float) {
        path.moveTo(0f, center)
        rectF.set(0f, 0f, center * 2f, center * 2f)
        path.arcTo(rectF, 180f, 90f)
        path.rLineTo(0f, builder.shadowSize * 1f)
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
        if (builder.showStart && builder.showTop) {
            canvas.drawPath(topStartCornerPath, topStartCornerPaint)
        }

        //top end corner
        if (builder.showTop && builder.showEnd) {
            canvas.save()
            canvas.translate(contentRegion.width(), 0f)
            canvas.rotate(90f)
            canvas.drawPath(topEndCornerPath, topEndCornerPaint)
            canvas.restore()
        }

        //bottom end corner
        if (builder.showEnd && builder.showBottom) {
            canvas.save()
            canvas.translate(contentRegion.width(), contentRegion.height())
            canvas.rotate(180f)
            canvas.drawPath(bottomEndCornerPath, bottomEndCornerPaint)
            canvas.restore()
        }

        //bottom start corner
        if (builder.showBottom && builder.showStart) {
            canvas.save()
            canvas.translate(0f, contentRegion.height())
            canvas.rotate(270f)
            canvas.drawPath(bottomStartCornerPath, bottomStartCornerPaint)
            canvas.restore()
        }
    }
}