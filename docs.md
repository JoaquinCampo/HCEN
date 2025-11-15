# Hcen

## Services

### AccessPolicy

Manages access policies for clinics and health workers. Allows creating, querying, and deleting access policies that grant clinics or health workers access to health users' clinical history.

**Methods:**

- `createClinicAccessPolicy(AddClinicAccessPolicyDTO dto)` → `ClinicAccessPolicyDTO`

  - Creates a new clinic access policy
  - Receives: [AddClinicAccessPolicyDTO](#addclinicaccesspolicydto)
  - Returns: [ClinicAccessPolicyDTO](#clinicaccesspolicydto)

- `createHealthWorkerAccessPolicy(AddHealthWorkerAccessPolicyDTO dto)` → `HealthWorkerAccessPolicyDTO`

  - Creates a new health worker access policy
  - Receives: [AddHealthWorkerAccessPolicyDTO](#addhealthworkeraccesspolicydto)
  - Returns: [HealthWorkerAccessPolicyDTO](#healthworkeraccesspolicydto)

- `findAllClinicAccessPolicies(String healthUserCi)` → `List<ClinicAccessPolicyDTO>`

  - Finds all clinic access policies for a health user
  - Receives: Health user CI (document ID)
  - Returns: List of [ClinicAccessPolicyDTO](#clinicaccesspolicydto)

- `findAllHealthWorkerAccessPolicies(String healthUserCi)` → `List<HealthWorkerAccessPolicyDTO>`

  - Finds all health worker access policies for a health user
  - Receives: Health user CI (document ID)
  - Returns: List of [HealthWorkerAccessPolicyDTO](#healthworkeraccesspolicydto)

- `deleteClinicAccessPolicy(String clinicAccessPolicyId)` → `void`

  - Deletes a clinic access policy
  - Receives: Clinic access policy ID
  - Returns: Nothing

- `deleteHealthWorkerAccessPolicy(String healthWorkerAccessPolicyId)` → `void`

  - Deletes a health worker access policy
  - Receives: Health worker access policy ID
  - Returns: Nothing

- `hasClinicAccess(String healthUserCi, String clinicName)` → `boolean`

  - Checks if a clinic has access to a health user's data
  - Receives: Health user CI, clinic name
  - Returns: true if access exists, false otherwise

- `hasHealthWorkerAccess(String healthUserCi, String healthWorkerCi)` → `boolean`

  - Checks if a health worker has access to a health user's data
  - Receives: Health user CI, health worker CI
  - Returns: true if access exists, false otherwise

**Todo:**

- `createSpecialtyAccessPolicy(AddSpecialtyAccessPolicyDTO dto)` → `SpecialtyAccessPolicyDTO`

  - Creates a new specialty access policy
  - Receives: [AddSpecialtyAccessPolicyDTO](#addspecialtyaccesspolicydto)
  - Returns: [SpecialtyAccessPolicyDTO](#specialtyaccesspolicydto)

- `findAllSpecialtyAccessPolicies(String healthUserCi)` → `List<SpecialtyAccessPolicyDTO>`

  - Finds all specialty access policies for a health user
  - Receives: Health user CI (document ID)
  - Returns: List of [SpecialtyAccessPolicyDTO](#specialtyaccesspolicydto)

- `deleteSpecialtyAccessPolicy(String specialtyAccessPolicyId)` → `void`

  - Deletes a specialty access policy
  - Receives: Specialty access policy ID
  - Returns: Nothing

- `hasSpecialtyAccessPolicy(String healthUserCi, List<String> specialtyNames)` → `boolean`

  - Checks if a specialty in the list has access to a health user's data
  - Receives: Health user CI, list of specialty names
  - Returns: true if access exists for at least one specialty, false otherwise

### AccessRequest

Manages access requests from health workers or clinics requesting access to health users' clinical history.

**Methods:**

- `create(AddAccessRequestDTO dto)` → `AccessRequestDTO`

  - Creates a new access request
  - Receives: [AddAccessRequestDTO](#addaccessrequestdto)
  - Returns: [AccessRequestDTO](#accessrequestdto)

- `findById(String id)` → `AccessRequestDTO`

  - Finds an access request by ID
  - Receives: Access request ID
  - Returns: [AccessRequestDTO](#accessrequestdto)

- `findAll(String healthUserCi, String healthWorkerCi, String clinicName)` → `List<AccessRequestDTO>`

  - Finds all access requests with optional filters
  - Receives: Optional health user CI, health worker CI, clinic name (can be null)
  - Returns: List of [AccessRequestDTO](#accessrequestdto) objects matching the filters

- `delete(String accessRequestId)` → `void`
  - Deletes an access request
  - Receives: Access request ID
  - Returns: Nothing

**Todo:**

- `findAll(String healthUserCi, String healthWorkerCi, String clinicName, List<String> specialtyNames)` → `List<AccessRequestDTO>`
  - Finds all access requests with optional filters
  - Receives: Optional health user CI, health worker CI, clinic name, list of specialty names
  - Returns: List of [AccessRequestDTO](#accessrequestdto) objects matching the filters

### Auth

Handles OIDC (OpenID Connect) authentication and configuration.

**OidcAuthenticationService:**

- `initiateAuthorization()` → `OidcAuthorizationResponseDTO`

  - Initiates the OIDC authorization flow
  - Receives: Nothing
  - Returns: [OidcAuthorizationResponseDTO](#oidcauthorizationresponsedto)

- `handleCallback(String code, String state)` → `OidcAuthResultDTO`

  - Handles the OIDC callback after user authentication
  - Receives: Authorization code, state parameter
  - Returns: [OidcAuthResultDTO](#oidcautresultdto)
  - Throws: Exception if authentication fails

- `buildLogoutUrl(String idToken)` → `String`
  - Builds the provider logout URL given an id_token
  - Receives: ID token
  - Returns: Logout URL string

**OidcConfigurationService:**

- Configuration service that reads OIDC parameters from environment variables
- Provides getters for authorize URL, token URL, userinfo URL, JWKS URL, client ID, client secret, redirect URI, scope, issuer, logout URL, etc.
- Singleton service initialized at startup

#### ClinicalDocument

Manages clinical documents, presigned URLs for uploads, access history, and RAG-based chat queries.

**Methods:**

- `getPresignedUploadUrl(PresignedUrlRequestDTO request)` → `PresignedUrlResponseDTO`

  - Requests a presigned URL for uploading a clinical document to S3
  - Receives: [PresignedUrlRequestDTO](#presignedurlrequestdto)
  - Returns: [PresignedUrlResponseDTO](#presignedurlresponsedto)

- `createClinicalDocument(CreateClinicalDocumentDTO dto)` → `String`

  - Creates a clinical document record after the file has been uploaded
  - Receives: [CreateClinicalDocumentDTO](#createclinicaldocumentdto)
  - Returns: Created document ID

- `fetchHealthWorkerAccessHistory(String healthWorkerCi, String healthUserCi)` → `List<ClinicalHistoryAccessLogResponseDTO>`

  - Fetches access history for a health worker
  - Receives: Health worker CI, optional health user CI to filter by
  - Returns: List of [ClinicalHistoryAccessLogResponseDTO](#clinicalhistoryaccesslogresponsedto)

- `chat(ChatRequestDTO request)` → `ChatResponseDTO`
  - Processes a chat query against patient documents using RAG (Retrieval-Augmented Generation)
  - Receives: [ChatRequestDTO](#chatrequestdto)
  - Returns: [ChatResponseDTO](#chatresponsedto)

### HealthUser

Manages health users (patients), their clinical history, and access history.

**Methods:**

- `findAll(String clinicName, String name, String ci, Integer pageIndex, Integer pageSize)` → `PaginationDTO<HealthUserDTO>`

  - Finds all health users with pagination and optional filters
  - Receives: Optional clinic name, name, CI, page index, page size
  - Returns: Paginated list of [HealthUserDTO](#healthuserdto)

- `create(AddHealthUserDTO addHealthUserDTO)` → `HealthUserDTO`

  - Creates a new health user
  - Receives: [AddHealthUserDTO](#addhealthuserdto)
  - Returns: [HealthUserDTO](#healthuserdto)

- `findById(String healthUserId)` → `HealthUserDTO`

  - Finds a health user by ID
  - Receives: Health user ID
  - Returns: [HealthUserDTO](#healthuserdto)

- `findByCi(String healthUserCi)` → `HealthUserDTO`

  - Finds a health user by CI (document ID)
  - Receives: Health user CI
  - Returns: [HealthUserDTO](#healthuserdto)

- `linkClinicToHealthUser(String healthUserId, String clinicName)` → `HealthUserDTO`

  - Links a clinic to a health user
  - Receives: Health user ID, clinic name
  - Returns: [HealthUserDTO](#healthuserdto)

- `fetchClinicalHistory(String healthUserCi, String healthWorkerCi, String clinicName, String providerName)` → `ClinicalHistoryResponseDTO`

  - Fetches clinical history for a health user
  - Receives: Health user CI, health worker CI, clinic name, provider name
  - Returns: [ClinicalHistoryResponseDTO](#clinicalhistoryresponsedto)

- `fetchHealthUserAccessHistory(String healthUserCi)` → `HealthUserAccessHistoryResponseDTO`
  - Fetches access history for a health user (who accessed their data)
  - Receives: Health user CI
  - Returns: [HealthUserAccessHistoryResponseDTO](#healthuseraccesshistoryresponsedto)

**Todo:**

- `fetchClinicalHistory(String healthUserCi, String healthWorkerCi, String clinicName, List<String> specialtyNames, String providerName)` → `ClinicalHistoryResponseDTO`

  - Fetches clinical history for a health user
  - Receives: Health user CI, optional health worker CI, optional clinic name, optional list of specialty names (can be empty)
  - Returns: [ClinicalHistoryResponseDTO](#clinicalhistoryresponsedto)

- ~~`fetchHealthUserAccessHistory(String healthUserCi)` → `HealthUserAccessHistoryResponseDTO`~~

  - ~~Fetches access history for a health user (who accessed their data)~~
  - ~~Receives: Health user CI~~
  - ~~Returns: [HealthUserAccessHistoryResponseDTO](#healthuseraccesshistoryresponsedto)~~

- `findHealthUserAccessHistory(String healthUserCi)` → `HealthUserAccessHistoryResponseDTO`
  - Retrieves access history for a health user (who accessed their data)
  - Receives: Health user CI
  - Returns: [HealthUserAccessHistoryResponseDTO](#healthuseraccesshistoryresponsedto)

### Provider

Manages healthcare providers and their associated clinics.

**Methods:**

- `create(AddProviderDTO addProviderDTO)` → `ProviderDTO`

  - Creates a new provider
  - Receives: [AddProviderDTO](#addproviderdto)
  - Returns: [ProviderDTO](#providerdto)

- `findAll()` → `List<ProviderDTO>`

  - Finds all providers
  - Receives: Nothing
  - Returns: List of [ProviderDTO](#providerdto)

- `findByName(String providerName)` → `ProviderDTO`

  - Finds a provider by name
  - Receives: Provider name
  - Returns: [ProviderDTO](#providerdto)

- `fetchClinicsByProvider(String providerName)` → `List<ClinicDTO>`
  - Fetches all clinics associated with a provider from the external API
  - Receives: Provider name
  - Returns: List of [ClinicDTO](#clinicdto) (empty list if none found or on error)

### NotificationToken

Manages notification tokens for push notifications and user subscription preferences.

**Methods:**

- `add(NotificationTokenDTO token)` → `NotificationTokenDTO`

  - Adds a notification token for a user
  - Receives: [NotificationTokenDTO](#notificationtokendto)
  - Returns: [NotificationTokenDTO](#notificationtokendto)

- `findByUserCi(String userCi)` → `List<NotificationTokenDTO>`

  - Finds all notification tokens for a user
  - Receives: User CI
  - Returns: List of [NotificationTokenDTO](#notificationtokendto)

- `delete(NotificationTokenDTO token)` → `void`

  - Deletes a notification token
  - Receives: [NotificationTokenDTO](#notificationtokendto)
  - Returns: Nothing

- `unsubscribe(String userCi, NotificationType notificationType)` → `void`

  - Unsubscribes a user from a notification type
  - Receives: User CI, notification type
  - Returns: Nothing

- `subscribe(String userCi, NotificationType notificationType)` → `void`

  - Subscribes a user to a notification type
  - Receives: User CI, notification type
  - Returns: Nothing

- `isUserSubscribedToNotificationType(String userCi, NotificationType notificationType)` → `boolean`

  - Checks if a user is subscribed to a notification type
  - Receives: User CI, notification type
  - Returns: true if subscribed, false otherwise

- `getSubscriptionPreferences(String userCi)` → `NotificationSubscriptionDTO`
  - Gets subscription preferences for a user
  - Receives: User CI
  - Returns: [NotificationSubscriptionDTO](#notificationsubscriptiondto)

### HcenAdmin

Manages HCEN administrators.

**Methods:**

- `create(AddHcenAdminDTO addHcenAdminDTO)` → `HcenAdminDTO`

  - Creates a new HCEN admin
  - Receives: [AddHcenAdminDTO](#addhcenadmindto)
  - Returns: [HcenAdminDTO](#hcenadmindto)

- `findAll()` → `List<HcenAdminDTO>`

  - Finds all HCEN admins
  - Receives: Nothing
  - Returns: List of [HcenAdminDTO](#hcenadmindto)

- `findByCi(String ci)` → `HcenAdminDTO`

  - Finds an admin by CI (document)
  - Receives: CI
  - Returns: [HcenAdminDTO](#hcenadmindto) if found, null otherwise

- `isHcenAdmin(String ci)` → `boolean`
  - Checks if a user with the given CI is an HCEN admin
  - Receives: CI
  - Returns: true if admin, false otherwise

### HealthWorker

Manages health workers (doctors, nurses, etc.).

**Methods:**

- `findAll()` → `List<HealthWorkerDTO>`

  - Finds all health workers
  - Receives: Nothing
  - Returns: List of [HealthWorkerDTO](#healthworkerdto)

- `findById(String id)` → `HealthWorkerDTO`

  - Finds a health worker by ID
  - Receives: Health worker ID
  - Returns: [HealthWorkerDTO](#healthworkerdto)

- `findByName(String name)` → `List<HealthWorkerDTO>`

  - Finds health workers by name
  - Receives: Name
  - Returns: List of [HealthWorkerDTO](#healthworkerdto)

- `findByClinicAndCi(String clinicName, String healthWorkerCi)` → `HealthWorkerDTO`
  - Finds a health worker by clinic name and CI
  - Receives: Clinic name, health worker CI
  - Returns: [HealthWorkerDTO](#healthworkerdto)

### Clinic

Manages clinics.

**Methods:**

- `create(AddClinicDTO addClinicDTO)` → `ClinicDTO`

  - Creates a new clinic
  - Receives: [AddClinicDTO](#addclinicdto)
  - Returns: [ClinicDTO](#clinicdto)

- `findByName(String name)` → `ClinicDTO`

  - Finds a clinic by name
  - Receives: Clinic name
  - Returns: [ClinicDTO](#clinicdto)

- `findAll()` → `List<ClinicDTO>`
  - Finds all clinics
  - Receives: Nothing
  - Returns: List of [ClinicDTO](#clinicdto)

### PushNotificationSender

Sends push notifications to users via Firebase Cloud Messaging.

**Methods:**

- `sendPushNotification(String title, String body)` → `void`

  - Sends a push notification (broadcast)
  - Receives: Title, body
  - Returns: Nothing

- `sendPushNotificationToToken(String title, String body, String token)` → `void`

  - Sends a push notification to a specific device token
  - Receives: Title, body, device token
  - Returns: Nothing

- `sendPushNotificationToTopic(String title, String body, String topic)` → `void`
  - Sends a push notification to a topic (all subscribers)
  - Receives: Title, body, topic name
  - Returns: Nothing

## REST API

The REST API is exposed through JAX-RS resources in the `@web` module. All endpoints are prefixed with `/api` (configured in `RestApplication`).

### Base Path

All REST endpoints are available under `/api`.

### Access Policy Resource

**Base Path:** `/api/access-policies`

- `POST /api/access-policies/clinic`

  - Creates a clinic access policy (queued)
  - Request Body: [AddClinicAccessPolicyDTO](#addclinicaccesspolicydto)
  - Response: `202 Accepted` with message

  **Todo:**

  - Messaging Queue implementation.

- `POST /api/access-policies/health-worker`

  - Creates a health worker access policy (queued)
  - Request Body: [AddHealthWorkerAccessPolicyDTO](#addhealthworkeraccesspolicydto)
  - Response: `202 Accepted` with message

  **Todo:**

  - Messaging Queue implementation.

- `GET /api/access-policies/clinic/health-user/{healthUserCi}`

  - Gets all clinic access policies for a health user
  - Path Parameter: `healthUserCi` (String)
  - Response: `200 OK` with `List<ClinicAccessPolicyDTO>`

- `GET /api/access-policies/health-worker/health-user/{healthUserCi}`

  - Gets all health worker access policies for a health user
  - Path Parameter: `healthUserCi` (String)
  - Response: `200 OK` with `List<HealthWorkerAccessPolicyDTO>`

- `DELETE /api/access-policies/clinic/{clinicAccessPolicyId}`

  - Deletes a clinic access policy
  - Path Parameter: `clinicAccessPolicyId` (String)
  - Response: `200 OK` with message

- `DELETE /api/access-policies/health-worker/{healthWorkerAccessPolicyId}`

  - Deletes a health worker access policy
  - Path Parameter: `healthWorkerAccessPolicyId` (String)
  - Response: `200 OK` with message

- `GET /api/access-policies/clinic/check-access?healthUserCi={ci}&clinicName={name}`

  - Checks if a clinic has access to a health user's data
  - Query Parameters: `healthUserCi` (String), `clinicName` (String)
  - Response: `200 OK` with `{"hasAccess": boolean}`

- `GET /api/access-policies/health-worker/check-access?healthUserCi={ci}&healthWorkerCi={ci}`

  - Checks if a health worker has access to a health user's data
  - Query Parameters: `healthUserCi` (String), `healthWorkerCi` (String)
  - Response: `200 OK` with `{"hasAccess": boolean}`

**Todo:**

- `GET /api/access-policies/specialty/health-user/{healthUserCi}`

  - Gets all specialty access policies for a health user
  - Path Parameter: `healthUserCi` (String)
  - Response: `200 OK` with `List<SpecialtyAccessPolicyDTO>`

- `DELETE /api/access-policies/specialty/{specialtyAccessPolicyId}`

  - Deletes a specialty access policy
  - Path Parameter: `specialtyAccessPolicyId` (String)
  - Response: `200 OK` with message

- `GET /api/access-policies/specialty/check-access?healthUserCi={ci}&specialtyName={name}`
  - Checks if a specialty has access to a health user's data
  - Query Parameters: `healthUserCi` (String), `specialtyName` (String)
  - Response: `200 OK` with `{"hasAccess": boolean}`

### Access Request Resource

**Base Path:** `/api/access-requests`

- `POST /api/access-requests`

  - Creates an access request (queued)
  - Request Body: [AddAccessRequestDTO](#addaccessrequestdto)
  - Response: `202 Accepted` with message

- `GET /api/access-requests?healthUserCi={ci}&healthWorkerCi={ci}&clinicName={name}`

  - Gets all access requests with optional filters
  - Query Parameters (all optional): `healthUserCi` (String), `healthWorkerCi` (String), `clinicName` (String)
  - Response: `200 OK` with `List<AccessRequestDTO>`

- `DELETE /api/access-requests/{accessRequestId}`
  - Deletes an access request
  - Path Parameter: `accessRequestId` (String)
  - Response: `200 OK` with message

**Todo:**

- `GET /api/access-requests?healthUserCi={ci}&healthWorkerCi={ci}`

  - Gets all access requests with optional filters
  - Query Parameters: `healthUserCi` (String), `healthWorkerCi` (String)
  - Response: `200 OK` with `List<AccessRequestDTO>`

### Authentication Resource

**Base Path:** `/api/auth`

- `POST /api/auth/gubuy/authorize`

  - Initiates OIDC authorization flow with gub.uy
  - Response: `200 OK` with [OidcAuthorizationResponseDTO](#oidcauthorizationresponsedto)
  - Error Responses: `503 Service Unavailable` if OIDC not configured, `500 Internal Server Error` on failure

- `GET /api/auth/gubuy/callback?code={code}&state={state}`

  - Handles OIDC callback from gub.uy
  - Query Parameters: `code` (String, required), `state` (String, required), `error` (String, optional), `error_description` (String, optional)
  - Response: `200 OK` with [OidcAuthResultDTO](#oidcautresultdto) (JSON) or `303 See Other` redirect (browser)
  - Error Responses: `400 Bad Request` for missing/invalid parameters

- `GET /api/auth/me`

  - Gets current authenticated user session info
  - Response: `200 OK` with user info object, `401 Unauthorized` if not authenticated

- `GET /api/auth/gubuy/logout`
  - Logs out local session and redirects to provider logout endpoint
  - Response: `303 See Other` redirect to logout URL or home

### Clinical Document Resource

**Base Path:** `/api/clinical-documents`

- `POST /api/clinical-documents/upload-url`

  - Gets a presigned URL for uploading a clinical document to S3
  - Request Body: [PresignedUrlRequestDTO](#presignedurlrequestdto)
  - Response: `200 OK` with [PresignedUrlResponseDTO](#presignedurlresponsedto)
  - Error Response: `500 Internal Server Error` on failure

- `POST /api/clinical-documents`

  - Creates a clinical document record after file upload
  - Request Body: [CreateClinicalDocumentDTO](#createclinicaldocumentdto)
  - Response: `201 Created` with [CreateClinicalDocumentResponseDTO](#createclinicaldocumentresponsedto)
  - Error Response: `500 Internal Server Error` on failure

  **Todo:**

  - Messaging Queue implementation.

- `GET /api/clinical-documents/clinical-history/health-workers/{health_worker_ci}/access-history?health_user_ci={ci}`

  - Fetches access history for a health worker
  - Path Parameter: `health_worker_ci` (String)
  - Query Parameter: `health_user_ci` (String, optional)
  - Response: `200 OK` with `List<ClinicalHistoryAccessLogResponseDTO>`
  - Error Response: `500 Internal Server Error` on failure

- `POST /api/clinical-documents/chat`

  - Processes a RAG-based chat query against patient documents
  - Request Body: [ChatRequestDTO](#chatrequestdto)
  - Response: `200 OK` with [ChatResponseDTO](#chatresponsedto)
  - Error Response: `500 Internal Server Error` on failure

  **Todo:**

  - Messaging Queue implementation.

### Clinical History Resource

**Base Path:** `/api/clinical-history`

- `GET /api/clinical-history/{healthUserCi}?healthWorkerCi={ci}&clinicName={name}&providerName={name}`

  - Fetches clinical history for a health user
  - Path Parameter: `healthUserCi` (String)
  - Query Parameters (all optional): `healthWorkerCi` (String), `clinicName` (String), `providerName` (String)
  - Response: `200 OK` with [ClinicalHistoryResponseDTO](#clinicalhistoryresponsedto)
  - Error Response: `500 Internal Server Error` on failure

  **Todo:**

  - Messaging Queue implementation.

- `GET /api/clinical-history/health-workers/{healthWorkerCi}/access-history?healthUserCi={ci}`

  - Fetches access history for a health worker
  - Path Parameter: `healthWorkerCi` (String)
  - Query Parameter: `healthUserCi` (String, optional)
  - Response: `200 OK` with `List<ClinicalHistoryAccessLogResponseDTO>`
  - Error Response: `500 Internal Server Error` on failure

- `GET /api/clinical-history/health-users/{healthUserCi}/access-history`

  - Fetches access history for a health user
  - Path Parameter: `healthUserCi` (String)
  - Response: `200 OK` with [HealthUserAccessHistoryResponseDTO](#healthuseraccesshistoryresponsedto)
  - Error Response: `500 Internal Server Error` on failure

- `POST /api/clinical-history/chat`

  - Processes a RAG-based chat query against patient documents
  - Request Body: [ChatRequestDTO](#chatrequestdto)
  - Response: `200 OK` with [ChatResponseDTO](#chatresponsedto)
  - Error Response: `500 Internal Server Error` on failure

  **Todo:**

  - Messaging Queue implementation.

### Clinic Resource

**Base Path:** `/api/clinics`

- `GET /api/clinics`

  - Gets all clinics
  - Response: `200 OK` with `List<ClinicDTO>`

- `GET /api/clinics/search?name={name}`
  - Finds a clinic by name
  - Query Parameter: `name` (String)
  - Response: `200 OK` with [ClinicDTO](#clinicdto), `404 Not Found` if not found

### HCEN Admin Resource

**Base Path:** `/api/hcen-admins`

- `POST /api/hcen-admins`

  - Creates a new HCEN admin
  - Request Body: [AddHcenAdminDTO](#addhcenadmindto)
  - Response: `201 Created` with [HcenAdminDTO](#hcenadmindto)
  - Error Response: `400 Bad Request` on validation failure

- `GET /api/hcen-admins/{ci}`
  - Finds an HCEN admin by CI
  - Path Parameter: `ci` (String)
  - Response: `200 OK` with [HcenAdminDTO](#hcenadmindto), `404 Not Found` if not found

### Health User Resource

**Base Path:** `/api/health-users`

- `GET /api/health-users?clinicName={clinicName}&name={name}&ci={ci}&pageIndex={index}&pageSize={size}`

  - Gets all health users with pagination and optional filters
  - Query Parameters (all optional): `clinicName` (String), `name` (String), `ci` (String), `pageIndex` (Integer), `pageSize` (Integer)
  - Response: `200 OK` with [PaginationDTO](#paginationdto)<HealthUserDTO>

- `GET /api/health-users/{ci}`

  - Finds a health user by CI
  - Path Parameter: `ci` (String)
  - Response: `200 OK` with [HealthUserDTO](#healthuserdto), `404 Not Found` if not found

- `POST /api/health-users`

  - Creates a new health user
  - Request Body: [AddHealthUserDTO](#addhealthuserdto)
  - Response: `201 Created` with [HealthUserDTO](#healthuserdto)
  - Error Response: `400 Bad Request` on validation failure

  **Todo:**

  - Messaging Queue implementation.

- `POST /api/health-users/{healthUserId}/link-clinic?clinicName={name}`

  - Links a clinic to a health user
  - Path Parameter: `healthUserId` (String)
  - Query Parameter: `clinicName` (String)
  - Response: `200 OK` with [HealthUserDTO](#healthuserdto)

  **Todo:**

  - Messaging Queue implementation.

### Health Worker Resource

**Base Path:** `/api/health-workers`

- `GET /api/health-workers?clinicName={name}&healthWorkerCi={ci}`
  - Finds a health worker by clinic name and CI
  - Query Parameters: `clinicName` (String), `healthWorkerCi` (String)
  - Response: `200 OK` with [HealthWorkerDTO](#healthworkerdto), `404 Not Found` if not found

### Notification Token Resource

**Base Path:** `/api/notification-tokens`

- `POST /api/notification-tokens`

  - Registers a notification token for a user
  - Request Body: [NotificationTokenDTO](#notificationtokendto)
  - Response: `201 Created` with [NotificationTokenDTO](#notificationtokendto)
  - Error Response: `400 Bad Request` on validation failure

- `DELETE /api/notification-tokens/{userCi}/{token}`

  - Unregisters a notification token
  - Path Parameters: `userCi` (String), `token` (String)
  - Response: `204 No Content`

- `POST /api/notification-tokens/unsubscribe`

  - Unsubscribes a user from a notification type
  - Request Body: [NotificationUnsubscribeRequest](#notificationunsubscriberequest)
  - Response: `204 No Content`
  - Error Response: `400 Bad Request` on validation failure or invalid notification type

- `POST /api/notification-tokens/subscribe`

  - Subscribes a user to a notification type
  - Request Body: [NotificationUnsubscribeRequest](#notificationunsubscriberequest)
  - Response: `204 No Content`
  - Error Response: `400 Bad Request` on validation failure or invalid notification type

- `GET /api/notification-tokens/subscription-preferences/{userCi}`
  - Gets subscription preferences for a user
  - Path Parameter: `userCi` (String)
  - Response: `200 OK` with [NotificationSubscriptionDTO](#notificationsubscriptiondto)
  - Error Response: `400 Bad Request` on validation failure

### REST-Specific DTOs

#### CreateClinicalDocumentResponseDTO

Response DTO for clinical document creation.

- `doc_id` (String): Unique identifier of the created document

#### NotificationUnsubscribeRequest

Request DTO for subscribing/unsubscribing from notifications.

- `userCi` (String): User's CI
- `notificationType` (String): Notification type (ACCESS_REQUEST or CLINICAL_HISTORY_ACCESS)

#### LinkHealthUserRequest

Request DTO for linking a clinic to a health user (currently not used in REST endpoints, but available).

- `clinicName` (String): Name of the clinic
- `document` (String): Document identifier

**Todo:**

- ~~`document` (String): Document identifier~~
- `healthUserCi` (String): Health user's CI

#### HealthUserPageResponse

Response DTO for paginated health user results (alternative to PaginationDTO, currently not used in REST endpoints).

- `items` (List<HealthUserDTO>): List of health users
- `page` (int): Current page number
- `size` (int): Page size
- `totalItems` (long): Total number of items
- `totalPages` (long): Total number of pages
- `hasNext` (boolean): Whether there is a next page
- `hasPrevious` (boolean): Whether there is a previous page

## DTOs

### AddClinicAccessPolicyDTO

Request DTO for creating a clinic access policy.

- `healthUserCi` (String): Health user's CI (document ID)
- `clinicName` (String): Name of the clinic
- `accessRequestId` (String, optional): ID of the access request that triggered this policy

### ClinicAccessPolicyDTO

Response DTO containing clinic access policy information.

- `id` (String): Unique identifier of the access policy
- `healthUserCi` (String): Health user's CI
- `clinic` ([ClinicDTO](#clinicdto)): Clinic information
- `createdAt` (LocalDate): Date when the policy was created

### AddHealthWorkerAccessPolicyDTO

Request DTO for creating a health worker access policy.

- `healthUserCi` (String): Health user's CI (document ID)
- `healthWorkerCi` (String): Health worker's CI
- `clinicName` (String): Name of the clinic
- `accessRequestId` (String, optional): ID of the access request that triggered this policy

### HealthWorkerAccessPolicyDTO

Response DTO containing health worker access policy information.

- `id` (String): Unique identifier of the access policy
- `healthUserCi` (String): Health user's CI
- `healthWorker` ([HealthWorkerDTO](#healthworkerdto)): Health worker information
- `clinic` ([ClinicDTO](#clinicdto)): Clinic information
- `createdAt` (LocalDate): Date when the policy was created

### AddAccessRequestDTO

Request DTO for creating an access request.

- `healthUserCi` (String): Health user's CI (document ID)
- `healthWorkerCi` (String): Health worker's CI requesting access
- `clinicName` (String): Name of the clinic

**Todo:**

- `specialtyNames` (Array<String>): List of specialty names

### AccessRequestDTO

Response DTO containing access request information.

- `id` (String): Unique identifier of the access request
- `healthUserId` (String): Health user's ID
- `healthUserCi` (String): Health user's CI
- `healthWorker` ([HealthWorkerDTO](#healthworkerdto)): Health worker information
- `clinic` ([ClinicDTO](#clinicdto)): Clinic information
- `createdAt` (LocalDate): Date when the request was created

**Todo:**

- `specialtyNames` (Array<String>): List of specialty names

### OidcAuthorizationResponseDTO

Response DTO for OIDC authorization initiation.

- `authorizationUrl` (String): URL to redirect user for authentication
- `state` (String): State parameter for CSRF protection

### OidcAuthResultDTO

Response DTO containing OIDC authentication result.

- `verified` (boolean): Whether authentication was verified
- `idToken` (String): ID token from OIDC provider
- `accessToken` (String): Access token from OIDC provider
- `expiresIn` (Integer): Token expiration time in seconds
- `scope` (String): Granted scopes
- `idTokenClaims` (OidcIdTokenDTO): Parsed ID token claims
- `userInfo` (OidcUserInfoDTO): User information from provider
- `logoutUrl` (String): URL for logging out

### PresignedUrlRequestDTO

Request DTO for getting a presigned URL for document upload.

- `fileName` (String): Name of the file to upload
- `contentType` (String): MIME type of the file
- `healthUserCi` (String): Health user's CI
- `healthWorkerCi` (String): Health worker's CI
- `clinicName` (String): Name of the clinic
- `providerName` (String): Name of the provider

**Todo:**

- `specialtyNames` (Array<String>): List of specialty names

### PresignedUrlResponseDTO

Response DTO containing presigned URL information.

- `uploadUrl` (String): Presigned URL for uploading the file
- `s3Url` (String): S3 URL where the file will be stored
- `objectKey` (String): S3 object key
- `expiresInSeconds` (Integer): URL expiration time in seconds

### CreateClinicalDocumentDTO

Request DTO for creating a clinical document record.

- `healthWorkerCi` (String): Health worker's CI who created the document
- `healthUserCi` (String): Health user's CI
- `clinicName` (String): Name of the clinic
- `s3Url` (String): S3 URL of the uploaded document

**Todo:**

- `title` (String): Title of the document
- `description` (String): Description of the document
- `content` (String): Content of the document
- `contentType` (String): Content type of the document
- `contentUrl` (String): URL of the document
- `providerName` (String): Name of the provider
- ~~`clinicName` (String): Name of the clinic~~

### ClinicalHistoryAccessLogResponseDTO

**Todo:**

Response DTO containing clinical history access log information.

- `id` (Long): Unique identifier of the access log
- `healthUserCi` (String): Health user's CI
- `healthWorkerCi` (String): Health worker's CI who accessed
- `clinicName` (String): Name of the clinic
- `requestedAt` (LocalDateTime): When the access was requested
- `viewed` (Boolean): Whether the history was viewed
- `decisionReason` (String): Reason for access decision

### ChatRequestDTO

Request DTO for RAG-based chat queries.

- `query` (String): User's query/question
- `conversationHistory` (List<MessageDTO>): Previous conversation messages
- `healthUserCi` (String): Health user's CI
- `documentId` (String, optional): Specific document ID to query

### ChatResponseDTO

Response DTO containing chat query results.

- `answer` (String): Generated answer to the query
- `sources` (List<ChunkSourceDTO>): Source chunks used for the answer

### ClinicalHistoryResponseDTO

Response DTO containing clinical history information.

- `healthUser` ([HealthUserDTO](#healthuserdto)): Health user information
- `documents` (List<DocumentResponseDTO>): List of clinical documents

### HealthUserAccessHistoryResponseDTO

Response DTO containing health user access history.

- `healthUser` ([HealthUserDTO](#healthuserdto)): Health user information
- `accessHistory` (List<[ClinicalHistoryAccessLogResponseDTO](#clinicalhistoryaccesslogresponsedto)>): List of access log entries

### AddHealthUserDTO

Request DTO for creating a health user. Extends [UserDTO](#userdto).

- Inherits all fields from [UserDTO](#userdto)
- `clinicNames` (Set<String>): Set of clinic names associated with the health user

### HealthUserDTO

Response DTO containing health user information. Extends [UserDTO](#userdto).

- Inherits all fields from [UserDTO](#userdto)
- `clinicNames` (Set<String>): Set of clinic names associated with the health user

### UserDTO

Base DTO for user information.

- `id` (String): Unique identifier
- `ci` (String): CI (document ID)
- `firstName` (String): First name
- `lastName` (String): Last name
- `gender` (Gender): Gender enum
- `email` (String): Email address
- `phone` (String): Phone number
- `address` (String): Physical address
- `dateOfBirth` (LocalDate): Date of birth
- `createdAt` (LocalDate): Creation date
- `updatedAt` (LocalDate): Last update date

### PaginationDTO

Generic DTO for paginated results.

- `items` (List<T>): List of items in the current page
- `pageIndex` (int): Current page index (0-based)
- `pageSize` (int): Number of items per page
- `total` (long): Total number of items
- `totalPages` (long): Total number of pages
- `hasNextPage` (boolean): Whether there is a next page
- `hasPreviousPage` (boolean): Whether there is a previous page

### AddProviderDTO

Request DTO for creating a provider.

- `providerName` (String): Name of the provider

### ProviderDTO

Response DTO containing provider information.

- `id` (String): Unique identifier of the provider
- `providerName` (String): Name of the provider
- `createdAt` (LocalDate): Creation date
- `updatedAt` (LocalDate): Last update date

### NotificationTokenDTO

DTO for notification tokens.

- `id` (String): Unique identifier of the token
- `userCi` (String): User's CI
- `token` (String): Device notification token

### NotificationSubscriptionDTO

Response DTO containing notification subscription preferences.

- `userCi` (String): User's CI
- `subscribedToAccessRequest` (boolean): Whether subscribed to access request notifications
- `subscribedToClinicalHistoryAccess` (boolean): Whether subscribed to clinical history access notifications

### AddHcenAdminDTO

Request DTO for creating an HCEN admin. Extends [UserDTO](#userdto).

- Inherits all fields from [UserDTO](#userdto)

### HcenAdminDTO

Response DTO containing HCEN admin information. Extends [UserDTO](#userdto).

- Inherits all fields from [UserDTO](#userdto)

### HealthWorkerDTO

Response DTO containing health worker information.

- `ci` (String): Health worker's CI (document ID)
- `firstName` (String): First name
- `lastName` (String): Last name
- `email` (String): Email address
- `phone` (String): Phone number
- `address` (String): Physical address
- `dateOfBirth` (LocalDate): Date of birth
- `clinicNames` (List<String>): List of clinic names where the health worker works

### AddClinicDTO

Request DTO for creating a clinic.

- `name` (String): Name of the clinic
- `email` (String): Email address
- `phone` (String): Phone number
- `address` (String): Physical address
- `clinicAdmin` ([ClinicAdminDTO](#clinicadmindto)): Clinic administrator information
- `providerName` (String): Name of the associated provider

### ClinicDTO

Response DTO containing clinic information.

- `id` (String): Unique identifier of the clinic
- `name` (String): Name of the clinic
- `email` (String): Email address
- `phone` (String): Phone number
- `address` (String): Physical address
- `createdAt` (LocalDate): Creation date
- `updatedAt` (LocalDate): Last update date
- `healthWorkers` (List<[HealthWorkerDTO](#healthworkerdto)>): List of health workers associated with the clinic

### ClinicAdminDTO

DTO for clinic administrator information.

- `ci` (String): Administrator's CI (document ID)
- `firstName` (String): First name
- `lastName` (String): Last name
- `email` (String): Email address
- `phone` (String): Phone number
- `address` (String): Physical address
- `dateOfBirth` (LocalDate): Date of birth

### DocumentResponseDTO

Response DTO containing clinical document information.

- `id` (String): Unique identifier of the document
- `healthWorker` ([HealthWorkerDTO](#healthworkerdto)): Health worker who created the document
- `clinic` ([ClinicDTO](#clinicdto)): Clinic information
- `createdAt` (LocalDateTime): Creation timestamp
- `s3Url` (String): S3 URL of the document

### MessageDTO

DTO for chat conversation messages.

- `role` (String): Role of the message sender (e.g., "user", "assistant")
- `content` (String): Message content

### ChunkSourceDTO

DTO for document chunk sources used in RAG responses.

- `documentId` (String): ID of the source document
- `chunkId` (String): ID of the chunk
- `text` (String): Text content of the chunk
- `similarityScore` (Double): Similarity score for the chunk
- `pageNumber` (Integer): Page number in the document
- `sectionTitle` (String): Title of the section

**Todo:**

### AddSpecialtyAccessPolicyDTO

Request DTO for creating a specialty access policy.

- `healthUserCi` (String): Health user's CI (document ID)
- `specialtyName` (String): Name of the specialty
- `accessRequestId` (String, optional): ID of the access request that triggered this policy

### SpecialtyAccessPolicyDTO

Response DTO containing specialty access policy information.

- `id` (String): Unique identifier of the access policy
- `healthUserCi` (String): Health user's CI
- `specialtyName` (String): Name of the specialty
- `createdAt` (LocalDate): Date when the policy was created
