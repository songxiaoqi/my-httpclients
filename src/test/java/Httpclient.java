import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.kahui.httpclient.IRawHttpClientService;
import com.kahui.httpclient.dependents.Pair;
import com.kahui.httpclient.enums.HttpMethod;
import com.kahui.httpclient.internal.DefaultResponseHandle;
import com.kahui.httpclient.internal.HttpCookieContainer;
import com.kahui.httpclient.internal.RawHttpClientService;

public class Httpclient {

	protected static final String HOST = "card.bankofbeijing.com.cn";//
    protected static final String ACCEPT_LANGUAGE = "zh-CN";
    protected static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 4.4.4; zh-cn; YQ601 Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/6.3 Mobile Safari/537.36";
    protected static final String ACCEPT_ENCODING = "gzip";
    protected static final String ENCODE = "UTF-8";
    private static final String DEVICE_TAB = "TabDefault";
    private static final Logger LOGGER = LoggerFactory.getLogger(Httpclient.class);
    private static final String OUT_TIME = "out_time";
	@Test
	public void demo(){
		IRawHttpClientService service = null;
        try{
            String url;
            service = new RawHttpClientService(HOST,443,true);
            ArrayList<NameValuePair> header = Lists.newArrayList();
            header.add(new BasicNameValuePair("Accept-Language", ACCEPT_LANGUAGE));
            header.add(new BasicNameValuePair("User-Agent", USER_AGENT));
            header.add(new BasicNameValuePair("Q-Refer", "000200"));
            header.add(new BasicNameValuePair("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
            header.add(new BasicNameValuePair("Accept-Charset", "utf-8, iso-8859-1, utf-16, *;q=0.7"));
            header.add(new BasicNameValuePair("Accept-Encoding", ACCEPT_ENCODING));
            header.add(new BasicNameValuePair("Connection:", "keep-alive"));
            header.add(new BasicNameValuePair("Host", HOST));

            Pair<List<NameValuePair>, String> response = service.request("https://card.bankofbeijing.com.cn/perbank/",
                    HttpMethod.GET,
                    header,
                    null,
                    new DefaultResponseHandle(),
                    ENCODE);
            HttpCookieContainer httpCookieContainer = new HttpCookieContainer();
            httpCookieContainer.accept(response.first);
            LOGGER.debug("cookie:",httpCookieContainer.toString());
            LOGGER.debug(response.getSecond());
           
        }catch(Exception e){
            throw new RuntimeException("PreLogin fail.", e);
        }finally {
            if (service != null) {
                service.close();
            }
        }
	}
}
