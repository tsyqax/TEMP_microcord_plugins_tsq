package com.tsq.plugins

import dev.mantikafasi.manticord.plugins.api.ManticordPlugin
import dev.mantikafasi.manticord.plugins.api.PluginScope
import dev.mantikafasi.manticord.plugins.api.PluginContextMenuSurface
import dev.mantikafasi.manticord.plugins.api.pluginContextMenuItem
import rx.Completable

import com.discord.utilities.rest.RestAPI
import com.discord.stores.StoreStream
import com.discord.R 

class ThreadDEL : ManticordPlugin {
    override fun start(scope: PluginScope): Completable = Completable.fromAction {
        val ui = scope.ui

        ui.registerContextMenuItem(
            pluginContextMenuItem(
                "channel_immediate_delete",
                "Delete Channel", 
                PluginContextMenuSurface.CHANNEL,
                onClick = { context ->
                    val targetId = context.channelId ?: return@pluginContextMenuItem
                    try {
                        scope.runOnMainThread { activity ->
                            val channelObj = StoreStream.getChannels().getChannel(targetId)
                            val channelName = channelObj?.name ?: "Channel"

                            val titleText = activity.getString(R.string.delete_channel)
                            val formattedBody = activity.getString(R.string.delete_channel_body).replace("!!{channelName}!!", channelName)

                            scope.dialogs.alert(titleText, formattedBody)
                            executeDelete(targetId)
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                }
            ) { priority = 25; isVisible = { it.channelId != null } }
        )

        ui.registerContextMenuItem(
            pluginContextMenuItem(
                "thread_immediate_delete",
                "Delete Thread", 
                PluginContextMenuSurface.THREAD,
                onClick = { context ->
                    val targetId = context.channelId ?: return@pluginContextMenuItem
                    try {
                        scope.runOnMainThread { activity ->
                            val channelObj = StoreStream.getChannels().getChannel(targetId)
                            val channelName = channelObj?.name ?: "Thread"

                            val titleText = activity.getString(R.string.delete_thread)
                            val formattedBody = activity.getString(R.string.delete_channel_body).replace("!!{channelName}!!", channelName)

                            scope.dialogs.alert(titleText, formattedBody)
                            executeDelete(targetId)
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                }
            ) { priority = 26; isVisible = { it.channelId != null } }
        )
    }

    private fun executeDelete(targetId: Long) {
        RestAPI.api.deleteChannel(targetId)
            .subscribe({
                StoreStream.getMessagesLoader().jumpToMessage(1L, 1L)
            }, { error ->
                error.printStackTrace()
            })
    }
}
