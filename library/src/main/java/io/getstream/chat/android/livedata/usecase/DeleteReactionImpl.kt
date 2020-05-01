package io.getstream.chat.android.livedata.usecase

import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.Reaction
import io.getstream.chat.android.livedata.utils.Call2
import io.getstream.chat.android.livedata.utils.CallImpl2
import io.getstream.chat.android.livedata.ChatDomainImpl
import java.security.InvalidParameterException

interface DeleteReaction {
    operator fun invoke(cid: String, reaction: Reaction): Call2<Message>
}

class DeleteReactionImpl(var domainImpl: ChatDomainImpl) : DeleteReaction {
    override operator fun invoke(cid: String, reaction: Reaction): Call2<Message> {
        if (cid.isEmpty()) {
            throw InvalidParameterException("cid cant be empty")
        }

        val channelRepo = domainImpl.channel(cid)

        var runnable = suspend {

            channelRepo.deleteReaction(reaction)
        }
        return CallImpl2(
            runnable,
            channelRepo.scope
        )
    }
}
