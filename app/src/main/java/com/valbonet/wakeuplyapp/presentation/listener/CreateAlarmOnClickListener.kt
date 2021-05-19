package com.valbonet.wakeuplyapp.presentation.listener

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.valbonet.wakeuplyapp.model.Video
import com.valbonet.wakeuplyapp.model.item.Item
import com.valbonet.wakeuplyapp.presentation.alarmclock.ActivityAlarmClock
import com.valbonet.wakeuplyapp.presentation.alarmclock.AlarmTime
import com.valbonet.wakeuplyapp.presentation.alarmclock.DbAccessor
import com.valbonet.wakeuplyapp.utils.UrlUtils
import com.valbonet.wakeuplyapp.utils.Utils
import java.util.*

class CreateAlarmOnClickListener(val appContext: Activity, val item: Item?): View.OnClickListener {

    override fun onClick(v: View?) {

        val bundle: Bundle? = appContext.intent.extras
        var alarmId = if (bundle != null && bundle.containsKey("alarmID")) bundle.getLong("alarmID") else null

        val db = DbAccessor(appContext)
        val now = Calendar.getInstance()

        if (alarmId == null) {
            val time = AlarmTime(now[Calendar.HOUR_OF_DAY],
                    now[Calendar.MINUTE], 0)
            alarmId = db.newAlarm(time, false, "")
        }

        val settings = db.readAlarmSettings(alarmId)

        settings.nickMuser = item?.author?.nickname
        settings.urlMuser = UrlUtils().createMuserURL(item)
        settings.videoAlarmName = item?.desc
        settings.videoAlarmUrl = UrlUtils().createVideoURL(item)
        //Is it necessary?
        //settings.urlImageMuser = Utils.reviewURL(muser.getImg())

        db.writeAlarmSettings(alarmId, settings)
        db.closeConnections()

        val myIntent = Intent(appContext, ActivityAlarmClock::class.java)
        myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        appContext.startActivity(myIntent)

        appContext.finish()
    }
}