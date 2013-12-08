package ketola.huutonet.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class HuutoNetItem
    implements Serializable
{

    private static final long serialVersionUID = 1L;

    private Date closeDate;

    private String title;

    private String category;

    private String description;

    private String imageThumbnailUrl;

    private String imageNormalUrl;

    private String imageOriginalUrl;

    private String huutoNetUrl;

    private BigDecimal priceCurrent;

    private BigDecimal priceStart;

    public Date getCloseDate()
    {
        return closeDate;
    }

    public void setCloseDate( Date closeDate )
    {
        this.closeDate = closeDate;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory( String category )
    {
        this.category = category;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getImageThumbnailUrl()
    {
        return imageThumbnailUrl;
    }

    public void setImageThumbnailUrl( String imageThumbnailUrl )
    {
        this.imageThumbnailUrl = imageThumbnailUrl;
    }

    public String getImageNormalUrl()
    {
        return imageNormalUrl;
    }

    public void setImageNormalUrl( String imageNormalUrl )
    {
        this.imageNormalUrl = imageNormalUrl;
    }

    public String getImageOriginalUrl()
    {
        return imageOriginalUrl;
    }

    public void setImageOriginalUrl( String imageOriginalUrl )
    {
        this.imageOriginalUrl = imageOriginalUrl;
    }

    public String getHuutoNetUrl()
    {
        return huutoNetUrl;
    }

    public void setHuutoNetUrl( String huutoNetUrl )
    {
        this.huutoNetUrl = huutoNetUrl;
    }

    public BigDecimal getPriceCurrent()
    {
        return priceCurrent;
    }

    public void setPriceCurrent( BigDecimal priceCurrent )
    {
        this.priceCurrent = priceCurrent;
    }

    public BigDecimal getPriceStart()
    {
        return priceStart;
    }

    public void setPriceStart( BigDecimal priceStart )
    {
        this.priceStart = priceStart;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }

}
