# Especificación para Generar Datos en mock-data.properties

## Formato General

El archivo `mock-data.properties` usa el formato estándar de Java Properties. Cada persona se define con un índice numérico secuencial empezando en 1.

## Estructura de Campos

### Autenticación (Requerido)
```properties
auth.organizacion=<string>          # Nombre de la organización (ej: ORGANISMO_TEST)
auth.password=<string>              # Contraseña de la entidad (ej: SECRETO_TEST)
```

### Datos de Persona

#### Campos Requeridos para Personas Válidas
```properties
person.<index>.ci=<string>                    # CI: Solo dígitos, 7-8 dígitos (ej: "12345678")
person.<index>.tipoDocumento=<string>         # "CI" o "DO" (default: "CI")
person.<index>.nombre1=<string>              # Primer nombre (MAYÚSCULAS, ej: "JUAN")
person.<index>.apellido1=<string>             # Primer apellido (MAYÚSCULAS, ej: "PEREZ")
person.<index>.fechaNacimiento=<string>      # Formato: yyyy-MMdd (ver formato de fecha abajo)
person.<index>.sexo=<integer>                # 1 = Masculino, 2 = Femenino
person.<index>.nacionalidad=<integer>        # 1 = Oriental, 3 = Extranjero, 0 = Desconocida
person.<index>.nombreEnCedula=<string>        # Nombre completo tal como aparece en la CI
```

#### Campos Opcionales
```properties
person.<index>.nombre2=<string>              # Segundo nombre (puede estar vacío)
person.<index>.apellido2=<string>             # Segundo apellido (puede estar vacío)
person.<index>.apellidoAdoptivo1=<string>    # Apellido adoptivo 1 (opcional)
person.<index>.apellidoAdoptivo2=<string>    # Apellido adoptivo 2 (opcional)
```

#### Para CIs con Errores Específicos
```properties
person.<index>.ci=<string>                   # CI que genera error
person.<index>.error=<integer>               # Código de error (500, 701, 1001, 1002, 1003, 10001, 10002)
```

## Formato de Fecha de Nacimiento

**Formato requerido:** `yyyy-MMdd`

Donde:
- `yyyy` = año con 4 dígitos
- `-` = guion literal (requerido)
- `MM` = mes con 2 dígitos (01-12)
- `dd` = día con 2 dígitos (01-31)

**Ejemplos válidos:**
- `1980-0503` = 3 de mayo de 1980
- `1995-1225` = 25 de diciembre de 1995
- `2010-0315` = 15 de marzo de 2010
- `2000-0101` = 1 de enero de 2000

**IMPORTANTE:**
- El guion es obligatorio después del año
- El mes y día deben tener exactamente 2 dígitos cada uno (usar 01, 02, etc., no 1, 2)
- No usar espacios
- El formato total debe tener exactamente 9 caracteres: `yyyy-MMdd`

## Validaciones de CI

- **Formato:** Solo dígitos, sin puntos ni guiones
- **Longitud:** 7 u 8 dígitos
- **Ejemplos válidos:** `12345678`, `1234567`
- **Ejemplos inválidos:** `12345678-9`, `12.345.678`, `ABC12345`

## Códigos de Error

| Código | Significado |
|--------|-------------|
| 500 | Persona inexistente |
| 701 | Datos de persona a regularizar (warning) |
| 1001 | No se pudo completar la consulta |
| 1002 | Límite de consultas excedido |
| 1003 | Número de cédula anulado |
| 10001 | Parámetros incorrectos |
| 10002 | Acceso No Autorizado |

## Valores de Sexo

- `1` = Masculino
- `2` = Femenino

## Valores de Nacionalidad

- `1` = Oriental (uruguayo)
- `3` = Extranjero
- `0` = Desconocida

## Ejemplo Completo

```properties
# Autenticación
auth.organizacion=ORGANISMO_TEST
auth.password=SECRETO_TEST

# Persona 1: Mayor de edad (nació en 1980, tiene ~45 años en 2025)
person.1.ci=12345678
person.1.tipoDocumento=CI
person.1.nombre1=JUAN
person.1.nombre2=PABLO
person.1.apellido1=PEREZ
person.1.apellido2=GONZALEZ
person.1.sexo=1
person.1.fechaNacimiento=1980-0503
person.1.nacionalidad=1
person.1.nombreEnCedula=JUAN PABLO PEREZ GONZALEZ

# Persona 2: Menor de edad (nació en 2010, tiene ~15 años en 2025)
person.2.ci=87654321
person.2.tipoDocumento=CI
person.2.nombre1=MARIA
person.2.nombre2=ANA
person.2.apellido1=LOPEZ
person.2.apellido2=SILVA
person.2.sexo=2
person.2.fechaNacimiento=2010-0315
person.2.nacionalidad=1
person.2.nombreEnCedula=MARIA ANA LOPEZ SILVA

# Persona 3: CI anulada
person.3.ci=11111111
person.3.error=1003

# Persona 4: Exactamente 18 años (nació hace 18 años)
person.4.ci=22222222
person.4.tipoDocumento=CI
person.4.nombre1=PEDRO
person.4.apellido1=SILVA
person.4.sexo=1
person.4.fechaNacimiento=2007-1117
person.4.nacionalidad=1
person.4.nombreEnCedula=PEDRO SILVA
```

## Reglas para Generar Datos Realistas

1. **Fechas de nacimiento:**
   - Para mayor de edad: usar años anteriores a (año actual - 18)
   - Para menor de edad: usar años entre (año actual - 17) y (año actual - 1)
   - Para exactamente 18: usar año = año actual - 18

2. **Nombres:**
   - Usar MAYÚSCULAS (como aparecen en documentos oficiales uruguayos)
   - Nombres comunes uruguayos: JUAN, MARIA, CARLOS, ANA, etc.
   - Apellidos comunes: PEREZ, GONZALEZ, RODRIGUEZ, LOPEZ, etc.

3. **CIs:**
   - Generar números de 7-8 dígitos
   - Evitar números muy simples como 11111111 (reservados para casos especiales)
   - Usar números variados para diferentes personas

4. **Consistencia:**
   - `nombreEnCedula` debe ser la concatenación de nombres y apellidos en orden
   - `sexo` debe ser consistente con el nombre (1 para nombres masculinos, 2 para femeninos)

## Errores Comunes a Evitar

1. ❌ Formato de fecha incorrecto:
   - `1980-5-3` (falta padding en mes/día)
   - `1980/05/03` (usar guion, no slash)
   - `05-03-1980` (orden incorrecto)

2. ❌ CI con formato incorrecto:
   - `12345678-9` (no incluir dígito verificador)
   - `12.345.678` (no usar puntos)

3. ❌ Campos faltantes para personas válidas:
   - Falta `nombre1` o `apellido1` (requeridos)
   - Falta `fechaNacimiento` (requerido para calcular edad)

4. ❌ Inconsistencias:
   - `sexo=1` con nombre femenino
   - `nombreEnCedula` no coincide con nombres/apellidos

