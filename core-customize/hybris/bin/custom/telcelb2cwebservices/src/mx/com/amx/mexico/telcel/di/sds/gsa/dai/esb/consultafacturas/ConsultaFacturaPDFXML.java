
package mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultafacturas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.amx.mexico.telcel.esb.v1_1.ControlDataRequestType;


/**
 * <p>
 * Java class for ConsultaFacturaPDFXML complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ConsultaFacturaPDFXML"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="controlData" type="{http://amx.com/mexico/telcel/esb/v1_1}ControlDataRequestType"/&gt;
 *         &lt;element name="consultaFacturaPDFXML" type="{http://www.amx.com.mx/mexico/telcel/di/sds/gsa/dai/esb/consultafacturas}ConsultaFacturaPDFXMLPetType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaFacturaPDFXML", propOrder =
{ "controlData", "consultaFacturaPDFXML" })
@XmlRootElement(name = "consultaFacturaPDFXML")
public class ConsultaFacturaPDFXML
{

	@XmlElement(required = true)
	protected ControlDataRequestType controlData;
	@XmlElement(required = true)
	protected ConsultaFacturaPDFXMLPetType consultaFacturaPDFXML;

	/**
	 * Gets the value of the controlData property.
	 * 
	 * @return possible object is {@link ControlDataRequestType }
	 * 
	 */
	public ControlDataRequestType getControlData()
	{
		return controlData;
	}

	/**
	 * Sets the value of the controlData property.
	 * 
	 * @param value
	 *           allowed object is {@link ControlDataRequestType }
	 * 
	 */
	public void setControlData(final ControlDataRequestType value)
	{
		this.controlData = value;
	}

	/**
	 * Gets the value of the consultaFacturaPDFXML property.
	 * 
	 * @return possible object is {@link ConsultaFacturaPDFXMLPetType }
	 * 
	 */
	public ConsultaFacturaPDFXMLPetType getConsultaFacturaPDFXML()
	{
		return consultaFacturaPDFXML;
	}

	/**
	 * Sets the value of the consultaFacturaPDFXML property.
	 * 
	 * @param value
	 *           allowed object is {@link ConsultaFacturaPDFXMLPetType }
	 * 
	 */
	public void setConsultaFacturaPDFXML(final ConsultaFacturaPDFXMLPetType value)
	{
		this.consultaFacturaPDFXML = value;
	}

}
