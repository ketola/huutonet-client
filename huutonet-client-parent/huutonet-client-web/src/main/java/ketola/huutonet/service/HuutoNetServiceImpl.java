package ketola.huutonet.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ketola.huutonet.domain.HuutoNetItem;
import ketola.huutonet.service.httpclient.OSProtocolSocketFactory;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.lang3.time.DateUtils;

public class HuutoNetServiceImpl
    implements HuutoNetService
{
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static final String URL_ITEMS = "http://api.huuto.net/somt/0.9/users/satukirppu/items";

    public HuutoNetServiceImpl()
    {
        // required to use http client on OpenShift
        Protocol.registerProtocol( "http", new Protocol( "http", new OSProtocolSocketFactory(), 80 ) );
    }

    public List<HuutoNetItem> fetchHuutoNetItems()
    {
        List<HuutoNetItem> items = new ArrayList<HuutoNetItem>();

        AbderaClient abderaClient = abderaClient();

        ClientResponse response = abderaClient.get( URL_ITEMS );
        Document<Feed> doc = response.getDocument();
        List<Entry> entries = doc.getRoot().getEntries();

        for ( Entry entry : entries )
        {
            ClientResponse entryResponse = abderaClient.get( entry.getId().toString() );
            Entry itemEntry = (Entry) entryResponse.getDocument().getRoot();

            HuutoNetItem item = new HuutoNetItem();

            item.setTitle( itemEntry.getTitle() );
            item.setCategory( itemEntry.getCategories().get( 0 ).getTerm() );
            item.setDescription( itemEntry.getSummary() );
            item.setHuutoNetUrl( itemEntry.getLink( "alternative" ).getAttributeValue( "href" ) );
            item.setImageNormalUrl( itemEntry.getLink( "image-normal" ).getAttributeValue( "href" ) );
            item.setImageOriginalUrl( itemEntry.getLink( "image-original" ).getAttributeValue( "href" ) );
            item.setImageThumbnailUrl( itemEntry.getLink( "image-thumbnail" ).getAttributeValue( "href" ) );

            for ( Element element : itemEntry.getElements() )
            {
                if ( element.getQName().getLocalPart().equals( "expirationTime" ) )
                {
                    try
                    {
                        item.setCloseDate( DateUtils.parseDate( element.getText().replaceAll( ":(\\d\\d)$", "$1" ),
                                                                new String[] { DATE_FORMAT } ) );
                    }
                    catch ( ParseException e )
                    {
                        throw new RuntimeException( "Date:" + element.getText(), e );
                    }
                }

                if ( element.getQName().getLocalPart().equals( "price" ) )
                {
                    for ( Element priceElement : element.getElements() )
                    {
                        if ( priceElement.getQName().getLocalPart().equals( "currentPrice" ) )
                        {
                            item.setPriceCurrent( new BigDecimal( priceElement.getText() ) );
                        }

                        if ( priceElement.getQName().getLocalPart().equals( "startingPrice" ) )
                        {
                            item.setPriceStart( new BigDecimal( priceElement.getText() ) );
                        }
                    }
                }

            }
            items.add( item );
        }

        return items;
    }

    protected AbderaClient abderaClient()
    {
        return new AbderaClient( Abdera.getInstance() );
    }

}
