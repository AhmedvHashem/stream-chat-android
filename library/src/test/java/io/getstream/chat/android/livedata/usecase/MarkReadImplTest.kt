package io.getstream.chat.android.livedata.usecase

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import io.getstream.chat.android.client.utils.Result
import io.getstream.chat.android.livedata.BaseConnectedIntegrationTest
import io.getstream.chat.android.livedata.utils.getOrAwaitValue
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MarkReadImplTest : BaseConnectedIntegrationTest() {

    @Test
    fun read() = runBlocking(Dispatchers.IO) {
        val before = Date()
        var channelControllerImpl = chatDomainImpl.channel(data.channel1.cid)
        channelControllerImpl.handleEvent(data.newMessageFromUser2)
        var unreadCount = channelControllerImpl.unreadCount.getOrAwaitValue()
        Truth.assertThat(unreadCount).isEqualTo(1)

        var result = chatDomain.useCases.markRead(data.channel1.cid).execute()
        assertSuccess(result as Result<Any>)
        val lastRead = channelControllerImpl.read.getOrAwaitValue().lastRead
        Truth.assertThat(lastRead).isEqualTo(data.messageFromUser2.createdAt)
        unreadCount = channelControllerImpl.unreadCount.getOrAwaitValue()
        Truth.assertThat(unreadCount).isEqualTo(0)
        Truth.assertThat(channelControllerImpl.toChannel().unreadCount).isEqualTo(0)
    }
}
