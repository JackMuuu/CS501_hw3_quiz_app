package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.quizapp.ui.theme.QuizAppTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

data class Flashcard(
    val question: String,
    val answer: String
)

val brown = Color(0xFF4B3023)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FlashcardsQuizApp()
                }
            }
        }
    }
}


@Composable
fun FlashcardsQuizApp() {
    val flashcards = remember {
        listOf(
            Flashcard("Which car company produces the “Mustang” model?", "Ford"),
            Flashcard("What does the acronym “SUV” stand for?", "Sports utility Vehicle"),
            Flashcard("What does MPG mean in terms of fuel?", "Miles per gallon"),
            Flashcard("Which country is home to the automaker BMW", "Germany"),
            Flashcard("What car brand uses the slogan “The Ultimate Driving Machine”?", "BMW"),
            Flashcard("Which car company produces the “911” sports car model?", "Porsche"),
            Flashcard("In what year did the iPhone 16 pro release?", "2024")
        )
    }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var isQuizComplete by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Show Snackbar when quiz is complete
    LaunchedEffect(isQuizComplete) {
        if (isQuizComplete) {
            snackbarHostState.showSnackbar(
                message = "Quiz Complete!",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .imePadding()
                    .navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        if (isQuizComplete) {
            QuizCompleteScreen(
                onRestartQuiz = {
                    currentQuestionIndex = 0
                    userInput = ""
                    isQuizComplete = false
                },
                modifier = Modifier.padding(innerPadding)
            )
            Box(modifier = Modifier.fillMaxSize())
        } else {
            FlashcardScreen(
                flashcard = flashcards[currentQuestionIndex],
                userInput = userInput,
                onUserInputChange = { userInput = it },
                onSubmitAnswer = {
                    val correctAnswer = flashcards[currentQuestionIndex].answer.trim().lowercase()
                    val userAnswer = userInput.trim().lowercase()

                    if (userAnswer == correctAnswer) {
                        coroutineScope.launch {
                            // Show "Correct!" Snackbar
                            snackbarHostState.showSnackbar(
                                message = "Correct!",
                                duration = SnackbarDuration.Short
                            )
                        }
                        if (currentQuestionIndex < flashcards.size - 1) {
                            currentQuestionIndex++
                            userInput = ""
                        } else {
                            isQuizComplete = true
                        }
                    } else {
                        coroutineScope.launch {
                            // Show "Incorrect" Snackbar
                            snackbarHostState.showSnackbar(
                                message = "Incorrect. Try again.",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun FlashcardScreen(
    flashcard: Flashcard,
    userInput: String,
    onUserInputChange: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Question
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Text(
                text = flashcard.question,
                style = MaterialTheme.typography.headlineSmall,
                color = brown,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Answer Input
        OutlinedTextField(
            value = userInput,
            onValueChange = onUserInputChange,
            label = { Text("You must know the Answer!") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = onSubmitAnswer,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = brown,
                contentColor = Color.White
            )
        ) {
            Text("Submit Answer", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
        }
    }
}

@Composable
fun QuizCompleteScreen(
    onRestartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Quiz Complete!",
            style = MaterialTheme.typography.headlineMedium,
            color = brown
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Restart Quiz Button
        Button(
            onClick = onRestartQuiz,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = brown,
                contentColor = Color.White
            )
        ) {
            Text("Restart Quiz")
        }
    }
}

//@Preview(showBackground = true)
//@Composable
