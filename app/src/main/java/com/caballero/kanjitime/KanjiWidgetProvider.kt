package com.caballero.kanjitime

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews

class KanjiWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val repository = KanjiRepository(context)
        for (widgetId in appWidgetIds) {
            val entry = repository.randomEntry(widgetId)
            appWidgetManager.updateAppWidget(widgetId, buildRemoteViews(context, entry))
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?,
    ) {
        val entry = KanjiRepository(context).randomEntry(appWidgetId)
        appWidgetManager.updateAppWidget(appWidgetId, buildRemoteViews(context, entry))
    }

    private fun buildRemoteViews(context: Context, entry: KanjiEntry): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_kanji)
        views.setTextViewText(R.id.widget_kanji, entry.kanji)
        views.setTextViewText(R.id.widget_reading, entry.reading)
        views.setTextViewText(R.id.widget_meaning, entry.meaning)
        views.setContentDescription(
            R.id.widget_kanji,
            context.getString(R.string.widget_kanji_description, entry.kanji, entry.reading, entry.meaning),
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            applyMaterialYou(context, views)
        }

        val openIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
        return views
    }

    @SuppressLint("NewApi")
    private fun applyMaterialYou(context: Context, views: RemoteViews) {
        val resources = context.resources
        val night = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
        val background = if (night) android.R.color.system_neutral1_900 else android.R.color.system_neutral1_100
        val text = if (night) android.R.color.system_neutral1_100 else android.R.color.system_neutral1_900
        val accent = if (night) android.R.color.system_accent1_100 else android.R.color.system_accent1_700

        views.setInt(R.id.widget_root, "setBackgroundColor", resources.getColor(background, null))
        views.setTextColor(R.id.widget_kanji, resources.getColor(text, null))
        views.setTextColor(R.id.widget_reading, resources.getColor(accent, null))
        views.setTextColor(R.id.widget_meaning, resources.getColor(text, null))
    }

    companion object {
        fun updateProviderInfo(context: Context, intervalMs: Long) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, KanjiWidgetProvider::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                manager.updateAppWidgetProviderInfo(component, metaDataKey(intervalMs))
            }

            val ids = manager.getAppWidgetIds(component)
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, Uri.EMPTY)
                .setPackage(context.packageName)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }

        private fun metaDataKey(intervalMs: Long): String = when (intervalMs) {
            2L * HOUR_MS -> "kanji_widget_info_2h"
            6L * HOUR_MS -> "kanji_widget_info_6h"
            12L * HOUR_MS -> "kanji_widget_info_12h"
            24L * HOUR_MS -> "kanji_widget_info_24h"
            else -> "kanji_widget_info_1h"
        }

        private const val HOUR_MS = 60L * 60 * 1000
    }
}
