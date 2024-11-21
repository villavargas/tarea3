/*
 *
 * [y] hybris Platform
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package mx.com.telcel.facades.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.ReferencePaymentInfoModel;
import de.hybris.platform.core.model.order.payment.SpeiPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import mx.com.telcel.core.model.AdditionalServiceEntryModel;
import mx.com.telcel.core.model.ConceptosRfcTelcelModel;
import mx.com.telcel.core.model.HolderLineModel;
import mx.com.telcel.core.model.ItemPaymentModel;
import mx.com.telcel.core.model.PaymentReceiptModel;
import mx.com.telcel.core.model.RegimenCapitalModel;
import mx.com.telcel.core.model.RegimenFiscalModel;
import mx.com.telcel.core.model.TelcelPoVariantModel;
import mx.com.telcel.core.service.BillingService;
import mx.com.telcel.facades.holderline.data.HolderLineData;
import mx.com.telcel.facades.order.data.OrderDetailEntryDeniedData;


/**
 * The type Tma abstract telcel order populator.
 *
 * @param <SOURCE>
 *           the type parameter
 * @param <TARGET>
 *           the type parameter
 */
public class TmaAbstractTelcelOrderPopulator<SOURCE extends AbstractOrderModel, TARGET extends AbstractOrderData>
		extends AbstractOrderPopulator<SOURCE, TARGET>
{

	@Resource(name = "referencePaymentInfoConverter")
	private Converter<ReferencePaymentInfoModel, CCPaymentInfoData> referencePaymentInfoConverter;

	@Resource(name = "holderLineConverter")
	private Converter<HolderLineModel, HolderLineData> holderLineConverter;

	@Resource(name = "speiPaymentInfoConverter")
	private Converter<SpeiPaymentInfoModel, CCPaymentInfoData> speiPaymentInfoConverter;

	@Resource(name = "billingService")
	private BillingService billingService;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);
		addAsTotals(source, target);
		addPaymentReferenceInformation(source, target);
		addPaymentSpeiInformation(source, target);
		addBillingAddress(source, target);
		addHolderLineAddress(source, target);

		final List<OrderDetailEntryDeniedData> entrysDenied = new ArrayList<>();
		if (Objects.nonNull(source.getPaymentInfo()) && Objects.nonNull(source.getPaymentInfo().getReceipt()))
		{
			final List<PaymentReceiptModel> paymentReceiptModelList = source.getPaymentInfo().getReceipt();
			for (final PaymentReceiptModel paymentReceiptModel : paymentReceiptModelList)
			{
				if (paymentReceiptModel.getRisk() != null && paymentReceiptModel.getRisk().equalsIgnoreCase("DENIED"))
				{
					final List<ItemPaymentModel> itemPaymentModelList = paymentReceiptModel.getItemsPayment();
					for (final ItemPaymentModel itemPaymentModel : itemPaymentModelList)
					{
						final OrderDetailEntryDeniedData orderDetailEntryDeniedData = new OrderDetailEntryDeniedData();
						orderDetailEntryDeniedData.setSku(itemPaymentModel.getSku());
						orderDetailEntryDeniedData.setSkuFather(itemPaymentModel.getSkuFather());
						orderDetailEntryDeniedData.setEntryNumber(itemPaymentModel.getEntrynumber());
						entrysDenied.add(orderDetailEntryDeniedData);
					}
				}
			}
			target.setEntrysDenied(entrysDenied);
		}
		target.setBlockMerge(Objects.isNull(source.getBlockMerge()) ? Boolean.FALSE : source.getBlockMerge());
	}

	private void addHolderLineAddress(final AbstractOrderModel source, final AbstractOrderData target)
	{
		final HolderLineModel holderLineModel = source.getAddressHolderLine();
		if (holderLineModel != null)
		{
			target.setHolderLineAddress(holderLineConverter.convert(holderLineModel));
		}
	}

	private void addBillingAddress(final AbstractOrderModel source, final AbstractOrderData target)
	{
		target.setRequireInvoice(
				source.getBillingAddress() != null && StringUtils.isNotEmpty(source.getBillingAddress().getRazonSocial()));
		if (source.getBillingAddress() != null)
		{
			target.setTelcelBillingAddress(getAddressBilling(source.getBillingAddress()));
		}
	}

	public AddressData getAddressBilling(final AddressModel source) throws ConversionException
	{
		final AddressData target = new AddressData();
		target.setExternalNumber(source.getExternalNumber());
		target.setInteriorNumber(source.getInteriorNumber());
		target.setReferences(source.getReferences());
		target.setRazonSocial(source.getRazonSocial());
		target.setRfc(source.getRfc());
		target.setConcepto(source.getConcepto());
		target.setRegimen(source.getRegimen());
		target.setRegimenCapital(source.getRegimenCapital());
		if (StringUtils.isNotEmpty(source.getConcepto()))
		{
			final ConceptosRfcTelcelModel conceptosRfcTelcelModel = billingService.getConceptoRfcById(source.getConcepto());
			final String conceptoDes = conceptosRfcTelcelModel != null ? conceptosRfcTelcelModel.getConcepto() : StringUtils.EMPTY;
			target.setConceptoDescription(conceptoDes);
		}
		if (StringUtils.isNotEmpty(source.getRegimenCapital()))
		{
			final RegimenCapitalModel regimenCapitalModel = billingService.getRegimenCapitalById(source.getRegimenCapital());
			final String regimenCapitalDescription = regimenCapitalModel != null ? regimenCapitalModel.getDescription()
					: StringUtils.EMPTY;
			target.setRegimenCapitalDescription(regimenCapitalDescription);
		}
		if (StringUtils.isNotEmpty(source.getRegimen()))
		{
			final RegimenFiscalModel regimenFiscalModel = billingService.getRegimenFiscalById(source.getRegimen());
			final String regimenFiscalDescription = regimenFiscalModel != null ? regimenFiscalModel.getDescription()
					: StringUtils.EMPTY;

			target.setRegimenDescription(regimenFiscalDescription);
		}
		target.setLastName(source.getLastname());
		target.setTitle(source.getTitle() != null ? source.getTitle().getCode() : "");
		target.setFirstName(source.getStreetname());
		target.setLastName(source.getLastname());
		target.setTown(source.getTown());
		target.setDistrict(source.getDistrict());
		target.setPostalCode(source.getPostalcode());
		target.setPhone(source.getPhone1());
		target.setEmail(source.getEmail());
		final RegionData regionData = new RegionData();
		if (source.getRegion() != null)
		{
			regionData.setIsocode(source.getRegion().getIsocode());
			regionData.setName(source.getRegion().getName());
			target.setRegion(regionData);
		}

		final CountryData countryData = new CountryData();
		if (source.getCountry() != null)
		{
			countryData.setIsocode(source.getCountry().getIsocode());
			countryData.setName(source.getCountry().getName());
			target.setCountry(countryData);

		}


		return target;
	}

	protected void addAsTotals(final AbstractOrderModel source, final AbstractOrderData target)
	{
		if (Objects.nonNull(source.getTotalPrice()) && Objects.nonNull(source.getTotalAsPrice()))
		{
			final double chipAsValue = getChipAsValues(source, 0D);
			target.setTotalWithoutAdditonalServices(createPrice(source,
					((source.getTotalPrice() - source.getTotalAsPrice()) + chipAsValue) - source.getDeliveryCost()));
		}
	}

	private double getChipAsValues(final AbstractOrderModel source, double chipAsValue)
	{
		for (final AbstractOrderEntryModel entryModel : source.getEntries())
		{
			if (entryModel.getProduct() instanceof TelcelPoVariantModel)
			{
				final TelcelPoVariantModel telcelPoVariantModel = (TelcelPoVariantModel) entryModel.getProduct();
				if (telcelPoVariantModel.getChipAdditionalServiceProduct() != null)
				{
					for (final AdditionalServiceEntryModel additionalServiceEntryModel : entryModel.getAdditionalServiceEntries())
					{
						chipAsValue += additionalServiceEntryModel.getBasePrice();
					}
				}
			}
		}
		return chipAsValue;
	}

	protected void addPaymentSpeiInformation(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		final PaymentInfoModel paymentInfo = source.getPaymentInfo();
		if (paymentInfo instanceof SpeiPaymentInfoModel)
		{
			final CCPaymentInfoData paymentInfoData = getSpeiPaymentInfoConverter().convert((SpeiPaymentInfoModel) paymentInfo);
			prototype.setPaymentInfo(paymentInfoData);
		}
	}


	/**
	 * Add payment reference information.
	 *
	 * @param source
	 *           the source
	 * @param prototype
	 *           the prototype
	 */
	protected void addPaymentReferenceInformation(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		final PaymentInfoModel paymentInfo = source.getPaymentInfo();
		if (paymentInfo instanceof ReferencePaymentInfoModel)
		{
			final CCPaymentInfoData paymentInfoData = getReferencePaymentInfoConverter()
					.convert((ReferencePaymentInfoModel) paymentInfo);
			prototype.setPaymentInfo(paymentInfoData);
		}
	}

	/**
	 * Gets reference payment info converter.
	 *
	 * @return the reference payment info converter
	 */
	public Converter<ReferencePaymentInfoModel, CCPaymentInfoData> getReferencePaymentInfoConverter()
	{
		return referencePaymentInfoConverter;
	}

	/**
	 * Sets reference payment info converter.
	 *
	 * @param referencePaymentInfoConverter
	 *           the reference payment info converter
	 */
	public void setReferencePaymentInfoConverter(
			final Converter<ReferencePaymentInfoModel, CCPaymentInfoData> referencePaymentInfoConverter)
	{
		this.referencePaymentInfoConverter = referencePaymentInfoConverter;
	}

	public Converter<SpeiPaymentInfoModel, CCPaymentInfoData> getSpeiPaymentInfoConverter()
	{
		return speiPaymentInfoConverter;
	}

	public void setSpeiPaymentInfoConverter(final Converter<SpeiPaymentInfoModel, CCPaymentInfoData> speiPaymentInfoConverter)
	{
		this.speiPaymentInfoConverter = speiPaymentInfoConverter;
	}
}
