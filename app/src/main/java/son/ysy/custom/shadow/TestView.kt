package son.ysy.custom.shadow

import android.content.Context
import android.util.AttributeSet
import android.view.View

class TestView(context: Context, attrs: AttributeSet?) : ShadowConstraintLayout(context, attrs) {
    init {
        View.inflate(context, R.layout.item_close, this)
        getShadowDrawableBuilder().resetShowTop(false)
            .resetRadius(20)
            .build()
    }
}