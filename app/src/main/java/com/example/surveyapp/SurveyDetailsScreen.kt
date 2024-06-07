package com.example.surveyapp

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun SurveyDetailsScreen(viewModel: SurveyViewModel = viewModel(), surveyId: Int) {
    var survey by remember { mutableStateOf<Survey?>(null) }
    var surveyResults by remember { mutableStateOf<List<SurveyResult>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(surveyId) {
        viewModel.readSurvey(surveyId) { surveyData ->
            survey = surveyData
        }
        viewModel.getSurveyResults(surveyId) { results ->
            surveyResults = results
        }
    }

    survey?.let { currentSurvey ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFFFF4E0))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            currentSurvey.Image?.let { imageBase64 ->
                val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentSurvey.SurveyTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            surveyResults.forEach { question ->
                Text(
                    text = question.QuestionText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                val totalVotes = question.Options.sumOf { it.Votes }

                question.Options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.voteOption(option.OptionID) { response ->
                                        // Oy kullanma işlemi sonrası sayıyı güncelle
                                        val updatedOptions = question.Options.map {
                                            if (it.OptionID == option.OptionID) {
                                                it.copy(Votes = it.Votes + 1)
                                            } else {
                                                it
                                            }
                                        }
                                        surveyResults = surveyResults.map {
                                            if (it.QuestionID == question.QuestionID) {
                                                it.copy(Options = updatedOptions)
                                            } else {
                                                it
                                            }
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3E2E2))
                        ) {
                            Text(text = option.OptionText, color = Color(0xFF8B0000))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(text = "Votes: ${option.Votes}")

                        Spacer(modifier = Modifier.width(8.dp))

                        if (totalVotes > 0) {
                            val percentage = (option.Votes.toFloat() / totalVotes * 100)
                            Text(text = String.format("(%.2f%%)", percentage))
                        }
                    }
                }
            }
        }
    }
}
