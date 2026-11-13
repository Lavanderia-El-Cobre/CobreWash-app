package com.elcobre.lavanderiaelcobre.data

/**
 * Dominio público de la web de Lavandería El Cobre: seguimiento de comandas (QR del
 * comprobante), aviso de privacidad y API de evidencias fotográficas de la flota.
 */
const val ORIGEN_WEB = "https://www.lavanderiaelcobrespa.com"

/**
 * Dominio anterior. Ya no se emite en ningún QR ni enlace; solo se acepta al escanear
 * comprobantes impresos antes del cambio, para que sigan abriendo su comanda.
 */
const val ORIGEN_WEB_ANTERIOR = "https://lavanderia-elcobre.vercel.app"
