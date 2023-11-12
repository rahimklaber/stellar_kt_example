import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.michaelbull.result.unwrap
import com.ionspin.kotlin.crypto.LibsodiumInitializer
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.rahimklaber.stellar.base.*
import me.rahimklaber.stellar.base.operations.Payment
import me.rahimklaber.stellar.horizon.Server
import me.rahimklaber.stellar.horizon.SubmitTransactionResponse
import me.rahimklaber.stellar.horizon.toAccount

class StellarStuff(val server: Server = Server("https://horizon-testnet.stellar.org")){

    val client = HttpClient()
    var keyPair: KeyPair = KeyPair.random()

    private val _balance = MutableStateFlow("")
    val balance = _balance.asStateFlow()

    fun createRandomKeyPair(){
        keyPair = KeyPair.random()
    }

    val fundAccount = suspend{
        val response = client.get("https://friendbot.stellar.org?addr=${keyPair.accountId}")
    }

    suspend fun loadNewBalance(){
        _balance.update {
            server.accounts().account(keyPair.accountId)
                .unwrap().balances.first().balance
        }
    }

    suspend fun receiveEvents(): Nothing{
        do {
            val exception = runCatching {
                server
                    .transactions()
                    .forAccount(keyPair.accountId)
                    .stream()
                    .collect{
                        println(it)
                        loadNewBalance()
                    }
            }
            println(exception.exceptionOrNull())
            delay(2000)
        }while (true)
    }

    suspend fun exampleSend(dest: String): SubmitTransactionResponse {
        val tx = transactionBuilder(server.accounts().account(keyPair.accountId).unwrap().toAccount(), Network.TESTNET){
            addOperation(Payment(dest,Asset.Native, tokenAmount(10_000_000_0)))
        }
        tx.sign(keyPair)

        return server.submitTransaction(tx)
    }
}