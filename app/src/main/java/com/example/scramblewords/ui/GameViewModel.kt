package com.example.scramblewords.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.scramblewords.data.MAX_NO_OF_WORDS
import com.example.scramblewords.data.SCORE_INCREASE
import com.example.scramblewords.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    //game ui state
    private val _uiState = MutableStateFlow(GameUiState())

    //Backing property to avoid state updates from other classes
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    //asStateFlow() make this mutable state flow a read-only state flow.

    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()

    var userGuess by mutableStateOf("")
        private set


    private fun pickRandomWordAndShuffle(): String {
        //continue picking up a new random word until you get one that hasn't been used before
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
        }
        return shuffleCurrentWord(currentWord)
    }

    private fun shuffleCurrentWord(currentWord: String): String {
        val tempWord = currentWord.toCharArray()

        //scramble the word
        tempWord.shuffle()
        while (String(tempWord).equals(currentWord)) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

     fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambleWord = pickRandomWordAndShuffle())
    }

    init {
        resetGame()
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }


    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            //user's guess is correct , increase the score
            val updateScore = _uiState.value.score.plus(SCORE_INCREASE)
            //call updateGameState() to prepare the game for next round
            updateGameState(updateScore)
        } else {
            //user's guess is wrong, show an error
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        //reset user guess
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            //last round of the game, update isGameOver to true, don't pick a new Word
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        }
        else {
            //normal round in the game
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambleWord = pickRandomWordAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc()
                )
            }
        }
    }

    fun skipWord() {
        updateGameState(_uiState.value.score)

        //reset your guess
        updateUserGuess("")
    }

}