import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import me.rahimklaber.stellar.horizon.SubmitTransactionResponse
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
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
            Row(Modifier.padding(10.dp).fillMaxWidth(50.0f), horizontalArrangement = Arrangement.SpaceAround) {
                SelectionContainer {
                    Text(stellarStuff.keyPair.accountId)
                }
            }
            Row(Modifier.padding(10.dp).fillMaxWidth(50.0f), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Button({
                        doingAction = true
                        scope.launch {
                            stellarStuff.fundAccount()
                            stellarStuff.loadNewBalance()
                            doingAction = false
                        }
                }) {
                    Text("Fund Account")
                }

                Text("Balance: $balance")
            }

            Row(Modifier.padding(10.dp).fillMaxWidth(50.0f), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Button({
                        doingAction = true
                        scope.launch {
                            println("somethnig")
                            submitOutPut = stellarStuff.exampleSend(recipient)
                            doingAction = false
                    }
                }) {
                    Text("send 10 XLM")
                }
                TextField(recipient, {recipient = it}, placeholder = {Text("recipient")})
            }


            Row(Modifier.padding(10.dp).fillMaxWidth(50.0f), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                LazyColumn {
                    item {
                        SelectionContainer {
                            Text(submitOutPut.toString())
                        }
                    }
                }
            }


        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}