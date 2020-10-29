package APIPackage.APITesting;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ExplorePopularPlace{

	String BodyResponseData;
	Response Resp;
	JsonPath JsPath;
	int NumberOfNearbyPlaces;
	Map<String, String> params = new HashMap<String, String>();

		
	@BeforeTest()
	public void Init()
	{

	 RestAssured.baseURI= "https://places.demo.api.here.com";
	 Resp = given()
				.queryParam("app_code", "AJKnXv84fjrb0KIHawS0Tg")
				.queryParam("app_id", "DemoAppId01082013GAL")
				.queryParam("cat", "sights-museums")
				.queryParam("in", "52.521,13.3807;r=7768")
				.queryParam("pretty", "true")
				.header("Accept","application/json")
				.header("Accept-Encoding","gzip")
				.when().get("/places/v1/discover/explore")
				.then().assertThat().statusCode(200).extract().response();
	 BodyResponseData= Resp.asString();
	 JsPath = new JsonPath(BodyResponseData);	 
	}
	
	@Test
	public void ValidateReponseCode()
	{
		// Validate that the repose code is 200
		Assert.assertEquals(Resp.getStatusCode(), 200);

	}
	
	@Test
	public void ValidateReponseTime()
	{
		// Validate that the response time is not more than 2000ms
		Assert.assertTrue(Resp.getStatusCode() <= 2000);

	}

	
	@Test
	public void validateNumberOfPlaces()
	{
		// Validate that the Number Of places founded is 20  
		NumberOfNearbyPlaces = JsPath.getInt("results.items.size()");
		Assert.assertEquals(NumberOfNearbyPlaces, 20);
		
	}
	


	@Test(dependsOnMethods = { "validateNumberOfPlaces" })
	public void ValidatePlacesCategory()
	{
		/**There is a Bug Here !!**/
		/*As When we submit a header of cat=sights-museums */
		/* Excepted : Only places that are categorized museum or sights should presented in the results*/
		/* Actual :  railway stations and music house are presented in the resualts*/
		/* Severity: High / Priority: medium  */
		
		//validate that the category of all results is museum 
		for (int i=0; i<NumberOfNearbyPlaces; i++)
		{
			Assert.assertTrue(JsPath.getString("results.items["+i+"].category.id").contains("museums"));

		}

	}
	
	@Test(dependsOnMethods = { "validateNumberOfPlaces" })
	public void ValidatePlacesIsExist()
	{
		String PlaceWeNeedToValidate = "Holocaust Memorial";
		ArrayList<String> ExistantPlacesesList = new ArrayList <String>();
		//Extracts The Existent Places		
		for (int i=0; i<NumberOfNearbyPlaces; i++)
		{
			ExistantPlacesesList.add(JsPath.getString("results.items["+i+"].title"));			

		}
		// validating the intended place is exist 
		Assert.assertTrue(ExistantPlacesesList.contains(PlaceWeNeedToValidate));
		
	}
	
	@Test(dependsOnMethods = { "validateNumberOfPlaces" })
	public void ValidateCurrentLocation()
	{

		String Address = JsPath.getString("search.context.location.address.text");	
		Assert.assertTrue(Address.contains("Luisenstraße 35"));

		
	}
	

}
