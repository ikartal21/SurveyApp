package com.example.surveyapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SurveyViewModel : ViewModel() {

    val apiClient = ApiClient

    fun addSurvey(survey: Survey, onResult: (ResponseMessage) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiClient.addSurvey(survey)
                onResult(response)
            } catch (e: Exception) {
                Log.e("SurveyViewModel", "Error adding survey", e)
                onResult(ResponseMessage("Error adding survey: ${e.message}"))
            }
        }
    }

    fun addQuestion(question: Question, onResult: (ResponseMessage) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiClient.addQuestion(question)
                onResult(response)
            } catch (e: Exception) {
                Log.e("SurveyViewModel", "Error adding question", e)
                onResult(ResponseMessage("Error adding question: ${e.message}"))
            }
        }
    }

    fun addOption(option: Option, onResult: (ResponseMessage) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiClient.addOption(option)
                onResult(response)
            } catch (e: Exception) {
                Log.e("SurveyViewModel", "Error adding option", e)
                onResult(ResponseMessage("Error adding option: ${e.message}"))
            }
        }
    }

    fun voteOption(optionId: Int?, onResult: (ResponseMessage) -> Unit) {
        optionId?.let {
            viewModelScope.launch {
                try {
                    val response = apiClient.voteOption(optionId)
                    onResult(response)
                } catch (e: Exception) {
                    Log.e("SurveyViewModel", "Error voting option", e)
                    onResult(ResponseMessage("Error voting option: ${e.message}"))
                }
            }
        }
    }

    fun getSurveyResults(surveyId: Int, onResult: (List<SurveyResult>) -> Unit) {
        viewModelScope.launch {
            try {
                val results = apiClient.getSurveyResults(surveyId)
                onResult(results)
            } catch (e: Exception) {
                Log.e("SurveyViewModel", "Error getting survey results", e)
            }
        }
    }

    fun readData(onResult: (List<Survey>) -> Unit) {
        viewModelScope.launch {
            try {
                val data = apiClient.readData()
                onResult(data)
            } catch (e: Exception) {
                Log.e("SurveyViewModel", "Error reading data", e)
            }
        }
    }

    fun readSurvey(surveyId: Int, onResult: (Survey) -> Unit) {
        viewModelScope.launch {
            try {
                val survey = apiClient.readSurvey(surveyId)
                onResult(survey)
            } catch (e: Exception) {
                Log.e("SurveyViewModel", "Error reading survey", e)
            }
        }
    }
}
