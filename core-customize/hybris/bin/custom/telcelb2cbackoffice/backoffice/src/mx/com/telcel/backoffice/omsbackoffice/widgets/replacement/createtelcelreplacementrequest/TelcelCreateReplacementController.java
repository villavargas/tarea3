/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.omsbackoffice.widgets.replacement.createtelcelreplacementrequest;

import com.amx.telcel.di.sds.esb.sap.cancelacion.LiberarRecursosResponse;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;
import com.hybris.cockpitng.util.notifications.NotificationService;
import de.hybris.platform.b2ctelcoservices.model.TmaSimpleProductOfferingModel;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultastock.ConsultarAlmacenCPResponse;
import mx.com.amx.mexico.telcel.di.sds.gsa.dai.esb.consultastock.MaterialAlmacenRespType;
import mx.com.telcel.backoffice.omsbackoffice.widgets.replacement.dtos.WarehouseSelectorDTO;
import mx.com.telcel.backoffice.omsbackoffice.widgets.replacement.renders.ProductModelComboboxRender;
import mx.com.telcel.backoffice.omsbackoffice.widgets.replacement.renders.WarehouseSelectorComboboxRender;
import mx.com.telcel.core.dtos.TelcelReplacementOrderDTO;
import mx.com.telcel.core.model.*;
import mx.com.telcel.core.models.liberarrecursos.InfoLiberarRecursos;
import mx.com.telcel.core.pojos.splitOrder.TelcelSplitOrderPojo;
import mx.com.telcel.core.service.ReplicateStockService;
import mx.com.telcel.core.service.TelcelWarehouseService;
import mx.com.telcel.core.services.TelcelOrderService;
import mx.com.telcel.core.services.TelcelReplacementOrderService;
import mx.com.telcel.core.services.mq.liberacionrecursos.LiberaRecursosMQService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The type Telcel create replacement controller.
 */
public class TelcelCreateReplacementController extends DefaultWidgetController {
    private static final Logger LOG = Logger.getLogger(TelcelCreateReplacementController.class.getName());
    private static final long serialVersionUID = 1L;
    private static final String TELCEL_PRODUCT_CATALOG = "telcelProductCatalog";
    private static final String ONLINE = "Online";
    /**
     * The constant COMPLETED.
     */
    protected static final Object COMPLETED = "completed";
    private static final String CANTIDAD = "10000";

    private ConsignmentModel consignment;
    //General
    @Wire
    private Textbox orderCode;
    @Wire
    private Textbox customer;
    @Wire
    private Textbox consignmentCode;
    @Wire
    private Textbox salesDocument;
    @Wire
    private Textbox address;
    @Wire
    private Textbox invoiceNumber;

    @Wire
    private Textbox seriesIMEI;

    @Wire
    private Textbox seriesICCID;

    @Wire
    private Textbox linea;

    //Product
    @Wire
    private Textbox productCode;

    private AbstractOrderModel parentOrder;
    private ProductModel parentProduct;

    @Wire
    private Textbox productName;

    @Wire
    private Textbox productMaxPrice;

    @Wire
    private Textbox bpWoTaxes;

    @Wire
    private Textbox basePricevalue;

    @Wire
    private Textbox currentPPWoTaxes;

    @Wire
    private Textbox value;

    @Wire
    private Combobox productSelector;

    @Wire
    private Combobox warehouseSelector;

    //ProductInf
    @Wire
    private Textbox selectorinfCode;

    @Wire
    private Textbox selectorinfName;

    @Wire
    private Doublebox bpWoTaxesinf;

    @Wire
    private Doublebox basePricevalueinf;

    @Wire
    private Doublebox currentPPWoTaxesinf;

    @Wire
    private Doublebox maxPriceinf;

    @Wire
    private Doublebox valueinf;

    @Wire
    private Doublebox addtitionalService;

    @Wire
    private Doublebox deliverycost;

    @Wire
    private Doublebox deliveryCostSummary;

    @Wire
    private Doublebox totalReplacement;

    @Wire
    private Checkbox runRollback;

    @WireVariable
    private TelcelReplacementOrderService telcelReplacementOrderService;

    @WireVariable
    private CatalogVersionService catalogVersionService;

    @WireVariable
    private transient NotificationService notificationService;

    @WireVariable
    private transient ModelService modelService;

    @WireVariable
    private TelcelOrderService telcelOrderService;

    @WireVariable
    private ReplicateStockService replicateStockService;

    @WireVariable
    private TelcelWarehouseService telcelWarehouseService;

    @WireVariable
    private Converter<AddressModel, AddressData> addressConverter;

    @WireVariable
    private UserService userService;

    @WireVariable
    private LiberaRecursosMQService liberaRecursosMQService;

    /**
     * Instantiates a new Telcel create replacement controller.
     */
    public TelcelCreateReplacementController() {
    }

    /**
     * Init create telcel replacement request form.
     *
     * @param inputOrder the input order
     */
    @SocketEvent(
            socketId = "inputObject"
    )
    public void initCreateTelcelReplacementRequestForm(ConsignmentModel inputOrder) {
        this.setConsignment(inputOrder);
        this.productSelector.setValue(null);
        this.productSelector.getItems().clear();
        this.warehouseSelector.setValue(null);
        this.warehouseSelector.getItems().clear();
        this.selectorinfCode.setValue(null);
        this.selectorinfName.setValue(null);
        this.bpWoTaxesinf.setValue(null);
        this.basePricevalueinf.setValue(null);
        this.currentPPWoTaxesinf.setValue(null);
        this.maxPriceinf.setValue(null);
        this.valueinf.setValue(null);
        this.addtitionalService.setValue(null);
        this.deliverycost.setValue(null);
        this.deliveryCostSummary.setValue(0D);
        this.totalReplacement.setValue(0D);

        final AbstractOrderModel orderModel = this.getConsignment().getOrder();
        final TelcelFacturaModel telcelFacturaModel = this.getConsignment().getTelcelFactura();
        final SeriesICCIDModel serieICCIDModel = this.getConsignment().getSeriesICCID();
        final ConsignmentEntryModel consignmentEntryModel = this.getConsignment().getConsignmentEntries().iterator().next();
        final AbstractOrderEntryModel entryModel = consignmentEntryModel.getOrderEntry();
        final ProductModel productModel = entryModel.getProduct();
        final AddressModel deliveryAddress = orderModel.getDeliveryAddress();
        this.getWidgetInstanceManager().setTitle(this.getWidgetInstanceManager().getLabel("telcelb2cbackoffice.createtelcelreplacementrequest.confirm.title")
                + " " + orderModel.getCode() + " " + this.getWidgetInstanceManager().getLabel("telcelb2cbackoffice.createtelcelreplacementrequest.confirm.cp")
                + " " + deliveryAddress.getPostalcode() + " " + this.getWidgetInstanceManager().getLabel("telcelb2cbackoffice.createtelcelreplacementrequest.confirm.region")
                + " " + orderModel.getRegionCode()
                + " " + orderModel.getCurrency().getIsocode());
        this.address.setValue(addressConverter.convert(deliveryAddress).getFormattedAddress());
        this.orderCode.setValue(orderModel.getCode());
        this.parentOrder = orderModel;
        this.customer.setValue(orderModel.getUser().getDisplayName());
        this.consignmentCode.setValue(this.getConsignment().getCode());
        this.salesDocument.setValue(this.getConsignment().getPoNoSalesDocument());
        this.invoiceNumber.setValue(telcelFacturaModel != null ? telcelFacturaModel.getNumeroFactura() : StringUtils.EMPTY);
        this.seriesIMEI.setValue(this.getConsignment().getSeriesImei() != null ? this.getConsignment().getSeriesImei().getImei() : StringUtils.EMPTY);
        this.seriesICCID.setValue(serieICCIDModel != null ? serieICCIDModel.getIccid() : StringUtils.EMPTY);
        this.linea.setValue(serieICCIDModel != null ? serieICCIDModel.getLinea() : StringUtils.EMPTY);
        this.parentProduct = productModel;
        this.productCode.setValue(productModel.getSku());
        String productName = productModel.getName();
        if (StringUtils.isEmpty(productName)) {
            productName = productModel.getName(new Locale("es", "MX"));
        }
        this.productName.setValue(productName);
        this.productMaxPrice.setValue(String.valueOf(entryModel.getMaxPrice()));
        this.bpWoTaxes.setValue(String.valueOf(entryModel.getBpWoTaxes()));
        this.basePricevalue.setValue(String.valueOf(entryModel.getBasePricevalue()));
        this.currentPPWoTaxes.setValue(String.valueOf(entryModel.getCurrentPPWoTaxes()));
        this.value.setValue(String.valueOf(entryModel.getBasePrice()));
        List<ProductModel> products = new ArrayList<ProductModel>();
        List<ProductModel> variantProducts = new ArrayList<ProductModel>();
        final TelcelPoVariantModel telcelPoVariantModel = (TelcelPoVariantModel) productModel;
        if (telcelPoVariantModel.getChipAdditionalServiceProduct() != null) {
            products.add(productModel);
        } else {
            //Se agrega el mismo producto en la posicion primera posicion
            variantProducts.add(productModel);
            boolean isActivable = productModel.getActivable() != null && productModel.getActivable();
            //Se agregan los productos variantes en la segunda posicion
            setVariantProductsToList(telcelPoVariantModel, variantProducts, isActivable);
            products.addAll(variantProducts);

            //Se agregan los productos en los cuales en precio y en region coincide con el producto a hacer el reemplazo
            final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion(TELCEL_PRODUCT_CATALOG, ONLINE);
            final List<PriceRowModel> priceRowModels = telcelReplacementOrderService.getTelcelSamePrice(orderModel.getRegionCode(), entryModel.getBasePrice(), catalogVersionModel);
            if (CollectionUtils.isNotEmpty(priceRowModels)) {
                setProductByPrice(priceRowModels, products, variantProducts, isActivable);
            }
        }
        //Si hay productos para mostrar se van a setear en esta parte
        if (CollectionUtils.isNotEmpty(products)) {
            ListModelList elements = new ListModelList(products);
            this.productSelector.setModel(elements);
            this.productSelector.setItemRenderer(new ProductModelComboboxRender());
        }

        this.addListeners();
    }

    private void setVariantProductsToList(TelcelPoVariantModel productModel, List<ProductModel> variantProducts, boolean isActivable) {
        final StorageModel storageModel = productModel.getStorage();
        final TmaSimpleProductOfferingModel tmaSimpleProductOfferingModel = productModel.getTmaBasePo();
        if (tmaSimpleProductOfferingModel != null && CollectionUtils.isNotEmpty(tmaSimpleProductOfferingModel.getTmaPoVariants())) {
            for (ProductModel model : tmaSimpleProductOfferingModel.getTmaPoVariants()) {
                if (!model.getCode().equals(productModel.getCode()) && CollectionUtils.isNotEmpty(model.getEurope1Prices())
                        && model.getApprovalStatus() != null && model.getApprovalStatus().equals(ArticleApprovalStatus.APPROVED)) {
                    if (isActivable == model.getActivable()) {
                        if (storageModel != null) {
                            final TelcelPoVariantModel telcelPoVariantModel = (TelcelPoVariantModel) model;
                            if (telcelPoVariantModel.getStorage() != null && telcelPoVariantModel.getStorage().equals(storageModel)) {
                                variantProducts.add(model);
                            }
                        } else {
                            variantProducts.add(model);
                        }
                    }
                }
            }
        }
    }

    private void setProductByPrice(List<PriceRowModel> priceRowModels, List<ProductModel> products, List<ProductModel> variantProducts, boolean isActivable) {
        for (PriceRowModel priceRowModel : priceRowModels) {
            final ProductModel replaceProductModel = priceRowModel.getProduct();
            if (isActivable == replaceProductModel.getActivable()) {
                if (replaceProductModel != null && replaceProductModel.getApprovalStatus() != null &&
                        replaceProductModel.getApprovalStatus().equals(ArticleApprovalStatus.APPROVED)) {
                    if (!variantProducts.contains(priceRowModel.getProduct())) {
                        products.add(priceRowModel.getProduct());
                    }
                }
            }
        }
    }

    /**
     * Add listeners.
     */
    protected void addListeners() {
        this.productSelector.addEventListener("onSelect", (event) -> {
            this.productSelector.invalidate();
            this.handleIndividualProductSelector(event);
        });
        this.deliverycost.addEventListener("onChanging", (event) -> {
            if (StringUtils.isNotEmpty(this.productSelector.getValue())) {
                this.setTotal();
            }
        });
        this.deliverycost.addEventListener("onFocus", (event) -> {
            if (StringUtils.isNotEmpty(this.productSelector.getValue())) {
                this.setTotal();
            }
        });
        this.deliverycost.addEventListener("onBlur", (event) -> {
            if (StringUtils.isNotEmpty(this.productSelector.getValue())) {
                this.setTotal();
            }
        });
    }

    private void handleIndividualProductSelector(Event event) {
        Object o = this.productSelector.getSelectedItem().getAttribute("product");
        if (o != null) {
            TelcelPoVariantModel productModel = (TelcelPoVariantModel) o;
            this.fillWarehouseSelector(productModel);
            this.selectorinfCode.setValue(productModel.getSku());
            String productName = productModel.getName();
            if (StringUtils.isEmpty(productName)) {
                productName = productModel.getName(new Locale("es", "MX"));
            }
            this.selectorinfName.setValue(productName);

            this.bpWoTaxesinf.setValue(Double.valueOf(this.bpWoTaxes.getValue()));
            this.basePricevalueinf.setValue(Double.valueOf(this.basePricevalue.getValue()));
            this.currentPPWoTaxesinf.setValue(Double.valueOf(this.currentPPWoTaxes.getValue()));
            this.valueinf.setValue(Double.valueOf(this.value.getValue()));
            this.maxPriceinf.setValue(Double.valueOf(this.productMaxPrice.getValue()));
            if (this.parentOrder.getDeliveryCost() > 0D) {
                this.deliverycost.setValue(this.parentOrder.getDeliveryCost());
            } else {
                this.deliverycost.setValue(0D);
            }

//            final ZoneDeliveryModeModel zoneDeliveryMode = productModel.getZoneDelivery();
//            if (zoneDeliveryMode != null && CollectionUtils.isNotEmpty(zoneDeliveryMode.getValues())) {
//                final ZoneDeliveryModeValueModel zoneDeliveryModeValueModel = zoneDeliveryMode.getValues().iterator().next();
//                this.deliverycost.setValue(zoneDeliveryModeValueModel.getValue());
//            } else {
//                this.deliverycost.setValue(0D);
//            }
        }

        final AdditionalServiceEntryModel additionalServiceEntryModel = this.getConsignment().getAdditionalServiceEntry();
        if (additionalServiceEntryModel != null) {
            this.addtitionalService.setValue(additionalServiceEntryModel.getBasePrice());
        } else {
            this.addtitionalService.setValue(0D);
        }

        this.setTotal();
    }

    private void fillWarehouseSelector(TelcelPoVariantModel telcelPoVariant) {
        this.warehouseSelector.setValue(null);
        this.warehouseSelector.getItems().clear();
        String postalCode = this.getConsignment().getOrder().getDeliveryAddress().getPostalcode();
        String requiereSim = "x";
        if (telcelPoVariant.getChipAdditionalServiceProduct() == null) {
            requiereSim = telcelPoVariant.getActivable() ? "1" : "x";
        }
        List<WarehouseSelectorDTO> warehouseSelectorDTOS = new ArrayList<>();
        try {
            ConsultarAlmacenCPResponse consultarAlmacenCPResponse = replicateStockService.getAlmacenForCp(postalCode, requiereSim, telcelPoVariant.getSku(), CANTIDAD, StringUtils.EMPTY);
            if (consultarAlmacenCPResponse != null && consultarAlmacenCPResponse.getConsultarAlmacenCPResponse() != null &&
                    consultarAlmacenCPResponse.getConsultarAlmacenCPResponse().getMaterialesResp() != null) {
                List<MaterialAlmacenRespType> materialAlmacenRespTypeList = consultarAlmacenCPResponse.getConsultarAlmacenCPResponse().getMaterialesResp();
                for (MaterialAlmacenRespType materialAlmacenRespType : materialAlmacenRespTypeList) {
                    if (materialAlmacenRespType != null && materialAlmacenRespType.getCobertura().compareTo(BigDecimal.ZERO) > 0) {
                        WarehouseSelectorDTO warehouseSelectorDTO = new WarehouseSelectorDTO();
                        warehouseSelectorDTO.setAlmacen(materialAlmacenRespType.getAlmacen());
                        warehouseSelectorDTO.setCentro(materialAlmacenRespType.getCentro());
                        if (StringUtils.isNotEmpty(materialAlmacenRespType.getNumMaterialSim())) {
                            warehouseSelectorDTO.setSim(materialAlmacenRespType.getNumMaterialSim());
                        }
                        warehouseSelectorDTOS.add(warehouseSelectorDTO);
                    }
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new WrongValueException(this.productSelector, this.getLabel("telcelb2cbackoffice.createtelcelreplacement.validation.invalid.errostock"));
        }
        if (CollectionUtils.isNotEmpty(warehouseSelectorDTOS)) {
            ListModelList elements = new ListModelList(warehouseSelectorDTOS);
            elements.addToSelection(elements.get(0));
            this.warehouseSelector.setModel(elements);
            this.warehouseSelector.setItemRenderer(new WarehouseSelectorComboboxRender());
        } else {
            throw new WrongValueException(this.productSelector, this.getLabel("telcelb2cbackoffice.createtelcelreplacement.validation.invalid.nostock"));
        }
    }

    private void setTotal() {
        Double addtitionalService = this.addtitionalService.getValue() != null ? this.addtitionalService.getValue() : 0.0D;
        Double deliverycost = this.deliverycost.getValue() != null ? this.deliverycost.getValue() : 0.0D;
        Double valueinf = this.valueinf.getValue() != null ? this.valueinf.getValue() : 0.0D;
        this.deliveryCostSummary.setValue(deliverycost);
        BigDecimal replacementAmount = BigDecimal.valueOf(addtitionalService + deliverycost + valueinf);
        this.totalReplacement.setValue(replacementAmount.setScale(2, RoundingMode.HALF_EVEN).doubleValue());
    }

    /**
     * Reset.
     */
    @ViewEvent(
            componentID = "resetcreatetelcelreplacement",
            eventName = "onClick"
    )
    public void reset() {
        this.initCreateTelcelReplacementRequestForm(this.getConsignment());
    }

    /**
     * Confirm creation.
     */
    @ViewEvent(
            componentID = "confirmcreatetelcelreplacement",
            eventName = "onClick"
    )
    public void confirmCreation() throws Exception {
        TelcelSplitOrderPojo telcelSplitOrderPojo = this.validateRequest();
        ConsignmentModel consignmentModel = this.getConsignment();

        if (this.runRollback.isChecked()) {
            try {
                runRollbackForConsignment(consignmentModel);
            } catch (Exception e) {
                this.getNotificationService().notifyUser("", "JustMessage", NotificationEvent.Level.FAILURE, new Object[]{this.getWidgetInstanceManager().getLabel("telcelb2cbackoffice.createtelcelreplacement.confirm.error.rollback")});
                this.sendOutput("confirm", COMPLETED);
                return;
            }
        }

        try {

            TelcelReplacementOrderDTO telcelReplacementOrderDTO = new TelcelReplacementOrderDTO();
            telcelReplacementOrderDTO.setItemValue(this.valueinf.getValue());
            telcelReplacementOrderDTO.setAdditionalService(this.addtitionalService.getValue());
            telcelReplacementOrderDTO.setDeliveryCost(this.deliveryCostSummary.getValue());
            telcelReplacementOrderDTO.setTotal(this.totalReplacement.getValue());
            telcelReplacementOrderDTO.setProductModel((TelcelPoVariantModel) this.productSelector.getSelectedItem().getAttribute("product"));
            telcelReplacementOrderDTO.setConsignmentModel(consignmentModel);
            telcelReplacementOrderDTO.setOrderModel((OrderModel) consignmentModel.getOrder());
            final UserModel currentUser = userService.getCurrentUser();
            telcelReplacementOrderDTO.setTelcelEmployee(currentUser);

            telcelReplacementOrderDTO.setBpWoTaxes(this.bpWoTaxesinf.getValue());
            telcelReplacementOrderDTO.setBasePricevalue(this.basePricevalueinf.getValue());
            telcelReplacementOrderDTO.setCurrentPPWoTaxes(this.currentPPWoTaxesinf.getValue());
            telcelReplacementOrderDTO.setMaxPrice(this.maxPriceinf.getValue());

            final AbstractOrderEntryModel orderEntry = consignmentModel.getConsignmentEntries().iterator().next().getOrderEntry();
            if (orderEntry.getEsquemaCobro() != null) {
                telcelReplacementOrderDTO.setEsquemaCobroModel(orderEntry.getEsquemaCobro());
            }
            String newOrderCode = this.getTelcelOrderService().createReplacementOrder(telcelReplacementOrderDTO, telcelSplitOrderPojo);
            consignmentModel.setWasReplacement(true);
            consignmentModel.setStatus(ConsignmentStatus.REPLACEMENT);
            getModelService().save(consignmentModel);

            this.getNotificationService().notifyUser("", "JustMessage", NotificationEvent.Level.SUCCESS, new Object[]{this.getWidgetInstanceManager().getLabel("telcelb2cbackoffice.createtelcelreplacement.confirm.success") + " - " + newOrderCode});
        } catch (Exception var4) {
            LOG.info(var4.getMessage(), var4);
            this.getNotificationService().notifyUser("", "JustMessage", NotificationEvent.Level.FAILURE, new Object[]{this.getWidgetInstanceManager().getLabel("telcelb2cbackoffice.createtelcelreplacement.confirm.error")});
        }
        this.sendOutput("confirm", COMPLETED);
    }

    private void runRollbackForConsignment(ConsignmentModel consignmentModel) throws Exception {
        final TelcelFacturaModel telcelFacturaModel = consignmentModel.getTelcelFactura();
        final boolean sendSAPSources = telcelFacturaModel == null || !StringUtils.isNotEmpty(telcelFacturaModel.getNumeroFactura());
        final ProductModel productModel = consignmentModel.getConsignmentEntries().iterator().next().getOrderEntry().getProduct();
        final boolean needActivation = productModel.getActivable();
        if (needActivation || sendSAPSources) {
            LOG.info("Calling rollback replacement.- consignment"+ consignmentModel.getCode() +", needActivation:" + needActivation + ", sendSAPSources:" + sendSAPSources);
            callRollback(consignmentModel.getOrder(), consignmentModel, sendSAPSources);
        } else {
            LOG.info(String.format("Rollback was not executed because it does not apply for consignment: %s", consignmentModel.getCode()));
        }
    }

    private void callRollback(final AbstractOrderModel orderModel, final ConsignmentModel consignmentModel, final boolean sendSAPSources)
            throws Exception {
        final InfoLiberarRecursos infoLiberarRecursos = new InfoLiberarRecursos();

        infoLiberarRecursos.setIdPedido(consignmentModel.getPoNoSalesDocument());
        infoLiberarRecursos.setIdEntrega(consignmentModel.getPoNoDelivery());
        infoLiberarRecursos.setbConsigment(sendSAPSources);
        infoLiberarRecursos.setRegion(orderModel.getRegionCode());

        if ((consignmentModel.getSeriesICCID() == null || StringUtils.isEmpty(consignmentModel.getSeriesICCID().getLinea()))
                && !sendSAPSources) {
            LOG.info("Rollback for RMA is not running.....................");
            return;
        }

        final SeriesICCIDModel serieICCIDModel = consignmentModel.getSeriesICCID();

        if (serieICCIDModel != null && StringUtils.isNotEmpty(serieICCIDModel.getLinea())) {
            final SeriesImeiModel serieImeiModel = consignmentModel.getSeriesImei();
            if (serieImeiModel != null) {
                infoLiberarRecursos.setImei(serieImeiModel.getImei());
                infoLiberarRecursos.setbImei(true);
            } else {
                infoLiberarRecursos.setbImei(false);
            }
            infoLiberarRecursos.setIccid(serieICCIDModel.getIccid());
            infoLiberarRecursos.setbIccid(true);
            infoLiberarRecursos.setMsisdn(serieICCIDModel.getLinea());
        } else {
            infoLiberarRecursos.setbImei(false);
            infoLiberarRecursos.setbIccid(false);
        }

        final LiberarRecursosResponse liberarRecursosResponse = liberaRecursosMQService.rollbackService(infoLiberarRecursos,
                consignmentModel);
        getModelService().save(consignmentModel);
        if (liberarRecursosResponse != null) {
            final ReleaseResourcesModel releaseResourcesModel = getModelService().create(ReleaseResourcesModel.class);
            releaseResourcesModel.setMessageUUID(liberarRecursosResponse.getControlData().getMessageUUID());
            releaseResourcesModel
                    .setIdTransaccion(liberarRecursosResponse.getLiberarRecursosActivacionResponse().getIdTransaccion());
            releaseResourcesModel.setIdProceso(liberarRecursosResponse.getLiberarRecursosActivacionResponse().getIdProceso());
            releaseResourcesModel.setRegion(infoLiberarRecursos.getRegion());
            releaseResourcesModel.setIdEntrega(infoLiberarRecursos.getIdEntrega());
            releaseResourcesModel.setIdPedido(infoLiberarRecursos.getIdPedido());
            releaseResourcesModel.setOrder(orderModel.getCode());
            releaseResourcesModel.setRMA(StringUtils.EMPTY);
            releaseResourcesModel.setReturnProcesCode(StringUtils.EMPTY);
            releaseResourcesModel.setMsisdn(
                    StringUtils.isNotEmpty(infoLiberarRecursos.getMsisdn()) ? infoLiberarRecursos.getMsisdn() : StringUtils.EMPTY);
            releaseResourcesModel.setIccid(StringUtils.EMPTY);
            releaseResourcesModel.setImei(StringUtils.EMPTY);
            releaseResourcesModel.setConsignment(consignmentModel);
            consignmentModel.setReleaseResources(releaseResourcesModel);
            getModelService().saveAll(consignmentModel);
        } else {
            throw new Exception(String.format("Error calling rollback service for consignment: %s", consignment.getCode()));
        }
    }

    private TelcelSplitOrderPojo validateRequest() throws Exception {
        if (this.productSelector.getSelectedItem() == null) {
            throw new WrongValueException(this.productSelector, this.getLabel("telcelb2cbackoffice.createtelcelreplacement.validation.invalid.productselector"));
        }
        if (this.warehouseSelector.getSelectedItem() == null) {
            throw new WrongValueException(this.warehouseSelector, this.getLabel("telcelb2cbackoffice.createtelcelreplacement.validation.invalid.warehouseSelector"));
        }
        if (this.totalReplacement.getValue() <= 0D) {
            throw new WrongValueException(this.totalReplacement, this.getLabel("telcelb2cbackoffice.createtelcelreplacement.validation.invalid.totalReplacement"));
        }
        WarehouseSelectorDTO warehouseSelectorDTO = (WarehouseSelectorDTO) this.warehouseSelector.getSelectedItem().getAttribute("warehouse");
        final String almacen = warehouseSelectorDTO.getAlmacen();
        final String centro = warehouseSelectorDTO.getCentro();
        final String numMaterialSim = warehouseSelectorDTO.getSim();
        String regionTelcel = StringUtils.EMPTY;
        final WarehouseModel warehouseModel = telcelWarehouseService.findByCentroAlmacen(centro,
                almacen);
        if (warehouseModel != null) {
            final PointOfServiceModel pointOfServiceModel = warehouseModel.getPointsOfService().iterator().next();
            if (pointOfServiceModel != null && pointOfServiceModel.getAddress() != null &&
                    pointOfServiceModel.getAddress().getRegionTelcel() != null) {
                regionTelcel = pointOfServiceModel.getAddress().getRegionTelcel().getCode();
            }
        } else {
            throw new Exception(String.format("Warehouse with almacen: %s and centro: %s not found", almacen, centro));
        }

        TelcelSplitOrderPojo telcelSplitOrderPojo = new TelcelSplitOrderPojo();
        telcelSplitOrderPojo.setAlmacen(almacen);
        telcelSplitOrderPojo.setCentro(centro);
        telcelSplitOrderPojo.setRegionTelcel(regionTelcel);
        if (StringUtils.isNotEmpty(numMaterialSim)) {
            telcelSplitOrderPojo.setNumMaterialSim(numMaterialSim);
        }
        AdditionalServiceEntryModel additionalServiceEntryModel = this.getConsignment().getAdditionalServiceEntry();
        if (additionalServiceEntryModel != null) {
            AdditionalServiceEntryModel newAdditionalServiceEntryModel = modelService.clone(additionalServiceEntryModel);
            telcelSplitOrderPojo.setAdditionalServiceEntryModel(newAdditionalServiceEntryModel);
            telcelSplitOrderPojo.setAdditionalServicesSku(additionalServiceEntryModel.getAdditionalServiceProduct().getSku());
        }
        telcelSplitOrderPojo.setEntryNumber(0);
        return telcelSplitOrderPojo;
    }

    /**
     * Gets consignment.
     *
     * @return the consignment
     */
    public ConsignmentModel getConsignment() {
        return consignment;
    }

    /**
     * Sets consignment.
     *
     * @param consignment the consignment
     */
    public void setConsignment(ConsignmentModel consignment) {
        this.consignment = consignment;
    }

    /**
     * Gets telcel replacement order service.
     *
     * @return the telcel replacement order service
     */
    public TelcelReplacementOrderService getTelcelReplacementOrderService() {
        return telcelReplacementOrderService;
    }

    /**
     * Sets telcel replacement order service.
     *
     * @param telcelReplacementOrderService the telcel replacement order service
     */
    public void setTelcelReplacementOrderService(TelcelReplacementOrderService telcelReplacementOrderService) {
        this.telcelReplacementOrderService = telcelReplacementOrderService;
    }

    /**
     * Gets notification service.
     *
     * @return the notification service
     */
    public NotificationService getNotificationService() {
        return notificationService;
    }

    /**
     * Sets notification service.
     *
     * @param notificationService the notification service
     */
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Gets model service.
     *
     * @return the model service
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * Sets model service.
     *
     * @param modelService the model service
     */
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * Gets telcel order service.
     *
     * @return the telcel order service
     */
    public TelcelOrderService getTelcelOrderService() {
        return telcelOrderService;
    }

    /**
     * Sets telcel order service.
     *
     * @param telcelOrderService the telcel order service
     */
    public void setTelcelOrderService(TelcelOrderService telcelOrderService) {
        this.telcelOrderService = telcelOrderService;
    }

    /**
     * Gets replicate stock service.
     *
     * @return the replicate stock service
     */
    public ReplicateStockService getReplicateStockService() {
        return replicateStockService;
    }

    /**
     * Sets replicate stock service.
     *
     * @param replicateStockService the replicate stock service
     */
    public void setReplicateStockService(ReplicateStockService replicateStockService) {
        this.replicateStockService = replicateStockService;
    }

    /**
     * Gets telcel warehouse service.
     *
     * @return the telcel warehouse service
     */
    public TelcelWarehouseService getTelcelWarehouseService() {
        return telcelWarehouseService;
    }

    /**
     * Sets telcel warehouse service.
     *
     * @param telcelWarehouseService the telcel warehouse service
     */
    public void setTelcelWarehouseService(TelcelWarehouseService telcelWarehouseService) {
        this.telcelWarehouseService = telcelWarehouseService;
    }

    /**
     * Gets address converter.
     *
     * @return the address converter
     */
    public Converter<AddressModel, AddressData> getAddressConverter() {
        return addressConverter;
    }

    /**
     * Sets address converter.
     *
     * @param addressConverter the address converter
     */
    public void setAddressConverter(Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }
}