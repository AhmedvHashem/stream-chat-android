package io.getstream.chat.android.livedata.usecase

import io.getstream.chat.android.client.call.Call
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.livedata.ChatDomainImpl
import io.getstream.chat.android.livedata.utils.CoroutineCall

public interface CreateChannel {
    /**
     * Creates a new channel. Will retry according to the retry policy if it fails
     * @see io.getstream.chat.android.livedata.utils.RetryPolicy
     *
     * @param channel the channel object
     * @return A call object with Channel as the return type
     */
    public operator fun invoke(channel: Channel): Call<Channel>
}

internal class CreateChannelImpl(private val domainImpl: ChatDomainImpl) : CreateChannel {
    override operator fun invoke(channel: Channel): Call<Channel> {
        val runnable = suspend {
            domainImpl.createChannel(channel)
        }
        return CoroutineCall(domainImpl.scope, runnable)
    }
}
