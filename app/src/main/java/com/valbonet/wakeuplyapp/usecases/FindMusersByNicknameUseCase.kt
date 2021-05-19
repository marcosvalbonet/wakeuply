package com.valbonet.wakeuplyapp.usecases

import com.valbonet.wakeuplyapp.data.DataRepository
import com.valbonet.wakeuplyapp.model.search.Muser
import io.reactivex.Observable

class FindMusersByNicknameUseCase {

    val dataRepository = DataRepository()

    operator fun invoke(nickname: String?): Observable<List<Muser>> {
        return dataRepository.findMusersByNickname(nickname)
    }
}