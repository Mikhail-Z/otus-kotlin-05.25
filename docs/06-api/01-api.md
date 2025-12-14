# API Спецификация SnapMatch MVP

## Аутентификация и авторизация

**Keycloak Integration**:
- Все API endpoints защищены JWT токенами от Keycloak
- Роли пользователей управляются в Keycloak: `CANDIDATE`, `HR_SPECIALIST`
- Информация о пользователе (userId, email, имя, роли) извлекается из JWT токена
- Аутентификация, регистрация и управление пользователями происходит напрямую через Keycloak

## Сущности (Entities)

### 1. Resume (Резюме)

**Описание**: Основная сущность, представляющая резюме кандидата

**Свойства**:
- `id: UUID` - Уникальный идентификатор резюме
- `userId: UUID` - Идентификатор пользователя из системы аутентификации
- `vacancyId: UUID` - Идентификатор вакансии
- `fileName: String` - Оригинальное имя файла
- `fileKey: String` - Ключ файла в хранилище (resumes/2024/01/uuid-file.pdf)
- `mimeType: String` - MIME-тип файла (application/pdf)
- `fileSize: Long` - Размер файла в байтах
- `uploadedAt: String` - Время загрузки (ISO 8601: "2024-01-15T10:30:00Z")
- `status: ResumeProcessingStatus` - Статус обработки резюме

### 2. Vacancy (Вакансия)

**Описание**: Сущность, представляющая вакансию

**Свойства**:
- `id: UUID` - Уникальный идентификатор вакансии
- `title: String` - Название вакансии
- `description: String` - Описание вакансии
- `scoreThreshold: Int` - Пороговое значение метрики (0-100)
- `isActive: Boolean` - Активна ли вакансия
- `createdAt: String` - Время создания (ISO 8601: "2024-01-15T10:30:00Z")
- `createdBy: UUID` - Идентификатор HR-специалиста из системы аутентификации

### 3. ResumeAnalysis (Результат анализа резюме)

**Описание**: Сущность, содержащая структурированный результат анализа резюме от LLM

**Свойства**:
- `id: UUID` - Уникальный идентификатор анализа
- `resumeId: UUID` - Идентификатор резюме (внешний ключ, связь 1:1)
- `score: Int` - Метрика соответствия (0-100)
- `analysis: ResumeAnalysisDetails` - Структурированный анализ от LLM
- `llmProvider: String` - Провайдер LLM, используемый для анализа
- `llmModel: String` - Конкретная модель LLM, используемая для анализа
- `createdAt: String` - Время создания анализа (ISO 8601: "2024-01-15T10:30:00Z")

### 4. ResumeAnalysisDetails (Детали анализа резюме)

**Описание**: Структура данных с детализированным анализом резюме от LLM

**Свойства**:
- `strengths: String[]` - Массив сильных сторон кандидата
- `weaknesses: String[]` - Массив слабых мест
- `recommendations: String[]` - Массив рекомендаций для улучшения
- `summary: String` - Общая сводка по резюме

## Перечисления (Enums)

### ResumeProcessingStatus
- `UPLOADED` - Загружено, ожидает обработки
- `PROCESSING` - Обрабатывается (валидация и анализ)
- `ACCEPTED` - Принято
- `REJECTED` - Отклонено

### UserRole (управляется в Keycloak)
- `CANDIDATE` - Соискатель
- `HR_SPECIALIST` - HR-специалист

### LLMProvider (Провайдер LLM)
- `GIGACHAT` - GigaChat от Сбера (используется в MVP)

## Валидационные правила

### Общие ограничения:
- **UUID**: Все UUID поля должны быть валидными UUID v4
- **Пагинация**: `page >= 0`, `perPage` от 1 до 1000 (по умолчанию 20)
- **Текстовые поля**: Не должны содержать только пробелы

### Resume (Резюме):
- **fileName**: 1-255 символов, допустимые символы: буквы, цифры, точки, дефисы, подчеркивания
- **fileSize**: Максимум 10MB (10,485,760 байт)
- **mimeType**: Только "application/pdf"
- **fileKey**: Формат "resumes/YYYY/MM/uuid-filename.pdf"

### Vacancy (Вакансия):
- **title**: 1-200 символов, обязательное поле
- **description**: 10-5000 символов, обязательное поле
- **scoreThreshold**: Число от 0 до 100 включительно

### ResumeAnalysis (Анализ резюме):
- **score**: Число от 0 до 100 включительно
- **llmProvider**: Должно быть одним из значений LLMProvider ("gigachat" для MVP)
- **llmModel**: 1-100 символов, название используемой модели LLM ("GigaChat-Pro" для MVP)

### Поиск и фильтрация:
- **keywords**: 1-100 символов
- Фильтры по UUID полям проверяются на существование в БД

## Ролевые ограничения доступа

### CANDIDATE (Соискатель):
- `POST /api/v1/resume/upload` - Загрузка своих резюме
- `POST /api/v1/resume/my` - Просмотр своих резюме
- `POST /api/v1/resume/get` - Просмотр резюме (только своих)
- `POST /api/v1/vacancy/list` - Просмотр активных вакансий
- `POST /api/v1/vacancy/search` - Поиск вакансий

### HR_SPECIALIST (HR-специалист):
- Все права CANDIDATE +
- `POST /api/v1/resume/get` - Просмотр любого резюме
- `POST /api/v1/resume/analysis` - Просмотр анализа резюме
- `POST /api/v1/vacancy/create` - Создание вакансий
- `POST /api/v1/vacancy/resumes` - Просмотр резюме по вакансии
- `POST /api/v1/vacancy/delete` - Деактивация вакансии

## MVP API Endpoints

### 1. Загрузка резюме
```
POST /api/v1/resume/upload
Authorization: Bearer {keycloak-jwt-token}
Content-Type: multipart/form-data
```
Тело запроса:
- file: File (PDF)
- vacancyId: UUID

Ответ: [ResumeUploadResponse](#resumeuploadresponse)


### 2. Получение резюме по ID
```
POST /api/v1/resume/get
Authorization: Bearer {keycloak-jwt-token}
Content-Type: application/json
```

Тело запроса: [ResumeGetRequest](#resumegetrequest)

Ответ: [ResumeResponse](#resumeresponse)


### 3. Получение моих резюме (для кандидата)
```
POST /api/v1/resume/my
Authorization: Bearer {keycloak-jwt-token}
Content-Type: application/json
```

Тело запроса: [PaginationRequest](#paginationrequest)

Ответ: [PagedResponse<ResumeListItem>](#pagedresponse)


### 4. Получение анализа резюме
```
POST /api/v1/resume/analysis
Authorization: Bearer {keycloak-jwt-token}
Content-Type: application/json
```

Тело запроса: [ResumeAnalysisRequest](#resumeanalysisrequest)
Ответ: [ResumeAnalysisResponse](#resumeanalysisresponse)


### 5. Создание вакансии
```
POST /api/v1/vacancy/create
Authorization: Bearer {keycloak-jwt-token}
Content-Type: application/json
```

Тело запроса: [VacancyCreateRequest](#vacancycreaterequest)

Ответ: [VacancyResponse](#vacancyresponse)


### 6. Получение активных вакансий
```
POST /api/v1/vacancy/list
Authorization: Bearer {keycloak-jwt-token}
Content-Type: application/json
```

Тело запроса: [PaginationRequest](#paginationrequest)

Ответ: [PagedResponse<VacancyResponse>](#pagedresponse)


### 7. Поиск вакансий по ключевым словам
```
POST /api/v1/vacancy/search
Authorization: Bearer {keycloak-jwt-token}
Content-Type: application/json
```

Тело запроса: [VacancySearchRequest](#vacancysearchrequest)

Ответ: [PagedResponse<VacancyResponse>](#pagedresponse)


### 8. Получение резюме по вакансии (для HR)
```
POST /api/v1/vacancy/resumes
Authorization: Bearer {keycloak-jwt-token}
Content-Type: application/json
```

Тело запроса: [VacancyResumesRequest](#vacancyresumesrequest)

Ответ: [PagedResponse<ResumeListItem>](#pagedresponse)


### 9. Удаление вакансии
```
POST /api/v1/vacancy/delete
Authorization: Bearer {keycloak-jwt-token}
Content-Type: application/json
```

Тело запроса: [VacancyDeleteRequest](#vacancydeleterequest)

Ответ: [VacancyDeleteResponse](#vacancydeleteresponse)


### 10. Получение вакансии по ID
```
POST /api/v1/vacancy/get
Authorization: Bearer {keycloak-jwt-token}
Content-Type: application/json
```

Тело запроса: [VacancyGetRequest](#vacancygetrequest)

Ответ: [VacancyResponse](#vacancyresponse)


### 11. WebSocket для уведомлений
```
WebSocket: /api/ws/notifications
Authorization: Bearer {keycloak-jwt-token}

Управление сессиями:
1. Браузер устанавливает WebSocket соединение при входе пользователя
2. Сервер извлекает userId из JWT токена и создает активную WebSocket сессию
3. При завершении анализа резюме сервер отправляет уведомление

Формат уведомления:
{
  "type": "resume.status.updated",
  "resumeId": "uuid",
  "status": "ACCEPTED"
}

При отклонении:
{
  "type": "resume.status.updated",
  "resumeId": "uuid",
  "status": "REJECTED"
}
```

## Модели запросов и ответов

### Модели запросов

#### ResumeGetRequest
```json
{
  "resumeId": "uuid"  // ID резюме для получения детальной информации
}
```

#### PaginationRequest
```json
{
  "page": "int",     // Номер страницы (начиная с 0)
  "perPage": "int"   // Количество элементов на странице (по умолчанию 20)
}
```

#### ResumeAnalysisRequest
```json
{
  "resumeId": "uuid"  // ID резюме для получения результатов AI анализа
}
```

#### VacancyCreateRequest
```json
{
  "title": "string",        // Название вакансии (например: "Java Developer")
  "description": "string",  // Подробное описание вакансии и компании
  "scoreThreshold": "int"   // Минимальный балл для автоматического принятия (0-100)
}
```

#### VacancySearchRequest
```json
{
  "keywords": "string",  // Ключевые слова для поиска в названии, описании и требованиях
  "page": "int",         // Номер страницы результатов поиска
  "perPage": "int"       // Количество результатов на странице
}
```

#### VacancyResumesRequest
```json
{
  "vacancyId": "uuid",  // ID вакансии для получения списка откликов
  "page": "int",        // Номер страницы списка резюме
  "perPage": "int"      // Количество резюме на странице
}
```

#### VacancyDeleteRequest
```json
{
  "vacancyId": "uuid"  // ID вакансии для деактивации (удаления из поиска)
}
```

#### VacancyGetRequest
```json
{
  "vacancyId": "uuid"  // ID вакансии для получения детальной информации
}
```

### Модели ответов

#### ResumeUploadResponse
```json
{
  "resumeId": "uuid",      // Уникальный ID созданного резюме
  "status": "UPLOADED",    // Статус загрузки (всегда UPLOADED при успехе)
  "message": "string"      // Сообщение о результате загрузки
}
```

#### ResumeResponse
```json
{
  "id": "uuid",                     // Уникальный ID резюме
  "userId": "uuid",                 // ID пользователя-владельца резюме
  "vacancyId": "uuid",              // ID вакансии, на которую подано резюме
  "fileName": "string",             // Оригинальное название файла (resume.pdf)
  "fileKey": "string",              // Ключ файла в MinIO хранилище
  "fileSize": "long",               // Размер файла в байтах
  "uploadedAt": "2024-01-15T10:30:00Z",  // Дата и время загрузки (ISO 8601)
  "status": "ResumeProcessingStatus",     // Текущий статус обработки резюме
  "userEmail": "string",            // Email кандидата (из Keycloak)
  "userName": "string?",            // Имя кандидата (может быть null)
  "vacancyTitle": "string",         // Название вакансии для удобства
  "analysis": {                     // Результат AI анализа (null если еще не проанализировано)
    "score": "int",                 // Оценка соответствия (0-100)
    "strengths": ["string"],        // Массив сильных сторон кандидата
    "weaknesses": ["string"],       // Массив слабых мест
    "recommendations": ["string"],  // Рекомендации для улучшения
    "summary": "string",            // Общая сводка по резюме от AI
    "llmProvider": "gigachat",      // Используемый LLM провайдер
    "llmModel": "GigaChat-Pro"      // Конкретная модель LLM
  }
}
```

#### ResumeListItem
```json
{
  "id": "uuid",                     // Уникальный ID резюме
  "userId": "uuid",                 // ID пользователя-кандидата
  "userEmail": "string",            // Email кандидата
  "userName": "string?",            // Имя кандидата (опционально)
  "vacancyTitle": "string",         // Название вакансии
  "status": "ResumeProcessingStatus", // Статус обработки
  "score": "int?",                  // Оценка от AI (null если не проанализировано)
  "uploadedAt": "2024-01-15T10:30:00Z" // Время загрузки
}
```

#### VacancyResponse
```json
{
  "id": "uuid",                     // Уникальный ID вакансии
  "title": "string",                // Название позиции
  "description": "string",          // Подробное описание вакансии
  "scoreThreshold": "int",          // Минимальный балл для автоприема (0-100)
  "isActive": "boolean",            // Активна ли вакансия для откликов
  "createdAt": "2024-01-15T10:30:00Z", // Время создания вакансии
  "createdBy": "uuid",              // ID HR-специалиста создавшего вакансию
  "createdByEmail": "string"        // Email HR-специалиста
}
```

#### ResumeAnalysisResponse
```json
{
  "id": "uuid",                     // Уникальный ID анализа
  "resumeId": "uuid",               // ID анализируемого резюме
  "score": "int",                   // Итоговая оценка соответствия (0-100)
  "analysis": {                     // Структурированный результат анализа
    "strengths": ["Опыт Java 8+ лет", "Знание Spring Boot"],     // Сильные стороны
    "weaknesses": ["Нет опыта с Kubernetes", "Слабые навыки тестирования"], // Слабые места
    "recommendations": ["Изучить Docker/K8s", "Пройти курсы по TDD"],       // Рекомендации
    "summary": "Сильный Java разработчик, но нужно подтянуть DevOps навыки" // Общая сводка
  },
  "llmProvider": "gigachat",        // Использованный LLM провайдер
  "llmModel": "GigaChat-Pro",       // Конкретная модель
  "createdAt": "2024-01-15T10:30:00Z" // Время создания анализа
}
```

#### PagedResponse<T>
```json
{
  "pageContent": ["T"],             // Массив элементов текущей страницы
  "totalElements": "long",          // Общее количество элементов в коллекции
  "page": "int",                    // Номер текущей страницы (с 0)
  "perPage": "int",                 // Размер страницы
  "hasNext": "boolean"              // Есть ли следующая страница
}
```

#### VacancyDeleteResponse
```json
{
  "success": "boolean",           // Успешность операции удаления
  "message": "string"             // Сообщение о результате операции
}
```

#### ErrorResponse
```json
{
  "error": {
    "code": "string",               // Код ошибки для программной обработки
    "message": "string",            // Человекочитаемое сообщение об ошибке
    "details": "object?"            // Дополнительные детали ошибки (опционально)
  }
}
```

## Асинхронная обработка через Kafka

**Процесс обработки резюме:**
1. **Upload Service**: Валидирует PDF файл, извлекает текст, сохраняет в MinIO и PostgreSQL
2. **Upload Service**: Публикует событие в Kafka topic `resume.uploaded`
3. **Scoring Service**: Получает событие из Kafka и начинает анализ
4. **Scoring Service**: Анализирует резюме с помощью GigaChat LLM
5. **Scoring Service**: Обновляет статус резюме в PostgreSQL
6. **Scoring Service**: Отправляет уведомление через WebSocket

## Коды ошибок

**Синхронные ошибки при загрузке файлов:**

*Неподдерживаемый тип файла:*
```json
{
  "error": {
    "code": "INVALID_FILE_TYPE",
    "message": "Only PDF files are allowed",
    "details": { "allowedTypes": ["application/pdf"] }
  }
}
```

*Поврежденный PDF файл:*
```json
{
  "error": {
    "code": "CORRUPTED_FILE",
    "message": "PDF file is corrupted or unreadable",
    "details": { "fileName": "resume.pdf" }
  }
}
```

*Превышен размер файла:*
```json
{
  "error": {
    "code": "FILE_TOO_LARGE",
    "message": "File size exceeds maximum limit of 10MB",
    "details": { "maxSize": 10485760, "actualSize": 15728640 }
  }
}
```

**Асинхронные ошибки** (через WebSocket):

*LLM недоступен:*
```json
{
  "type": "resume.processing.failed",
  "resumeId": "uuid",
  "error": {
    "code": "LLM_SERVICE_UNAVAILABLE",
    "message": "AI analysis service is temporarily unavailable"
  }
}
```

*Внутренняя ошибка сервиса:*
```json
{
  "type": "resume.processing.failed",
  "resumeId": "uuid",
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "message": "An unexpected error occurred during processing"
  }
}
```
