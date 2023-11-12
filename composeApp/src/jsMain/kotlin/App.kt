import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.defer.renderWithDeferred
import com.varabyte.kobweb.silk.prepareSilkFoundation
import kotlinx.coroutines.*
import kotlinx.serialization.descriptors.serialDescriptor
import me.rahimklaber.stellar.base.KeyPair
import me.rahimklaber.stellar.horizon.SubmitTransactionResponse
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.dpi
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable


@OptIn(DelicateCoroutinesApi::class)
suspend fun main() {
    GlobalScope.async {
        do{

            delay(100)
        }while (!KeyPair.isInit)
    }.await()
    renderComposable(rootElementId = "root") {
        prepareSilkFoundation(initSilk = { ctx ->
            com.varabyte.kobweb.silk.init.initSilkWidgets(ctx) // REQUIRED
        }) {

            renderWithDeferred {
                Content()
            }
        }
    }
}

@Composable
fun Content()  {
    val scope = rememberCoroutineScope()
    val stellarStuff = remember { StellarStuff() }

    LaunchedEffect(null){
        stellarStuff.receiveEvents()
    }

    val balance by stellarStuff.balance.collectAsState()
    var doingAction by remember { mutableStateOf(false) }

    var recipient by remember { mutableStateOf("") }
    var submitOutPut by remember { mutableStateOf<SubmitTransactionResponse?>(null) }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(50.percent), horizontalArrangement = Arrangement.SpaceAround) {
            Text(stellarStuff.keyPair.accountId)
        }
        Row(Modifier.padding(1.percent).fillMaxWidth(50.percent), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
           Button({
               onClick {
                   doingAction = true
                   scope.launch {
                       stellarStuff.fundAccount()
                       stellarStuff.loadNewBalance()
                       doingAction = false
                   }
               }
           }) {
                Text("Fund Account")
           }

            Text("Balance: $balance")
        }

        Row(Modifier.padding(1.percent).fillMaxWidth(50.percent), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Button({
                onClick {
                    doingAction = true
                    scope.launch {
                        println("somethnig")
                        submitOutPut = stellarStuff.exampleSend(recipient)
                        doingAction = false
                    }
                }
            }) {
                Text("send 10 XLM")
            }
            org.jetbrains.compose.web.dom.TextInput(recipient){
                onInput {
                    recipient = it.target.value
                    console.info(it)
                }
                placeholder("recipient")
            }
//            TextInput(recipient,{recipient = it}, placeholder = "recipient")
        }


        Row(Modifier.padding(1.percent).fillMaxWidth(50.percent), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Pre {
                if (submitOutPut != null)
                    Text(JSON.stringify(JSON.parse(JSON.stringify(submitOutPut)), undefined, 2))
            }
        }


    }
}