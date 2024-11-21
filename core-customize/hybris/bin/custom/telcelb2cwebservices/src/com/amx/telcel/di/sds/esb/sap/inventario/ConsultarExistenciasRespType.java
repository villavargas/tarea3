
package com.amx.telcel.di.sds.esb.sap.inventario;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConsultarExistenciasRespType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConsultarExistenciasRespType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="existencia" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}ExistenciaType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="resultado" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}ResultadoType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultarExistenciasRespType", propOrder = {
    "existencia",
    "resultado"
})
public class ConsultarExistenciasRespType {

    protected List<ExistenciaType> existencia;
    protected List<ResultadoType> resultado;

    /**
     * Gets the value of the existencia property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the existencia property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExistencia().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExistenciaType }
     * 
     * 
     */
    public List<ExistenciaType> getExistencia() {
        if (existencia == null) {
            existencia = new ArrayList<ExistenciaType>();
        }
        return this.existencia;
    }

    /**
     * Gets the value of the resultado property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resultado property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResultado().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResultadoType }
     * 
     * 
     */
    public List<ResultadoType> getResultado() {
        if (resultado == null) {
            resultado = new ArrayList<ResultadoType>();
        }
        return this.resultado;
    }

}
