package com.valbonet.wakeuplyapp.usecases

import com.valbonet.wakeuplyapp.data.DataRepository
import com.valbonet.wakeuplyapp.model.search.Muser
import io.reactivex.Observable

class GetMusersUseCase {

    val dataRepository = DataRepository()

    operator fun invoke(offset: String?, sizeList: String?): Observable<List<Muser>> {
        return dataRepository.getMusersList(offset, sizeList)
    }
}