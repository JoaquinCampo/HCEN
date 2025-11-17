# Prompt para Generar Datos en mock-data.properties

Usa este prompt con ChatGPT o cualquier agente de IA para generar datos correctos:

---

## Prompt

Genera un archivo `mock-data.properties` con datos de prueba para un servicio mock de PDI (Plataforma de Interoperabilidad de Uruguay) que simula consultas de datos filiatorios por CI.

### Requisitos del Formato:

1. **Autenticación** (requerido):
   ```
   auth.organizacion=ORGANISMO_TEST
   auth.password=SECRETO_TEST
   ```

2. **Formato de Persona**:
   - Cada persona usa índice numérico secuencial: `person.1`, `person.2`, etc.
   - Campos requeridos: `ci`, `tipoDocumento`, `nombre1`, `apellido1`, `fechaNacimiento`, `sexo`, `nacionalidad`, `nombreEnCedula`
   - Campos opcionales: `nombre2`, `apellido2`, `apellidoAdoptivo1`, `apellidoAdoptivo2`

3. **Formato de CI**:
   - Solo dígitos, sin puntos ni guiones
   - Longitud: 7 u 8 dígitos
   - Ejemplo: `12345678`

4. **Formato de Fecha de Nacimiento** (CRÍTICO):
   - Formato exacto: `yyyy-MMdd` (9 caracteres total)
   - Donde: `yyyy` = año 4 dígitos, `-` = guion literal, `MM` = mes 2 dígitos (01-12), `dd` = día 2 dígitos (01-31)
   - Ejemplos correctos: `1980-0503` (3 mayo 1980), `1995-1225` (25 dic 1995), `2010-0315` (15 mar 2010)
   - IMPORTANTE: El guion es obligatorio, mes y día deben tener 2 dígitos con padding cero

5. **Valores de Sexo**:
   - `1` = Masculino
   - `2` = Femenino

6. **Valores de Nacionalidad**:
   - `1` = Oriental (uruguayo)
   - `3` = Extranjero
   - `0` = Desconocida

7. **Nombres**:
   - Usar MAYÚSCULAS (como en documentos oficiales uruguayos)
   - Nombres comunes uruguayos: JUAN, MARIA, CARLOS, ANA, PEDRO, etc.
   - Apellidos comunes: PEREZ, GONZALEZ, RODRIGUEZ, LOPEZ, SILVA, etc.

8. **Códigos de Error** (para CIs con errores):
   - `500` = Persona inexistente
   - `701` = Datos a regularizar
   - `1003` = CI anulada
   - `10002` = Acceso no autorizado

### Casos de Prueba Requeridos:

1. **Persona mayor de edad** (≥18 años):
   - Usar fecha de nacimiento anterior a (año actual - 18)
   - Ejemplo: si año actual es 2025, usar fecha antes de 2007

2. **Persona menor de edad** (<18 años):
   - Usar fecha de nacimiento entre (año actual - 17) y (año actual - 1)
   - Ejemplo: si año actual es 2025, usar fecha entre 2008 y 2024

3. **Persona exactamente 18 años**:
   - Usar fecha de nacimiento = año actual - 18
   - Ejemplo: si año actual es 2025, usar año 2007

4. **CI anulada**:
   - Solo incluir: `person.X.ci` y `person.X.error=1003`

5. **CI inexistente**:
   - No incluir entrada (el servicio retornará error 500 automáticamente)

### Reglas de Consistencia:

- `nombreEnCedula` debe ser: `nombre1 nombre2 apellido1 apellido2` (sin espacios extra)
- `sexo` debe ser consistente con el nombre (1 para masculinos, 2 para femeninos)
- `tipoDocumento` debe ser "CI" o "DO" (default: "CI")

### Ejemplo de Salida Esperada:

```properties
# Authentication
auth.organizacion=ORGANISMO_TEST
auth.password=SECRETO_TEST

# Person 1: Adult (born 1980, ~45 years old in 2025)
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

# Person 2: Minor (born 2010, ~15 years old in 2025)
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

# Person 3: Cancelled CI
person.3.ci=11111111
person.3.error=1003
```

### Errores Comunes a Evitar:

- ❌ `fechaNacimiento=1980-5-3` → Debe ser `1980-0503` (con padding)
- ❌ `fechaNacimiento=05-03-1980` → Formato incorrecto, debe ser `yyyy-MMdd`
- ❌ `ci=12345678-9` → No incluir dígito verificador
- ❌ `nombreEnCedula` no coincide con nombres/apellidos
- ❌ `sexo=1` con nombre femenino

Genera al menos 5 personas de prueba incluyendo: 2 mayores de edad, 1 menor de edad, 1 exactamente 18 años, y 1 CI anulada. Usa el año actual 2025 para calcular las fechas.

