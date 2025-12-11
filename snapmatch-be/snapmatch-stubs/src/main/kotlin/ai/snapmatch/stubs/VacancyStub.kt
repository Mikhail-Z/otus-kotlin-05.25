package ai.snapmatch.stubs

import ai.snapmatch.common.models.UserId
import ai.snapmatch.common.models.Vacancy
import ai.snapmatch.common.models.VacancyId
import kotlinx.datetime.Instant

object VacancyStub {
    fun get(): Vacancy = VACANCY_KOTLIN_DEV.copy()

    fun prepareResult(block: Vacancy.() -> Unit): Vacancy = get().apply(block)

    fun prepareSearchList(filter: String = "developer") = listOf(
        vacancyKotlin("550e8400-e29b-41d4-a716-446655440011", filter),
        vacancyJava("550e8400-e29b-41d4-a716-446655440012", filter),
    )

    private fun vacancyKotlin(id: String, filter: String) =
        vacancy(VACANCY_KOTLIN_DEV, id = id, filter = filter)

    private fun vacancyJava(id: String, filter: String) =
        vacancy(VACANCY_JAVA_DEV, id = id, filter = filter)

    private fun vacancy(base: Vacancy, id: String, filter: String) = base.copy(
        id = VacancyId(id),
        title = "${base.title} - $filter $id",
        description = "${base.description} - $filter requirements",
    )

    private val VACANCY_KOTLIN_DEV = Vacancy(
        id = VacancyId("550e8400-e29b-41d4-a716-446655440001"),
        title = "Senior Kotlin Developer",
        description = "Разработка backend сервисов на Kotlin/Spring Boot. Опыт работы с микросервисами, Docker, Kubernetes.",
        scoreThreshold = 75,
        location = "Москва",
        minExperienceYears = 3,
        skills = listOf("Kotlin", "Spring Boot", "PostgreSQL", "Docker", "Kubernetes"),
        salaryFrom = 200000,
        salaryTo = 350000,
        isActive = true,
        createdAt = Instant.parse("2023-12-01T10:00:00Z"),
        createdBy = UserId("550e8400-e29b-41d4-a716-446655440101")
    )

    private val VACANCY_JAVA_DEV = Vacancy(
        id = VacancyId("550e8400-e29b-41d4-a716-446655440002"),
        title = "Java Developer",
        description = "Разработка enterprise приложений на Java. Опыт работы с Spring Framework, Hibernate.",
        scoreThreshold = 70,
        location = "Санкт-Петербург",
        minExperienceYears = 2,
        skills = listOf("Java", "Spring", "Hibernate", "Maven", "Git"),
        salaryFrom = 150000,
        salaryTo = 280000,
        isActive = true,
        createdAt = Instant.parse("2023-12-01T11:00:00Z"),
        createdBy = UserId("550e8400-e29b-41d4-a716-446655440102")
    )

}