/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.facades.converters.populator;

import de.hybris.platform.b2ctelcoservices.model.TmaPoVariantModel;
import de.hybris.platform.b2ctelcoservices.model.TmaProductOfferingModel;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;


/**
 * Populates {@link VariantOptionData} with variant media data. If no medias are found on variant, then medias from base
 * product
 * offering are used.
 *
 * @since 1810
 */
public class TmaVariantOptionDataMediaPopulator<SOURCE extends TmaPoVariantModel, TARGET extends VariantOptionData> implements
        Populator<SOURCE, TARGET>
{
    private Converter<MediaModel, ImageData> imageConverter;
    private ImageFormatMapping acceleratorImageFormatMapping;
    private List<String> imageFormats;

    @Override
    public void populate(final SOURCE poVariantModel, final TARGET variantOptionData)
    {
        final Collection<VariantOptionQualifierData> qualifierDataList = new ArrayList<>();
        final Collection<MediaModel> mediaModels = getMediaModels(poVariantModel);
        if (!CollectionUtils.isEmpty(mediaModels))
        {
            mediaModels.stream().forEach(mediaModel ->
            {
                if(Objects.nonNull(mediaModel.getMediaFormat()))
                {
                    qualifierDataList.add(getQualifierData(mediaModel));
                }
            });
        }
        /**Se comenta línea de variantOptionQualifiers para mejora de performance en PDP debido a que las
         URL de las imagenes no se ocupan**/
        //variantOptionData.setVariantOptionQualifiers(qualifierDataList);
    }

    private Collection<MediaModel> getMediaModels(final TmaPoVariantModel poVariantModel)
    {
        final Collection<MediaModel> variantMedias = getContainerMedias(poVariantModel);
        return variantMedias.isEmpty() ? getContainerMedias(poVariantModel.getTmaBasePo()) : variantMedias;
    }

    private Collection<MediaModel> getContainerMedias(final TmaProductOfferingModel poModel)
    {
        final Collection<MediaModel> medias = new ArrayList<>();
        final List<MediaContainerModel> galleryImages = poModel.getGalleryImages();
        if (galleryImages != null)
        {
            galleryImages.forEach(mediaContainer -> medias.addAll(mediaContainer.getMedias()));
        }
        return medias;
    }

    private VariantOptionQualifierData getQualifierData(final MediaModel mediaModel)
    {
        final ImageData imageData = getImageConverter().convert(mediaModel);
        imageData.setFormat(getMediaFormat(mediaModel.getMediaFormat().getName()));
        final VariantOptionQualifierData qualifierData = new VariantOptionQualifierData();
        qualifierData.setImage(imageData);
        return qualifierData;
    }

    protected String getMediaFormat(final String format)
    {
        for (final String imageFormat : imageFormats)
        {
            if (format.equals(getAcceleratorImageFormatMapping().getMediaFormatQualifierForImageFormat(imageFormat)))
            {
                return imageFormat;
            }
        }
        return null;
    }

    protected Converter<MediaModel, ImageData> getImageConverter()
    {
        return imageConverter;
    }

    @Required
    public void setImageConverter(
            final Converter<MediaModel, ImageData> imageConverter)
    {
        this.imageConverter = imageConverter;
    }

    protected List<String> getImageFormats()
    {
        return imageFormats;
    }

    @Required
    public void setImageFormats(final List<String> imageFormats)
    {
        this.imageFormats = imageFormats;
    }

    protected ImageFormatMapping getAcceleratorImageFormatMapping()
    {
        return acceleratorImageFormatMapping;
    }

    @Required
    public void setAcceleratorImageFormatMapping(final ImageFormatMapping acceleratorImageFormatMapping)
    {
        this.acceleratorImageFormatMapping = acceleratorImageFormatMapping;
    }
}


