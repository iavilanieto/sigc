/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.newvi.sic.servicios;

import ec.com.newvi.sic.dto.DominioDto;
import ec.com.newvi.sic.dto.SesionDto;
import ec.com.newvi.sic.modelo.Dominios;
import ec.com.newvi.sic.util.excepciones.NewviExcepcion;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.Local;

/**
 *
 * @author NEWVI
 */
@Local
@PermitAll
public interface ParametrosServicio {
    
    /**
     * Generar un nuevo dominio, de acuerdo a un objeto entregado.
     * @param nuevoDominio el nuevo dominio a ser ingresado.
     * @param sesion Sesion que realiza la operación
     * @return Codigo del nuevo dominio.
     * @throws NewviExcepcion 
     */
    public String generaNuevoDominio(Dominios nuevoDominio, SesionDto sesion) throws NewviExcepcion;
    
    /**
     * Actualiza un dominio existente.
     * @param dominio el dominio a actualizar
     * @param sesion Sesion que realiza la operación
     * @return Codigo del dominio actualizado.
     * @throws NewviExcepcion 
     */
    public String actualizarDominio (Dominios dominio, SesionDto sesion) throws NewviExcepcion;
    
    /**
     * Devuelve un dominio dado un id
     * @param idDominio Integer, codigo del dominio a obtener.
     * @return dominio
     * @throws NewviExcepcion 
     */
    public Dominios seleccionarDominio (Integer idDominio) throws NewviExcepcion;
    
    /**
     * Devuelve un listado de dominios
     * @return Listado de dominios
     */
    public List<Dominios> consultarDominios();
    
    /**
     * Elimina el dominio dado.
     * @param dominio El dominio a ser eliminado
     * @param sesion Sesion que realiza la operación
     * @return Codigo del dominio eliminado
     * @throws NewviExcepcion 
     */
    public String eliminarDominio (Dominios dominio, SesionDto sesion) throws NewviExcepcion;
    
    /**
     * Devuelve un listados de dominios filtrado por grupos
     * @return Listado de dominois filtrado por grupos
     */
    public List<Dominios> consultarGruposDominios();
    /**
     * Devuelve un listados de dominios dado un grupo
     * @param grupo grupo enviado
     * @param relacion relacion enviada
     * @return Listado de dominios filtrado por grupo
     */
    public List<Dominios> consultarDominiosPorGrupo(String grupo, String relacion);

    /**
     * Devuelve el listado de hijos por padre
     * @param dominio dominio donde se extrara los hijos
     * @return Listado de hijos
     */
    public List<Dominios> consultarHijos(Dominios dominio);
    
    /**
     * Devuelve un listado de dominios dto referente a un grupo
     * @param grupo grupo al que pertenece los dominios
     * @param relacion relacion al que pertenece los dominios
     * @return listado de dominios dto
     */
    public List<DominioDto> listarDominiosDto(String grupo, String relacion);
    
    
    
    
}
