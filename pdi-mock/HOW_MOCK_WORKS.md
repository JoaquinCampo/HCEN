# ¿Cómo Funciona el Mock y de Dónde Vienen los Datos?

## La Situación Real

**Problema:** El servicio PDI real NO es público. Solo entidades autorizadas pueden accederlo.

**Solución:** Creamos un **MOCK** (simulación) del servicio para desarrollo y testing.

## ¿De Dónde Vienen los Datos del Mock?

### Respuesta Corta

Los datos del mock son **DATOS FICTICIOS/SIMULADOS** que **NOSOTROS CREAMOS MANUALMENTE** para poder desarrollar y testear el sistema.

### No Son Datos Reales

- ❌ NO consultamos el servicio real de PDI
- ❌ NO tenemos acceso a datos reales de usuarios
- ❌ NO podemos obtener información real de CIs

### Son Datos de Prueba

- ✅ Los creamos manualmente en `mock-data.properties`
- ✅ Son datos ficticios para desarrollo/testing
- ✅ Simulan cómo funcionaría el servicio real

## Flujo en Desarrollo vs Producción

### En Desarrollo (con Mock)

```
Usuario prueba el sistema
  ↓
Ingresa CI: "12345678"
  ↓
Sistema consulta Mock PDI
  ↓
Mock busca en mock-data.properties
  ↓
Si encuentra CI → Devuelve datos ficticios configurados
Si NO encuentra → Error 500 (persona inexistente)
```

**Limitación:** Solo funcionan las CIs que están configuradas en `mock-data.properties`

### En Producción (con Servicio Real)

```
Usuario real usa el sistema
  ↓
Ingresa su CI real: "54053584"
  ↓
Sistema consulta Servicio PDI Real (del gobierno)
  ↓
Servicio PDI consulta base de datos oficial DNIC
  ↓
Devuelve datos REALES del usuario
```

**Ventaja:** Funciona con CIs reales de cualquier usuario registrado en Uruguay

## Cómo Usar el Mock

### Para Desarrollo

1. **Configurar datos de prueba** en `mock-data.properties`:

   ```properties
   person.1.ci=12345678
   person.1.fechaNacimiento=1980-0503
   person.1.nombre1=JUAN
   ...
   ```

2. **Usar esas CIs en tus tests**:

   - CI `12345678` → Usuario mayor de edad
   - CI `87654321` → Usuario menor de edad
   - CI `11111111` → CI anulada (error)

3. **Cualquier otra CI** → Retornará error 500 (persona inexistente)

### Para Testing

Cuando escribas tests o pruebes el sistema:

- Usa las CIs configuradas en el mock
- O agrega más CIs al archivo `mock-data.properties`

## Ejemplo Práctico

**Escenario de Desarrollo:**

```java
// Test
String ci = "12345678"; // CI configurada en mock-data.properties
boolean esMayor = ageVerificationService.verificarMayorDeEdad(ci);
// ✅ Funciona porque está en el mock

String ciReal = "54053584"; // CI real de un usuario
boolean esMayor = ageVerificationService.verificarMayorDeEdad(ciReal);
// ❌ Retorna error porque NO está en el mock
```

**Solución:** Agregar la CI al mock:

```properties
person.5.ci=54053584
person.5.fechaNacimiento=1990-0615
person.5.nombre1=MARIA
...
```

## ¿Cómo Obtener Datos para el Mock?

### Opción 1: Crear Datos Ficticios (Recomendado)

- Inventar CIs y datos de prueba
- Usar nombres comunes uruguayos
- Calcular fechas que den las edades que necesitas probar

### Opción 2: Usar Datos de Prueba del Sistema

Si ya tienes usuarios de prueba en tu sistema:

- Usar las mismas CIs
- Configurar las mismas fechas de nacimiento en el mock
- Mantener consistencia entre sistemas

### Opción 3: Generar Datos con IA

Usar el prompt en `PROMPT_FOR_AI.md` para generar datos de prueba consistentes.

## Importante

### El Mock NO Reemplaza al Servicio Real

- **Mock:** Para desarrollo/testing con datos ficticios
- **Servicio Real:** Para producción con datos reales del gobierno

### En Producción

Cuando el sistema esté en producción:

- Se configura la URL del servicio PDI real
- El servicio real consulta la base de datos oficial del DNIC
- Funciona con CIs reales de cualquier usuario

## Resumen

**Pregunta:** ¿Cómo obtenemos información del usuario para el mock?

**Respuesta:**

- Los datos del mock son **ficticios** que creamos manualmente
- Los configuramos en `mock-data.properties`
- Son solo para desarrollo/testing
- En producción se usa el servicio real que sí tiene datos reales
