package com.example.surveyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.surveyapp.ui.theme.SurveyAppTheme
import com.example.surveyapp.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurveyAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") { MainScreen(navController) }
                    composable("createSurvey") {
                        val surveyViewModel: SurveyViewModel = viewModel()
                        CreateSurveyScreen(viewModel = surveyViewModel) {
                            navController.navigate("surveyList")
                        }
                    }
                    composable("surveyList") {
                        val surveyViewModel: SurveyViewModel = viewModel()
                        SurveyListScreen(viewModel = surveyViewModel) { surveyId ->
                            navController.navigate("surveyDetails/$surveyId")
                        }
                    }
                    composable("surveyDetails/{surveyId}") { backStackEntry ->
                        val surveyId = backStackEntry.arguments?.getString("surveyId")?.toIntOrNull() ?: return@composable
                        val surveyViewModel: SurveyViewModel = viewModel()
                        SurveyDetailsScreen(surveyId = surveyId, viewModel = surveyViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF4E0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ONLINE SURVEY PLATFORM",
            color = Color(0xFF8B0000),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFE3E2E2),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Button(
                onClick = { navController.navigate("createSurvey") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Add Icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "CREATE NEW SURVEY",
                        color = Color(0xFF8B0000),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Button(
            onClick = { navController.navigate("surveyList") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
            elevation = ButtonDefaults.buttonElevation(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "SURVEY LIST",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
