package com.example.surveyapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun encodeImageToBase64(bitmap: Bitmap, format: Bitmap.CompressFormat): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(format, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

@Composable
fun CreateSurveyScreen(viewModel: SurveyViewModel = viewModel(), onSurveyCreated: () -> Unit) {
    var questions by remember { mutableStateOf(listOf(QuestionUI("", listOf("", "")))) }
    var surveyTitle by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var showPhotoDialog by remember { mutableStateOf(false) }
    var encodedImage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                // Determine the image format
                val format = if (it.toString().endsWith("png")) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
                encodedImage = encodeImageToBase64(bitmap, format)
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            bitmap?.let {
                encodedImage = encodeImageToBase64(it, Bitmap.CompressFormat.JPEG)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF4E0))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = surveyTitle,
            onValueChange = { surveyTitle = it },
            placeholder = { Text("Enter Survey Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFFFF4E0))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            questions.forEachIndexed { index, question ->
                QuestionView(question = question, onQuestionChange = { newQuestionText ->
                    questions = questions.toMutableList().apply {
                        this[index] = this[index].copy(text = newQuestionText)
                    }
                }, onOptionChange = { optionIndex, newOptionText ->
                    questions = questions.toMutableList().apply {
                        val newOptions = this[index].options.toMutableList().apply {
                            this[optionIndex] = newOptionText
                        }
                        this[index] = this[index].copy(options = newOptions)
                    }
                }, onAddOption = {
                    questions = questions.toMutableList().apply {
                        this[index] = this[index].copy(options = this[index].options + "")
                    }
                })
            }

            Button(
                onClick = {
                    questions = questions + QuestionUI("", listOf("", ""))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3E2E2)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "Add a question",
                    color = Color(0xFF8B0000)
                )
            }

            Button(
                onClick = {
                    showPhotoDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3E2E2)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_photo_camera),
                        contentDescription = "Camera Icon",
                        tint = Color(0xFF8B0000),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add a Photo",
                        color = Color(0xFF8B0000)
                    )
                }
            }

            if (showPhotoDialog) {
                PhotoSelectionDialog(
                    onDismiss = { showPhotoDialog = false },
                    onSelectCamera = {
                        showPhotoDialog = false
                        cameraLauncher.launch()
                    },
                    onSelectGallery = {
                        showPhotoDialog = false
                        galleryLauncher.launch("image/*")
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .background(Color(0xFFE3E2E2))
                    .padding(16.dp)
                    .clickable {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(context, { _, year, month, dayOfMonth ->
                            TimePickerDialog(context, { _, hourOfDay, minute ->
                                deadline = "$year-${month + 1}-$dayOfMonth"
                                time = "$hourOfDay:$minute:00"
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "Select Deadline",
                        tint = Color(0xFF8B0000)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Set a Deadline",
                            color = Color(0xFF8B0000),
                            fontSize = 18.sp
                        )
                        if (deadline.isNotEmpty()) {
                            Text(
                                text = formatDateForCreateSurvey(deadline),
                                color = Color(0xFF8B0000),
                                fontSize = 18.sp
                            )
                            Text(
                                text = time,
                                color = Color(0xFF8B0000),
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.addSurvey(Survey(null, surveyTitle, deadline, time, encodedImage)) { surveyResponse ->
                            val surveyId = surveyResponse.SurveyID ?: return@addSurvey

                            questions.forEach { question ->
                                viewModel.addQuestion(Question(surveyId, question.text)) { questionResponse ->
                                    val questionId = questionResponse.QuestionID ?: return@addQuestion

                                    question.options.forEach { optionText ->
                                        viewModel.addOption(Option(null, questionId, optionText)) { optionResponse ->
                                            Log.d("CreateSurveyScreen", "Option added: ${optionResponse.OptionID}")
                                        }
                                    }
                                }
                            }
                            onSurveyCreated()  // Anket oluşturulduktan sonra yönlendirme yap
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text(
                    text = "Send Survey",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun QuestionView(
    question: QuestionUI,
    onQuestionChange: (String) -> Unit,
    onOptionChange: (Int, String) -> Unit,
    onAddOption: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        OutlinedTextField(
            value = question.text,
            onValueChange = onQuestionChange,
            placeholder = { Text(text = "What is your question?") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            question.options.forEachIndexed { index, option ->
                OutlinedTextField(
                    value = option,
                    onValueChange = { onOptionChange(index, it) },
                    placeholder = { Text(text = "Option ${index + 1}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
        }
        Button(
            onClick = onAddOption,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3E2E2)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Add Option",
                color = Color(0xFF8B0000)
            )
        }
    }
}

@Composable
fun PhotoSelectionDialog(
    onDismiss: () -> Unit,
    onSelectCamera: () -> Unit,
    onSelectGallery: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select Photo Source") },
        text = { Text(text = "Please choose whether you want to take a photo or select from gallery.") },
        confirmButton = {
            TextButton(onClick = onSelectCamera) {
                Text("Camera")
            }
        },
        dismissButton = {
            TextButton(onClick = onSelectGallery) {
                Text("Gallery")
            }
        }
    )
}

data class QuestionUI(val text: String, val options: List<String>)

fun formatDateForCreateSurvey(dateString: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString)
        val formattedSdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formattedSdf.format(date)
    } catch (e: Exception) {
        dateString
    }
}
