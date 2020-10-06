package com.kola.cleannotes.business.data.network

import com.google.protobuf.Api
import com.kola.cleannotes.business.data.cache.CacheErrors
import com.kola.cleannotes.business.domain.state.DataState
import com.kola.cleannotes.business.domain.state.MessageType
import com.kola.cleannotes.business.domain.state.StateEvent
import com.kola.cleannotes.business.domain.state.UIComponentType
import com.squareup.okhttp.Response

abstract class ApiResponseHandler <ViewState,Data>(
    private val  response: ApiResult<Data?>,
    private val  stateEvent: StateEvent?
){
suspend fun getResult():DataState<ViewState>{
    return  when(response){
        is ApiResult.GenericError->{
            DataState.error(
                response = com.kola.cleannotes.business.domain.state.Response(
                    message = "${stateEvent?.errorInfo()}\n\nReason: ${response.errorMessage}",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                ),
                stateEvent = stateEvent
            )
        }
        is ApiResult.NetworkError->{
            DataState.error(
                response = com.kola.cleannotes.business.domain.state.Response(
                    message = "${stateEvent?.errorInfo()}\n\nReason: ${NetworkErrors.NETWORK_ERROR}",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                ),
                stateEvent = stateEvent
            )
        }
        is ApiResult.Sucess->{
            if(response.value == null){
                DataState.error(
                    response = com.kola.cleannotes.business.domain.state.Response(
                        message = "${stateEvent?.errorInfo()}\n\nReason: ${NetworkErrors.NETWORK_DATA_NULL}.",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    ),
                    stateEvent = stateEvent
                )
            }
            else{
                handleSuccess(resultObj = response.value)
            }
        }
    }
}

  abstract suspend fun handleSuccess(resultObj:Data):DataState<ViewState>

}