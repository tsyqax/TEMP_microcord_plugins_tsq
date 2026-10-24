package com.tsq.plugins

import dev.mantikafasi.manticord.plugins.api.ManticordPlugin
import dev.mantikafasi.manticord.plugins.api.PluginScope
import dev.mantikafasi.manticord.plugins.api.pluginMessageDecorator
import android.widget.TextView
import android.text.Spannable
import android.text.style.URLSpan
import rx.Completable

class NoPhoneLink : ManticordPlugin {
    override fun start(scope: PluginScope): Completable = Completable.fromAction {
        scope.ui.registerMessageDecorator(pluginMessageDecorator("disable_phone_link", decorate = { views, _ ->
            val spannable = (views.content as? TextView)?.text as? Spannable ?: return@pluginMessageDecorator
            spannable.getSpans(0, spannable.length, URLSpan::class.java)?.forEach { span ->
                if (span.url?.startsWith("tel:") == true) spannable.removeSpan(span)
            }
        }))
    }
}
