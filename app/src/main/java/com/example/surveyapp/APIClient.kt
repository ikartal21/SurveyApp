package com.example.surveyapp

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

object ApiClient {
    private const val BASE_URL = "http://YOUR_SERVER"

    private val httpClient = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            level = LogLevel.BODY
        }
    }

    suspend fun addSurvey(survey: Survey): ResponseMessage {
        return httpClient.post("$BASE_URL/add_survey") {
            contentType(ContentType.Application.Json)
            body = survey
        }
    }

    suspend fun addQuestion(question: Question): ResponseMessage {
        return httpClient.post("$BASE_URL/add_question") {
            contentType(ContentType.Application.Json)
            body = question
        }
    }

    suspend fun addOption(option: Option): ResponseMessage {
        return httpClient.post("$BASE_URL/add_option") {
            contentType(ContentType.Application.Json)
            body = option
        }
    }

    suspend fun voteOption(optionId: Int): ResponseMessage {
        return httpClient.post("$BASE_URL/vote_option") {
            contentType(ContentType.Application.Json)
            body = mapOf("OptionID" to optionId)
        }
    }

    suspend fun getSurveyResults(surveyId: Int): List<SurveyResult> {
        return httpClient.get("$BASE_URL/survey_results/$surveyId")
    }

    suspend fun readData(): List<Survey> {
        return httpClient.get("$BASE_URL/read_data")
    }

    suspend fun readSurvey(surveyId: Int): Survey {
        return httpClient.get("$BASE_URL/read_survey/$surveyId")
    }
}
