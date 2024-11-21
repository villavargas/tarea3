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

import de.hybris.platform.b2ctelcofacades.converters.populator.variants.TmaPoVariantMatrixPopulator;
import de.hybris.platform.b2ctelcoservices.model.TmaPoVariantModel;
import de.hybris.platform.b2ctelcoservices.model.TmaSimpleProductOfferingModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantMatrixElementData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantValueCategoryModel;
import mx.com.telcel.core.model.TelcelPoVariantModel;
import mx.com.telcel.core.util.TelcelUtil;
import mx.com.telcel.facades.product.data.ColorTelcelData;
import mx.com.telcel.facades.product.data.StorageTelcelData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

public class TelcelTmaPoVariantMatrixPopulator extends TmaPoVariantMatrixPopulator {

    @Resource
    private TelcelUtil telcelUtil; // NOSONAR

    @Override
    protected void createNodesForVariant(TmaPoVariantModel variantModel, VariantMatrixElementData currentParentNode) {

        if (variantModel instanceof TelcelPoVariantModel) {
            TelcelPoVariantModel variant = (TelcelPoVariantModel) variantModel;
            for (final VariantValueCategoryModel valueCategory : getVariantValuesCategories(variant)) {
                final VariantMatrixElementData existingNode = getExistingNode(currentParentNode, valueCategory);
                if (existingNode == null) {
                    final VariantMatrixElementData createdNode = createNode(currentParentNode, valueCategory);
                    createdNode.getVariantOption().setCode(variant.getCode());
                    createdNode.getVariantOption().setName(variant.getName());
                    if (Objects.nonNull(variant.getColor()) && Objects.nonNull(variant.getColor().getCode())) {

                        ColorTelcelData colorTelcelData = new ColorTelcelData();
                        colorTelcelData.setName(variant.getColor().getName());
                        colorTelcelData.setCode(variant.getColor().getCode());
                        colorTelcelData.setHexadecimal(variant.getColor().getHexCode());
                        createdNode.getVariantOption().setColor(colorTelcelData);

                    }

                    if (Objects.nonNull(variant.getStorage()) && Objects.nonNull(variant.getStorage().getStorageValue())) {
                        StorageTelcelData storageTelcelData = new StorageTelcelData();
                        storageTelcelData.setUnit(variant.getStorage().getStorageUnit().getName());
                        storageTelcelData.setValue(variant.getStorage().getStorageValue().toString());
                        createdNode.getVariantOption().setCapacidad(storageTelcelData);
                    }

                    currentParentNode = createdNode;
                } else {
                    currentParentNode = existingNode;
                }
            }
        }

    }


    protected List<VariantValueCategoryModel> getVariantValuesCategories(final TmaPoVariantModel poVariant) {
        final List<VariantValueCategoryModel> variantValueCategories = poVariant.getSupercategories().stream()
                .filter(categoryModel -> (categoryModel instanceof VariantValueCategoryModel))
                .map(categoryModel -> (VariantValueCategoryModel) categoryModel)
                .collect(Collectors.toList());
        Collections.sort(variantValueCategories, getValueCategoryComparator());
        return variantValueCategories;
    }

    @Override
    public void populate(final ProductModel productModel, final ProductData productData) {
        if (!(productModel instanceof TmaSimpleProductOfferingModel)) {
            return;
        }
        final TmaSimpleProductOfferingModel spoModel = (TmaSimpleProductOfferingModel) productModel;
        final Collection<TmaPoVariantModel> variantsTelcel = getVariants(spoModel);
        Collection<TmaPoVariantModel> variants = validateAvailabilityVariant(variantsTelcel);
        final boolean hasVariants = CollectionUtils.isNotEmpty(variants);
        productData.setMultidimensional(Boolean.valueOf(hasVariants));

        if (hasVariants) {
            final VariantMatrixElementData nodeZero = createEmptyNode();
            final TmaPoVariantModel starterVariant = getStarterVariant(spoModel, variants);
            createNodesForVariant(starterVariant, nodeZero);

            variants.stream().filter(v -> v instanceof TmaPoVariantModel).collect(Collectors.toList()).forEach(variant ->
            {
                if (!variant.getCode().equals(spoModel.getCode()))//don't process the first variant again
                {
                    createNodesForVariant(variant, nodeZero);
                }
            });

            orderTree(nodeZero.getElements());
            productData.setVariantMatrix(nodeZero.getElements());
        }
    }

    private Collection<TmaPoVariantModel> validateAvailabilityVariant(Collection<TmaPoVariantModel> variantsTelcel) {
        final String region = telcelUtil.getRegionByRequest(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()); // NOSONAR
        Collection<TmaPoVariantModel> variants = new ArrayList<>();
        if (StringUtils.isNotEmpty(region)) {
            filterVariantsByRegion(variantsTelcel, region, variants);
            return variants;
        } else {
            return variantsTelcel;
        }

    }

    private void filterVariantsByRegion(Collection<TmaPoVariantModel> variantsTelcel, String region, Collection<TmaPoVariantModel> variants) {
        switch (region) {
            case "1":
                for (TmaPoVariantModel model : variantsTelcel) {
                    if (!model.getR1()) {
                        variants.add(model);
                    }
                }
                break;
            case "2":
                for (TmaPoVariantModel model : variantsTelcel) {
                    if (!model.getR2()) {
                        variants.add(model);
                    }
                }
                break;
            case "3":
                for (TmaPoVariantModel model : variantsTelcel) {
                    if (!model.getR3()) {
                        variants.add(model);
                    }
                }
                break;
            case "4":
                for (TmaPoVariantModel model : variantsTelcel) {
                    if (!model.getR4()) {
                        variants.add(model);
                    }
                }
                break;
            case "5":
                for (TmaPoVariantModel model : variantsTelcel) {
                    if (!model.getR5()) {
                        variants.add(model);
                    }
                }
                break;
            case "6":
                for (TmaPoVariantModel model : variantsTelcel) {
                    if (!model.getR6()) {
                        variants.add(model);
                    }
                }
                break;
            case "7":
                for (TmaPoVariantModel model : variantsTelcel) {
                    if (!model.getR7()) {
                        variants.add(model);
                    }
                }
                break;
            case "8":
                for (TmaPoVariantModel model : variantsTelcel) {
                    if (!model.getR8()) {
                        variants.add(model);
                    }
                }
                break;
            case "9":
                for (TmaPoVariantModel model : variantsTelcel) {
                    if (!model.getR9()) {
                        variants.add(model);
                    }
                }
                break;
        }
    }


}
