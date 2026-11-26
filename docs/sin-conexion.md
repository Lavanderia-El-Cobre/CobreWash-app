# Modo sin conexión (Android)

La app puede seguir trabajando sin internet y enviar después lo que se hizo, conservando la hora exacta de cada acción.

## Qué se puede hacer sin conexión
| Rol | Acciones que quedan en cola |
|---|---|
| Operario / admin | Completar etapa, registrar incidencia, conteo de inventario |
| Repartidor / conductor | Entregar comanda, reportar problema de entrega, salida y regreso del vehículo (con fotos) |

El resto exige conexión: login, recuperar contraseña, cuentas, términos, privacidad, estimación de IA, crear, editar o anular comandas, reportes y asignarse comandas por QR. El QR de insumos sí funciona sin red, con el último catálogo guardado.

Para abrir la app sin red hay que haber marcado **"Recordarme"**. Sin esa opción, al reabrirla se pide la contraseña, y para eso hace falta conexión.

## Cómo funciona
- **Cola persistente** (`data/offline`): cada acción se guarda primero en el teléfono, en `filesDir/cola_acciones.json`, que se escribe completo y se reemplaza en cada cambio. Después se intenta enviar. Sobrevive al cierre de la app y a reinicios del teléfono.
- **Identificador por acción**: es un UUID generado en el teléfono (`idOperacion`). El backend lo registra en `OperacionMovil` dentro de la misma transacción; un reintento responde `OPERACION_YA_APLICADA` y la app lo da por enviado. Así no se duplica nada.
- **Hora original y hora de recepción**: el backend guarda `ocurridoEn` (la hora en el teléfono) y `recibidoEn` (`request.time`) por separado.
  - Si el teléfono no se reinició, la hora se calcula con el reloj monotónico contra la hora del servidor (cabecera `Date` de la web). Así, un reloj del teléfono mal puesto no la afecta.
  - Si el teléfono se reinició, se usa su reloj corregido por el desfase con el servidor. Si el desfase supera 2 minutos, se marca `relojDudoso`.
  - Se guarda también la zona horaria del teléfono.
  - El servidor rechaza horas con más de 72 h de antigüedad o más de 5 min en el futuro (`HORA_FUERA_DE_RANGO`).
- **Orden y dependencias**: las acciones de una misma comanda o salida se envían en orden. Si una queda rechazada, las siguientes de esa entidad esperan. Mientras un avance, una entrega o un regreso no se envía, no se acepta otro igual sobre la misma entidad.
- **Reintentos**: con espera creciente (15 s, 30 s… hasta 5 min).
  - WorkManager envía la cola en segundo plano cuando hay red, aunque la app esté cerrada.
  - La app también envía al recuperar la red, al volver a primer plano y desde la bandeja.
- **Rechazos**: si el servidor rechaza una acción de la cola, esta queda en **"Requiere atención"** con el motivo. Se puede reintentar o descartar desde la bandeja (la píldora sobre la barra inferior). Nunca se borra sola. Si el rechazo ocurre en el primer intento, con la persona en la pantalla, se muestra como error, igual que antes.
- **Sesión**:
  - Cada acción se envía solo con la misma cuenta que la hizo.
  - Si la sesión venció o la cuenta perdió acceso, la cola se pausa sin borrar nada.
  - Al reconectar, la app verifica la cuenta y cierra la sesión si quedó inactiva o cambió de rol.
- **Fotos**: primero se guarda el movimiento y después se suben las fotos, que tienen ids estables. Las fotos en cola no se borran con la limpieza de capturas.
- **Caché de lectura**: guarda la última lista de comandas del operario, los vehículos y entregas del repartidor y los insumos. Se usa solo sin conexión y se borra al cerrar sesión, porque contiene datos de clientes.

## Backend
Las mutaciones `…Movil` están en `CobreWash-web/dataconnect/example/movil.gql`. Las genera `pnpm dataconnect:movil` a partir de las originales y se prueban con `pnpm test:movil`.

**Hay que desplegar el esquema y los conectores antes de distribuir esta versión de la app**. Sin ellos, las acciones de la cola fallarían. Las versiones anteriores de la app no se ven afectadas.

## iOS
No está incluido: iOS aún no tiene conexión al backend. La lógica de la cola (`commonMain`) está lista para cuando la tenga.
