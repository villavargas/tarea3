
package com.amx.telcel.di.sds.esb.sap.inventario;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConsultarExistenciasType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConsultarExistenciasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="materialAlmacen" type="{http://www.amx.com/telcel/di/sds/esb/sap/inventario}MaterialAlmacenType" maxOccurs="2000"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultarExistenciasType", propOrder = {
    "materialAlmacen"
})
public class ConsultarExistenciasType {

    @XmlElement(required = true)
    protected List<MaterialAlmacenType> materialAlmacen;

    /**
     * Gets the value of the materialAlmacen property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the materialAlmacen property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMaterialAlmacen().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MaterialAlmacenType }
     * 
     * 
     */
    public List<MaterialAlmacenType> getMaterialAlmacen() {
        if (materialAlmacen == null) {
            materialAlmacen = new ArrayList<MaterialAlmacenType>();
        }
        return this.materialAlmacen;
    }

}
