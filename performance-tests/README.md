# Performance Tests con JMeter

Este directorio contiene las pruebas de performance para el componente central HCEN, implementadas con Apache JMeter.

## Objetivos

Las pruebas de performance cumplen con el requerimiento 35 del laboratorio:

1. **Verificar que los tiempos de respuesta no se degraden a lo largo de la prueba**
2. **Encontrar el punto de quiebre del sistema**
3. **Identificar cuellos de botella que impiden que la aplicación responda de forma aceptable**

## Estructura

```
performance-tests/
├── jmeter/
│   ├── test-plans/          # Planes de prueba JMeter (.jmx)
│   ├── data/                # Datos de prueba (CSV)
│   ├── config/              # Configuraciones de JMeter
│   └── reports/             # Reportes generados (gitignored)
├── scripts/                 # Scripts de ejecución
└── README.md                # Esta documentación
```

## Requisitos Previos

### Instalación de JMeter

1. **Descargar JMeter**: https://jmeter.apache.org/download_jmeter.cgi
2. **Requisitos**: Java 8 o superior
3. **Instalación**:
   ```bash
   # macOS (con Homebrew)
   brew install jmeter
   
   # Linux (con apt)
   sudo apt-get install jmeter
   
   # Manual: Descomprimir y agregar al PATH
   export JMETER_HOME=/path/to/apache-jmeter-5.6
   export PATH=$PATH:$JMETER_HOME/bin
   ```

### Verificar Instalación

```bash
jmeter --version
```

## Configuración

### Variables de Entorno

Puedes configurar las siguientes variables de entorno:

- `BASE_URL`: URL base de la aplicación (default: `http://localhost:8080`)
- `API_BASE_PATH`: Path base de la API (default: `/api`)
- `TEST_DATA_DIR`: Directorio con datos de prueba (default: `./jmeter/data`)
- `REPORT_OUTPUT_DIR`: Directorio para reportes (default: `./jmeter/reports`)
- `JMETER_HOME`: Directorio de instalación de JMeter (si no está en PATH)

### Archivos de Configuración

- `jmeter/config/jmeter.properties`: Configuración base de JMeter
- `jmeter/config/user.properties`: Propiedades específicas del proyecto

## Preparación de Datos

Antes de ejecutar las pruebas, prepara los datos de prueba:

```bash
cd HCEN/performance-tests
./scripts/prepare-test-data.sh
```

Esto genera los archivos CSV necesarios en `jmeter/data/`.

**Nota**: Los archivos CSV son templates. Puedes necesitar poblar la base de datos con datos reales antes de ejecutar las pruebas.

## Planes de Prueba Disponibles

### 1. Clinical History Stability Test

**Archivo**: `clinical-history-stability.jmx`

**Objetivo**: Verificar que los tiempos de respuesta no se degraden durante la prueba.

**Configuración**:
- Usuarios concurrentes: 10-50
- Ramp-up: 60 segundos
- Duración: 30 minutos
- Endpoint principal: `GET /api/clinical-history/{healthUserCi}`

**Ejecutar**:
```bash
./scripts/run-performance-tests.sh clinical-history-stability
```

### 2. Breakpoint Test

**Archivo**: `breakpoint-test.jmx`

**Objetivo**: Encontrar el punto de quiebre del sistema con ramp-up agresivo.

**Configuración**:
- Usuarios concurrentes: hasta 200
- Ramp-up: 10 minutos
- Duración: hasta 1 hora o fallo
- Endpoints múltiples (mix aleatorio)

**Ejecutar**:
```bash
./scripts/run-performance-tests.sh breakpoint-test --users 200
```

### 3. Bottleneck Analysis Test

**Archivo**: `bottleneck-analysis.jmx`

**Objetivo**: Identificar cuellos de botella en componentes específicos.

**Configuración**:
- Usuarios concurrentes: 20
- Ramp-up: 30 segundos
- Duración: 15 minutos
- Endpoints específicos por componente (DB queries, JMS operations)

**Ejecutar**:
```bash
./scripts/run-performance-tests.sh bottleneck-analysis
```

## Ejecución de Pruebas

### Ejecución Básica

```bash
cd HCEN/performance-tests
./scripts/run-performance-tests.sh <test-plan-name>
```

### Ejecución con Opciones Personalizadas

```bash
./scripts/run-performance-tests.sh clinical-history-stability \
  --base-url http://localhost:8080 \
  --users 50 \
  --duration 1800 \
  --ramp-up 120
```

### Opciones Disponibles

- `--base-url URL`: URL base de la aplicación
- `--api-path PATH`: Path base de la API
- `--data-dir DIR`: Directorio con datos de prueba
- `--report-dir DIR`: Directorio para reportes
- `--jmeter-home DIR`: Directorio de instalación de JMeter
- `--users N`: Número de usuarios concurrentes
- `--duration SECONDS`: Duración de la prueba en segundos
- `--ramp-up SECONDS`: Tiempo de ramp-up en segundos

## Autenticación Mock

Las pruebas utilizan un endpoint mock de autenticación para evitar depender del servicio OIDC real:

- **Endpoint**: `POST /api/auth/test/token`
- **Parámetros**: `ci` (opcional), `email` (opcional)
- **Uso**: Crea una sesión de prueba con datos mock

**Nota**: Este endpoint debe estar habilitado solo en entornos de prueba/desarrollo.

## Interpretación de Resultados

### Reportes Generados

Después de ejecutar una prueba, se generan:

1. **Archivo JTL**: Resultados en formato XML (`*.jtl`)
2. **Reporte HTML**: Reporte visual completo (`*_html/index.html`)

### Métricas Clave

#### Response Time (Tiempo de Respuesta)
- **Average**: Tiempo promedio de respuesta
- **Min/Max**: Tiempos mínimo y máximo
- **Percentiles (p50, p90, p95, p99)**: Distribución de tiempos

**Objetivo**: Verificar que no haya degradación a lo largo de la prueba.

#### Throughput
- **Requests/sec**: Número de requests por segundo
- **Transactions/sec**: Transacciones completadas por segundo

**Objetivo**: Identificar el throughput máximo del sistema.

#### Error Rate
- **Error %**: Porcentaje de requests con error
- **Tipos de error**: 4xx, 5xx, timeouts

**Objetivo**: Mantener error rate bajo (< 1% idealmente).

#### Response Times Over Time
- Gráfico que muestra la evolución de tiempos de respuesta
- **Objetivo**: Detectar degradación gradual

### Análisis de Cuellos de Botella

Para identificar cuellos de botella:

1. Comparar tiempos de respuesta entre diferentes endpoints
2. Analizar tiempos de conexión vs tiempo de procesamiento
3. Revisar métricas de recursos del servidor (si disponibles)
4. Identificar endpoints con latencia consistentemente alta

## Troubleshooting

### JMeter no encontrado

```bash
# Verificar instalación
which jmeter

# O establecer JMETER_HOME
export JMETER_HOME=/path/to/apache-jmeter-5.6
./scripts/run-performance-tests.sh clinical-history-stability
```

### Error de conexión

- Verificar que la aplicación esté ejecutándose
- Verificar la URL base (`--base-url`)
- Verificar firewall/red

### Datos de prueba no encontrados

```bash
# Regenerar datos de prueba
./scripts/prepare-test-data.sh
```

### Autenticación falla

- Verificar que el endpoint `/api/auth/test/token` esté disponible
- Verificar logs de la aplicación
- Verificar que el modo test esté habilitado

### Reportes no se generan

- Verificar permisos de escritura en el directorio de reportes
- Verificar espacio en disco
- Revisar logs de JMeter para errores

## Mejores Prácticas

1. **Ejecutar en ambiente aislado**: No ejecutar pruebas en producción
2. **Monitorear recursos**: Monitorear CPU, memoria, I/O durante las pruebas
3. **Ejecutar múltiples veces**: Ejecutar cada prueba varias veces para validar consistencia
4. **Documentar resultados**: Guardar reportes para comparación futura
5. **Ajustar gradualmente**: Aumentar carga gradualmente para identificar límites

## Integración con CI/CD

Para integrar en CI/CD (futuro):

```yaml
# Ejemplo GitLab CI
performance-test:
  script:
    - cd HCEN/performance-tests
    - ./scripts/run-performance-tests.sh clinical-history-stability
  artifacts:
    paths:
      - HCEN/performance-tests/jmeter/reports/
    expire_in: 7 days
```

## Referencias

- [Documentación oficial de JMeter](https://jmeter.apache.org/usermanual/index.html)
- [Mejores prácticas de JMeter](https://jmeter.apache.org/usermanual/best-practices.html)
- Requerimiento 35 del laboratorio TSE

## Soporte

Para problemas o preguntas:
1. Revisar esta documentación
2. Revisar logs de JMeter y la aplicación
3. Consultar con el equipo de desarrollo

