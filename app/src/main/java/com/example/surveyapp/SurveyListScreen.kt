package com.example.surveyapp

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SurveyListScreen(viewModel: SurveyViewModel = viewModel(), onSurveyClick: (Int) -> Unit) {
    var surveys by remember { mutableStateOf<List<Survey>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.readData { data ->
            surveys = data
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF4E0))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        surveys.forEach { survey ->
            SurveyItem(survey = survey, onClick = { survey.SurveyID?.let { onSurveyClick(it) } })
        }
    }
}

@Composable
fun SurveyItem(survey: Survey, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            survey.Image?.let { imageBase64 ->
                val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    // Fallback if bitmap is null
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Gray)
                    )
                }
            } ?: Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Gray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = survey.SurveyTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                survey.Deadline?.let { deadline ->
                    Text(text = "Deadline: ${formatDateForSurveyList(deadline)}", fontSize = 16.sp)
                }
                survey.Time?.let { time ->
                    Text(text = "Time: $time", fontSize = 16.sp)
                }
            }
        }
    }
}

fun formatDateForSurveyList(dateString: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString)
        val formattedSdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formattedSdf.format(date)
    } catch (e: Exception) {
        dateString
    }
}
