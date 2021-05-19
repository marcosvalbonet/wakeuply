package com.valbonet.wakeuplyapp.usecases

import com.valbonet.wakeuplyapp.data.DataRepository
import retrofit2.Call

class UpdateUserIdUseCase {

    val dataRepository = DataRepository()

    operator fun invoke(nickname: String?, userId: String?): Call<Boolean> {
        return dataRepository.updateUserId(nickname, userId)
    }
}