/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.newvi.sic.dao;

import ec.com.newvi.sic.enums.EnumEstadoRegistro;
import ec.com.newvi.sic.modelo.Predios;
import java.io.Serializable;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Andrés
 */
@Stateless
@PermitAll
public class PrediosFacade extends AbstractFacade<Predios, Integer>  implements Serializable{

    public PrediosFacade() {
        super(Predios.class, Integer.class);
    }
    
    public List<Predios> buscarPredio(){
    // Busca un listado de Predios
        Query q = this.getEntityManager().createQuery("SELECT predio FROM Predios predio where predio.catEstado = :ESTADO");
        q.setParameter("ESTADO", EnumEstadoRegistro.A);
        //@return listado de Predios
        return q.getResultList();
    }
}