# Inventario del operario

Android consulta `GetInsumosOperario` y `GetInsumoPorQr` del conector `example`
con `SERVER_ONLY`. El catálogo contiene insumos activos de Firebase, no un enum
local de productos. La carga obtiene páginas de 50 y elimina duplicados por ID.

La pantalla permite buscar por nombre, consultar stock actual, mínimo y unidad,
y abrir el detalle al escanear o ingresar el `codigoQr` UUID del insumo. El
escáner general también distingue ese UUID del enlace oficial de una comanda.
Un QR desconocido, inactivo o mal formado no genera datos ni cambia existencias.

Los umbrales coinciden con `lib/inventario/movimientos.ts` de la web: mínimo
positivo y stock menor o igual a la mitad implica Crítico; menor o igual al
mínimo implica Bajo; los demás activos son OK. No se inventan reportes enviados.

El stock se vuelve a consultar cada 30 segundos mientras la app está visible,
al regresar al primer plano, al entrar a Insumos y al pulsar Actualizar. Ante
errores se conservan los datos de la última consulta exitosa. Cerrar o cambiar
sesión limpia el catálogo, selección y errores y descarta respuestas tardías.

Se mantienen los permisos de la web: solo administración registra entradas,
salidas o edita insumos, y la app no solicita la bitácora administrativa. El
operario solo envía conteos físicos con `RegistrarConteoInventario` (con la
`revisionStock` vigente del insumo); quedan pendientes hasta que administración
los aprueba o rechaza desde la intranet, y recién al aprobarse cambia el stock.
La app muestra el último conteo de cada insumo y su estado con
`GetMisConteosInventario`. El SDK de Kotlin se regenera desde el conector
`example` del repositorio web (`firebase dataconnect:sdk:generate`). El antiguo formulario de alertas mock se
retiró porque no persistía sus datos en Firebase.

Pruebas Android: `InventarioTest`. Pruebas reales del servidor, sin producción:

```
node scripts/prepare-production-test.mjs
firebase emulators:exec --only dataconnect --project demo-production --config scratch/production-test/firebase.json "node scripts/test-insumos-app.mjs"
```

El script está en el repositorio web. iOS informa explícitamente que Firebase
no está integrado todavía, sin mostrar inventario simulado.
