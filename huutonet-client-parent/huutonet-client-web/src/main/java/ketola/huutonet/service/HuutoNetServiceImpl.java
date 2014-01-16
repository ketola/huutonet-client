package ketola.huutonet.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ketola.huutonet.domain.HuutoNetItem;
import ketola.huutonet.domain.HuutoNetItem.Type;
import ketola.huutonet.service.httpclient.OSProtocolSocketFactory;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

public class HuutoNetServiceImpl
    implements HuutoNetService
{
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static final String URL_ITEMS = "http://api.huuto.net/somt/0.9/items?seller=satukirppu&num=50";

    public static final String URL_ITEM = "http://api.huuto.net/somt/0.9/items/%s";

    public HuutoNetServiceImpl()
    {
        // required to use http client on OpenShift
        Protocol.registerProtocol( "http", new Protocol( "http", new OSProtocolSocketFactory(), 80 ) );
    }

    @Override
    public List<String> fetchItemIds()
    {
        List<String> itemIds = new ArrayList<String>();

        AbderaClient abderaClient = abderaClient();

        ClientResponse response = abderaClient.get( URL_ITEMS );
        Document<Feed> doc = response.getDocument();
        List<Entry> entries = doc.getRoot().getEntries();

        for ( Entry entry : entries )
        {
            String id = StringUtils.substringAfterLast( entry.getId().toString(), "/" );
            itemIds.add( id );
        }

        return itemIds;
    }

    @Override
    public HuutoNetItem fetchItem( String id )
    {
        AbderaClient abderaClient = abderaClient();

        ClientResponse entryResponse = abderaClient.get( String.format( URL_ITEM, id ) );

        Entry itemEntry = (Entry) entryResponse.getDocument().getRoot();

        return toHuutoNetItem( itemEntry );
    }

    public List<HuutoNetItem> fetchHuutoNetItems()
    {
        List<HuutoNetItem> items = new ArrayList<HuutoNetItem>();

        AbderaClient abderaClient = abderaClient();

        ClientResponse response = abderaClient.get( URL_ITEMS );
        Document<Feed> doc = response.getDocument();
        List<Entry> entries = doc.getRoot().getEntries();

        List<ItemReader> itemReaders = new ArrayList<HuutoNetServiceImpl.ItemReader>();

        // create an item reader thread for each item
        for ( Entry entry : entries )
        {
            ItemReader itemReader = new ItemReader( entry, abderaClient );
            itemReaders.add( itemReader );
            itemReader.run();
        }

        // wait for all threads to finish
        for ( ItemReader itemReader : itemReaders )
        {
            try
            {
                itemReader.join();
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
        }

        // get the items that were read from each reader
        for ( ItemReader itemReader : itemReaders )
        {
            items.add( itemReader.item );
        }

        return items;
    }

    protected AbderaClient abderaClient()
    {
        return new AbderaClient( Abdera.getInstance() );
    }

    private class ItemReader
        extends Thread
    {
        private Entry entry;

        private AbderaClient client;

        private HuutoNetItem item;

        public ItemReader( Entry entry, AbderaClient client )
        {
            this.entry = entry;
            this.client = client;
        }

        @Override
        public void run()
        {
            ClientResponse entryResponse = client.get( entry.getId().toString() );
            Entry itemEntry = (Entry) entryResponse.getDocument().getRoot();
            this.item = toHuutoNetItem( itemEntry );
        }

    }

    private static HuutoNetItem toHuutoNetItem( Entry itemEntry )
    {
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

            else if ( element.getQName().getLocalPart().equals( "price" ) )
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

            else if ( element.getQName().getLocalPart().equals( "intention" ) )
            {
                if ( element.getAttributeValue( "type" ).equals( "AUCTION" ) )
                {
                    item.setType( Type.AUCTION );
                }
                else if ( element.getAttributeValue( "type" ).equals( "BUY_NOW" ) )
                {
                    item.setType( Type.BUY_NOW );
                }
            }
        }
        return item;
    }

}
