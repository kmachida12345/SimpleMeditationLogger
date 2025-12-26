package com.github.kmachida12345.simplemeditationlogger.domain.usecase

import com.github.kmachida12345.simplemeditationlogger.data.entity.MeditationSession
import com.github.kmachida12345.simplemeditationlogger.data.repository.MeditationSessionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetMeditationHistoryUseCase @Inject constructor(
    private val repository: MeditationSessionRepository
) {
    operator fun invoke(): Flow<List<MeditationSession>> {
        return repository.getAllSessions()
    }
    
    fun getByDate(date: LocalDate): Flow<List<MeditationSession>> {
        return repository.getSessionsByDate(date)
    }
}
