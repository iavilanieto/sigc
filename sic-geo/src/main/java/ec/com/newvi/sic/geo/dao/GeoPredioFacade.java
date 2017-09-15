/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.newvi.sic.geo.dao;

import ec.com.newvi.sic.dto.SesionDto;
import ec.com.newvi.sic.enums.EnumNewviExcepciones;
import ec.com.newvi.sic.geo.modelo.GeoPredio;
import ec.com.newvi.sic.util.excepciones.NewviExcepcion;
import ec.com.newvi.sic.util.logs.LoggerNewvi;
import java.io.Serializable;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

/**
 *
 * @author israelavila
 */
@Stateless
@PermitAll
public class GeoPredioFacade extends AbstractFacade<GeoPredio, Integer> implements Serializable {

    public GeoPredioFacade() {
        super(GeoPredio.class, Integer.class);
    }

    public String obtenerBordePredio(String codigoPredio, SesionDto sesion) throws NewviExcepcion {
        Query q = this.getEntityManager().createNativeQuery("SELECT ST_AsText(ST_Transform(ST_SetSRID(ST_Expand(ST_Extent(geom),5),32717),3857)) "
                + " FROM public.he002_lote as predio "
                + " WHERE predio.lot_codigo = :CODIGOPREDIO ");
        q.setParameter("CODIGOPREDIO", codigoPredio.trim());
        try {
            Object caja = q.getSingleResult();
            return (String) caja;
        } catch (NonUniqueResultException ex) {
            throw new NewviExcepcion(EnumNewviExcepciones.ERR203, ex);
        } catch (NoResultException ex) {
            LoggerNewvi.getLogNewvi(this.getClass()).error(EnumNewviExcepciones.ERR203.presentarMensajeCodigo().concat(" (").concat(ex.getMessage()).concat(")"), sesion);
            return null;
        } catch (Exception ex) {
            LoggerNewvi.getLogNewvi(this.getClass()).error(ex, sesion);
            throw new NewviExcepcion(EnumNewviExcepciones.ERR000, ex);
        }

    }

}