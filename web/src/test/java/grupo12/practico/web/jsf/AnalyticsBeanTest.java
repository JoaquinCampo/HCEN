package grupo12.practico.web.jsf;

import grupo12.practico.services.Logger.LoggerServiceLocal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyticsBean Tests")
class AnalyticsBeanTest {

    @Mock
    private LoggerServiceLocal loggerService;

    private AnalyticsBean analyticsBean;

    @BeforeEach
    void setUp() throws Exception {
        analyticsBean = new AnalyticsBean();

        // Use reflection to inject mocked dependency
        var serviceField = AnalyticsBean.class.getDeclaredField("loggerService");
        serviceField.setAccessible(true);
        serviceField.set(analyticsBean, loggerService);
    }

    @Test
    @DisplayName("init - Should load analytics on initialization")
    void init_ShouldLoadAnalyticsOnInitialization() {
        Map<String, Long> mockHealthUserLogs = createMockMap("CREATED", 10L);
        Map<String, Long> mockAccessRequestLogs = createMockMap("REQUESTED", 5L);
        Map<String, Long> mockClinicalHistoryLogs = createMockMap("SELF_ACCESS", 8L);
        Map<String, Long> mockLogActivity = createMockMap("HealthUser", 15L);

        when(loggerService.getHealthUserLogsByAction(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockHealthUserLogs);
        when(loggerService.getAccessRequestLogsByAction(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockAccessRequestLogs);
        when(loggerService.getClinicalHistoryLogsByAccessType(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockClinicalHistoryLogs);
        when(loggerService.getDocumentLogsCount(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(20L);
        when(loggerService.getLogActivityByDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockLogActivity);

        analyticsBean.init();

        verify(loggerService).getHealthUserLogsByAction(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(loggerService).getAccessRequestLogsByAction(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(loggerService).getClinicalHistoryLogsByAccessType(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(loggerService).getDocumentLogsCount(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(loggerService).getLogActivityByDate(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("loadAnalytics - Should load all analytics data")
    void loadAnalytics_ShouldLoadAllAnalyticsData() {
        Map<String, Long> mockHealthUserLogs = createMockMap("CREATED", 10L);
        Map<String, Long> mockAccessRequestLogs = createMockMap("REQUESTED", 5L);
        Map<String, Long> mockClinicalHistoryLogs = createMockMap("SELF_ACCESS", 8L);
        Map<String, Long> mockLogActivity = createMockMap("HealthUser", 15L);

        when(loggerService.getHealthUserLogsByAction(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockHealthUserLogs);
        when(loggerService.getAccessRequestLogsByAction(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockAccessRequestLogs);
        when(loggerService.getClinicalHistoryLogsByAccessType(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockClinicalHistoryLogs);
        when(loggerService.getDocumentLogsCount(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(20L);
        when(loggerService.getLogActivityByDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockLogActivity);

        analyticsBean.loadAnalytics();

        assertEquals(mockHealthUserLogs, analyticsBean.getHealthUserLogsByAction());
        assertEquals(mockAccessRequestLogs, analyticsBean.getAccessRequestLogsByAction());
        assertEquals(mockClinicalHistoryLogs, analyticsBean.getClinicalHistoryLogsByAccessType());
        assertEquals(20L, analyticsBean.getDocumentLogsCount());
        assertEquals(mockLogActivity, analyticsBean.getLogActivityByType());
    }

    @Test
    @DisplayName("setDaysRange - Should update days range and reload analytics")
    void setDaysRange_ShouldUpdateDaysRangeAndReloadAnalytics() {
        Map<String, Long> mockLogActivity = createMockMap("HealthUser", 15L);
        when(loggerService.getHealthUserLogsByAction(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(createMockMap("CREATED", 10L));
        when(loggerService.getAccessRequestLogsByAction(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(createMockMap("REQUESTED", 5L));
        when(loggerService.getClinicalHistoryLogsByAccessType(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(createMockMap("SELF_ACCESS", 8L));
        when(loggerService.getDocumentLogsCount(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(20L);
        when(loggerService.getLogActivityByDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockLogActivity);

        analyticsBean.setDaysRange(7);

        assertEquals(7, analyticsBean.getDaysRange());
        // setDaysRange calls loadAnalytics which calls all service methods once
        verify(loggerService).getHealthUserLogsByAction(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("getTotalLogs - Should return sum of all log activities")
    void getTotalLogs_ShouldReturnSumOfAllLogActivities() {
        Map<String, Long> mockLogActivity = new HashMap<>();
        mockLogActivity.put("HealthUser", 10L);
        mockLogActivity.put("AccessRequest", 5L);
        mockLogActivity.put("ClinicalHistory", 8L);

        // Set the logActivityByType field via reflection
        assertDoesNotThrow(() -> {
            var field = AnalyticsBean.class.getDeclaredField("logActivityByType");
            field.setAccessible(true);
            field.set(analyticsBean, mockLogActivity);
        });

        assertEquals(23L, analyticsBean.getTotalLogs());
    }

    @Test
    @DisplayName("getTotalLogs - Should return 0 when logActivityByType is null")
    void getTotalLogs_ShouldReturnZeroWhenLogActivityByTypeIsNull() {
        // logActivityByType is null by default
        assertEquals(0L, analyticsBean.getTotalLogs());
    }

    @Test
    @DisplayName("getHealthUserActionLabel - Should return correct labels for actions")
    void getHealthUserActionLabel_ShouldReturnCorrectLabelsForActions() {
        assertEquals("Usuarios Creados", analyticsBean.getHealthUserActionLabel("CREATED"));
        assertEquals("Clínicas Vinculadas", analyticsBean.getHealthUserActionLabel("CLINIC_LINKED"));
        assertEquals("UNKNOWN", analyticsBean.getHealthUserActionLabel("UNKNOWN"));
    }

    @Test
    @DisplayName("getAccessRequestActionLabel - Should return correct labels for actions")
    void getAccessRequestActionLabel_ShouldReturnCorrectLabelsForActions() {
        assertEquals("Solicitadas", analyticsBean.getAccessRequestActionLabel("REQUESTED"));
        assertEquals("Aceptadas por Clínica", analyticsBean.getAccessRequestActionLabel("ACCEPTED_BY_CLINIC"));
        assertEquals("Aceptadas por Trabajador",
                analyticsBean.getAccessRequestActionLabel("ACCEPTED_BY_HEALTH_WORKER"));
        assertEquals("Aceptadas por Especialidad", analyticsBean.getAccessRequestActionLabel("ACCEPTED_BY_SPECIALTY"));
        assertEquals("Denegadas", analyticsBean.getAccessRequestActionLabel("DENIED"));
        assertEquals("UNKNOWN", analyticsBean.getAccessRequestActionLabel("UNKNOWN"));
    }

    @Test
    @DisplayName("getClinicalHistoryAccessTypeLabel - Should return correct labels for access types")
    void getClinicalHistoryAccessTypeLabel_ShouldReturnCorrectLabelsForAccessTypes() {
        assertEquals("Acceso Propio", analyticsBean.getClinicalHistoryAccessTypeLabel("SELF_ACCESS"));
        assertEquals("Por Clínica", analyticsBean.getClinicalHistoryAccessTypeLabel("BY_CLINIC"));
        assertEquals("Por Trabajador", analyticsBean.getClinicalHistoryAccessTypeLabel("BY_HEALTH_WORKER"));
        assertEquals("Por Especialidad", analyticsBean.getClinicalHistoryAccessTypeLabel("BY_SPECIALTY"));
        assertEquals("UNKNOWN", analyticsBean.getClinicalHistoryAccessTypeLabel("UNKNOWN"));
    }

    @Test
    @DisplayName("getLogTypeLabel - Should return correct labels for log types")
    void getLogTypeLabel_ShouldReturnCorrectLabelsForLogTypes() {
        assertEquals("Usuarios de Salud", analyticsBean.getLogTypeLabel("HealthUser"));
        assertEquals("Solicitudes de Acceso", analyticsBean.getLogTypeLabel("AccessRequest"));
        assertEquals("Historias Clínicas", analyticsBean.getLogTypeLabel("ClinicalHistory"));
        assertEquals("Documentos", analyticsBean.getLogTypeLabel("Document"));
        assertEquals("UNKNOWN", analyticsBean.getLogTypeLabel("UNKNOWN"));
    }

    @Test
    @DisplayName("getDaysRange - Should return default days range")
    void getDaysRange_ShouldReturnDefaultDaysRange() {
        assertEquals(30, analyticsBean.getDaysRange());
    }

    private Map<String, Long> createMockMap(String key, Long value) {
        Map<String, Long> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
