/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.backoffice.omsbackoffice.widgets.returns.createreturnrequest;

import com.hybris.backoffice.i18n.BackofficeLocaleService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.util.DefaultWidgetController;
import com.hybris.cockpitng.util.notifications.NotificationService;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commerceservices.event.CreateReturnEvent;
import de.hybris.platform.consignmenttrackingservices.service.ConsignmentTrackingService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.refund.RefundService;
import de.hybris.platform.returns.OrderReturnRecordsHandlerException;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;
import mx.com.telcel.backoffice.omsbackoffice.widgets.returns.dtos.ReturnEntryToCreateDto;
import mx.com.telcel.core.model.SeriesICCIDModel;
import mx.com.telcel.core.model.SeriesImeiModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.InputElement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class TelcelCreateReturnRequestController extends DefaultWidgetController {
    private static final Logger LOG = Logger.getLogger(TelcelCreateReturnRequestController.class.getName());
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_NUMBER_OF_DIGITS = 2;
    protected static final String IN_SOCKET = "inputObject";
    protected static final String OUT_CONFIRM = "confirm";
    protected static final Object COMPLETED = "completed";
    protected static final int COLUMN_INDEX_RETURNABLE_QUANTITY = 5;
    protected static final int COLUMN_INDEX_RETURN_QUANTITY = 6;
    protected static final int COLUMN_INDEX_RETURN_AMOUNT = 7;
    protected static final int COLUMN_INDEX_RETURN_REASON = 8;
    protected static final int COLUMN_INDEX_RETURN_COMMENT = 9;
    protected static final int DEFAULT_AMOUNT_SCALE = 2;
    public static final String CONSIGNMENTS = "consignments";
    public static final String PIPE = "|";
    private final List<String> refundReasons = new ArrayList();
    private transient Set<ReturnEntryToCreateDto> returnEntriesToCreate;
    private OrderModel order;
    @Wire
    private Textbox orderCode;
    @Wire
    private Textbox customer;
    @Wire
    private Doublebox totalDiscounts;
    @Wire
    private Doublebox orderTotal;
    @Wire
    private Combobox globalReason;
    @Wire
    private Textbox globalComment;
    @Wire
    private Grid returnEntries;
    @Wire
    private Checkbox isReturnInstore;
    @Wire
    private Checkbox refundDeliveryCost;
    @Wire
    private Checkbox refundAdditionalService;
    @Wire
    private Checkbox globalReturnEntriesSelection;
    @Wire
    private Doublebox totalRefundAmount;
    @Wire
    private Doublebox estimatedTax;
    @Wire
    private Doublebox deliveryCost;
    @Wire
    private Doublebox additionalService;
    @WireVariable
    private transient ReturnService returnService;
    @WireVariable
    private transient RefundService refundService;
    @WireVariable
    private transient EventService eventService;
    @WireVariable
    private transient EnumerationService enumerationService;
    @WireVariable
    private transient ModelService modelService;
    @WireVariable
    private transient BackofficeLocaleService cockpitLocaleService;
    @WireVariable
    private transient CockpitEventQueue cockpitEventQueue;
    @WireVariable
    private transient NotificationService notificationService;
    @WireVariable
    private ConsignmentTrackingService consignmentTrackingService;
    @WireVariable
    private UserService userService;

    private static final List<RefundReason> referenceTypes = Arrays.asList(RefundReason.BANKSTATEMENT,
            RefundReason.FRAUDRISK,
            RefundReason.DELIVERYFAILED,
            RefundReason.CANCELPRODTEST,
            RefundReason.CANCELINCOMPLETEORDER,
            RefundReason.CANCELLOSS,
            RefundReason.CANCELREQUESTCLIENT,
            RefundReason.RETURNREQUESTCLIENT,
            RefundReason.CANCELINTERNALISSUE,
            RefundReason.MANUFACTURING_DEFECTS);

    public TelcelCreateReturnRequestController() {
    }

    @SocketEvent(
            socketId = "inputObject"
    )
    public void initCreateReturnRequestForm(OrderModel inputOrder) {
        this.setOrder(inputOrder);
        this.refundReasons.clear();
        this.isReturnInstore.setChecked(false);
        this.refundDeliveryCost.setChecked(false);
        this.refundAdditionalService.setChecked(false);
        this.additionalService.setValue(0D);
        this.globalReturnEntriesSelection.setChecked(false);
        this.deliveryCost.setValue(this.getOrder().getDeliveryCost());
        this.refundDeliveryCost.setDisabled(this.getReturnService().getReturnRequests(this.getOrder().getCode()).stream().anyMatch((returnRequest) -> {
            return returnRequest.getRefundDeliveryCost() && returnRequest.getStatus() != ReturnStatus.CANCELED;
        }));
        this.getWidgetInstanceManager().setTitle(this.getWidgetInstanceManager().getLabel("customersupportbackoffice.createreturnrequest.confirm.title") + " " + this.getOrder().getCode());
        this.orderCode.setValue(this.getOrder().getCode());
        this.customer.setValue(this.getOrder().getUser().getDisplayName());
        this.orderTotal.setValue(this.getOrder().getTotalPrice());
        this.setTotalDiscounts();
        Locale locale = this.getCockpitLocaleService().getCurrentLocale();
        this.getEnumerationService().getEnumerationValues(RefundReason.class).forEach((reason) -> {
            if (referenceTypes.contains(reason)) {
                this.refundReasons.add(this.getEnumerationService().getEnumerationName(reason, locale));
            }
        });
        this.globalReason.setModel(new ListModelArray(this.refundReasons));
        Map<AbstractOrderEntryModel, Long> returnableOrderEntries = this.getReturnService().getAllReturnableEntries(inputOrder);
        this.returnEntriesToCreate = new HashSet();
        returnableOrderEntries.forEach((orderEntry, returnableQty) -> {
            this.returnEntriesToCreate.add(new ReturnEntryToCreateDto(orderEntry, returnableQty.intValue(), this.refundReasons));
        });
        this.getReturnEntries().setModel(new ListModelList(this.returnEntriesToCreate));
        this.getReturnEntries().renderAll();
        this.addListeners();
    }

    protected void addListeners() {
        List<Component> rows = this.returnEntries.getRows().getChildren();
        Iterator var3 = rows.iterator();

        while (var3.hasNext()) {
            Component row = (Component) var3.next();
            Iterator var5 = row.getChildren().iterator();

            while (var5.hasNext()) {
                Component myComponent = (Component) var5.next();
                if (myComponent instanceof Combobox) {
                    final String comboName = ((Combobox) myComponent).getName();
                    if (comboName != null && comboName.equals(CONSIGNMENTS)) {
                        myComponent.addEventListener("onCustomConsignmentChange", (event) -> {
                            Events.echoEvent("onLaterCustomConsignmentChange", myComponent, event.getData());
                        });
                        myComponent.addEventListener("onLaterCustomConsignmentChange", (event) -> {
                            Clients.clearWrongValue(myComponent);
                            myComponent.invalidate();
                            this.handleIndividualRefundConsignment(event);
                        });
                    } else {
                        myComponent.addEventListener("onCustomChange", (event) -> {
                            Events.echoEvent("onLaterCustomChange", myComponent, event.getData());
                        });
                        myComponent.addEventListener("onLaterCustomChange", (event) -> {
                            Clients.clearWrongValue(myComponent);
                            myComponent.invalidate();
                            this.handleIndividualRefundReason(event);
                        });
                    }
                } else if (myComponent instanceof Checkbox) {
                    myComponent.addEventListener("onCheck", (event) -> {
                        this.handleRow((Row) event.getTarget().getParent());
                        this.calculateTotalRefundAmount();
                    });
                } else if (myComponent instanceof Intbox) {
                    myComponent.addEventListener("onChanging", this::handleIndividualQuantityToReturn);
                } else if (myComponent instanceof Doublebox) {
                    myComponent.addEventListener("onChanging", this::handleIndividualAmountToReturn);
                    myComponent.addEventListener("onFocus", this::handleIndividualAmountToReturnFocus);
                    myComponent.addEventListener("onBlur", this::handleIndividualAmountToReturnBlur);
                } else if (myComponent instanceof Textbox) {
                    myComponent.addEventListener("onChanging", (event) -> {
                        this.autoSelect(event);
                        ((ReturnEntryToCreateDto) ((Row) event.getTarget().getParent()).getValue()).setRefundEntryComment(((InputEvent) event).getValue());
                    });
                }
            }
        }

        this.globalReason.addEventListener("onSelect", this::handleGlobalReason);
        this.globalComment.addEventListener("onChanging", this::handleGlobalComment);
        this.globalReturnEntriesSelection.addEventListener("onCheck", (event) -> {
            this.selectAllEntries();
        });
        this.refundDeliveryCost.addEventListener("onClick", (event) -> {
            this.calculateTotalRefundAmount();
        });

        this.refundAdditionalService.addEventListener("onClick", (event) -> {
            this.calculateTotalRefundAmount();
        });
    }

    protected void handleIndividualAmountToReturn(Event event) {
        ((Checkbox) event.getTarget().getParent().getChildren().iterator().next()).setChecked(true);
        Row myRow = (Row) event.getTarget().getParent();
        ReturnEntryToCreateDto returnEntryDto = (ReturnEntryToCreateDto) myRow.getValue();
        String refundAmountStr = ((InputEvent) event).getValue();
        BigDecimal newAmount = refundAmountStr != null && !refundAmountStr.isEmpty() ? BigDecimal.valueOf(Double.parseDouble(refundAmountStr)) : BigDecimal.ZERO;
        returnEntryDto.getRefundEntry().setAmount(newAmount);
        this.calculateIndividualTaxEstimate(returnEntryDto);
        this.calculateTotalRefundAmount();
    }

    protected void handleIndividualAmountToReturnFocus(Event event) {
        this.applyAmountToRefundInput(event, false);
    }

    protected void handleIndividualAmountToReturnBlur(Event event) {
        this.applyAmountToRefundInput(event, true);
    }

    protected int getDigitsNumber(RefundEntryModel refundEntryModel) {
        int digitsNumber = 2;
        if (refundEntryModel != null) {
            digitsNumber = refundEntryModel.getOrderEntry().getOrder().getCurrency().getDigits();
        }

        return digitsNumber;
    }

    private void applyAmountToRefundInput(Event event, boolean rounded) {
        Row myRow = (Row) event.getTarget().getParent();
        ReturnEntryToCreateDto returnEntryDto = (ReturnEntryToCreateDto) myRow.getValue();
        BigDecimal refundAmountTemp = returnEntryDto.getRefundEntry().getAmount() != null ? returnEntryDto.getRefundEntry().getAmount() : BigDecimal.ZERO;
        if (rounded) {
            this.applyToRow(refundAmountTemp.setScale(this.getDigitsNumber(returnEntryDto.getRefundEntry()), RoundingMode.HALF_DOWN).doubleValue(), 7, myRow);
        } else {
            this.applyToRow(refundAmountTemp.doubleValue(), 7, myRow);
        }

    }

    protected void calculateIndividualTaxEstimate(ReturnEntryToCreateDto returnEntryDto) {
        if (returnEntryDto.getQuantityToReturn() <= returnEntryDto.getReturnableQuantity()) {
            RefundEntryModel refundEntry = returnEntryDto.getRefundEntry();
            Optional<TaxValue> orderEntryOptional = refundEntry.getOrderEntry().getTaxValues().stream().findFirst();
            BigDecimal orderEntryTax = BigDecimal.ZERO;
            if (orderEntryOptional.isPresent()) {
                orderEntryTax = BigDecimal.valueOf(((TaxValue) orderEntryOptional.get()).getValue());
            }

            if (refundEntry.getAmount().compareTo(BigDecimal.valueOf(refundEntry.getOrderEntry().getTotalPrice())) >= 0) {
                returnEntryDto.setTax(orderEntryTax);
            } else {
                BigDecimal returnEntryTax = orderEntryTax.multiply(refundEntry.getAmount()).divide(BigDecimal.valueOf(refundEntry.getOrderEntry().getTotalPrice()), RoundingMode.HALF_UP).setScale(refundEntry.getOrderEntry().getOrder().getCurrency().getDigits(), RoundingMode.HALF_UP);
                returnEntryDto.setTax(returnEntryTax);
            }
        }

    }

    protected void handleIndividualQuantityToReturn(Event event) {
        this.autoSelect(event);
        Row myRow = (Row) event.getTarget().getParent();
        ReturnEntryToCreateDto myReturnEntry = (ReturnEntryToCreateDto) myRow.getValue();
        String returnQuantityStr = ((InputEvent) event).getValue();
        int amountEntered = returnQuantityStr != null && !returnQuantityStr.isEmpty() ? Integer.parseInt(returnQuantityStr) : 0;
        this.calculateRowAmount(myRow, myReturnEntry, amountEntered);
    }

    protected void handleIndividualRefundReason(Event event) {
        Optional<RefundReason> refundReason = this.getCustomSelectedRefundReason(event);
        if (refundReason.isPresent()) {
            this.autoSelect(event);
            ((ReturnEntryToCreateDto) ((Row) event.getTarget().getParent()).getValue()).getRefundEntry().setReason((RefundReason) refundReason.get());
        }

    }

    protected void handleIndividualRefundConsignment(Event event) {
        final String consignmentCode = this.getCustomSelectedConsignment(event);
        if (StringUtils.isNotEmpty(consignmentCode)) {
            this.autoSelect(event);
            Optional<ConsignmentModel> consignment = this.getConsignmentTrackingService().getConsignmentForCode(this.getOrder().getCode(), consignmentCode);
            if (consignment.isPresent()) {
                //((ReturnEntryToCreateDto) ((Row) event.getTarget().getParent()).getValue()).getRefundEntry().setConsignmentCode(consignmentCode);
                ((ReturnEntryToCreateDto) ((Row) event.getTarget().getParent()).getValue()).getRefundEntry().setConsignment(consignment.get());
                Row myRow = (Row) event.getTarget().getParent();
                this.setConsignmentValues(consignment.get(), myRow);
            }
        }
        this.calculateTotalRefundAmount();
    }

    private void setConsignmentValues(ConsignmentModel consignmentModel, Row myRow) {
        this.applyToRow(consignmentModel.getPoNoSalesDocument() + PIPE + consignmentModel.getPoNoDelivery(), 11, myRow);
        String siap = StringUtils.EMPTY;
        final SeriesICCIDModel serieICCIDModel = consignmentModel.getSeriesICCID();
        final SeriesImeiModel serieImeiModel = consignmentModel.getSeriesImei();
        if (serieICCIDModel != null) {
            siap = serieICCIDModel.getIccid();
            if (StringUtils.isNotEmpty(serieICCIDModel.getLinea())) {
                siap += PIPE + serieICCIDModel.getLinea();
            }
        }
        if (serieImeiModel != null) {
            siap += PIPE + serieImeiModel.getImei();
        }
        this.refundAdditionalService.setChecked(false);
        if(consignmentModel.getAdditionalServiceEntry() != null){
            this.refundAdditionalService.setDisabled(false);
            this.additionalService.setValue(consignmentModel.getAdditionalServiceEntry().getBasePrice());
        }else{
            this.refundAdditionalService.setDisabled(true);
            this.additionalService.setValue(0D);
        }
        this.applyToRow(siap, 12, myRow);

    }

    private String getCustomSelectedConsignment(Event event) {
        String result = StringUtils.EMPTY;
        if (event.getTarget() instanceof Combobox) {
            Object o = event.getData();
            if (Objects.nonNull(o)) {
                result = String.valueOf(o);
            }
        }
        return result;
    }

    protected void handleGlobalComment(Event event) {
        this.applyToGrid(((InputEvent) event).getValue(), 9);
        this.returnEntries.getRows().getChildren().stream().filter((entry) -> {
            return ((Checkbox) entry.getChildren().iterator().next()).isChecked();
        }).forEach((entry) -> {
            ((ReturnEntryToCreateDto) ((Row) entry).getValue()).setRefundEntryComment(((InputEvent) event).getValue());
        });
    }

    protected void handleGlobalReason(Event event) {
        Optional<RefundReason> refundReason = this.getSelectedRefundReason(event);
        if (refundReason.isPresent()) {
            this.applyToGrid(this.getReasonIndex((RefundReason) refundReason.get()), 8);
            this.returnEntries.getRows().getChildren().stream().filter((entry) -> {
                return ((Checkbox) entry.getChildren().iterator().next()).isChecked();
            }).forEach((entry) -> {
                ((ReturnEntryToCreateDto) ((Row) entry).getValue()).getRefundEntry().setReason((RefundReason) refundReason.get());
            });
        }

    }

    protected void calculateRowAmount(Row myRow, ReturnEntryToCreateDto myReturnEntry, int qtyEntered) {
        BigDecimal newAmount = myReturnEntry.isDiscountApplied() ? BigDecimal.ZERO : BigDecimal.valueOf(myReturnEntry.getRefundEntry().getOrderEntry().getBasePrice() * (double) qtyEntered);
        myReturnEntry.setQuantityToReturn(qtyEntered);
        myReturnEntry.getRefundEntry().setAmount(newAmount);
        this.applyToRow(newAmount.setScale(2, RoundingMode.HALF_EVEN).doubleValue(), 7, myRow);
        this.calculateIndividualTaxEstimate(myReturnEntry);
        this.calculateTotalRefundAmount();
    }

    protected void calculateTotalRefundAmount() {
        this.calculateEstimatedTax();
        Double calculatedRefundAmount = this.refundDeliveryCost.isChecked() ? this.getOrder().getDeliveryCost() : 0.0D;
        Double calculatedRefundAmountAdditionalService = this.refundAdditionalService.isChecked() ? this.refundAdditionalService.getValue() : 0.0D;
        calculatedRefundAmount = calculatedRefundAmountAdditionalService + calculatedRefundAmount + ((BigDecimal) this.returnEntriesToCreate.stream().map((entry) -> {
            return entry.getRefundEntry().getAmount();
        }).reduce(BigDecimal.ZERO, BigDecimal::add)).doubleValue();
        this.totalRefundAmount.setValue(BigDecimal.valueOf(calculatedRefundAmount).add(BigDecimal.valueOf(this.estimatedTax.doubleValue())).doubleValue());
    }

    protected void calculateEstimatedTax() {
        BigDecimal totalTax = (BigDecimal) this.returnEntriesToCreate.stream().filter((returnEntryToCreate) -> {
            return returnEntryToCreate.getQuantityToReturn() > 0 && returnEntryToCreate.getTax() != null;
        }).map(ReturnEntryToCreateDto::getTax).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (this.refundDeliveryCost.isChecked()) {
            BigDecimal deliveryCostTax = BigDecimal.valueOf(this.getOrder().getTotalTax() - this.getOrder().getEntries().stream().filter((entry) -> {
                return !entry.getTaxValues().isEmpty();
            }).mapToDouble((entry) -> {
                return ((TaxValue) entry.getTaxValues().stream().findFirst().get()).getValue();
            }).sum());
            totalTax = totalTax.add(deliveryCostTax);
        }

        this.estimatedTax.setValue(totalTax.doubleValue());
    }

    protected void autoSelect(Event event) {
        ((Checkbox) event.getTarget().getParent().getChildren().iterator().next()).setChecked(true);
    }

    protected void handleRow(Row row) {
        ReturnEntryToCreateDto myEntry = (ReturnEntryToCreateDto) row.getValue();
        if (row.getChildren().iterator().next() instanceof Checkbox) {
            if (!((Checkbox) row.getChildren().iterator().next()).isChecked()) {
                this.applyToRow(0, 6, row);
                this.applyToRow((Object) null, 8, row);
                this.applyToRow(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN).doubleValue(), 7, row);
                this.applyToRow((Object) null, 9, row);
                myEntry.setQuantityToReturn(0);
                myEntry.getRefundEntry().setAmount(BigDecimal.ZERO);
                myEntry.getRefundEntry().setReason((RefundReason) null);
                myEntry.setRefundEntryComment((String) null);
            } else {
                this.applyToRow(this.globalReason.getSelectedIndex(), 8, row);
                this.applyToRow(this.globalComment.getValue(), 9, row);
                Optional<RefundReason> reason = this.matchingComboboxReturnReason(this.globalReason.getSelectedItem() != null ? this.globalReason.getSelectedItem().getLabel() : null);
                myEntry.getRefundEntry().setReason(reason.isPresent() ? (RefundReason) reason.get() : null);
                myEntry.setRefundEntryComment(this.globalComment.getValue());
            }
        }

        this.calculateTotalRefundAmount();
    }

    protected void selectAllEntries() {
        this.applyToGrid(Boolean.TRUE, 0);
        Iterator var2 = this.returnEntries.getRows().getChildren().iterator();

        while (var2.hasNext()) {
            Component row = (Component) var2.next();
            Component firstComponent = (Component) row.getChildren().iterator().next();
            if (firstComponent instanceof Checkbox) {
                ((Checkbox) firstComponent).setChecked(this.globalReturnEntriesSelection.isChecked());
            }

            this.handleRow((Row) row);
            if (this.globalReturnEntriesSelection.isChecked()) {
                int returnableQty = Integer.parseInt(((Label) row.getChildren().get(5)).getValue());
                this.applyToRow(returnableQty, 6, row);
                this.calculateRowAmount((Row) row, (ReturnEntryToCreateDto) ((Row) row).getValue(), returnableQty);
            }
        }

        if (this.globalReturnEntriesSelection.isChecked()) {
            this.returnEntriesToCreate.forEach((entry) -> {
                entry.setQuantityToReturn(entry.getReturnableQuantity());
            });
            this.calculateTotalRefundAmount();
        }

    }

    protected int getReasonIndex(RefundReason refundReason) {
        int index = 0;
        String myReason = this.getEnumerationService().getEnumerationName(refundReason, this.getCockpitLocaleService().getCurrentLocale());

        for (Iterator var5 = this.refundReasons.iterator(); var5.hasNext(); ++index) {
            String reason = (String) var5.next();
            if (myReason.equals(reason)) {
                break;
            }
        }

        return index;
    }

    protected Optional<RefundReason> getSelectedRefundReason(Event event) {
        Optional<RefundReason> result = Optional.empty();
        if (!((SelectEvent) event).getSelectedItems().isEmpty()) {
            Object selectedValue = ((Comboitem) ((SelectEvent) event).getSelectedItems().iterator().next()).getValue();
            result = this.matchingComboboxReturnReason(selectedValue.toString());
        }

        return result;
    }

    protected Optional<RefundReason> getCustomSelectedRefundReason(Event event) {
        Optional<RefundReason> reason = Optional.empty();
        if (event.getTarget() instanceof Combobox) {
            Object selectedValue = event.getData();
            reason = this.matchingComboboxReturnReason(selectedValue.toString());
        }

        return reason;
    }

    protected void applyToGrid(Object data, int childrenIndex) {
        Iterator var4 = this.returnEntries.getRows().getChildren().iterator();

        while (var4.hasNext()) {
            Component row = (Component) var4.next();
            Component firstComponent = (Component) row.getChildren().iterator().next();
            if (firstComponent instanceof Checkbox && ((Checkbox) firstComponent).isChecked()) {
                this.applyToRow(data, childrenIndex, row);
            }
        }

    }

    protected void applyToRow(Object data, int childrenIndex, Component row) {
        int index = 0;
        Iterator var6 = row.getChildren().iterator();

        while (var6.hasNext()) {
            Component myComponent = (Component) var6.next();
            if (index != childrenIndex) {
                ++index;
            } else {
                if (myComponent instanceof Checkbox && data != null) {
                    ((Checkbox) myComponent).setChecked((Boolean) data);
                }

                if (myComponent instanceof Combobox) {
                    if (!(data instanceof Integer)) {
                        ((Combobox) myComponent).setSelectedItem((Comboitem) null);
                    } else {
                        ((Combobox) myComponent).setSelectedIndex((Integer) data);
                    }
                } else if (myComponent instanceof Intbox) {
                    ((Intbox) myComponent).setValue((Integer) data);
                } else if (myComponent instanceof Doublebox) {
                    ((Doublebox) myComponent).setValue((Double) data);
                } else if (myComponent instanceof Textbox) {
                    ((Textbox) myComponent).setValue((String) data);
                }

                ++index;
            }
        }

    }

    @ViewEvent(
            componentID = "resetcreatereturnrequest",
            eventName = "onClick"
    )
    public void reset() {
        this.globalReason.setSelectedItem((Comboitem) null);
        this.globalComment.setValue("");
        this.initCreateReturnRequestForm(this.getOrder());
        this.calculateTotalRefundAmount();
    }

    @ViewEvent(
            componentID = "confirmcreatereturnrequest",
            eventName = "onClick"
    )
    public void confirmCreation() {
        this.validateRequest();

        try {
            ReturnRequestModel returnRequest = this.getReturnService().createReturnRequest(this.getOrder());
            final UserModel currentUser = userService.getCurrentUser();
            returnRequest.setUser(currentUser);
            returnRequest.setRefundAdditionalService(this.refundAdditionalService.isChecked());
            returnRequest.setRefundDeliveryCost(this.refundDeliveryCost.isChecked());
            ReturnStatus status = this.isReturnInstore.isChecked() ? ReturnStatus.RECEIVED : ReturnStatus.APPROVAL_PENDING;
            returnRequest.setStatus(status);
            this.getModelService().save(returnRequest);
            this.returnEntriesToCreate.stream().filter((entry) -> {
                return entry.getQuantityToReturn() != 0;
            }).forEach((entry) -> {
                this.createRefundWithCustomAmount(returnRequest, entry);
            });
            this.applyReturnRequest(returnRequest);
            CreateReturnEvent createReturnEvent = new CreateReturnEvent();
            createReturnEvent.setReturnRequest(returnRequest);
            this.getEventService().publishEvent(createReturnEvent);
            this.getNotificationService().notifyUser("", "JustMessage", Level.SUCCESS, new Object[]{this.getWidgetInstanceManager().getLabel("customersupportbackoffice.createreturnrequest.confirm.success") + " - " + returnRequest.getRMA()});
        } catch (Exception var4) {
            LOG.info(var4.getMessage(), var4);
            this.getNotificationService().notifyUser("", "JustMessage", Level.FAILURE, new Object[]{this.getWidgetInstanceManager().getLabel("customersupportbackoffice.createreturnrequest.confirm.error")});
        }

        this.sendOutput("confirm", COMPLETED);
    }

    private void applyReturnRequest(ReturnRequestModel returnRequest) throws OrderReturnRecordsHandlerException {
        try {
            this.getRefundService().apply(returnRequest.getOrder(), returnRequest);
        } catch (IllegalStateException var2) {
            LOG.info("Order " + this.getOrder().getCode() + " Return record already in progress");
        }

    }

    protected RefundEntryModel createRefundWithCustomAmount(ReturnRequestModel returnRequest, ReturnEntryToCreateDto entry) {
        ReturnAction actionToExecute = this.isReturnInstore.isChecked() ? ReturnAction.IMMEDIATE : ReturnAction.HOLD;
        RefundEntryModel refundEntryToBeCreated = this.getReturnService().createRefund(returnRequest, entry.getRefundEntry().getOrderEntry(), entry.getRefundEntryComment(), (long) entry.getQuantityToReturn(), actionToExecute, entry.getRefundEntry().getReason());
        refundEntryToBeCreated.setAmount(entry.getRefundEntry().getAmount());
        //refundEntryToBeCreated.setConsignmentCode(entry.getRefundEntry().getConsignmentCode());
        ConsignmentModel consignmentModel = entry.getRefundEntry().getConsignment();
        refundEntryToBeCreated.setConsignment(consignmentModel);
        returnRequest.setSubtotal(returnRequest.getSubtotal().add(entry.getRefundEntry().getAmount()));
        this.getModelService().save(refundEntryToBeCreated);
        consignmentModel.setWasItReturned(true);
        this.getModelService().save(consignmentModel);
        return refundEntryToBeCreated;
    }

    protected void validateReturnEntry(ReturnEntryToCreateDto entry) {
        InputElement amountInput;
        if (entry.getQuantityToReturn() > entry.getReturnableQuantity()) {
            amountInput = (InputElement) this.targetFieldToApplyValidation(entry.getRefundEntry().getOrderEntry().getProduct().getCode(), 1, 6);
            throw new WrongValueException(amountInput, this.getLabel("customersupportbackoffice.createreturnrequest.validation.invalid.quantity"));
        } else if (entry.getRefundEntry().getReason() != null && entry.getQuantityToReturn() == 0) {
            amountInput = (InputElement) this.targetFieldToApplyValidation(entry.getRefundEntry().getOrderEntry().getProduct().getCode(), 1, 6);
            throw new WrongValueException(amountInput, this.getLabel("customersupportbackoffice.createreturnrequest.validation.missing.quantity"));
        } else if (entry.getRefundEntry().getReason() == null && entry.getQuantityToReturn() > 0) {
            Combobox combobox = (Combobox) this.targetFieldToApplyValidation(entry.getRefundEntry().getOrderEntry().getProduct().getCode(), 1, 8);
            throw new WrongValueException(combobox, this.getLabel("customersupportbackoffice.createreturnrequest.validation.missing.reason"));
//        } else if (StringUtils.isEmpty(entry.getRefundEntryComment()) && entry.getQuantityToReturn() > 0) {
//            InputElement commentsInput = (InputElement) this.targetFieldToApplyValidation(entry.getRefundEntry().getOrderEntry().getProduct().getCode(), 1, 9);
//            throw new WrongValueException(commentsInput, this.getLabel("customersupportbackoffice.createreturnrequest.validation.missing.comment"));
        } else if (entry.getRefundEntry().getConsignment() == null && entry.getQuantityToReturn() > 0) {
            Combobox combobox = (Combobox) this.targetFieldToApplyValidation(entry.getRefundEntry().getOrderEntry().getProduct().getCode(), 1, 10);
            throw new WrongValueException(combobox, this.getLabel("customersupportbackoffice.createreturnrequest.validation.missing.consignment"));
        } else if (entry.getQuantityToReturn() > 0 && entry.getRefundEntry().getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            amountInput = (InputElement) this.targetFieldToApplyValidation(entry.getRefundEntry().getOrderEntry().getProduct().getCode(), 1, 7);
            throw new WrongValueException(amountInput, this.getLabel("customersupportbackoffice.createreturnrequest.validation.invalid.amount"));
        }
    }

    protected void validateRequest() {
        Iterator var2 = this.getReturnEntries().getRows().getChildren().iterator();

        while (var2.hasNext()) {
            Component row = (Component) var2.next();
            Component firstComponent = (Component) row.getChildren().iterator().next();
            if (firstComponent instanceof Checkbox && ((Checkbox) firstComponent).isChecked()) {
                InputElement returnQty = (InputElement) row.getChildren().get(6);
                if (returnQty.getRawValue().equals(0)) {
                    throw new WrongValueException(returnQty, this.getLabel("customersupportbackoffice.createreturnrequest.validation.missing.quantity"));
                }
            }
        }

        ListModelList<ReturnEntryToCreateDto> modelList = (ListModelList) this.getReturnEntries().getModel();
        if (modelList.stream().allMatch((entry) -> {
            return entry.getQuantityToReturn() == 0;
        })) {
            throw new WrongValueException(this.globalReturnEntriesSelection, this.getLabel("customersupportbackoffice.createreturnrequest.validation.missing.selectedLine"));
        } else {
            modelList.forEach(this::validateReturnEntry);
        }
    }

    protected Component targetFieldToApplyValidation(String stringToValidate, int indexLabelToCheck, int indexTargetComponent) {
        Iterator var5 = this.returnEntries.getRows().getChildren().iterator();

        while (var5.hasNext()) {
            Component component = (Component) var5.next();
            Label label = (Label) component.getChildren().get(indexLabelToCheck);
            if (label.getValue().equals(stringToValidate)) {
                return (Component) component.getChildren().get(indexTargetComponent);
            }
        }

        return null;
    }

    protected Optional<RefundReason> matchingComboboxReturnReason(String refundReasonLabel) {
        return this.getEnumerationService().getEnumerationValues(RefundReason.class).stream().filter((reason) -> {
            return this.getEnumerationService().getEnumerationName(reason, this.getCockpitLocaleService().getCurrentLocale()).equals(refundReasonLabel);
        }).findFirst();
    }

    protected void setTotalDiscounts() {
        Double totalDiscount = this.getOrder().getTotalDiscounts() != null ? this.getOrder().getTotalDiscounts() : 0.0D;
        totalDiscount = totalDiscount + this.getOrder().getEntries().stream().mapToDouble((entry) -> {
            return entry.getDiscountValues().stream().mapToDouble(DiscountValue::getAppliedValue).sum();
        }).sum();
        this.totalDiscounts.setValue(totalDiscount);
    }

    protected OrderModel getOrder() {
        return this.order;
    }

    public void setOrder(OrderModel order) {
        this.order = order;
    }

    public Grid getReturnEntries() {
        return this.returnEntries;
    }

    public void setReturnEntries(Grid returnEntries) {
        this.returnEntries = returnEntries;
    }

    public ReturnService getReturnService() {
        return this.returnService;
    }

    public void setReturnService(ReturnService returnService) {
        this.returnService = returnService;
    }

    public EventService getEventService() {
        return this.eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    protected EnumerationService getEnumerationService() {
        return this.enumerationService;
    }

    public void setEnumerationService(EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
    }

    protected ModelService getModelService() {
        return this.modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    protected BackofficeLocaleService getCockpitLocaleService() {
        return this.cockpitLocaleService;
    }

    public void setCockpitLocaleService(BackofficeLocaleService cockpitLocaleService) {
        this.cockpitLocaleService = cockpitLocaleService;
    }

    protected CockpitEventQueue getCockpitEventQueue() {
        return this.cockpitEventQueue;
    }

    public void setCockpitEventQueue(CockpitEventQueue cockpitEventQueue) {
        this.cockpitEventQueue = cockpitEventQueue;
    }

    protected RefundService getRefundService() {
        return this.refundService;
    }

    public void setRefundService(RefundService refundService) {
        this.refundService = refundService;
    }

    protected NotificationService getNotificationService() {
        return this.notificationService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public ConsignmentTrackingService getConsignmentTrackingService() {
        return consignmentTrackingService;
    }

    public void setConsignmentTrackingService(ConsignmentTrackingService consignmentTrackingService) {
        this.consignmentTrackingService = consignmentTrackingService;
    }
}