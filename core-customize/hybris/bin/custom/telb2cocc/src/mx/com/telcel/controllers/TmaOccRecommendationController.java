/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.controllers;

import static de.hybris.platform.b2ctelcoservices.constants.B2ctelcoservicesConstants.SELECTED_PROCESS_TYPE;

import de.hybris.platform.b2ctelcocommercewebservicescommons.data.TmaOfferProductListData;
import de.hybris.platform.b2ctelcocommercewebservicescommons.data.TmaProcessTypeListData;
import de.hybris.platform.b2ctelcocommercewebservicescommons.data.TmaSubscriptionBasesData;
import de.hybris.platform.b2ctelcocommercewebservicescommons.data.TmaSubscriptionBasesListData;
import de.hybris.platform.b2ctelcocommercewebservicescommons.data.TmaSubscriptionSelectionListData;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.TmaEligibleSubscriptionSelectionListWsDto;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.TmaOfferProductListWsDto;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.TmaOfferProductWsDto;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.TmaProcessTypeListWsDto;
import de.hybris.platform.b2ctelcocommercewebservicescommons.dto.TmaRecommendationProductsWsDto;
import de.hybris.platform.b2ctelcofacades.data.TmaOfferData;
import de.hybris.platform.b2ctelcofacades.data.TmaSubscriptionBaseData;
import de.hybris.platform.b2ctelcofacades.data.TmaSubscriptionSelectionData;
import de.hybris.platform.b2ctelcofacades.product.TmaProductOfferFacade;
import de.hybris.platform.b2ctelcofacades.user.TmaCustomerFacade;
import de.hybris.platform.b2ctelcoservices.enums.TmaProcessType;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * Controller exposing operations related to the Recommendation offer for the customer
 *
 * @since 1907
 */
@Controller
@RequestMapping("/{baseSiteId}/users/{userId}")
@Api(tags = "Recommendation", description = "The Recommendation offer's API")
@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
public class TmaOccRecommendationController extends BaseController
{
	private static final String SUBSCRIPTION_NOT_FOUND = "tmaProcessType.noSubscriptions";

	@Resource(name = "tmaProductOfferFacade")
	private TmaProductOfferFacade tmaProductOfferFacade;
	@Resource(name = "customerFacade")
	private TmaCustomerFacade customerFacade;
//	@Resource(name = "tmaSubscriptionBaseValidator")
//	private TmaSubscriptionBaseValidator tmaSubscriptionBaseValidator;
	//@Resource(name = "tmaProcessTypeValidator")
	//private TmaProcessTypeValidator tmaProcessTypeValidator;
	@Resource(name = "sessionService")
	private SessionService sessionService;

	/**
	 * @deprecated since 2007.
	 * Use instead recommendations implemented in TMF version {@link de.hybris.platform.b2ctelcotmfwebservices.v2.controller.TmaProcessTypeManagementApiController}.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@GetMapping("/qualifiedProcessTypes")
	@ResponseBody
	@ApiOperation(nickname = "getProcessTypes", value = "Get customer eligible process types", notes = "Returns customer eligible process types.")
	@ApiBaseSiteIdAndUserIdParam
	@Deprecated(since = "2007")
	public TmaProcessTypeListWsDto getProcessTypes(
			@ApiParam(value = "This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final Set<TmaProcessType> eligibleProcessTypes = customerFacade.retrieveEligibleProcessTypes();
		final List<String> processTypes = new ArrayList<>();
		final TmaProcessTypeListData processTypeListData = new TmaProcessTypeListData();
		if (CollectionUtils.isNotEmpty(eligibleProcessTypes))
		{
			eligibleProcessTypes.forEach(processType ->
			{
				processTypes.add(processType.getCode());
			});
		}
		processTypeListData.setProcessTypeCode(processTypes);
		return getDataMapper().map(processTypeListData, TmaProcessTypeListWsDto.class, fields);
	}

	/**
	 * @deprecated since 2007.
	 * Use instead recommendations implemented in TMF version {@link de.hybris.platform.b2ctelcotmfwebservices.v2.controller.TmaRecommendationApiController}.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@GetMapping("/recommendations")
	@ResponseBody
	@ApiOperation(nickname = "getRecommendations", value = "Get recommended product offering list for the given subscription", notes = "Returns list of recommended product offering.")
	@ApiBaseSiteIdAndUserIdParam
	@Deprecated(since = "2007")
	public TmaRecommendationProductsWsDto getOffers(
			@ApiParam(value = "Process type for the subscription.") @RequestParam(required = false) final String processType,
			@ApiParam(value = "List of subscriberIdentities. The format of a subscriberIdentities : subscriberIdentity1" + "\\_\\_"
					+ "billingSystemId1, subscriberIdentity2" + "\\_\\_"
					+ "billingSystemId2.") @RequestParam(required = false) final String subscriberIdentities,
			@ApiParam(value = "Affected product id.") @RequestParam(required = false) final String affectedProductCode,
			@ApiParam(value = "This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final TmaRecommendationProductsWsDto recommendationProducts = new TmaRecommendationProductsWsDto();
		if (StringUtils.isEmpty(processType) || StringUtils.isEmpty(subscriberIdentities)
				|| StringUtils.isEmpty(affectedProductCode))
		{
			return recommendationProducts;
		}
	//	validate(processType, "TmaProcessType", tmaProcessTypeValidator);
		getEligibleSubscriptionSelections(processType);
		sessionService.setAttribute(SELECTED_PROCESS_TYPE, processType);
		final List<TmaSubscriptionBaseData> selectedSubscriptions = customerFacade
				.getSubscriptionBasesFromString(subscriberIdentities);
		//validate(selectedSubscriptions, "TmaSubscriptionBase", tmaSubscriptionBaseValidator);
		final Set<TmaSubscriptionBaseData> eligibleSubscriptions = customerFacade
				.retrieveEligibleSubscriptions(TmaProcessType.valueOf(processType));
		final String bpoCode = CollectionUtils.isNotEmpty(eligibleSubscriptions) ? getBpoCode(eligibleSubscriptions)
				: StringUtils.EMPTY;
		if ((CollectionUtils.isNotEmpty(eligibleSubscriptions))
				&& (!canDisplayOffers(bpoCode, affectedProductCode, selectedSubscriptions, eligibleSubscriptions)))
		{
			return recommendationProducts;
		}
		final Set<String> requiredProductCodes = customerFacade
				.getMainTariffProductCodesForSubscriptionBases(selectedSubscriptions);
		if (requiredProductCodes.isEmpty())
		{
			return recommendationProducts;
		}
		addAffectedProductToResponseBody(affectedProductCode, recommendationProducts, fields);
		final TmaOfferProductListData tmaOfferDataList = new TmaOfferProductListData();
		tmaOfferDataList
				.setOfferProduct(obtainOffersForProvidedData(processType, affectedProductCode, bpoCode, requiredProductCodes));
		if (CollectionUtils.isNotEmpty(tmaOfferDataList.getOfferProduct()))
		{
			final TmaOfferProductListWsDto tmaOfferListWsDto = new TmaOfferProductListWsDto();
			final List<TmaOfferProductWsDto> tmaOfferWsDtos = new ArrayList<>();
			tmaOfferDataList.getOfferProduct().forEach(offerProduct ->
			{
				final TmaOfferProductWsDto tmaOfferWsDto = getDataMapper().map(offerProduct, TmaOfferProductWsDto.class, fields);
				tmaOfferWsDtos.add(tmaOfferWsDto);
			});
			tmaOfferListWsDto.setOfferProduct(tmaOfferWsDtos);
			recommendationProducts.setOffers(tmaOfferListWsDto);
		}
		return recommendationProducts;
	}

	private String getBpoCode(final Set<TmaSubscriptionBaseData> eligibleSubscriptions)
	{
		String bpoCode = "";
		for (final TmaSubscriptionBaseData subscriptionBaseData : eligibleSubscriptions)
		{
			if (!ObjectUtils.isEmpty(subscriptionBaseData.getTmaSubscribedProductData()))
			{
				bpoCode = subscriptionBaseData.getTmaSubscribedProductData().iterator().next().getParentPOCode();
				if (StringUtils.isNotEmpty(bpoCode))
				{
					break;
				}
			}
		}
		return bpoCode;
	}

	/**
	 * @deprecated since 2007.
	 * Use instead recommendations implemented in TMF version {@link de.hybris.platform.b2ctelcotmfwebservices.v2.controller.TmaSubscriptionBaseApiController}
	 * and {@link de.hybris.platform.b2ctelcotmfwebservices.v2.controller.TmaSubscriptionApiController}.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@GetMapping("{processType}/subscriptionBases")
	@ResponseBody
	@ApiOperation(nickname = "getSubscriptionBases", value = "Get subscriptions of customer based on process type and eligibility rules", notes = "Returns subscriptions details of the customer based on process type and eligibility rules")
	@ApiBaseSiteIdAndUserIdParam
	@Deprecated(since = "2007")
	public TmaEligibleSubscriptionSelectionListWsDto getSubscriptionBases(
			@ApiParam(value = "process type.", required = true) @PathVariable final String processType,
			@ApiParam(value = "This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{

	//	validate(processType, "TmaProcessType", tmaProcessTypeValidator);
		final Map<String, List<TmaSubscriptionSelectionData>> eligibleSubscriptionSelections = getEligibleSubscriptionSelections(
				processType);
		final List<TmaSubscriptionBasesData> tmaSubscriptionBasesDataList = new ArrayList<>();
		eligibleSubscriptionSelections.entrySet().forEach(mapItem ->
		{
			final TmaSubscriptionBasesData tmaSubscriptionBasesData = new TmaSubscriptionBasesData();
			final TmaSubscriptionSelectionListData tmaSubscriptionSelectionBaseDataList = new TmaSubscriptionSelectionListData();
			tmaSubscriptionBasesData.setBillingArrangementId(mapItem.getKey());
			tmaSubscriptionSelectionBaseDataList.setSubscriptionSelection(mapItem.getValue());
			tmaSubscriptionBasesData.setSubscriptionSelectionList(tmaSubscriptionSelectionBaseDataList);
			tmaSubscriptionBasesDataList.add(tmaSubscriptionBasesData);
		});

		final TmaSubscriptionBasesListData tmaSubscriptionBasesListData = new TmaSubscriptionBasesListData();
		tmaSubscriptionBasesListData.setEligibleSubscriptionSelection(tmaSubscriptionBasesDataList);
		return getDataMapper().map(tmaSubscriptionBasesListData, TmaEligibleSubscriptionSelectionListWsDto.class, fields);
	}

	private void addAffectedProductToResponseBody(final String affectedProductCode,
			final TmaRecommendationProductsWsDto retentionRecommendationsWsDto, final String fields)
	{
		final ProductData affectedProductData = tmaProductOfferFacade.getPoForCode(affectedProductCode, Arrays
				.asList(ProductOption.SUMMARY, ProductOption.STOCK, ProductOption.PRICE, ProductOption.PRODUCT_OFFERING_PRICES));
		retentionRecommendationsWsDto.setProduct(getDataMapper().map(affectedProductData, ProductWsDTO.class, fields));
	}

	private boolean canDisplayOffers(final String bpoCode, final String affectedProductCode,
			final List<TmaSubscriptionBaseData> selectedSubscriptions, final Set<TmaSubscriptionBaseData> eligibleSubscriptions)
	{
		return isValidBpo(bpoCode, affectedProductCode)
				&& areSubscriptionsEligibleForProcess(selectedSubscriptions, eligibleSubscriptions);
	}

	private boolean isValidBpo(final String bpoCode, final String affectedProductCode)
	{
		return StringUtils.isNotEmpty(bpoCode) && tmaProductOfferFacade.isValidParent(affectedProductCode, bpoCode);
	}

	private boolean areSubscriptionsEligibleForProcess(final List<TmaSubscriptionBaseData> selectedSubscriptions,
			final Set<TmaSubscriptionBaseData> eligibleSubscriptions)
	{
		return selectedSubscriptions.stream()
				.allMatch(selectedSubscription -> isSubscriptionEligible(selectedSubscription, eligibleSubscriptions));
	}

	private boolean isSubscriptionEligible(final TmaSubscriptionBaseData subscriptionBaseData,
			final Set<TmaSubscriptionBaseData> eligibleSubscriptions)
	{
		final Optional<TmaSubscriptionBaseData> subscription = eligibleSubscriptions.stream()
				.filter(eligibleSubscription -> eligibleSubscription.getSubscriberIdentity()
						.equals(subscriptionBaseData.getSubscriberIdentity())
						&& eligibleSubscription.getBillingSystemId().equals(subscriptionBaseData.getBillingSystemId()))
				.findFirst();
		return subscription.isPresent();
	}

	private List<TmaOfferData> obtainOffersForProvidedData(final String processType, final String affectedProductCode,
			final String bpoCode, final Set<String> requiredProductCodes)
	{
		return tmaProductOfferFacade.getOffers(processType, affectedProductCode, bpoCode, requiredProductCodes);
	}

/*
 * private void validateSubscriberIdentities(final List<TmaSubscriptionBaseData> selectedSubscriptions) {
 * selectedSubscriptions.forEach(subscriptionBaseData -> { validate(subscriptionBaseData, "TmaSubscriptionBase",
 * tmaSubscriptionBaseValidator); }); }
 */

	private Map<String, List<TmaSubscriptionSelectionData>> getEligibleSubscriptionSelections(final String processType)
	{
		try
		{
			return customerFacade.determineEligibleSubscriptionSelections(TmaProcessType.valueOf(processType));
		}
		catch (final ModelNotFoundException modelNotFoundException)
		{
			throw new NotFoundException(getMessageSource().getMessage(SUBSCRIPTION_NOT_FOUND, new Object[]
			{ processType }, getI18nService().getCurrentLocale()));
		}
	}
}
