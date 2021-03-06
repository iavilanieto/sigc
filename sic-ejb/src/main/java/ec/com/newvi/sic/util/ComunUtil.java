/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.newvi.sic.util;

import ec.com.newvi.sic.dto.DominioDto;
import ec.com.newvi.sic.dto.FichaCatastralDto;
import ec.com.newvi.sic.enums.EnumMeses;
import ec.com.newvi.sic.enums.EnumNewviExcepciones;
import ec.com.newvi.sic.enums.EnumTipoIdentificacion;
import ec.com.newvi.sic.modelo.Contribuyentes;
import ec.com.newvi.sic.modelo.Predios;
import ec.com.newvi.sic.modelo.Propiedad;
import ec.com.newvi.sic.servicios.ParametrosServicio;
import ec.com.newvi.sic.util.excepciones.NewviExcepcion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author israelavila
 */
public class ComunUtil {

    /**
     * Comprueba si un objeto es nulo
     *
     * @param objeto
     * @return
     */
    public static Boolean esNulo(Object objeto) {
        return objeto == null;
    }

    /**
     * Comprueba si una cadena está vacía
     *
     * @param cadena
     * @return
     */
    public static Boolean esCadenaVacia(String cadena) {
        return esNulo(cadena) || cadena.isEmpty();
    }

    public static Boolean esNumeroPositivo(Integer numero) {
        return !esNulo(numero) && (numero.compareTo(0) >= 0);
    }

    public static BigDecimal obtenerNumeroDecimalDesdeTexto(String texto, String formatoMonedaSistema) throws NewviExcepcion {
        if (!ComunUtil.esNulo(texto) && !ComunUtil.esCadenaVacia(texto)) {
            if (texto.contains("$")) {
                return generarValorFormatoMoneda(texto, formatoMonedaSistema);
            } else {
                return new BigDecimal(texto);
            }
        } else {
            throw new NewviExcepcion(EnumNewviExcepciones.ERR012);
        }
    }

    public static String generarFormatoMoneda(BigDecimal valor, String formato) throws NewviExcepcion {
        DecimalFormat formatoMoneda = new DecimalFormat(formato);
        try {
            return formatoMoneda.format(valor.setScale(2, RoundingMode.HALF_UP));
        } catch (IllegalArgumentException ex) {
            throw new NewviExcepcion(EnumNewviExcepciones.ERR208, ex);
        }
    }

    public static BigDecimal generarValorFormatoMoneda(String texto, String formato) throws NewviExcepcion {
        DecimalFormat formatoMoneda = new DecimalFormat(formato);
        formatoMoneda.setParseBigDecimal(true);
        try {
            return new BigDecimal(formatoMoneda.parse(texto).toString());
        } catch (ParseException ex) {
            throw new NewviExcepcion(EnumNewviExcepciones.ERR013);
        }

    }

    public static String reemplazarTokens(String text,
            Map<String, String> replacements) {
        Pattern pattern = Pattern.compile("\\[(.+?)\\]");
        Matcher matcher = pattern.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String replacement = replacements.get(matcher.group(1));
            if (replacement != null) {
                // matcher.appendReplacement(buffer, replacement);
                // see comment 
                matcher.appendReplacement(buffer, "");
                buffer.append(replacement);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static List<DominioDto> obtenerSubNodosHijos(ParametrosServicio parametrosServicio) {
        return parametrosServicio.listarDominiosDto("TENENCIA", "Hijo");
    }

    public static String obtenerCodigoTenencia(List<DominioDto> lista, String descripcion, String codigo) {
        for (DominioDto dominioDto : lista) {
            if ((dominioDto.getDominio().getDomiPadre()).trim().equals(codigo.trim()) && (dominioDto.getDescripcion()).trim().equals(descripcion.trim())) {
                return dominioDto.getDominio().getDomiCodigo().trim();
            }
        }
        return null;
    }

    public static String obtenerDescuento(Contribuyentes contribuyente) {
        if (!esCadenaVacia(contribuyente.getStsEspeciales()) && ("Predios Municipales").equals((contribuyente.getStsEspeciales().trim()))) {
            return contribuyente.getStsEspeciales().trim();
        }
        return "Ninguno";
    }

    public static String generarScriptTenencia(List<Predios> listaPredios, ParametrosServicio parametrosServicio) {
        List<DominioDto> dominios = obtenerSubNodosHijos(parametrosServicio);
        String sql = "";
        Integer cont = 0;
        for (Predios predioNuevo : listaPredios) {
            FichaCatastralDto a = new FichaCatastralDto(predioNuevo);
            Propiedad propiedad = a.getPropiedad();
            String transDomi = propiedad.getStsTransferenciadominio().getStsTransferenciadominio();
            String sitAct = propiedad.getStsSituacion().getStsSituacion();
            String tenencia = propiedad.getStsTenencia().getStsTenencia();
            String escritura = propiedad.getStsEscritura().getStsEscritura();
            String descuento = obtenerDescuento(a.getContribuyentePropiedad());
            String urbanoMarginal = "NO";
            sql += "\ninsert into cat_ciu_tenencia VALUES (" + (++cont) + "," + propiedad.getCodPropiedad() + ",'" + obtenerCodigoTenencia(dominios, transDomi, "1201") + "','TENENCIA','TRANSFERENCIA DOMINIO','" + transDomi + "', 'A', NULL, NULL, NULL, NULL, NULL, NULL);"
                    + "\ninsert into cat_ciu_tenencia VALUES (" + (++cont) + "," + propiedad.getCodPropiedad() + ",'" + obtenerCodigoTenencia(dominios, sitAct, "1202") + "','TENENCIA','SITUACION ACTUAL','" + sitAct + "', 'A', NULL, NULL, NULL, NULL, NULL, NULL);"
                    + "\ninsert into cat_ciu_tenencia VALUES (" + (++cont) + "," + propiedad.getCodPropiedad() + ",'" + obtenerCodigoTenencia(dominios, tenencia, "1203") + "','TENENCIA','TENENCIA DOMINIO','" + tenencia + "', 'A', NULL, NULL, NULL, NULL, NULL, NULL);"
                    + "\ninsert into cat_ciu_tenencia VALUES (" + (++cont) + "," + propiedad.getCodPropiedad() + ",'" + obtenerCodigoTenencia(dominios, escritura, "1204") + "','TENENCIA','ESCRITURA','" + escritura + "', 'A', NULL, NULL, NULL, NULL, NULL, NULL);"
                    + "\ninsert into cat_ciu_tenencia VALUES (" + (++cont) + "," + propiedad.getCodPropiedad() + ",'" + obtenerCodigoTenencia(dominios, descuento, "1205") + "','TENENCIA','Descuentos Especiales','" + descuento + "', 'A', NULL, NULL, NULL, NULL, NULL, NULL);"
                    + "\ninsert into cat_ciu_tenencia VALUES (" + (++cont) + "," + propiedad.getCodPropiedad() + ",'" + obtenerCodigoTenencia(dominios, urbanoMarginal, "1206") + "','TENENCIA','URBANO MARGINAL','" + urbanoMarginal + "', 'A', NULL, NULL, NULL, NULL, NULL, NULL);";
        }

        return sql;
    }

    public static Date hoy() {
        Date fechaIngreso = Calendar.getInstance().getTime();
        return fechaIngreso;
    }

    public static Integer obtenerAnioDesdeFecha(Date fecha) {
        Calendar calendar = Calendar.getInstance();
        if (!ComunUtil.esNulo(fecha)) {
            calendar.setTime(fecha);
        }
        return calendar.get(Calendar.YEAR);
    }

    public static String obtenerMesDesdeFecha(Date fecha) {
        Calendar calendar = Calendar.getInstance();
        if (!ComunUtil.esNulo(fecha)) {
            calendar.setTime(fecha);
        }
        Integer dia = calendar.get(Calendar.DAY_OF_YEAR);
        Integer dia2 = calendar.get(Calendar.DAY_OF_MONTH);
        Integer mes = calendar.get(Calendar.MONTH) + 1;

        return EnumMeses.obtenerDescripcionMes(calendar.get(Calendar.MONTH) + 1);
    }

    public static Integer obtenerDiaDesdeFecha(Date fecha) {
        Calendar calendar = Calendar.getInstance();
        if (!ComunUtil.esNulo(fecha)) {
            calendar.setTime(fecha);
        }
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static Boolean esPar(Integer numero) {
        return numero % 2 == 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    private static List<Integer> iniciarCoeficientes() {
        List<Integer> coeficientes = new ArrayList<>();
        Integer contador = 0;
        while (contador <= 8) {
            if (esPar(contador)) {
                coeficientes.add(2);
            } else {
                coeficientes.add(1);
            }
            contador++;
        }
        return coeficientes;
    }

    public static Boolean esCedulaValida(String cedula) {
        Integer total;
        Integer longituCedula;
        Integer numProvincias;
        Integer tercerDigito;
        Integer provincia;
        Integer digitoTres;

        Integer digitoVerificadorObtenido;
        Integer digitoVerificadorRecibido;

        longituCedula = 10;
        if (cedula.matches("[0-9]*") && cedula.length() == longituCedula) {
            numProvincias = 24;
            tercerDigito = 6;
            provincia = Integer.parseInt(cedula.charAt(0) + "" + cedula.charAt(1));
            digitoTres = Integer.parseInt(cedula.charAt(2) + "");
            if ((provincia > 0 && provincia <= numProvincias) && digitoTres < tercerDigito) {
                digitoVerificadorRecibido = Integer.parseInt(cedula.charAt(9) + "");
                total = obtenerTotalCoeficienteCedula(cedula);
                digitoVerificadorObtenido = obtenerDigitoVerificadorCalculado(total);

                if (digitoVerificadorObtenido.equals(digitoVerificadorRecibido)) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }

    private static Integer obtenerTotalCoeficienteCedula(String cedula) {
        List<Integer> coeficientes = iniciarCoeficientes();
        Integer valor;
        Integer total = 0;
        Integer contador = 0;
        for (Integer coeficiente : coeficientes) {
            valor = coeficiente * Integer.parseInt(cedula.charAt(contador++) + "");
            total = valor >= 10 ? total + (valor - 9) : total + valor;
        }
        return total;
    }

    private static Integer obtenerDigitoVerificadorCalculado(Integer total) {
        return total > +10
                ? (total % 10) != 0
                        ? 10 - (total % 10)
                        : (total % 10) : total;
    }
    /*public static Boolean esRucNaturalValido(String ruc) {
        if (validarInicial(ruc, 13)
                && validarCodigoProvincia(ruc.substring(0, 2))
                && validarTercerDigito(String.valueOf(ruc.charAt(2)), EnumTipoIdentificacion.CEDULA_RUC_NATURAL)
                && validarCodigoEstablecimiento(ruc.substring(10, 13))
                && algoritmoModulo10(ruc, Integer.parseInt(String.valueOf(ruc.charAt(9))))) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public static Boolean esRucPrivadoValido(String ruc) {
        if (validarInicial(ruc, 13)
                && validarCodigoProvincia(ruc.substring(0, 2))
                && validarTercerDigito(String.valueOf(ruc.charAt(2)), EnumTipoIdentificacion.RUC_SOCIEDAD_PRIVADA)
                && validarCodigoEstablecimiento(ruc.substring(10, 13))
                && algoritmoModulo10(ruc, Integer.parseInt(String.valueOf(ruc.charAt(9)))) && algoritmoModulo11(ruc.substring(0, 9), Integer.parseInt(String.valueOf(ruc.charAt(9))), EnumTipoIdentificacion.RUC_SOCIEDAD_PRIVADA)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public static Boolean esCedulaValida(String cedula) {

        if (validarInicial(cedula, 10)
                && validarCodigoProvincia(cedula.substring(0, 2))
                && validarTercerDigito(String.valueOf(cedula.charAt(2)), EnumTipoIdentificacion.CEDULA_RUC_NATURAL)
                && algoritmoModulo10(cedula, Integer.parseInt(String.valueOf(cedula.charAt(9))))) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    private static boolean validarInicial(String cedula, int caracteres) {

        if (!ComunUtil.esNulo(cedula) && cedula.matches("[0-9]*") && cedula.length() == caracteres) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    private static Boolean validarCodigoProvincia(String cedula) {
        if (Integer.parseInt(cedula) < 0 || Integer.parseInt(cedula) > 24) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    private static boolean validarTercerDigito(String numero, EnumTipoIdentificacion tipo) {
        Boolean retorno = Boolean.FALSE;

        switch (tipo) {
            case CEDULA_RUC_NATURAL:

                if (Integer.parseInt(numero) > 0 || Integer.parseInt(numero) <= 5) {
                    retorno = Boolean.TRUE;
                }
                break;
            case RUC_SOCIEDAD_PRIVADA:
                if (Integer.parseInt(numero) == 9) {
                    retorno = Boolean.TRUE;
                }
                break;

            case RUC_SOCIEDAD_PUBLICA:
                if (Integer.parseInt(numero) == 6) {
                    retorno = Boolean.TRUE;
                }
                break;
            default:
                retorno = Boolean.FALSE;
        }

        return retorno;
    }

    private static Integer[] iniciarDigitosTemporales(String digitosIniciales) {
        Integer indice = 0;
        Integer[] digitosInicialesTMP = new Integer[digitosIniciales.length()];
        for (char valorPosicion : digitosIniciales.toCharArray()) {
            digitosInicialesTMP[indice] = Integer.parseInt(String.valueOf(valorPosicion));
            indice++;
        }
        return digitosInicialesTMP;
    }

    private static Integer obtenerTotalProducto(Integer[] arrayCoeficientes, Integer[] digitosInicialesTMP) {

        Integer key = 0;
        Integer total = 0;

        for (Integer valorPosicion : digitosInicialesTMP) {
            if (key < arrayCoeficientes.length) {
                valorPosicion = (digitosInicialesTMP[key] * arrayCoeficientes[key]);

                if (valorPosicion >= 10) {
                    char[] valorPosicionSplit = String.valueOf(valorPosicion).toCharArray();
                    valorPosicion = (Integer.parseInt(String.valueOf(valorPosicionSplit[0]))) + (Integer.parseInt(String.valueOf(valorPosicionSplit[1])));

                }
                total = total + valorPosicion;
            }

            key++;
        }

        return total;
    }

    private static Boolean esIgualDigitoVerificador(Integer total, Integer digitoVerificador) {

        Integer residuo;
        Integer resultado;

        residuo = total % 10;

        resultado = residuo == 0 ? 0 : 10 - residuo;

        if (resultado.equals(digitoVerificador)) {
            return Boolean.TRUE;
        } else {
            return Boolean.TRUE;
        }
    }

    private static Boolean algoritmoModulo10(String digitosIniciales, Integer digitoVerificador) {
        Integer[] arrayCoeficientes = new Integer[]{2, 1, 2, 1, 2, 1, 2, 1, 2};
        Integer[] digitosInicialesTMP;
        digitosInicialesTMP = iniciarDigitosTemporales(digitosIniciales);
        return esIgualDigitoVerificador(obtenerTotalProducto(arrayCoeficientes, digitosInicialesTMP), digitoVerificador);
    }

    private static Boolean validarCodigoEstablecimiento(String numero) {
        if (Integer.parseInt(numero) > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }*/

}
