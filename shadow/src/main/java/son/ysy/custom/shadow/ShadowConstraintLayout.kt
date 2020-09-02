package son.ysy.custom.shadow

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import son.ysy.custom.shadow.drawable.ShadowDrawable

class ShadowConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    init {
        val shadowDrawable = getShadowDrawable()

        context.obtainStyledAttributes(attrs, R.styleable.ShadowConstraintLayout).apply {
            shadowDrawable.resetShowStart(
                getBoolean(
                    R.styleable.ShadowConstraintLayout_shadowClShowStart,
                    ShadowDrawable.Config.DEFAULT_SHOW_START
                )
            ).resetShowTop(
                getBoolean(
                    R.styleable.ShadowConstraintLayout_shadowClShowTop,
                    ShadowDrawable.Config.DEFAULT_SHOW_TOP
                )
            ).resetShowEnd(
                getBoolean(
                    R.styleable.ShadowConstraintLayout_shadowClShowEnd,
                    ShadowDrawable.Config.DEFAULT_SHOW_END
                )
            ).resetShowBottom(
                getBoolean(
                    R.styleable.ShadowConstraintLayout_shadowClShowBottom,
                    ShadowDrawable.Config.DEFAULT_SHOW_BOTTOM
                )
            ).resetShadowSize(
                getDimensionPixelSize(
                    R.styleable.ShadowConstraintLayout_shadowClSize,
                    ShadowDrawable.Config.DEFAULT_SHADOW_SIZE
                )
            ).resetShadowColor(
                getColor(
                    R.styleable.ShadowConstraintLayout_shadowClColor,
                    ShadowDrawable.Config.DEFAULT_SHADOW_COLOR
                )
            )
            getDimensionPixelSize(
                R.styleable.ShadowConstraintLayout_shadowClRadius,
                ShadowDrawable.Config.DEFAULT_RADIUS
            ).apply {
                shadowDrawable.resetTopStartRadius(
                    getDimensionPixelSize(
                        R.styleable.ShadowConstraintLayout_shadowClTopStartRadius,
                        -1
                    ).takeUnless { it == -1 }
                        ?: this
                ).resetTopEndRadius(
                    getDimensionPixelSize(
                        R.styleable.ShadowConstraintLayout_shadowClTopEndRadius,
                        -1
                    ).takeUnless { it == -1 }
                        ?: this
                ).resetBottomEndRadius(
                    getDimensionPixelSize(
                        R.styleable.ShadowConstraintLayout_shadowClBottomEndRadius,
                        -1
                    ).takeUnless { it == -1 }
                        ?: this
                ).resetBottomStartRadius(
                    getDimensionPixelSize(
                        R.styleable.ShadowConstraintLayout_shadowClBottomStartRadius,
                        -1
                    ).takeUnless { it == -1 }
                        ?: this
                )
            }
        }.recycle()
    }

    override fun setBackground(background: Drawable?) {
        val originBackground = this.background
        when {
            background is ShadowDrawable -> {
                super.setBackground(background)
            }
            originBackground is ShadowDrawable -> {
                originBackground.setWrappedDrawable(background)
            }
            else -> {
                super.setBackground(ShadowDrawable(background))
            }
        }
    }

    @Synchronized
    fun getShadowDrawable(): ShadowDrawable {
        val originBackground = background
        return if (originBackground is ShadowDrawable) {
            originBackground
        } else {
            ShadowDrawable(originBackground).apply {
                background = this
            }
        }
    }
}