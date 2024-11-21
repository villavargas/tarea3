
package com.amx.telcel.di.sds.esb.sap.facturacion;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import com.amx.mexico.telcel.esb.v1_0_2.DetailType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.amx.telcel.di.sds.esb.sap.facturacion package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _RealizaPedido_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/facturacion", "realizaPedido");
    private final static QName _RealizaPedidoResponse_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/facturacion", "realizaPedidoResponse");
    private final static QName _RealizaPedidoExcep_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/facturacion", "realizaPedidoExcep");
    private final static QName _BorraDocumentosRequest_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/facturacion", "borraDocumentosRequest");
    private final static QName _BorraDocumentosResponse_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/facturacion", "borraDocumentosResponse");
    private final static QName _BorraDocumentosExcep_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/facturacion", "borraDocumentosExcep");
    private final static QName _ActualizaPago_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/facturacion", "actualizaPago");
    private final static QName _BorraDocumentos_QNAME = new QName("http://www.amx.com/telcel/di/sds/esb/sap/facturacion", "borraDocumentos");
    private final static QName _SapTiCirculoAzulMANDT_QNAME = new QName("", "MANDT");
    private final static QName _SapTiCirculoAzulPEDIDO_QNAME = new QName("", "PEDIDO");
    private final static QName _SapTiCirculoAzulPOSNR_QNAME = new QName("", "POSNR");
    private final static QName _SapTiCirculoAzulFOLIOCA_QNAME = new QName("", "FOLIOCA");
    private final static QName _SapTiCirculoAzulREGION_QNAME = new QName("", "REGION");
    private final static QName _SapTiCirculoAzulNOMBRE_QNAME = new QName("", "NOMBRE");
    private final static QName _SapTiCirculoAzulTELEFONO_QNAME = new QName("", "TELEFONO");
    private final static QName _SapTiCirculoAzulCUENTA_QNAME = new QName("", "CUENTA");
    private final static QName _SapTiCirculoAzulPUNTOVENTA_QNAME = new QName("", "PUNTOVENTA");
    private final static QName _SapTiCirculoAzulTIPOREDENCION_QNAME = new QName("", "TIPOREDENCION");
    private final static QName _SapTiCirculoAzulFECHAFOLIO_QNAME = new QName("", "FECHAFOLIO");
    private final static QName _SapTiCirculoAzulHORAFOLIO_QNAME = new QName("", "HORAFOLIO");
    private final static QName _SapTiCirculoAzulPLANNUEVO_QNAME = new QName("", "PLANNUEVO");
    private final static QName _SapTiCirculoAzulADDENDUMNUEVO_QNAME = new QName("", "ADDENDUMNUEVO");
    private final static QName _SapTiCirculoAzulFECHASUGERIDA_QNAME = new QName("", "FECHASUGERIDA");
    private final static QName _SapTiCirculoAzulPROMOCION_QNAME = new QName("", "PROMOCION");
    private final static QName _SapTiCirculoAzulMARCA_QNAME = new QName("", "MARCA");
    private final static QName _SapTiCirculoAzulMODELO_QNAME = new QName("", "MODELO");
    private final static QName _SapTiCirculoAzulESNIMEIT_QNAME = new QName("", "ESNIMEIT");
    private final static QName _SapTiCirculoAzulESNIMEIP_QNAME = new QName("", "ESNIMEIP");
    private final static QName _SapTiCirculoAzulVALPUNTOS_QNAME = new QName("", "VALPUNTOS");
    private final static QName _SapTiCirculoAzulVALPESOS_QNAME = new QName("", "VALPESOS");
    private final static QName _SapTiCirculoAzulVALPESOSIVA_QNAME = new QName("", "VALPESOS_IVA");
    private final static QName _SapTiCirculoAzulNOMUSUARIO_QNAME = new QName("", "NOMUSUARIO");
    private final static QName _SapTiCirculoAzulUSUARIO_QNAME = new QName("", "USUARIO");
    private final static QName _SapTiCirculoAzulORIGEN_QNAME = new QName("", "ORIGEN");
    private final static QName _SapTiCirculoAzulESNDIGITO_QNAME = new QName("", "ESNDIGITO");
    private final static QName _SapTiCirculoAzulDESCROEXT_QNAME = new QName("", "DESCROEXT");
    private final static QName _SapTiCirculoAzulPRECIOROEXT_QNAME = new QName("", "PRECIOROEXT");
    private final static QName _SapTiCirculoAzulDESCRDESCUENTO_QNAME = new QName("", "DESCRDESCUENTO");
    private final static QName _SapTiCirculoAzulMATNR_QNAME = new QName("", "MATNR");
    private final static QName _SapTiItemPRCGROUP1_QNAME = new QName("", "PRC_GROUP1");
    private final static QName _SapTiItemPRCGROUP2_QNAME = new QName("", "PRC_GROUP2");
    private final static QName _SapTiItemZFOLIOCA_QNAME = new QName("", "ZFOLIOCA");
    private final static QName _SapTiItemPMNTTRMS_QNAME = new QName("", "PMNTTRMS");
    private final static QName _SapTiItemREQQTY_QNAME = new QName("", "REQ_QTY");
    private final static QName _SapTiKunnrPARTNROLE_QNAME = new QName("", "PARTN_ROLE");
    private final static QName _SapTiKunnrPARTNNUMB_QNAME = new QName("", "PARTN_NUMB");
    private final static QName _SapTiKunnrHOUSENO2_QNAME = new QName("", "HOUSE_NO2");
    private final static QName _SapSiHeaderSALESGRP_QNAME = new QName("", "SALES_GRP");
    private final static QName _SapSiHeaderPRICEGRP_QNAME = new QName("", "PRICE_GRP");
    private final static QName _SapSiHeaderREF1S_QNAME = new QName("", "REF_1_S");
    private final static QName _SapSiHeaderPOMETHS_QNAME = new QName("", "PO_METH_S");
    private final static QName _SapSiHeaderPOMETHOD_QNAME = new QName("", "PO_METHOD");
    private final static QName _SapSiHeaderREFDOCL_QNAME = new QName("", "REF_DOC_L");
    private final static QName _SapSiHeaderZLSCH01_QNAME = new QName("", "ZLSCH01");
    private final static QName _SapSiHeaderZZACCTYPE01_QNAME = new QName("", "ZZACC_TYPE01");
    private final static QName _SapSiHeaderZLSCH02_QNAME = new QName("", "ZLSCH02");
    private final static QName _SapSiHeaderZZACCTYPE02_QNAME = new QName("", "ZZACC_TYPE02");
    private final static QName _SapSiHeaderZLSCH03_QNAME = new QName("", "ZLSCH03");
    private final static QName _SapSiHeaderZZACCTYPE03_QNAME = new QName("", "ZZACC_TYPE03");
    private final static QName _SapSiHeaderZLSCH04_QNAME = new QName("", "ZLSCH04");
    private final static QName _SapSiHeaderZZACCTYPE04_QNAME = new QName("", "ZZACC_TYPE04");
    private final static QName _SapSiHeaderIPAPP_QNAME = new QName("", "IPAPP");
    private final static QName _SapSiHeaderPRINTER_QNAME = new QName("", "PRINTER");
    private final static QName _SapSiHeaderPURCHDATE_QNAME = new QName("", "PURCH_DATE");
    private final static QName _SapSiHeaderPRICELIST_QNAME = new QName("", "PRICE_LIST");
    private final static QName _SapToReturnTYPE_QNAME = new QName("", "TYPE");
    private final static QName _SapToReturnID_QNAME = new QName("", "ID");
    private final static QName _SapToReturnNUMBER_QNAME = new QName("", "NUMBER");
    private final static QName _SapToReturnMESSAGE_QNAME = new QName("", "MESSAGE");
    private final static QName _RealizaPedidoRespTypePONOSALESDOCUMENT_QNAME = new QName("", "PO_NO_SALESDOCUMENT");
    private final static QName _RealizaPedidoRespTypePONODELIVERY_QNAME = new QName("", "PO_NO_DELIVERY");
    private final static QName _RealizaPedidoRespTypePONOKUNNR_QNAME = new QName("", "PO_NO_KUNNR");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.amx.telcel.di.sds.esb.sap.facturacion
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RealizaPedidoOpPetType }
     * 
     */
    public RealizaPedidoOpPetType createRealizaPedidoOpPetType() {
        return new RealizaPedidoOpPetType();
    }

    /**
     * Create an instance of {@link RealizaPedidoOpRespType }
     * 
     */
    public RealizaPedidoOpRespType createRealizaPedidoOpRespType() {
        return new RealizaPedidoOpRespType();
    }

    /**
     * Create an instance of {@link BorraDocumentosOpPetType }
     * 
     */
    public BorraDocumentosOpPetType createBorraDocumentosOpPetType() {
        return new BorraDocumentosOpPetType();
    }

    /**
     * Create an instance of {@link BorraDocumentosOpRespType }
     * 
     */
    public BorraDocumentosOpRespType createBorraDocumentosOpRespType() {
        return new BorraDocumentosOpRespType();
    }

    /**
     * Create an instance of {@link PedidoType }
     * 
     */
    public PedidoType createPedidoType() {
        return new PedidoType();
    }

    /**
     * Create an instance of {@link RealizaPedidoPetType }
     * 
     */
    public RealizaPedidoPetType createRealizaPedidoPetType() {
        return new RealizaPedidoPetType();
    }

    /**
     * Create an instance of {@link RealizaPedidoRespType }
     * 
     */
    public RealizaPedidoRespType createRealizaPedidoRespType() {
        return new RealizaPedidoRespType();
    }

    /**
     * Create an instance of {@link BorraDocumentosRespType }
     * 
     */
    public BorraDocumentosRespType createBorraDocumentosRespType() {
        return new BorraDocumentosRespType();
    }

    /**
     * Create an instance of {@link SapToReturn }
     * 
     */
    public SapToReturn createSapToReturn() {
        return new SapToReturn();
    }

    /**
     * Create an instance of {@link SapSiHeader }
     * 
     */
    public SapSiHeader createSapSiHeader() {
        return new SapSiHeader();
    }

    /**
     * Create an instance of {@link SapTiKunnr }
     * 
     */
    public SapTiKunnr createSapTiKunnr() {
        return new SapTiKunnr();
    }

    /**
     * Create an instance of {@link SapTiItem }
     * 
     */
    public SapTiItem createSapTiItem() {
        return new SapTiItem();
    }

    /**
     * Create an instance of {@link SapTiCond }
     * 
     */
    public SapTiCond createSapTiCond() {
        return new SapTiCond();
    }

    /**
     * Create an instance of {@link SapTiText }
     * 
     */
    public SapTiText createSapTiText() {
        return new SapTiText();
    }

    /**
     * Create an instance of {@link SapTiCirculoAzul }
     * 
     */
    public SapTiCirculoAzul createSapTiCirculoAzul() {
        return new SapTiCirculoAzul();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RealizaPedidoOpPetType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RealizaPedidoOpPetType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion", name = "realizaPedido")
    public JAXBElement<RealizaPedidoOpPetType> createRealizaPedido(RealizaPedidoOpPetType value) {
        return new JAXBElement<RealizaPedidoOpPetType>(_RealizaPedido_QNAME, RealizaPedidoOpPetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RealizaPedidoOpRespType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RealizaPedidoOpRespType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion", name = "realizaPedidoResponse")
    public JAXBElement<RealizaPedidoOpRespType> createRealizaPedidoResponse(RealizaPedidoOpRespType value) {
        return new JAXBElement<RealizaPedidoOpRespType>(_RealizaPedidoResponse_QNAME, RealizaPedidoOpRespType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DetailType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DetailType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion", name = "realizaPedidoExcep")
    public JAXBElement<DetailType> createRealizaPedidoExcep(DetailType value) {
        return new JAXBElement<DetailType>(_RealizaPedidoExcep_QNAME, DetailType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BorraDocumentosOpPetType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BorraDocumentosOpPetType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion", name = "borraDocumentosRequest")
    public JAXBElement<BorraDocumentosOpPetType> createBorraDocumentosRequest(BorraDocumentosOpPetType value) {
        return new JAXBElement<BorraDocumentosOpPetType>(_BorraDocumentosRequest_QNAME, BorraDocumentosOpPetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BorraDocumentosOpRespType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BorraDocumentosOpRespType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion", name = "borraDocumentosResponse")
    public JAXBElement<BorraDocumentosOpRespType> createBorraDocumentosResponse(BorraDocumentosOpRespType value) {
        return new JAXBElement<BorraDocumentosOpRespType>(_BorraDocumentosResponse_QNAME, BorraDocumentosOpRespType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DetailType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DetailType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion", name = "borraDocumentosExcep")
    public JAXBElement<DetailType> createBorraDocumentosExcep(DetailType value) {
        return new JAXBElement<DetailType>(_BorraDocumentosExcep_QNAME, DetailType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PedidoType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PedidoType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion", name = "actualizaPago")
    public JAXBElement<PedidoType> createActualizaPago(PedidoType value) {
        return new JAXBElement<PedidoType>(_ActualizaPago_QNAME, PedidoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PedidoType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PedidoType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.amx.com/telcel/di/sds/esb/sap/facturacion", name = "borraDocumentos")
    public JAXBElement<PedidoType> createBorraDocumentos(PedidoType value) {
        return new JAXBElement<PedidoType>(_BorraDocumentos_QNAME, PedidoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "MANDT", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulMANDT(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulMANDT_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PEDIDO", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulPEDIDO(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulPEDIDO_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "POSNR", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulPOSNR(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulPOSNR_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "FOLIOCA", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulFOLIOCA(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulFOLIOCA_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "REGION", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulREGION(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulREGION_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "NOMBRE", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulNOMBRE(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulNOMBRE_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "TELEFONO", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulTELEFONO(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulTELEFONO_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "CUENTA", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulCUENTA(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulCUENTA_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PUNTOVENTA", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulPUNTOVENTA(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulPUNTOVENTA_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "TIPOREDENCION", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulTIPOREDENCION(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulTIPOREDENCION_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "FECHAFOLIO", scope = SapTiCirculoAzul.class)
    public JAXBElement<XMLGregorianCalendar> createSapTiCirculoAzulFECHAFOLIO(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_SapTiCirculoAzulFECHAFOLIO_QNAME, XMLGregorianCalendar.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "HORAFOLIO", scope = SapTiCirculoAzul.class)
    public JAXBElement<XMLGregorianCalendar> createSapTiCirculoAzulHORAFOLIO(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_SapTiCirculoAzulHORAFOLIO_QNAME, XMLGregorianCalendar.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PLANNUEVO", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulPLANNUEVO(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulPLANNUEVO_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ADDENDUMNUEVO", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulADDENDUMNUEVO(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulADDENDUMNUEVO_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "FECHASUGERIDA", scope = SapTiCirculoAzul.class)
    public JAXBElement<XMLGregorianCalendar> createSapTiCirculoAzulFECHASUGERIDA(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_SapTiCirculoAzulFECHASUGERIDA_QNAME, XMLGregorianCalendar.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PROMOCION", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulPROMOCION(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulPROMOCION_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "MARCA", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulMARCA(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulMARCA_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "MODELO", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulMODELO(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulMODELO_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ESNIMEIT", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulESNIMEIT(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulESNIMEIT_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ESNIMEIP", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulESNIMEIP(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulESNIMEIP_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "VALPUNTOS", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulVALPUNTOS(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulVALPUNTOS_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "VALPESOS", scope = SapTiCirculoAzul.class)
    public JAXBElement<BigDecimal> createSapTiCirculoAzulVALPESOS(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SapTiCirculoAzulVALPESOS_QNAME, BigDecimal.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "VALPESOS_IVA", scope = SapTiCirculoAzul.class)
    public JAXBElement<BigDecimal> createSapTiCirculoAzulVALPESOSIVA(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SapTiCirculoAzulVALPESOSIVA_QNAME, BigDecimal.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "NOMUSUARIO", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulNOMUSUARIO(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulNOMUSUARIO_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "USUARIO", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulUSUARIO(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulUSUARIO_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ORIGEN", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulORIGEN(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulORIGEN_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ESNDIGITO", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulESNDIGITO(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulESNDIGITO_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "DESCROEXT", scope = SapTiCirculoAzul.class)
    public JAXBElement<BigDecimal> createSapTiCirculoAzulDESCROEXT(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SapTiCirculoAzulDESCROEXT_QNAME, BigDecimal.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PRECIOROEXT", scope = SapTiCirculoAzul.class)
    public JAXBElement<BigDecimal> createSapTiCirculoAzulPRECIOROEXT(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SapTiCirculoAzulPRECIOROEXT_QNAME, BigDecimal.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "DESCRDESCUENTO", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulDESCRDESCUENTO(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulDESCRDESCUENTO_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "MATNR", scope = SapTiCirculoAzul.class)
    public JAXBElement<String> createSapTiCirculoAzulMATNR(String value) {
        return new JAXBElement<String>(_SapTiCirculoAzulMATNR_QNAME, String.class, SapTiCirculoAzul.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PRC_GROUP1", scope = SapTiItem.class)
    public JAXBElement<String> createSapTiItemPRCGROUP1(String value) {
        return new JAXBElement<String>(_SapTiItemPRCGROUP1_QNAME, String.class, SapTiItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PRC_GROUP2", scope = SapTiItem.class)
    public JAXBElement<String> createSapTiItemPRCGROUP2(String value) {
        return new JAXBElement<String>(_SapTiItemPRCGROUP2_QNAME, String.class, SapTiItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ZFOLIOCA", scope = SapTiItem.class)
    public JAXBElement<String> createSapTiItemZFOLIOCA(String value) {
        return new JAXBElement<String>(_SapTiItemZFOLIOCA_QNAME, String.class, SapTiItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PMNTTRMS", scope = SapTiItem.class)
    public JAXBElement<String> createSapTiItemPMNTTRMS(String value) {
        return new JAXBElement<String>(_SapTiItemPMNTTRMS_QNAME, String.class, SapTiItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "REQ_QTY", scope = SapTiItem.class)
    public JAXBElement<String> createSapTiItemREQQTY(String value) {
        return new JAXBElement<String>(_SapTiItemREQQTY_QNAME, String.class, SapTiItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PARTN_ROLE", scope = SapTiKunnr.class)
    public JAXBElement<String> createSapTiKunnrPARTNROLE(String value) {
        return new JAXBElement<String>(_SapTiKunnrPARTNROLE_QNAME, String.class, SapTiKunnr.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PARTN_NUMB", scope = SapTiKunnr.class)
    public JAXBElement<String> createSapTiKunnrPARTNNUMB(String value) {
        return new JAXBElement<String>(_SapTiKunnrPARTNNUMB_QNAME, String.class, SapTiKunnr.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "HOUSE_NO2", scope = SapTiKunnr.class)
    public JAXBElement<String> createSapTiKunnrHOUSENO2(String value) {
        return new JAXBElement<String>(_SapTiKunnrHOUSENO2_QNAME, String.class, SapTiKunnr.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "SALES_GRP", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderSALESGRP(String value) {
        return new JAXBElement<String>(_SapSiHeaderSALESGRP_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PRICE_GRP", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderPRICEGRP(String value) {
        return new JAXBElement<String>(_SapSiHeaderPRICEGRP_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PMNTTRMS", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderPMNTTRMS(String value) {
        return new JAXBElement<String>(_SapTiItemPMNTTRMS_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "REF_1_S", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderREF1S(String value) {
        return new JAXBElement<String>(_SapSiHeaderREF1S_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PO_METH_S", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderPOMETHS(String value) {
        return new JAXBElement<String>(_SapSiHeaderPOMETHS_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PO_METHOD", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderPOMETHOD(String value) {
        return new JAXBElement<String>(_SapSiHeaderPOMETHOD_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "REF_DOC_L", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderREFDOCL(String value) {
        return new JAXBElement<String>(_SapSiHeaderREFDOCL_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ZLSCH01", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderZLSCH01(String value) {
        return new JAXBElement<String>(_SapSiHeaderZLSCH01_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ZZACC_TYPE01", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderZZACCTYPE01(String value) {
        return new JAXBElement<String>(_SapSiHeaderZZACCTYPE01_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ZLSCH02", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderZLSCH02(String value) {
        return new JAXBElement<String>(_SapSiHeaderZLSCH02_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ZZACC_TYPE02", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderZZACCTYPE02(String value) {
        return new JAXBElement<String>(_SapSiHeaderZZACCTYPE02_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ZLSCH03", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderZLSCH03(String value) {
        return new JAXBElement<String>(_SapSiHeaderZLSCH03_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ZZACC_TYPE03", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderZZACCTYPE03(String value) {
        return new JAXBElement<String>(_SapSiHeaderZZACCTYPE03_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ZLSCH04", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderZLSCH04(String value) {
        return new JAXBElement<String>(_SapSiHeaderZLSCH04_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ZZACC_TYPE04", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderZZACCTYPE04(String value) {
        return new JAXBElement<String>(_SapSiHeaderZZACCTYPE04_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "IPAPP", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderIPAPP(String value) {
        return new JAXBElement<String>(_SapSiHeaderIPAPP_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PRINTER", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderPRINTER(String value) {
        return new JAXBElement<String>(_SapSiHeaderPRINTER_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PURCH_DATE", scope = SapSiHeader.class)
    public JAXBElement<XMLGregorianCalendar> createSapSiHeaderPURCHDATE(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_SapSiHeaderPURCHDATE_QNAME, XMLGregorianCalendar.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PRICE_LIST", scope = SapSiHeader.class)
    public JAXBElement<String> createSapSiHeaderPRICELIST(String value) {
        return new JAXBElement<String>(_SapSiHeaderPRICELIST_QNAME, String.class, SapSiHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "TYPE", scope = SapToReturn.class)
    public JAXBElement<String> createSapToReturnTYPE(String value) {
        return new JAXBElement<String>(_SapToReturnTYPE_QNAME, String.class, SapToReturn.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ID", scope = SapToReturn.class)
    public JAXBElement<String> createSapToReturnID(String value) {
        return new JAXBElement<String>(_SapToReturnID_QNAME, String.class, SapToReturn.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "NUMBER", scope = SapToReturn.class)
    public JAXBElement<String> createSapToReturnNUMBER(String value) {
        return new JAXBElement<String>(_SapToReturnNUMBER_QNAME, String.class, SapToReturn.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "MESSAGE", scope = SapToReturn.class)
    public JAXBElement<String> createSapToReturnMESSAGE(String value) {
        return new JAXBElement<String>(_SapToReturnMESSAGE_QNAME, String.class, SapToReturn.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PO_NO_SALESDOCUMENT", scope = RealizaPedidoRespType.class)
    public JAXBElement<String> createRealizaPedidoRespTypePONOSALESDOCUMENT(String value) {
        return new JAXBElement<String>(_RealizaPedidoRespTypePONOSALESDOCUMENT_QNAME, String.class, RealizaPedidoRespType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PO_NO_DELIVERY", scope = RealizaPedidoRespType.class)
    public JAXBElement<String> createRealizaPedidoRespTypePONODELIVERY(String value) {
        return new JAXBElement<String>(_RealizaPedidoRespTypePONODELIVERY_QNAME, String.class, RealizaPedidoRespType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "PO_NO_KUNNR", scope = RealizaPedidoRespType.class)
    public JAXBElement<String> createRealizaPedidoRespTypePONOKUNNR(String value) {
        return new JAXBElement<String>(_RealizaPedidoRespTypePONOKUNNR_QNAME, String.class, RealizaPedidoRespType.class, value);
    }

}
