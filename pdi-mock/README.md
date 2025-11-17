# PDI Mock Service

Mock del Servicio Básico de Información de DNIC (Plataforma de Interoperabilidad) para desarrollo y testing.

## Descripción

Este servicio simula el comportamiento del Servicio Básico de Información ofrecido por DNIC a través de la PDI, permitiendo:

- Consultar datos filiatorios de personas por CI (`ObtPersonaPorDoc`)
- Obtener información del servicio (`ProductDesc`)

## Requisitos

- Java 17+
- Maven 3.6+

## Compilación

```bash
mvn clean compile
```

## Ejecución

### Opción 1: Ejecutar directamente

```bash
mvn exec:java -Dexec.mainClass="grupo12.practico.pdimock.server.PdiMockServer" -Dexec.args="8080"
```

### Opción 2: Compilar y ejecutar JAR

```bash
mvn clean package
java -jar target/pdi-mock-1.0-SNAPSHOT.war
```

El servicio estará disponible en: `http://localhost:8080/wsServicioDeInformacionBasico`

WSDL disponible en: `http://localhost:8080/wsServicioDeInformacionBasico?wsdl`

## Configuración

### Datos de Prueba

Los datos de prueba se configuran en `src/main/resources/mock-data.properties`:

```properties
# Autenticación
auth.organizacion=ORGANISMO_TEST
auth.password=SECRETO_TEST

# Personas de prueba
person.1.ci=12345678
person.1.nombre1=JUAN
person.1.nombre2=PABLO
person.1.apellido1=PEREZ
person.1.apellido2=GONZALEZ
person.1.fechaNacimiento=1980-0503
# ...
```

### CIs de Prueba Predefinidas

- `12345678`: CI válida - Usuario mayor de edad (1980)
- `87654321`: CI válida - Usuario menor de edad (2010)
- `11111111`: CI anulada (error 1003)
- Cualquier otra CI: Persona inexistente (error 500)

## Uso desde el Cliente HCEN

El cliente SOAP en el módulo EJB se configura mediante:

1. **Archivo de propiedades**: `ejb/src/main/resources/META-INF/pdi-service.properties`
2. **Variables de entorno**: `PDI_SERVICE_URL`, `PDI_ORGANIZACION`, `PDI_PASSWORD`
3. **System properties**: `pdi.service.url`, `pdi.organizacion`, `pdi.password`

Prioridad: System Property > Environment Variable > Properties File > Default

## Códigos de Error

- `500`: Persona inexistente
- `701`: Datos de persona a regularizar (warning)
- `1001`: No se pudo completar la consulta
- `1002`: Límite de consultas excedido
- `1003`: Número de cédula anulado
- `10001`: Parámetros incorrectos
- `10002`: Acceso No Autorizado

## Testing

```bash
mvn test
```

