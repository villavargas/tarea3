
package mx.com.telcel.notificacionfacturatelmextelcel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Posiciones complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Posiciones"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="posicion" type="{http://ibcomercio/telcel.com/commerce/services/SAP/NotificacionFacturaTelmex/}Posicion" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Posiciones", propOrder =
{ "posicion" })
public class Posiciones
{

	@XmlElement(required = true)
	protected List<Posicion> posicion;

	/**
	 * Gets the value of the posicion property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the posicion property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getPosicion().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Posicion }
	 * 
	 * 
	 */
	public List<Posicion> getPosicion()
	{
		if (posicion == null)
		{
			posicion = new ArrayList<Posicion>();
		}
		return this.posicion;
	}

}
