package com.valbonet.wakeuplyapp.usecases

import com.valbonet.wakeuplyapp.data.DataRepository
import retrofit2.Call

class UpdateSecUidUseCase {

    val dataRepository = DataRepository()

    operator fun invoke(nickname: String?, secUid: String?): Call<Boolean> {
        return dataRepository.updateSecUid(nickname, secUid)
    }
}