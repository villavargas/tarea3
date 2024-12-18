/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.product.CommerceProductReferenceService;
import de.hybris.platform.commerceservices.product.data.ReferenceData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.stocknotificationservices.model.StockNotificationProcessModel;
import de.hybris.platform.util.Utilities;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * Context for sending Back In Stock Notification Email
 */
public class TelcelBackInStockNotificationEmailContext extends AbstractEmailContext<StockNotificationProcessModel>
{
	private static final List<ProductReferenceTypeEnum> referenceTypes = Arrays.asList(ProductReferenceTypeEnum.SIMILAR);
	private static final int MAXIMUM_ALLOWED_PRODUCT_REFERENCE = 5;
	private static final String CUSTOMER_NAME = "customerName";

	private CommerceProductReferenceService<ProductReferenceTypeEnum, ProductModel> commerceProductReferenceService;
	private Converter<ProductModel, ProductData> productConverter;
	private Converter<ProductModel, ProductData> productPriceAndStockConverter;

	private ProductData productData;
	private List<ProductData> productReferences;



	private Locale emailLocale;

	@Override
	public void init(final StockNotificationProcessModel businessProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(businessProcessModel, emailPageModel);

		final CustomerModel customerModel = businessProcessModel.getCustomer();
		put(CUSTOMER_NAME, customerModel.getName());

		productData = getProductConverter().convert(businessProcessModel.getProduct());
		put("modelo", productData.getModelo());
		put("marca", productData.getMarca());

		getProductPriceAndStockConverter().convert(businessProcessModel.getProduct(), productData);
		setEmailLocale(businessProcessModel);
		updateBaseUrl(businessProcessModel, emailLocale);
		updateTitle(businessProcessModel, emailLocale);
		updateProductName(businessProcessModel, emailLocale);
		put("StringUtils", StringUtils.class);
		put("ObjectUtils", ObjectUtils.class);
		productReferences = findProductRelatedToProduct(businessProcessModel);
	}

	protected void setEmailLocale(final StockNotificationProcessModel businessProcessModel)
	{
		final String isoCode = getEmailLanguage(businessProcessModel).getIsocode();
		final String[] loc = Utilities.parseLocaleCodes(isoCode);
		emailLocale = new Locale(loc[0], loc[1], loc[2]);
	}

	protected void updateReferenceProductName(final ProductModel product, final ProductData productData, final Locale emailLocale)
	{
		productData.setName(product.getName(emailLocale));
	}

	protected void updateProductName(final StockNotificationProcessModel businessProcessModel, final Locale emailLocale)
	{
		final String productName = businessProcessModel.getProduct().getName(emailLocale);
		productData.setName(productName);
	}

	protected void updateTitle(final StockNotificationProcessModel businessProcessModel, final Locale emailLocale)
	{
		String title="";
		if(businessProcessModel.getCustomer()!=null && businessProcessModel.getCustomer().getTitle()!=null) {
			title = businessProcessModel.getCustomer().getTitle().getName(emailLocale);
		}
		put(TITLE, title);
	}

	protected void updateBaseUrl(final StockNotificationProcessModel businessProcessModel, final Locale emailLocale)
	{
		final String baseUrl = (String) get(BASE_URL);
		final String baseSecrueUrl = (String) get(SECURE_BASE_URL);
		final String defaultIsoCode = businessProcessModel.getBaseSite().getDefaultLanguage().getIsocode();
		final String siteIsoCode = emailLocale.getLanguage();
		put(BASE_URL, baseUrl.replaceAll("/" + defaultIsoCode + "$", "/" + siteIsoCode));
		put(SECURE_BASE_URL, baseSecrueUrl.replaceAll("/" + defaultIsoCode + "$", "/" + siteIsoCode));
	}

	@Override
	protected BaseSiteModel getSite(final StockNotificationProcessModel stockNotificationProcessModel)
	{
		return stockNotificationProcessModel.getBaseSite();
	}

	@Override
	protected CustomerModel getCustomer(final StockNotificationProcessModel stockNotificationProcessModel)
	{
		return stockNotificationProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final StockNotificationProcessModel stockNotificationProcessModel)
	{
		return stockNotificationProcessModel.getLanguage();
	}

	public ProductData getProductData()
	{
		return productData;
	}

	/**
	 * Find the reference products for the product being interested by customer
	 *
	 * @param stockNotificationProcessModel
	 *           buisness model contains the interested product
	 * @return Return a list of product data
	 */
	public List<ProductData> findProductRelatedToProduct(final StockNotificationProcessModel stockNotificationProcessModel)
	{
		final ProductModel currentProduct = stockNotificationProcessModel.getProduct();
		final List<ReferenceData<ProductReferenceTypeEnum, ProductModel>> references = getCommerceProductReferenceService()
				.getProductReferencesForCode(currentProduct.getCode(), referenceTypes,
						Integer.valueOf(MAXIMUM_ALLOWED_PRODUCT_REFERENCE));

		final List<ProductData> result = new ArrayList<ProductData>();

		for (final ReferenceData<ProductReferenceTypeEnum, ProductModel> reference : references)
		{
			final ProductModel targetProductModel = reference.getTarget();
			final ProductData targetProductData = getProductConverter().convert(targetProductModel);
			updateReferenceProductName(targetProductModel, targetProductData, emailLocale);
			result.add(targetProductData);
		}
		return result;
	}

	public Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	public List<ProductData> getProductReferences()
	{
		return productReferences;
	}

	public CommerceProductReferenceService<ProductReferenceTypeEnum, ProductModel> getCommerceProductReferenceService()
	{
		return commerceProductReferenceService;
	}

	@Required
	public void setCommerceProductReferenceService(
			final CommerceProductReferenceService<ProductReferenceTypeEnum, ProductModel> commerceProductReferenceService)
	{
		this.commerceProductReferenceService = commerceProductReferenceService;
	}

	protected Converter<ProductModel, ProductData> getProductPriceAndStockConverter()
	{
		return productPriceAndStockConverter;
	}

	@Required
	public void setProductPriceAndStockConverter(final Converter<ProductModel, ProductData> productPriceAndStockConverter)
	{
		this.productPriceAndStockConverter = productPriceAndStockConverter;
	}

}
