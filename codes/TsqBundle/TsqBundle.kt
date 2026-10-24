package com.tsq.plugins

import dev.mantikafasi.manticord.plugins.api.ManticordPlugin
import dev.mantikafasi.manticord.plugins.api.PluginScope
import dev.mantikafasi.manticord.plugins.api.pluginMessageDecorator
import dev.mantikafasi.manticord.plugins.api.pluginSettings

import android.widget.TextView
import android.text.Spannable
import android.text.style.URLSpan
import android.util.TypedValue
import android.util.Log
import rx.Completable

class TsqBundle: ManticordPlugin {
	private var bsBool = 0
	
	override val settingsSchema = pluginSettings {
		action(key = "nt_desc1", title = "[Notice]", description = "Changes will take effect after toggling the plugin off and on.") {}
		section("[1]")
        toggle("ena_npl", "Enable NoPhoneLink", default = true)
        section("[2]")
	    toggle("ena_cfs", "Enable ChatFontSizer", default = false)
		text("cfs_fontsize", "└ Chat Font Size (SP)", default = "15")
    }
	
	companion object {
		private const val LT = "TsqBundle"
        const val npl = 0
        const val cfs = 1

    }
	
	fun updateFlag(index: Int, value: Boolean) {
		val bit = if (value) 1 else 0
		bsBool = (bsBool and (1 shl index).inv()) or (bit shl index)
	}
	
	fun getFlag(index: Int): Boolean {
		return (bsBool and (1 shl index)) != 0
	}
	
	override fun start(scope: PluginScope): Completable = Completable.fromAction {
		val ui = scope.ui
		val settings = scope.settings
		
		Log.i(LT, "Started, If you have others already in this, then turn off them!")

		updateFlag(npl, settings.getBoolean("ena_npl", true))
		updateFlag(cfs, settings.getBoolean("ena_cfs", false))

		Log.i(LT, "enabled:" + java.lang.Integer.bitCount(bsBool))
		
		if (getFlag(npl)) {
			ui.registerMessageDecorator(pluginMessageDecorator("disable_phone_link", decorate = { views, _ ->
				val spannable = (views.content as? TextView)?.text as? Spannable ?: return@pluginMessageDecorator
				spannable.getSpans(0, spannable.length, URLSpan::class.java)?.forEach { span ->
					if (span.url?.startsWith("tel:") == true) spannable.removeSpan(span)
				}
			}))
		}
		
		if (getFlag(cfs)) {
			val fontSizeStr = settings.getString("messagesFontScale", "15")
			val targetSize = fontSizeStr.toFloatOrNull() ?: 15f
			
			ui.registerMessageDecorator(pluginMessageDecorator("chat_font_sizer", decorate = { views, _ ->
				try {
					val textView = views.content as? TextView
					if (textView != null) {
						textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, targetSize)
					}
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}))
		}
	}
}

