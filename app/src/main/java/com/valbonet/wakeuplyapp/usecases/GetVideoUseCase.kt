package com.valbonet.wakeuplyapp.usecases

import com.valbonet.wakeuplyapp.data.DataRepository
import com.valbonet.wakeuplyapp.model.item.Tiktoker
import com.valbonet.wakeuplyapp.utils.UtilsVideo
import io.reactivex.Observable

class GetVideoUseCase {

    val dataRepository = DataRepository()

    operator fun invoke(nickname: String?, videoID: String?, cookie: String?): Observable<String> {
        return dataRepository.getVideoData(nickname, videoID, cookie)
    }
}