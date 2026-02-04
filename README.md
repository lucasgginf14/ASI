# 🏠 Bnbria - Sistema de Gestión de Reservas

## 📋 Introducción

Este README describe los detalles técnicos de la implementación del proyecto.  Para información sobre la funcionalidad principal, consulta la documentación del proyecto.

---

## 🚀 Cómo Ejecutar el Proyecto

### ⚠️ Consideraciones Importantes

Al ejecutar el proyecto se generarán automáticamente: 
- Imágenes de ejemplo
- Datos de prueba que ejemplifican el potencial real del sistema

> **Nota:** Algunos escenarios de prueba pueden no reflejar un flujo normal de uso (por ejemplo, reseñas asociadas a reservas no completadas).

### 🔧 Configuración de Rutas de Imágenes

Para que la carga de imágenes sea exitosa, debes configurar la ruta según tu entorno:

| Entorno | Ruta a usar |
|---------|-------------|
| 🪟 Windows | `C:\...` |
| 🐧 Linux | `/var/...` |
| 🐳 Docker | `/var/...` |

**Archivo de configuración:**
```yaml
📁 server/src/main/resources/application.yml
```

### 👥 Credenciales de Acceso

#### 🔐 Administradores
| Email | Contraseña |
|-------|------------|
| adrian.gantes@udc.es | adrian123 |
| lucas.garcia.garcia@udc.es | lucas123 |

#### 🏡 Anfitriones
| Email | Contraseña |
|-------|------------|
| anfitrion1@bnbria.es | anfitrion123 |
| anfitrion2@bnbria.es | anfitrion123 |

#### 🧳 Huéspedes
| Email | Contraseña |
|-------|------------|
| huesped1@bnbria.es | huesped123 |
| huesped2@bnbria.es | huesped123 |

---

## 🛠️ Detalles de Implementación

### 📅 Sistema de Reservas

#### Gestión Automática de Estados

El sistema verifica automáticamente el estado de las reservas y las actualiza según corresponda:

- ✅ **Completada** - La reserva ha finalizado exitosamente
- 🔄 **En progreso** - La reserva está actualmente activa
- ❌ **Cancelada por el sistema** - No fue aprobada por el anfitrión a tiempo

**Configuración actual:** La verificación se ejecuta cada **5 minutos** para facilitar las pruebas. Para dichas pruebas se recomienda modificar directamente las fechas en la base de datos si las reservas no empiezan o terminan en el día actual.

> 💡 **En producción:** Se recomienda configurar la ejecución una vez al día a las 12:00 del mediodía.

#### Generación Automática de Reseñas

Cuando una reserva pasa al estado "completada", se generan automáticamente: 
- 📝 Una reseña para el anfitrión
- 📝 Una reseña para el huésped

#### Gestión de Conflictos de Fechas

**Política de reservas:**
- Los usuarios pueden realizar múltiples reservas en rangos de fechas solapados
- Cuando un anfitrión acepta una reserva, todas las demás reservas que se solapen en esas fechas se **cancelan automáticamente**

**Cancelaciones automáticas:**
Las reservas no aceptadas o confirmadas se cancelan si: 
- 🔄 El anfitrión edita la información de la propiedad
- 🚫 Un administrador rechaza la propiedad

---

### 🏘️ Sistema de Propiedades

#### 🗺️ Geolocalización

El sistema utiliza la **Nominatim API** para obtener las coordenadas geográficas de las propiedades. 

**Limitaciones conocidas:**
- ⚠️ Algunas ubicaciones pueden no ser encontradas
- ⚠️ Ciertos lugares pueden ser geolocalizados incorrectamente

> **Nota técnica:** Se han detectado inconsistencias al introducir direcciones manualmente.  Aunque se proporcionen direcciones correctas, la API puede devolver coordenadas diferentes, incluso escribiendo la dirección exacta recibida al buscar las coordenadas desde la propia API.

### 🤖 Uso de IA

- Se ha usado Gemini para la generación de imágenes de prueba.
- Durante el desarrollo se ha utilizado GitHub Copilot, mayoritariamente en front. El resultado ha sido supervisado y corregido manualmente, con gran variedad de pruebas.
