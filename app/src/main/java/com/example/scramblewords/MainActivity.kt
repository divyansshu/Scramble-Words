package com.example.scramblewords

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scramblewords.ui.GameViewModel
import com.example.scramblewords.ui.theme.ScrambleWordsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScrambleWordsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GameScreen()
                }
            }
        }
    }
}

@Composable
fun GameStatus(
    modifier: Modifier = Modifier,
    wordCount: Int,
    score: Int
) {
    Row(
        modifier
            .padding(16.dp)
            .fillMaxWidth()
            .size(48.dp)
    ) {

        Text(
            text = stringResource(R.string.wordsCount, wordCount),
            fontSize = 18.sp
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            text = stringResource(R.string.score, score),
            fontSize = 18.sp
        )
    }

}

@Composable
fun GameLayout(
    onUserGuessChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    userGuess: String,
    modifier: Modifier = Modifier,
    currentScrambleWord: String,
    isGuessWrong: Boolean
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = currentScrambleWord,
            fontSize = 45.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = stringResource(R.string.instructions),
            fontSize = 17.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )


        OutlinedTextField(
            value = userGuess,
            singleLine = true,
            onValueChange = onUserGuessChanged,
            label = {
                if (isGuessWrong) {
                    Text(stringResource(R.string.wrong_Guess))
                } else
                    Text(stringResource(R.string.label))
            },
            isError = isGuessWrong,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onKeyboardDone })
        )
    }
}

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel()
) {

    //this ensures that whenever there is a change in the uiState value,
    // recomposition occurs for the composables using the gameUiState value
    val gameUiState by gameViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameStatus(
            wordCount = gameUiState.currentWordCount,
            score = gameUiState.score
        )
        GameLayout(
            onUserGuessChanged = { gameViewModel.updateUserGuess(it) },
            onKeyboardDone = { gameViewModel.checkUserGuess() },
            userGuess = gameViewModel.userGuess,
            currentScrambleWord = gameUiState.currentScrambleWord,
            isGuessWrong = gameUiState.isGuessedWordWrong
        )

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {

            OutlinedButton(
                onClick = { gameViewModel.skipWord() },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {

                Text(stringResource(R.string.skip))
            }

            Button(
                onClick = { gameViewModel.checkUserGuess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {

                Text(stringResource(R.string.Submit))
            }
        }
        if(gameUiState.isGameOver) {
            FinalScoreDialog(score = gameUiState.score, onPlayAgain = {gameViewModel.resetGame()})
        }
    }
}

@Composable
fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {

    val activity = (LocalContext.current as Activity)

    AlertDialog(onDismissRequest = { },
        title = { Text(stringResource(R.string.congratulations)) },
        text = { Text(stringResource(R.string.you_scored, score)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = { activity.finish() }) {
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            TextButton(onClick = { onPlayAgain() }) {
                Text(text = stringResource(R.string.play_again))
            }
        })
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ScrambleWordsTheme {
        GameScreen()
    }
}