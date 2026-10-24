package com.tsq.plugins

import dev.mantikafasi.manticord.plugins.api.ManticordPlugin
import dev.mantikafasi.manticord.plugins.api.PluginScope
import dev.mantikafasi.manticord.plugins.api.pluginMessageDecorator
import dev.mantikafasi.manticord.plugins.api.pluginSettings
import android.widget.TextView
import android.util.TypedValue
import rx.Completable

class ChatFontSizer : ManticordPlugin {
    override val settingsSchema = pluginSettings {
        text("messagesFontScale", "Messages Font Size (SP)", default = "16")
    }

    override fun start(scope: PluginScope): Completable = Completable.fromAction {
        val ui = scope.ui

        ui.registerMessageDecorator(pluginMessageDecorator("chat_font_sizer", decorate = { views, _ ->
            try {
                val textView = views.content as? TextView
                if (textView != null) {
                    val fontSizeStr = scope.settings.getString("messagesFontScale", "15")
                    val targetSize = fontSizeStr.toFloatOrNull() ?: 15f
                    
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, targetSize)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }))
    }
}
