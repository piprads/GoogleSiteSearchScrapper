package simplescrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Sample Google Site Search
 * @author radheshg
 *
 */
public class SimpleScrapper {

	static HashMap<String, String> sourcesDateUrlMap = new HashMap<String, String>();

	public static void main(String[] args) {

		try {

			FileReader f1 = new FileReader("sources");
			BufferedReader sourceBufferedReader = new BufferedReader(f1);

			String sourceName = null;
			while ((sourceName = sourceBufferedReader.readLine()) != null) {

				FileReader f = new FileReader("querywords");
				BufferedReader bufferedReader = new BufferedReader(f);
				String mainSource = sourceName.substring(0,
						sourceName.indexOf('.'));
				// String mainSource = "timesofindia";

				String line = null;
				
				try{

				while ((line = bufferedReader.readLine()) != null) {

					StringBuilder builder = new StringBuilder();

					// String sampleUrlString =
					// "http://www.google.com/search?num=100&q=site:http://timesofindia.indiatimes.com+"
					// + line;
					String sampleUrlString = "http://www.google.com/search?num=100&q=site:http://"
							+ sourceName + "+" + line;

					// String sampleUrlString =
					// StringEscapeUtils.escapeJava("http://www.google.com/#sclient=psy-ab&hl=en&source=hp&q=site:hindu.com+malaria&pbx=1&oq=site:hindu.com+malaria&aq=f&aqi=&aql=1&gs_sm=e&gs_upl=25269l27300l0l27489l8l7l0l0l0l0l186l817l3.4l7l0&bav=on.2,or.r_gc.r_pw.r_cp.,cf.osb&fp=55ec5d59dd72fa0b&biw=1366&bih=635");

					URL connectionUrl = new URL(sampleUrlString);

					URLConnection urlConnection = connectionUrl
							.openConnection();
					urlConnection
							.setRequestProperty("User-Agent",
									"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.3) Gecko/20100401");
					BufferedReader in = new BufferedReader(
							new InputStreamReader(
									urlConnection.getInputStream()));
					int inputChar;

					while ((inputChar = in.read()) != -1) {
						builder.append((char) inputChar);
					}
					in.close();

					// System.out.println(builder);

					HashMap<String, String> searchNodes = new HashMap<String, String>();

					searchNodes = getUrlsFromSearchPage(builder.toString());

					FileWriter writer1 = new FileWriter(mainSource + "_" + line);
					for (String string : searchNodes.keySet()) {
						System.out.println(string + " "
								+ searchNodes.get(string));
						writer1.write(string + " " + searchNodes.get(string)
								+ "\n");

					}
					writer1.close();
				}
				}
				catch(Exception e){
					
				}
			}
		} catch (Exception e) {
			System.out.println("exception occured.. Ok!");
		}
	}

	private static HashMap<String, String> getUrlsFromSearchPage(
			String searchHtmlString) {

		try {
			Document doc = Jsoup.parse(searchHtmlString);

			Elements searchElements = doc.getElementsByClass("g");

			for (Element element : searchElements) {

				String date = null, url = null;

				Elements dateElements = element.getElementsByClass("f");
				for (Element dateElement : dateElements) {
					System.out.println(dateElement.text().substring(0, 12));
					date = dateElement.text().substring(0, 12);
					break;
				}

				Elements anchor = element.getElementsByTag("h3");

				// System.out.println(anchor.html());

				url = StringUtils.substringBetween(anchor.html(), "href=\"",
						"\" ");
				System.out.println(url);
				/*
				 * for (Element anchElement : anchor) { Elements aElements =
				 * anchElement.getElementsByTag("a"); for (Element finalElements
				 * : aElements) {
				 * 
				 * System.out.println("--" + finalElements.html());
				 * 
				 * Element hrefElement = finalElements.getElementById("href");
				 * System.out.println(hrefElement); } }
				 */

				/*
				 * Elements cites = element.getElementsByTag("cite");
				 * 
				 * for (Element citeElement : cites) {
				 * System.out.println(citeElement.text()); url =
				 * citeElement.text(); }
				 */

				sourcesDateUrlMap.put(date, url);
			}
		} catch (Exception e) {
		}

		return sourcesDateUrlMap;
	}

}
